package Eidi._123Fastq.Cli;

import Eidi._123Fastq.GUI.Statics;
import Eidi._123Fastq.QualityControlPackage.Modules.ModuleFactory;
import Eidi._123Fastq.QualityControlPackage.Report.HTMLReportArchive;
import Eidi._123Fastq.QualityControlPackage.Results.QcAnalysisResult;
import Eidi._123Fastq.QualityControlPackage.Results.QcRunner;
import Eidi._123Fastq.QualityControlPackage.Sequence.BAMFile;
import Eidi._123Fastq.QualityControlPackage.Sequence.SequenceFactory;
import Eidi._123Fastq.QualityControlPackage.Sequence.SequenceFile;
import Eidi._123Fastq.SingleCellPackage.SingleCellRequest;
import Eidi._123Fastq.SingleCellPackage.SingleCellRunner;
import Eidi._123Fastq.TrimFactoryPackage.FastqSerializer;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

final class Cli {
    private static final String INVOCATION = "java -jar 123Fastq.jar";
    static final String[] NO_FLAGS = {};
    private static final String COMMON_HELP =
            "\nUse --quiet to suppress progress messages. Successful outputs are listed on stdout.\n";

    private Cli() {
    }

    static int run(String[] args, PrintStream out, PrintStream err) {
        if (args.length == 0 || "--help".equals(args[0]) || "-h".equals(args[0]) || "help".equals(args[0])) {
            if (args.length > 2 || (args.length == 2 && !printHelp(args[1], out))) {
                err.println("Use " + INVOCATION + " --help to list commands.");
                return 2;
            }
            if (args.length < 2) {
                printHelp("", out);
            }
            return 0;
        }
        String command = args[0];
        if (("--version".equals(command) || "version".equals(command)) && args.length == 1) {
            out.println("123Fastq " + Statics.VERSION);
            return 0;
        }
        String[] options = Arrays.copyOfRange(args, 1, args.length);
        List<String> tokens = Arrays.asList(options);
        if (tokens.contains("--help") || tokens.contains("-h")) {
            if (printHelp(command, out)) {
                return 0;
            }
            err.println("Unknown command '" + command + "'. Use --help to list commands.");
            return 2;
        }

        try {
            switch (command) {
                case "qc":
                    qc(options, out, err);
                    break;
                case "trim":
                    TrimCommand.run(options, out, err);
                    break;
                case "split-barcodes":
                    BarcodeCommand.run(options, out, err);
                    break;
                case "sam-to-fastq":
                    ConvertCommand.sam(options, out, err);
                    break;
                case "fast5-to-fastq":
                    ConvertCommand.fast5(options, out, err);
                    break;
                case "single-cell":
                    singleCell(options, out, err);
                    break;
                default:
                    throw new Arguments.Usage("Unknown command '" + command + "'.");
            }
            return 0;
        } catch (Arguments.Usage | InvalidPathException ex) {
            err.println("Error: " + ex.getMessage());
            err.println("See " + INVOCATION + " --help or " + INVOCATION + " <command> --help.");
            return 2;
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            err.println("Cancelled.");
            return 130;
        } catch (Exception | LinkageError ex) {
            String message = ex.getMessage();
            err.println(command + " failed: " + (message == null ? ex.getClass().getSimpleName() : message));
            return 1;
        }
    }

    private static void qc(String[] options, PrintStream out, PrintStream err) throws Exception {
        Arguments args = Arguments.parse(options, new String[] {"input", "out-dir", "threads"},
                new String[] {"compare", "mapped-only"}, "input");
        List<Path> inputs = args.paths("input");
        boolean compare = args.flag("compare");
        boolean mappedOnly = args.flag("mapped-only");
        if (compare && inputs.size() != 2) {
            throw new Arguments.Usage("--compare requires exactly two --input files.");
        }
        int threads = args.integer("threads", 1, 1, 64);
        Path directory = args.path("out-dir");
        for (Path input : inputs) {
            inputFile(input);
            if (mappedOnly && !alignment(input)) {
                throw new Arguments.Usage("--mapped-only requires SAM or BAM inputs.");
            }
        }

        List<Path> reports = new ArrayList<>();
        List<Path> archives = new ArrayList<>();
        try (StagedOutputs outputs = new StagedOutputs()) {
            for (int i = 0; i < (compare ? 1 : inputs.size()); i++) {
                Path first = inputs.get(i);
                String stem = stem(first);
                if (compare) {
                    stem += "_vs_" + stem(inputs.get(1));
                }
                Path report = directory.resolve(stem + "_123Fastq.html");
                Path archive = directory.resolve(stem + "_123Fastq.zip");
                Path stagedReport = outputs.stage(report);
                outputs.stage(archive);
                try (SequenceFile firstFile = sequence(first, mappedOnly)) {
                    QcAnalysisResult result = analyze(firstFile, threads, args.flag("quiet"), err);
                    if (compare) {
                        try (SequenceFile secondFile = sequence(inputs.get(1), mappedOnly)) {
                            QcAnalysisResult other = analyze(secondFile, threads, args.flag("quiet"), err);
                            new HTMLReportArchive(firstFile, secondFile, result.getModules(), other.getModules(), stagedReport.toFile());
                        }
                    } else {
                        new HTMLReportArchive(firstFile, result.getModules(), stagedReport.toFile());
                    }
                }
                reports.add(report);
                archives.add(archive);
            }
            outputs.publish();
        }
        for (int i = 0; i < reports.size(); i++) {
            output(out, "report", reports.get(i));
            output(out, "archive", archives.get(i));
        }
    }

    private static QcAnalysisResult analyze(SequenceFile input, int threads, boolean quiet, PrintStream err)
            throws Exception {
        if (!input.hasNext()) {
            throw new IOException("No reads available for QC in " + input.getFile());
        }
        if (!quiet) {
            err.println("Checking " + input.getFile());
        }
        int[] lastProgress = {0};
        return new QcRunner().run(input, ModuleFactory.getStandardModuleList(input.name()), threads,
                quiet ? null : (count, percent) -> {
                    if (percent >= lastProgress[0] + 10) {
                        err.println(input.name() + ": " + percent + "% (" + count + " reads)");
                        lastProgress[0] = percent;
                    }
                });
    }

    private static SequenceFile sequence(Path input, boolean mappedOnly) throws Exception {
        return mappedOnly ? new BAMFile(input.toFile(), true) : SequenceFactory.getSequenceFile(input.toFile());
    }

    private static void singleCell(String[] options, PrintStream out, PrintStream err) throws Exception {
        Arguments args = Arguments.parse(options, new String[] {"r1", "r2", "fasta", "gtf", "whitelist",
            "out-dir", "star", "chemistry", "expected-cells", "threads", "index-ram"}, new String[] {"exon-only"}, "r1", "r2");
        List<Path> r1 = args.paths("r1");
        List<Path> r2 = args.paths("r2");
        if (r1.size() != r2.size()) {
            throw new Arguments.Usage("Provide the same number of --r1 and --r2 files in corresponding lane order.");
        }
        List<SingleCellRequest.ReadPair> pairs = new ArrayList<>();
        for (int i = 0; i < r1.size(); i++) {
            pairs.add(new SingleCellRequest.ReadPair(r1.get(i), r2.get(i)));
        }
        SingleCellRequest.Chemistry chemistry;
        String kit = args.value("chemistry", "v3");
        if (kit.equals("v2")) {
            chemistry = SingleCellRequest.Chemistry.THREE_PRIME_V2;
        } else if (kit.equals("v3") || kit.equals("v3.1")) {
            chemistry = SingleCellRequest.Chemistry.THREE_PRIME_V3;
        } else {
            throw new Arguments.Usage("--chemistry must be v2, v3, or v3.1.");
        }

        int expected = args.integer("expected-cells", 3000, 1, 1_000_000);
        int threads = args.integer("threads", Math.min(8, Runtime.getRuntime().availableProcessors()), 1, 256);
        int indexRam = args.integer("index-ram", 32, 1, 1024);
        SingleCellRequest request = new SingleCellRequest(pairs, args.path("fasta"), args.path("gtf"),
                args.path("whitelist"), StagedOutputs.directory(args.path("out-dir")), args.optionalPath("star"),
                chemistry, !args.flag("exon-only"), expected, threads, indexRam);
        SingleCellRunner.Result result = SingleCellRunner.run(request, args.flag("quiet") ? message -> {} : err::println);
        output(out, "run", result.directory);
        output(out, "filtered_matrix", result.filteredMatrix);
        output(out, "raw_matrix", result.rawMatrix);
        out.println("called_cells\t" + result.cells);
        out.println("genes\t" + result.genes);
        out.println("filtered_umis\t" + result.umis);
    }

    static Path inputFile(Path file) throws IOException {
        Path absolute = file.toAbsolutePath().normalize();
        if (!Files.isRegularFile(absolute) || !Files.isReadable(absolute)) {
            throw new IOException("Cannot read input file: " + absolute);
        }
        return absolute;
    }

    static FastqSerializer writer(Path path) throws IOException {
        FastqSerializer writer = new FastqSerializer();
        writer.open(path.toFile(), false);
        return writer;
    }

    static void output(PrintStream out, String name, Path path) {
        out.println(name + "\t" + path.toAbsolutePath().normalize());
    }

    static void checkInterrupted() throws InterruptedException {
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptedException();
        }
    }

    private static boolean alignment(Path path) {
        String name = path.getFileName().toString().toLowerCase(Locale.ROOT);
        return name.endsWith(".sam") || name.endsWith(".bam");
    }

    private static String stem(Path path) {
        String name = path.getFileName().toString().replaceFirst("(?i)\\.(gz|bz2)$", "")
                .replaceFirst("(?i)\\.(fastq|fq|sam|bam|fast5|txt)$", "");
        return name.isEmpty() ? "reads" : name;
    }

    private static boolean printHelp(String command, PrintStream out) {
        switch (command) {
            case "":
                out.println("Usage: " + INVOCATION + " <command> [options]\n"
                        + "\n  qc             Generate QC HTML and ZIP reports"
                        + "\n  trim           Trim single-end or paired-end FASTQ reads"
                        + "\n  split-barcodes Assign FASTQ reads to samples by end barcodes"
                        + "\n  sam-to-fastq   Convert SAM or BAM to FASTQ"
                        + "\n  fast5-to-fastq Extract embedded basecalls from single-read FAST5 files"
                        + "\n  single-cell    Generate raw and cell-filtered RNA-seq UMI matrices"
                        + "\n  gui            Open the desktop application"
                        + "\n\nUse <command> --help for examples and options, or --version for the version.");
                return true;
            case "gui":
                out.println("Usage: " + INVOCATION + " gui");
                return true;
            case "qc":
                out.print("Usage: " + INVOCATION + " qc --input FILE [--input FILE ...] --out-dir DIR\n"
                        + "Generate one HTML/ZIP report per file. Formats: FASTQ (plain, .gz or .bz2), SAM/BAM, single-read FAST5.\n"
                        + "\n  --compare      Generate a comparative report from exactly two inputs."
                        + "\n  --mapped-only  Analyze mapped SAM/BAM reads, excluding soft-clipped bases."
                        + "\n  --threads N    QC workers (1 by default).\n" + COMMON_HELP
                        + "\nExample: qc --input R1.fq.gz --input R2.fq.gz --compare --out-dir qc-reports\n");
                return true;
            case "trim":
                out.print(TrimCommand.HELP + COMMON_HELP);
                return true;
            case "split-barcodes":
                out.print(BarcodeCommand.HELP + COMMON_HELP);
                return true;
            case "sam-to-fastq":
                out.print(ConvertCommand.SAM_HELP + COMMON_HELP);
                return true;
            case "fast5-to-fastq":
                out.print(ConvertCommand.FAST5_HELP + COMMON_HELP);
                return true;
            case "single-cell":
                out.print("Usage: " + INVOCATION + " single-cell --r1 R1 --r2 R2 --fasta FASTA --gtf GTF\n"
                        + "                         --whitelist FILE --out-dir DIR [options]\n"
                        + "\nUse raw paired FASTQ files for one 10x Chromium 3′ gene-expression library."
                        + "\nRepeat --r1 and --r2 in corresponding lane order. Plain and gzip inputs are accepted.\n"
                        + "\n  --chemistry v2|v3|v3.1  10x chemistry (v3 by default)."
                        + "\n  --star FILE             STAR executable (defaults to STAR on PATH, requires 2.7.11 or later 2.x)."
                        + "\n  --exon-only             Count exonic reads only (default includes intronic reads)."
                        + "\n  --expected-cells N      Approximate recovered cells for cell calling (3000 by default)."
                        + "\n  --threads N             STAR threads (up to 8 by default, subject to available CPUs)."
                        + "\n  --index-ram GIB         STAR index memory limit (32 by default).\n"
                        + "\nAllow room for uncompressed reads and a temporary STAR index on the output volume.\n" + COMMON_HELP);
                return true;
            default:
                return false;
        }
    }
}
