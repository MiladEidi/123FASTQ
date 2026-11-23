package Eidi._123Fastq.Cli;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipFile;

public final class CliSmokeTest {
    public static void main(String[] args) throws Exception {
        Path originalJar = Paths.get(args.length == 0 ? "dist/123Fastq.jar" : args[0]).toAbsolutePath();
        Path directory = Files.createTempDirectory("123fastq-cli smoke ");
        try {
            Files.copy(originalJar, directory.resolve("test.jar"));
            Launcher run = new Launcher(directory);
            require(run.invoke(0, "--help").contains("single-cell"), "Top-level help");
            require(run.invoke(0, "trim", "--help").contains("--orphans-r1"), "Command help");
            run.invoke(2, "trim", "--unknown");
            run.invoke(2, "single-cell", "--r1", "one.fq", "--r1", "two.fq", "--r2", "a.fq");

            StringBuilder r1 = new StringBuilder(read("first/1", "ACGTGGGG") + read("second/1", "ACGAGGGG"));
            StringBuilder r2 = new StringBuilder(read("first/2", "ACGTGGGG") + read("second/2", "ACGT"));
            for (int i = 0; i < 800; i++) {
                r1.append(read("more-" + i + "/1", "ACGTGGGG"));
                r2.append(read("more-" + i + "/2", "ACGTGGGG"));
            }
            write(directory.resolve("one.fq"), r1.toString());
            write(directory.resolve("two.fq"), r2.toString());
            String trimmed = run.invoke(0, "trim", "--r1", "one.fq", "--r2", "two.fq", "--out-r1", "kept 1.fq.gz",
                    "--out-r2", "kept 2.fq.gz", "--orphans-r1", "orphans.fq", "--step", "head-crop:1",
                    "--step", "min-length:6", "--threads", "2", "--quiet");
            require(trimmed.startsWith("paired_r1\t") && trimmed.contains("orphans_r1\t"), "Stable output keys");
            require(gunzip(directory.resolve("kept 1.fq.gz")).contains("\nCGTGGGG\n"), "Ordered trim steps and gzip output");
            require(gunzip(directory.resolve("kept 1.fq.gz")).endsWith(read("more-799/1", "CGTGGGG")), "Parallel output order");
            require(readText(directory.resolve("orphans.fq")).startsWith("@second/1"), "Paired-end orphan");
            run.invoke(1, "trim", "--input", "one.fq", "--output", "orphans.fq", "--step", "min-length:1");
            require(readText(directory.resolve("orphans.fq")).startsWith("@second/1"), "Existing output is protected");

            write(directory.resolve("bad.fq"), read("good", "ACGT") + "@bad\nACGT\n+\n");
            run.invoke(1, "trim", "--input", "bad.fq", "--output", "partial.fq", "--step", "min-length:1");
            require(!Files.exists(directory.resolve("partial.fq")), "Malformed read leaves no partial file");

            write(directory.resolve("sample sheet.tsv"), "sampleA\tACGT\nsampleB\tACGA\n");
            write(directory.resolve("indexes.fq"), read("a", "ACGTGGGG") + read("b", "ACGAGGGG") + read("ambiguous", "ACGCGGGG"));
            run.invoke(0, "split-barcodes", "--input", "indexes.fq", "--barcodes", "sample sheet.tsv",
                    "--out-dir", "barcodes", "--max-mismatches", "1", "--clip", "--gzip");
            require(gunzip(directory.resolve("barcodes/sampleA.fastq.gz")).equals(read("a", "GGGG")), "Barcode clipping");
            require(readText(directory.resolve("barcodes/barcode-summary.tsv")).contains("ambiguous\t1"), "Barcode tie accounting");

            write(directory.resolve("test.sam"), "@HD\tVN:1.6\tSO:unsorted\n@SQ\tSN:chr1\tLN:100\n"
                    + "rev\t16\tchr1\t1\t60\t5M\t*\t0\t0\tAACCT\t!#%+I\n");
            run.invoke(0, "sam-to-fastq", "--input", "test.sam", "--output", "converted.fq");
            require(readText(directory.resolve("converted.fq")).equals("@rev\nAGGTT\n+\nI+%#!\n"),
                    "SAM reverse orientation and quality");

            run.invoke(0, "qc", "--input", "one.fq", "--input", "two.fq", "--compare", "--out-dir", "qc reports",
                    "--threads", "2", "--quiet");
            Path report = directory.resolve("qc reports/one_vs_two_123Fastq.html");
            require(readText(report).contains("Basic Statistics"), "Headless HTML report");
            try (ZipFile archive = new ZipFile(directory.resolve("qc reports/one_vs_two_123Fastq.zip").toFile())) {
                require(archive.getEntry("one_vs_two_123Fastq/123Fastq_data.txt") != null, "Partner QC archive");
            }
            try (Stream<Path> paths = Files.walk(directory)) {
                require(paths.noneMatch(path -> path.getFileName().toString().startsWith(".123fastq-")), "Scratch cleanup");
            }
            System.out.println("Portable JAR CLI smoke tests passed.");
        } finally {
            try (Stream<Path> paths = Files.walk(directory)) {
                for (Path path : paths.sorted(Comparator.reverseOrder()).collect(Collectors.toList())) {
                    Files.delete(path);
                }
            }
        }
    }

    private static String read(String name, String sequence) {
        return "@" + name + "\n" + sequence + "\n+\n" + sequence.replaceAll(".", "I") + "\n";
    }

    private static void write(Path path, String text) throws IOException {
        Files.write(path, text.getBytes(StandardCharsets.UTF_8));
    }

    private static String readText(Path path) throws IOException {
        return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
    }

    private static String gunzip(Path path) throws IOException {
        try (GZIPInputStream stream = new GZIPInputStream(Files.newInputStream(path));
                java.io.ByteArrayOutputStream result = new java.io.ByteArrayOutputStream()) {
            byte[] buffer = new byte[4096];
            int count;
            while ((count = stream.read(buffer)) != -1) {
                result.write(buffer, 0, count);
            }
            return new String(result.toByteArray(), StandardCharsets.UTF_8);
        }
    }

    private static void require(boolean pass, String message) {
        if (!pass) {
            throw new AssertionError(message);
        }
    }

    private static final class Launcher {
        private final Path work;

        Launcher(Path work) {
            this.work = work;
        }

        String invoke(int expected, String... arguments) throws Exception {
            String java = Paths.get(System.getProperty("java.home"), "bin", "java").toString();
            List<String> command = new ArrayList<>(Arrays.asList(java, "-Djava.awt.headless=true", "-jar", "test.jar"));
            command.addAll(Arrays.asList(arguments));
            Path out = work.resolve("stdout.txt");
            Path err = work.resolve("stderr.txt");
            Process process = new ProcessBuilder(command).directory(work.toFile())
                    .redirectOutput(out.toFile()).redirectError(err.toFile()).start();
            try {
                require(process.waitFor(30, TimeUnit.SECONDS), "CLI timed out: " + arguments[0]);
                require(process.exitValue() == expected, "Exit " + process.exitValue() + " for "
                        + Arrays.toString(arguments) + "\n" + readText(err));
                return readText(out);
            } finally {
                process.destroyForcibly();
            }
        }
    }
}
