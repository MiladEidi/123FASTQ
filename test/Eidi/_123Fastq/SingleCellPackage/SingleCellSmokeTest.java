package Eidi._123Fastq.SingleCellPackage;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.GZIPOutputStream;

public final class SingleCellSmokeTest {
    private static final String BARCODE1 = "AACCGGTTAACCGGTT";
    private static final String BARCODE2 = "CCTTAAGGCCTTAAGG";
    private static final String AMBIENT = "TTCCGGAATTCCGGAA";
    private static final String QUALITY = "IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIII";

    public static void main(String[] args) throws Exception {
        Path directory = Files.createTempDirectory("single-cell smoke ");
        try {
            Fixture fixture = new Fixture(directory);
            verifyInputChecks(fixture);
            verifyMatrixChecks(directory);
            if (args.length > 0) {
                // Pass --bundled to test the default STAR packaged with 123Fastq.
                verifyStar(fixture, "--bundled".equals(args[0]) ? null : Paths.get(args[0]));
                verifyCancellation(fixture);
            }
            System.out.println("Single-cell smoke tests passed.");
        } finally {
            try (Stream<Path> paths = Files.walk(directory)) {
                for (Path path : paths.sorted(java.util.Comparator.reverseOrder()).collect(Collectors.toList())) {
                    Files.delete(path);
                }
            }
        }
    }

    private static void verifyInputChecks(Fixture fixture) throws Exception {
        Path scratch = Files.createDirectory(fixture.directory.resolve("input check"));
        SingleCellInputs.Reference info = SingleCellInputs.stage(fixture.request(null, false), scratch, ignored -> {});
        require(info.bases == 12_000 && info.contigs == 1 && info.longestRead == 75, "Reference and read metadata");
        try (BufferedReader input = SingleCellInputs.reader(scratch.resolve("barcode.fastq"))) {
            require(input.readLine().equals("@first-0"), "Normalize R1 and R2 identifiers");
            require(input.readLine().length() == 28, "Remove trailing barcode cycles");
        }

        Path scratchForMismatch = Files.createDirectory(fixture.directory.resolve("mismatch check"));
        Path badGtf = write(fixture.directory.resolve("bad.gtf"), fixture.annotations.replace("chrSynthetic", "chrWrong"));
        SingleCellRequest wrongAssembly = new SingleCellRequest(fixture.pairs, fixture.fasta, badGtf,
                fixture.whitelist, fixture.directory, null, SingleCellRequest.Chemistry.THREE_PRIME_V3,
                false, 2, 2, 1);
        expectIo(() -> SingleCellInputs.stage(wrongAssembly, scratchForMismatch, ignored -> {}), "absent from FASTA");

        Path scratchForReads = Files.createDirectory(fixture.directory.resolve("read check"));
        Path mismatchedR2 = write(fixture.directory.resolve("mismatched R2.fq"),
                fixture.record("different", fixture.genome.substring(1000, 1075), 2));
        SingleCellRequest wrongPair = new SingleCellRequest(
                Arrays.asList(new SingleCellRequest.ReadPair(fixture.pairs.get(0).barcodeRead, mismatchedR2)),
                fixture.fasta, fixture.gtf, fixture.whitelist, fixture.directory, null,
                SingleCellRequest.Chemistry.THREE_PRIME_V3, false, 2, 2, 1);
        expectIo(() -> SingleCellInputs.stage(wrongPair, scratchForReads, ignored -> {}), "identifiers differ");

        Path scratchForTruncation = Files.createDirectory(fixture.directory.resolve("truncated read check"));
        String completeRead = fixture.record("first-0", fixture.genome.substring(1000, 1075), 2);
        Path truncatedR2 = write(fixture.directory.resolve("truncated R2.fq.gz"), completeRead + "@first-1/2\nACGT\n+");
        SingleCellRequest truncated = new SingleCellRequest(
                Arrays.asList(new SingleCellRequest.ReadPair(fixture.pairs.get(0).barcodeRead, truncatedR2)),
                fixture.fasta, fixture.gtf, fixture.whitelist, fixture.directory, null,
                SingleCellRequest.Chemistry.THREE_PRIME_V3, false, 2, 2, 1);
        expectIo(() -> SingleCellInputs.stage(truncated, scratchForTruncation, ignored -> {}), "record 2 in R2");
    }

    private static void verifyMatrixChecks(Path directory) throws Exception {
        Path source = Files.createDirectory(directory.resolve("matrix source"));
        write(source.resolve("features.tsv"), "gene1\tGene 1\n");
        write(source.resolve("barcodes.tsv"), "");
        write(source.resolve("matrix.mtx"), "%%MatrixMarket matrix coordinate integer general\n%\n1 0 0\n");
        SingleCellMatrix.Dimensions empty = SingleCellMatrix.export(source, directory.resolve("zero cell matrix"));
        require(empty.genes == 1 && empty.barcodes == 0 && empty.umis == 0, "Valid zero-cell matrix");
        write(source.resolve("barcodes.tsv"), BARCODE1 + "\n");
        write(source.resolve("matrix.mtx"), "%%MatrixMarket matrix coordinate integer general\n1 1 2\n1 1 3\n");
        expectIo(() -> SingleCellMatrix.export(source, directory.resolve("incomplete matrix")), "differs from its header");
        write(source.resolve("barcodes.tsv"), BARCODE1 + "\n" + BARCODE2 + "\n");
        write(source.resolve("matrix.mtx"), "%%MatrixMarket matrix coordinate integer general\n1 2 2\n1 1 3\n1 2 0 \n");
        Path withZero = directory.resolve("matrix with explicit zero");
        SingleCellMatrix.Dimensions sparse = SingleCellMatrix.export(source, withZero);
        require(sparse.entries == 1 && sparse.umis == 3, "Explicit zero counts are excluded");
        try (BufferedReader matrix = SingleCellInputs.reader(withZero.resolve("matrix.mtx.gz"))) {
            List<String> rows = matrix.lines().collect(Collectors.toList());
            require(rows.size() == 4 && rows.contains("1 2 1"), "Update dimensions after removing zero entries");
        }
    }

    private static void verifyStar(Fixture fixture, Path executable) throws Exception {
        List<String> messages = new ArrayList<>();
        SingleCellRunner.Result result;
        try {
            result = SingleCellRunner.run(fixture.request(executable, true), messages::add);
        } catch (Exception ex) {
            messages.forEach(System.err::println);
            throw ex;
        }
        require(executable != null || messages.stream().anyMatch(m -> m.startsWith("Using the STAR " + BundledStar.VERSION)),
                "Use the bundled STAR when none is selected");
        require(result.genes == 2, "Expected two genes");
        require(result.cells == 2, "Expected two called cells, got " + result.cells + "\n" + messages);
        require(result.umis == 52 && result.rawUmis == 53,
                "Expected exon and intron UMIs, deduplication across pairs, and ambient barcode filtering. Got "
                        + result.umis + " and " + result.rawUmis);
        require(!Files.exists(result.directory.resolve("work")), "Clean up staged reads and index");
        require(Files.isRegularFile(result.directory.resolve("run-settings.txt")), "Run settings");
        require(Files.isRegularFile(result.directory.resolve("Summary.csv")), "STARsolo report");
        try (BufferedReader matrix = SingleCellInputs.reader(result.filteredMatrix.resolve("matrix.mtx.gz"))) {
            List<String> rows = matrix.lines().collect(Collectors.toList());
            require(rows.contains("2 2 2") && rows.contains("1 1 31") && rows.contains("2 2 21"),
                    "Expected coordinates and counts in Matrix Market, got " + rows);
        }
        SingleCellRunner.Result exonic = SingleCellRunner.run(fixture.request(executable, false), ignored -> {});
        require(exonic.umis == 51 && exonic.rawUmis == 52, "Exon-only mode excludes the intronic UMI");

        SingleCellRequest v2 = new SingleCellRequest(fixture.pairs, fixture.fasta, fixture.gtf, fixture.whitelist,
                fixture.directory, executable, SingleCellRequest.Chemistry.THREE_PRIME_V2, false, 2, 2, 1);
        require(SingleCellRunner.run(v2, ignored -> {}).umis == 51, "v2 extracts 10-base UMIs from longer R1 reads");

        Path r1 = write(fixture.directory.resolve("unmapped R1.fq"), fixture.record("unmapped", BARCODE1 + "ACGTACGTACGT", 1));
        Path r2 = write(fixture.directory.resolve("unmapped R2.fq"), fixture.record("unmapped", QUALITY.replace('I', 'N'), 2));
        SingleCellRequest unmapped = new SingleCellRequest(Arrays.asList(new SingleCellRequest.ReadPair(r1, r2)),
                fixture.fasta, fixture.gtf, fixture.whitelist, fixture.directory, executable,
                SingleCellRequest.Chemistry.THREE_PRIME_V3, true, 2, 2, 1);
        SingleCellRunner.Result empty = SingleCellRunner.run(unmapped, ignored -> {});
        require(empty.umis == 0 && empty.rawUmis == 0 && empty.cells == 0, "Zero alignments yield empty matrices");
    }

    private static void verifyCancellation(Fixture fixture) throws Exception {
        Path output = Files.createDirectory(fixture.directory.resolve("cancel test"));
        Path wrapper = write(output.resolve("STAR wrapper"), "#!/bin/sh\n"
                + "if [ \"$1\" = --version ]; then echo 2.7.11b; exit; fi\n"
                + "sleep 30 &\n"
                + "printf '%s\\n' \"$!\" > \"$0.child\"\n"
                + "wait\n");
        require(wrapper.toFile().setExecutable(true), "Make the test STAR wrapper executable");
        Path childPid = output.resolve("STAR wrapper.child");
        SingleCellRequest request = new SingleCellRequest(fixture.pairs, fixture.fasta, fixture.gtf, fixture.whitelist,
                output, wrapper, SingleCellRequest.Chemistry.THREE_PRIME_V3, true, 2, 2, 1);
        AtomicReference<Throwable> outcome = new AtomicReference<>();
        Thread worker = new Thread(() -> {
            try {
                SingleCellRunner.run(request, ignored -> {});
            } catch (Exception ex) {
                outcome.set(ex);
            }
        });
        worker.start();
        long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(10);
        try {
            while (worker.isAlive() && (!Files.exists(childPid) || Files.size(childPid) == 0)
                    && System.nanoTime() < deadline) {
                Thread.sleep(25);
            }
            require(Files.isRegularFile(childPid), "STAR wrapper launched its index subprocess: " + outcome.get());
        } finally {
            worker.interrupt();
            worker.join(5000);
        }
        require(!worker.isAlive() && outcome.get() instanceof InterruptedException, "Analysis cancelled promptly");
        String pid = new String(Files.readAllBytes(childPid), StandardCharsets.US_ASCII).trim();
        Process child = new ProcessBuilder("ps", "-p", pid, "-o", "stat=").start();
        try (BufferedReader outputReader = new BufferedReader(new java.io.InputStreamReader(child.getInputStream()))) {
            String status = outputReader.readLine();
            require(status == null || status.trim().startsWith("Z"), "Cancellation stops the wrapped STAR subprocess");
        }
        child.waitFor();
        try (Stream<Path> children = Files.list(output)) {
            Path run = children.filter(Files::isDirectory).findFirst().orElseThrow(AssertionError::new);
            require(!Files.exists(run.resolve("work")) && !Files.exists(run.resolve("counts")), "Cancel cleans up partial output");
        }
    }

    private static void expectIo(CheckedOperation operation, String contains) throws Exception {
        try {
            operation.run();
        } catch (IOException ex) {
            require(ex.getMessage().contains(contains), "Expected '" + contains + "' in " + ex.getMessage());
            return;
        }
        throw new AssertionError("Expected an IOException containing '" + contains + "'");
    }

    private interface CheckedOperation {
        void run() throws Exception;
    }

    private static void require(boolean pass, String message) {
        if (!pass) {
            throw new AssertionError(message);
        }
    }

    private static Path write(Path path, String text) throws IOException {
        byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
        if (path.getFileName().toString().endsWith(".gz")) {
            try (GZIPOutputStream output = new GZIPOutputStream(Files.newOutputStream(path))) {
                output.write(bytes);
            }
            return path;
        }
        return Files.write(path, bytes);
    }

    private static final class Fixture {
        final Path directory;
        final String genome;
        final String annotations =
                exon(901, 1500, "gene1") + exon(1801, 2200, "gene1") + exon(4001, 5200, "gene2");
        final Path fasta;
        final Path gtf;
        final Path whitelist;
        final List<SingleCellRequest.ReadPair> pairs;

        Fixture(Path directory) throws IOException {
            this.directory = directory;
            Random random = new Random(21351);
            StringBuilder sequence = new StringBuilder();
            for (int i = 0; i < 12_000; i++) {
                sequence.append("ACGT".charAt(random.nextInt(4)));
            }
            genome = sequence.toString();
            fasta = write(directory.resolve("reference with spaces.fa.gz"), ">chrSynthetic description\n"
                    + genome.replaceAll("(.{60})", "$1\n"));
            gtf = write(directory.resolve("genes.gtf"), annotations);
            whitelist = write(directory.resolve("whitelist.txt.gz"), BARCODE1 + "\n" + BARCODE2 + "\n" + AMBIENT + "\n");

            StringBuilder r1 = new StringBuilder();
            StringBuilder r2 = new StringBuilder();
            List<String> umis = new ArrayList<>();
            for (int i = 0; i < 32; i++) {
                StringBuilder umi = new StringBuilder();
                for (int j = 0; j < 12; j++) {
                    umi.append("ACGT".charAt(random.nextInt(4)));
                }
                umis.add(umi.toString());
            }
            for (int i = 0; i < 30; i++) {
                add(r1, r2, "first-" + i, BARCODE1, umis.get(i), 1000 + i);
            }
            for (int i = 0; i < 21; i++) {
                add(r1, r2, "second-" + i, BARCODE2, umis.get(i), 4500 + i);
            }
            add(r1, r2, "ambient", AMBIENT, umis.get(31), 1100);
            add(r1, r2, "intronic", BARCODE1, umis.get(30), 1600);
            Path firstR1 = write(directory.resolve("lane 1 R1.fq.gz"), r1.toString());
            Path firstR2 = write(directory.resolve("lane 1 R2.fq"), r2.toString());

            r1.setLength(0);
            r2.setLength(0);
            add(r1, r2, "other-lane-duplicate", BARCODE1, umis.get(0), 1000);
            Path secondR1 = write(directory.resolve("lane 2 R1.fq"), r1.toString());
            Path secondR2 = write(directory.resolve("lane 2 R2.fq.gz"), r2.toString());
            pairs = Arrays.asList(new SingleCellRequest.ReadPair(firstR1, firstR2),
                    new SingleCellRequest.ReadPair(secondR1, secondR2));
        }

        SingleCellRequest request(Path executable, boolean introns) {
            return new SingleCellRequest(pairs, fasta, gtf, whitelist, directory, executable,
                    SingleCellRequest.Chemistry.THREE_PRIME_V3, introns, 2, 2, 1);
        }

        private void add(StringBuilder r1, StringBuilder r2, String id, String barcode, String umi, int location) {
            r1.append(record(id, barcode + umi + "AA", 1));
            r2.append(record(id, genome.substring(location, location + 75), 2));
        }

        String record(String id, String sequence, int mate) {
            return "@" + id + "/" + mate + "\n" + sequence + "\n+\n" + QUALITY.substring(0, sequence.length()) + "\n";
        }

        private static String exon(int start, int end, String gene) {
            return "chrSynthetic\tsmoke\texon\t" + start + "\t" + end + "\t.\t+\t.\tgene_id \""
                    + gene + "\"; transcript_id \"" + gene + "_t\"; gene_name \"" + gene + "\";\n";
        }
    }
}
