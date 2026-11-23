package Eidi._123Fastq.Cli;

import Eidi._123Fastq.TrimFactoryPackage.FastqRecord;
import Eidi._123Fastq.TrimFactoryPackage.FastqSerializer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

final class BarcodeCommand {
    static final String HELP = "Usage: java -jar 123Fastq.jar split-barcodes --input FILE --barcodes TSV --out-dir DIR [options]\n"
            + "\nThe tab-separated sample sheet has one sample name and one equal-length A/C/G/T barcode per row."
            + "\nBlank lines and lines beginning with # are skipped. Barcode matching is anchored to the selected end."
            + "\n  --end 5|3            Match the 5′ end (default) or 3′ end."
            + "\n  --max-mismatches N   Permit up to N substitutions (0 by default)."
            + "\n  --clip              Remove the matched barcode and its quality scores."
            + "\n  --gzip              Write compressed output FASTQs."
            + "\nUnmatched, ambiguous and barcode-only reads with --clip go to Undetermined.fastq."
            + "\nbarcode-summary.tsv records counts by sample and reason.\n";
    private static final String UNDETERMINED = "Undetermined";

    private BarcodeCommand() {
    }

    static void run(String[] options, PrintStream out, PrintStream err) throws Exception {
        Arguments args = Arguments.parse(options, new String[] {"input", "barcodes", "out-dir", "end", "max-mismatches"},
                new String[] {"clip", "gzip"});
        Path input = Cli.inputFile(args.path("input"));
        Path sampleSheet = Cli.inputFile(args.path("barcodes"));
        List<Barcode> barcodes = load(sampleSheet);
        int width = barcodes.get(0).sequence.length();
        int mismatches = args.integer("max-mismatches", 0, 0, width - 1);
        String end = args.value("end", "5");
        if (!end.equals("5") && !end.equals("3")) {
            throw new Arguments.Usage("--end must be 5 or 3.");
        }
        Path directory = args.path("out-dir");
        Path report = directory.resolve("barcode-summary.tsv");
        Counts counts;
        List<Path> fastqFiles;
        try (StagedOutputs outputs = new StagedOutputs()) {
            Path summary = outputs.stage(report);
            try (FastqInput reads = new FastqInput(input, 33);
                    BoundedWriters writers = new BoundedWriters(outputs, directory, args.flag("gzip"))) {
                counts = distribute(reads, writers, barcodes, width, mismatches, end.equals("5"), args.flag("clip"));
                fastqFiles = new ArrayList<>(writers.staged.keySet());
            }
            Files.write(summary, counts.report(barcodes).getBytes(StandardCharsets.UTF_8));
            outputs.publish();
        }
        if (!args.flag("quiet")) {
            err.println("Processed " + counts.total + " reads. Assigned " + counts.assigned + " to samples.");
        }
        for (Path fastq : fastqFiles) {
            Cli.output(out, "fastq", fastq);
        }
        Cli.output(out, "report", report);
    }

    private static Counts distribute(FastqInput reads, BoundedWriters writers, List<Barcode> barcodes,
            int width, int maxMismatches, boolean fivePrime, boolean clip) throws IOException, InterruptedException {
        Counts counts = new Counts();
        FastqRecord read;
        while ((read = reads.next()) != null) {
            counts.total++;
            String sequence = read.getSequence();
            int start = fivePrime ? 0 : sequence.length() - width;
            Barcode best = null;
            int lowestDistance = maxMismatches + 1;
            boolean tie = false;
            if (sequence.length() >= width) {
                for (Barcode barcode : barcodes) {
                    int distance = distance(sequence, start, barcode.sequence, lowestDistance);
                    if (distance < lowestDistance) {
                        best = barcode;
                        lowestDistance = distance;
                        tie = false;
                    } else if (distance == lowestDistance && best != null) {
                        tie = true;
                    }
                }
            }
            if (best == null) {
                writers.write(UNDETERMINED, read);
                counts.unmatched++;
            } else if (tie) {
                writers.write(UNDETERMINED, read);
                counts.ambiguous++;
            } else if (clip && sequence.length() == width) {
                writers.write(UNDETERMINED, read);
                counts.barcodeOnly++;
            } else {
                if (clip) {
                    read = new FastqRecord(read, fivePrime ? width : 0, sequence.length() - width, (javax.swing.JTextArea) null);
                }
                writers.write(best.sample, read);
                counts.assigned++;
                best.hits++;
            }
        }
        return counts;
    }

    private static int distance(String read, int start, String barcode, int cutoff) {
        int mismatches = 0;
        for (int i = 0; i < barcode.length() && mismatches <= cutoff; i++) {
            if (read.charAt(start + i) != barcode.charAt(i)) {
                mismatches++;
            }
        }
        return mismatches;
    }

    private static List<Barcode> load(Path path) throws IOException, InterruptedException {
        List<Barcode> barcodes = new ArrayList<>();
        Set<String> samples = new HashSet<>();
        Set<String> sequences = new HashSet<>();
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String row;
            int line = 0;
            while ((row = reader.readLine()) != null) {
                Cli.checkInterrupted();
                line++;
                if (row.trim().isEmpty() || row.trim().startsWith("#")) {
                    continue;
                }
                String[] fields = row.split("\t", -1);
                if (fields.length != 2) {
                    throw new IOException("Expected a sample name and barcode separated by a tab at line " + line + ".");
                }
                String sample = fields[0].trim();
                String sequence = fields[1].trim().toUpperCase(Locale.ROOT);
                if (!sample.matches("[A-Za-z0-9][A-Za-z0-9_.-]{0,127}") || UNDETERMINED.equalsIgnoreCase(sample)) {
                    throw new IOException("Invalid or reserved sample name at barcode sheet line " + line + ".");
                }
                if (!sequence.matches("[ACGT]+")
                        || (!barcodes.isEmpty() && sequence.length() != barcodes.get(0).sequence.length())) {
                    throw new IOException("Expected equal-length A/C/G/T barcodes at sheet line " + line + ".");
                }
                if (!samples.add(sample.toLowerCase(Locale.ROOT)) || !sequences.add(sequence)) {
                    throw new IOException("Duplicate sample or barcode at sheet line " + line + ".");
                }
                barcodes.add(new Barcode(sample, sequence));
            }
        }
        if (barcodes.isEmpty()) {
            throw new IOException("No barcodes found in " + path);
        }
        return barcodes;
    }

    private static final class Barcode {
        final String sample;
        final String sequence;
        long hits;

        Barcode(String sample, String sequence) {
            this.sample = sample;
            this.sequence = sequence;
        }
    }

    private static final class Counts {
        long total;
        long assigned;
        long unmatched;
        long ambiguous;
        long barcodeOnly;

        String report(List<Barcode> barcodes) {
            StringBuilder report = new StringBuilder("sample\tbarcode\tclassification\treads\n");
            for (Barcode barcode : barcodes) {
                report.append(barcode.sample).append('\t').append(barcode.sequence)
                        .append("\tassigned\t").append(barcode.hits).append('\n');
            }
            report.append(UNDETERMINED).append("\t\tunmatched\t").append(unmatched).append('\n');
            report.append(UNDETERMINED).append("\t\tambiguous\t").append(ambiguous).append('\n');
            report.append(UNDETERMINED).append("\t\tbarcode_only\t").append(barcodeOnly).append('\n');
            return report.toString();
        }
    }

    private static final class BoundedWriters implements AutoCloseable {
        private static final int MAX_OPEN = 32;
        private final Map<Path, FastqSerializer> active = new LinkedHashMap<>(MAX_OPEN, 0.75f, true);
        private final Map<Path, Path> staged = new LinkedHashMap<>();
        private final StagedOutputs outputs;
        private final Path directory;
        private final String suffix;

        BoundedWriters(StagedOutputs outputs, Path directory, boolean gzip) {
            this.outputs = outputs;
            this.directory = directory;
            this.suffix = gzip ? ".fastq.gz" : ".fastq";
        }

        void write(String sample, FastqRecord record) throws IOException {
            Path target = directory.resolve(sample + suffix);
            FastqSerializer writer = active.get(target);
            if (writer == null) {
                if (active.size() == MAX_OPEN) {
                    Iterator<FastqSerializer> oldest = active.values().iterator();
                    oldest.next().close();
                    oldest.remove();
                }
                Path path = staged.get(target);
                boolean append = path != null;
                if (!append) {
                    path = outputs.stage(target);
                    staged.put(target, path);
                }
                writer = new FastqSerializer();
                writer.open(path.toFile(), append);
                active.put(target, writer);
            }
            writer.writeRecord(record);
        }

        @Override
        public void close() throws IOException {
            IOException failure = null;
            for (FastqSerializer writer : active.values()) {
                try {
                    writer.close();
                } catch (IOException ex) {
                    if (failure == null) {
                        failure = ex;
                    } else {
                        failure.addSuppressed(ex);
                    }
                }
            }
            if (failure != null) {
                throw failure;
            }
        }
    }
}
