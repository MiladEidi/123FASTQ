package Eidi._123Fastq.SingleCellPackage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Runs one STARsolo analysis. Call on a background thread and interrupt it to cancel. */
public final class SingleCellRunner {
    static final int MIN_ALIGNMENT_SCORE = 30;
    private static final Pattern STAR_VERSION = Pattern.compile("^(?:STAR[_ -])?(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})[a-zA-Z0-9._+-]*$");
    private static final String RAW = "raw_feature_bc_matrix";
    private static final String FILTERED = "filtered_feature_bc_matrix";
    private static final String PID_FILE = "star.pid";

    public static final class Result {
        public final Path directory;
        public final Path rawMatrix;
        public final Path filteredMatrix;
        public final long genes;
        public final long cells;
        public final long umis;
        public final long rawUmis;

        private Result(Path directory, SingleCellMatrix.Dimensions raw, SingleCellMatrix.Dimensions filtered) {
            this.directory = directory;
            this.rawMatrix = directory.resolve("counts").resolve(RAW);
            this.filteredMatrix = directory.resolve("counts").resolve(FILTERED);
            this.genes = filtered.genes;
            this.cells = filtered.barcodes;
            this.umis = filtered.umis;
            this.rawUmis = raw.umis;
        }
    }

    static final class RunFailure extends IOException {
        final Path directory;

        RunFailure(Path directory, IOException cause) {
            super(cause.getMessage() + System.lineSeparator() + "Run folder: " + directory, cause);
            this.directory = directory;
        }
    }

    private SingleCellRunner() {
    }

    public static Result run(SingleCellRequest request, Consumer<String> progress)
            throws IOException, InterruptedException {
        checkInterrupted();
        Path output = request.outputParent.toAbsolutePath().normalize();
        if (!Files.isDirectory(output) || !Files.isWritable(output)) {
            throw new IOException("Choose an existing writable output directory: " + output);
        }
        Path job = Files.createTempDirectory(output, "single-cell-");
        Path scratch = job.resolve("work");
        Path log = job.resolve("run.log");
        try {
            Files.createDirectory(scratch);
            report(log, progress, "Run folder: " + job);
            String star = resolveStar(request, log, progress);
            String version = checkStar(scratch, star);
            report(log, progress, "Found STAR " + version);
            writeSettings(job.resolve("run-settings.txt"), request, version);
            report(log, progress, "Validating and staging inputs before starting STAR.");
            SingleCellInputs.Reference reference = SingleCellInputs.stage(request, scratch,
                    message -> report(log, progress, message));

            Files.createDirectory(scratch.resolve("index"));
            Files.createDirectory(scratch.resolve("star"));
            report(log, progress, "Building the STAR index from the FASTA and GTF.");
            buildIndex(star, request, reference, scratch, log);
            report(log, progress, "Aligning reads, correcting barcodes, counting UMIs, and calling cells.");
            quantify(star, request, scratch, log);

            report(log, progress, "Checking and compressing the raw and filtered count matrices.");
            Path pending = Files.createDirectory(scratch.resolve("counts"));
            Path starOutput = scratch.resolve("star/Solo.out").resolve(request.feature());
            Path rawMatrix = pending.resolve(RAW);
            SingleCellMatrix.Dimensions raw = SingleCellMatrix.export(starOutput.resolve("raw"), rawMatrix);
            Path starFiltered = starOutput.resolve("filtered");
            // STARsolo omits the filtered files when it has no cells to call.
            SingleCellMatrix.Dimensions filtered = raw.umis == 0 && Files.notExists(starFiltered)
                    ? SingleCellMatrix.emptyFiltered(rawMatrix, pending.resolve(FILTERED), raw.genes)
                    : SingleCellMatrix.export(starFiltered, pending.resolve(FILTERED));
            if (raw.genes != filtered.genes || filtered.barcodes > raw.barcodes
                    || filtered.entries > raw.entries || filtered.umis > raw.umis) {
                throw new IOException("The filtered matrix is inconsistent with the raw matrix.");
            }
            copyReports(scratch.resolve("star"), starOutput, job);
            writeSummary(pending.resolve("matrix-summary.tsv"), raw, filtered);

            checkInterrupted();
            Path counts = job.resolve("counts");
            try {
                Files.move(pending, counts, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException ex) {
                Files.move(pending, counts);
            }
            if (raw.umis == 0) {
                report(log, progress, "Warning: STAR counted zero UMIs. Review the barcode and alignment statistics.");
            } else if (filtered.barcodes == 0) {
                report(log, progress, "Warning: no barcodes passed cell calling. The raw matrix is available.");
            }
            report(log, progress, String.format(Locale.ROOT, "Finished. %,d genes, %,d called cells, %,d filtered UMIs.",
                    filtered.genes, filtered.barcodes, filtered.umis));
            return new Result(job, raw, filtered);
        } catch (IOException ex) {
            report(log, progress, "Failed: " + ex.getMessage());
            throw new RunFailure(job, ex);
        } catch (InterruptedException ex) {
            report(log, progress, "Analysis cancelled.");
            throw ex;
        } finally {
            try {
                discardScratch(scratch);
            } catch (IOException ex) {
                report(log, progress, "Could not remove temporary files in " + scratch + ": " + ex.getMessage());
            }
        }
    }

    private static String resolveStar(SingleCellRequest request, Path log, Consumer<String> progress)
            throws IOException {
        if (request.starExecutable != null) {
            return request.starExecutable.toAbsolutePath().toString();
        }
        String unavailable = BundledStar.unavailableReason();
        if (unavailable == null) {
            Path bundled = BundledStar.executable();
            report(log, progress, "Using the STAR " + BundledStar.VERSION + " bundled with 123Fastq"
                    + (BundledStar.needsRosetta() ? " through Rosetta 2" : "") + ": " + bundled);
            return bundled.toString();
        }
        report(log, progress, unavailable + " Using STAR on PATH.");
        return "STAR";
    }

    private static String checkStar(Path scratch, String star) throws IOException, InterruptedException {
        Path output = scratch.resolve("version.txt");
        try {
            execute(scratch, output, 15, star, "--version");
        } catch (IOException ex) {
            throw new IOException("Could not run STAR. Select a STAR 2.7.11 or newer executable. "
                    + ex.getMessage(), ex);
        }
        String version = tail(output).trim();
        Matcher match = STAR_VERSION.matcher(version);
        if (!match.matches() || Integer.parseInt(match.group(1)) != 2
                || Integer.parseInt(match.group(2)) < 7
                || (Integer.parseInt(match.group(2)) == 7 && Integer.parseInt(match.group(3)) < 11)) {
            throw new IOException("STAR 2.7.11 or a later STAR 2.x release is required. Detected: " + version);
        }
        return version;
    }

    private static void buildIndex(String star, SingleCellRequest request, SingleCellInputs.Reference reference,
            Path scratch, Path log) throws IOException, InterruptedException {
        int overhang = Math.max(1, reference.longestRead - 1);
        int saBases = Math.max(1, Math.min(14, log2(reference.bases) / 2 - 1));
        long binSize = Math.max(reference.bases / reference.contigs, reference.longestRead);
        int chrBinBits = Math.max(1, Math.min(18, log2(binSize)));
        execute(scratch, log, 0, star, "--runMode", "genomeGenerate",
                "--runThreadN", Integer.toString(request.threads), "--genomeDir", "index",
                "--genomeFastaFiles", "genome.fa", "--sjdbGTFfile", "genes.gtf",
                "--sjdbOverhang", Integer.toString(overhang), "--genomeSAindexNbases", Integer.toString(saBases),
                "--genomeChrBinNbits", Integer.toString(chrBinBits),
                "--limitGenomeGenerateRAM", Long.toString(request.indexRamGiB * (1L << 30)),
                "--outFileNamePrefix", "star/index-");
    }

    private static void quantify(String star, SingleCellRequest request, Path scratch, Path log)
            throws IOException, InterruptedException {
        // STARsolo takes the cDNA FASTQ first, then the separate barcode/UMI mate.
        execute(scratch, log, 0, star,
                "--runThreadN", Integer.toString(request.threads), "--genomeDir", "index",
                "--readFilesIn", "cdna.fastq", "barcode.fastq", "--outFileNamePrefix", "star/",
                "--outSAMtype", "None", "--soloType", "CB_UMI_Simple", "--soloCBwhitelist", "whitelist.txt",
                "--soloCBstart", "1", "--soloCBlen", Integer.toString(SingleCellRequest.Chemistry.BARCODE_LENGTH),
                "--soloUMIstart", "17", "--soloUMIlen", Integer.toString(request.chemistry.umiLength),
                "--soloFeatures", request.feature(), "--soloStrand", "Forward",
                "--soloCBmatchWLtype", "1MM_multi_Nbase_pseudocounts", "--soloUMIdedup", "1MM_CR",
                "--soloUMIfiltering", "MultiGeneUMI_CR", "--clipAdapterType", "CellRanger4",
                "--outFilterScoreMin", Integer.toString(MIN_ALIGNMENT_SCORE),
                "--soloCellFilter", "CellRanger2.2", Integer.toString(request.expectedCells), "0.99", "10");
    }

    private static int log2(long value) {
        return 63 - Long.numberOfLeadingZeros(value);
    }

    private static void execute(Path scratch, Path output, int timeoutSeconds, String... command)
            throws IOException, InterruptedException {
        checkInterrupted();
        if (timeoutSeconds == 0) {
            append(output, "$ " + Arrays.toString(command));
        }
        Path pidFile = scratch.resolve(PID_FILE);
        Files.deleteIfExists(pidFile);
        // Record the PID before exec so Java 8 can also stop children of a packaged STAR wrapper.
        List<String> args = new ArrayList<>(Arrays.asList("/bin/sh", "-c",
                "printf '%s\\n' \"$$\" > " + PID_FILE + " || exit 1; exec \"$@\"", "123fastq-star"));
        args.addAll(Arrays.asList(command));
        Process process = new ProcessBuilder(args).directory(scratch.toFile())
                .redirectErrorStream(true).redirectOutput(ProcessBuilder.Redirect.appendTo(output.toFile())).start();
        try {
            if (timeoutSeconds > 0) {
                if (!process.waitFor(timeoutSeconds, TimeUnit.SECONDS)) {
                    throw new IOException("STAR --version timed out.");
                }
            } else {
                process.waitFor();
            }
            checkInterrupted();
            if (process.exitValue() != 0) {
                throw new IOException("STAR exited with status " + process.exitValue() + ". "
                        + (process.exitValue() == 137 ? "Check available system RAM. " : "")
                        + "Last log lines:" + System.lineSeparator() + tail(output));
            }
        } finally {
            stop(process, pidFile, output);
        }
    }

    private static void stop(Process process, Path pidFile, Path log) {
        if (!process.isAlive()) {
            return;
        }
        boolean interrupted = Thread.interrupted();
        try {
            if (Files.notExists(pidFile) && process.waitFor(100, TimeUnit.MILLISECONDS)) {
                return;
            }
            stopChildren(process, pidFile);
            process.waitFor(250, TimeUnit.MILLISECONDS);
        } catch (InterruptedException ex) {
            interrupted = true;
        } catch (IOException ex) {
            try {
                append(log, "Could not inspect STAR's child processes: " + ex.getMessage());
            } catch (IOException logError) {
                ex.addSuppressed(logError);
                Logger.getLogger(SingleCellRunner.class.getName())
                        .log(Level.WARNING, "Could not stop STAR child processes", ex);
            }
        } finally {
            process.destroy();
            try {
                if (!process.waitFor(2, TimeUnit.SECONDS)) {
                    process.destroyForcibly();
                }
            } catch (InterruptedException ex) {
                process.destroyForcibly();
                interrupted = true;
            }
            // Wait for termination before removing the files STAR can still be writing.
            while (process.isAlive()) {
                try {
                    process.waitFor();
                } catch (InterruptedException ex) {
                    interrupted = true;
                }
            }
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private static void stopChildren(Process star, Path pidFile) throws IOException, InterruptedException {
        if (Files.notExists(pidFile) || !star.isAlive()) {
            return;
        }
        String root = new String(Files.readAllBytes(pidFile), StandardCharsets.US_ASCII).trim();
        if (!root.matches("[1-9][0-9]*")) {
            throw new IOException("Invalid STAR PID file.");
        }
        Process ps = new ProcessBuilder("ps", "-A", "-o", "pid=", "-o", "ppid=").start();
        Map<String, List<String>> children = new HashMap<>();
        try (BufferedReader rows = new BufferedReader(new InputStreamReader(ps.getInputStream(), StandardCharsets.US_ASCII))) {
            String row;
            while ((row = rows.readLine()) != null) {
                String[] fields = row.trim().split("\\s+");
                if (fields.length == 2 && fields[0].matches("[1-9][0-9]*")) {
                    children.computeIfAbsent(fields[1], unused -> new ArrayList<>()).add(fields[0]);
                }
            }
            if (ps.waitFor() != 0) {
                throw new IOException("ps failed to list child processes.");
            }
        } finally {
            ps.destroy();
        }
        List<String> descendants = new ArrayList<>();
        descendants.add(root);
        for (int i = 0; i < descendants.size(); i++) {
            descendants.addAll(children.getOrDefault(descendants.get(i), Collections.emptyList()));
        }
        descendants.remove(0);
        if (!descendants.isEmpty() && star.isAlive()) {
            Collections.reverse(descendants);
            List<String> kill = new ArrayList<>(Arrays.asList("/bin/kill", "-KILL"));
            kill.addAll(descendants);
            new ProcessBuilder(kill).redirectErrorStream(true).redirectOutput(ProcessBuilder.Redirect.INHERIT)
                    .start().waitFor();
        }
    }

    static void checkInterrupted() throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException("Single-cell analysis was cancelled.");
        }
    }

    private static String tail(Path path) throws IOException {
        try (RandomAccessFile file = new RandomAccessFile(path.toFile(), "r")) {
            int size = (int) Math.min(4096, file.length());
            file.seek(file.length() - size);
            byte[] bytes = new byte[size];
            file.readFully(bytes);
            return new String(bytes, StandardCharsets.UTF_8).trim();
        }
    }

    private static void copyReports(Path star, Path feature, Path job) throws IOException {
        for (String name : Arrays.asList("Log.out", "Log.final.out", "Log.progress.out", "index-Log.out")) {
            Path source = star.resolve(name);
            if (Files.isRegularFile(source)) {
                Files.copy(source, job.resolve("STAR-" + name));
            }
        }
        for (String name : Arrays.asList("Summary.csv", "Features.stats", "Barcodes.stats")) {
            Path source = feature.resolve(name);
            if (!Files.isRegularFile(source)) {
                source = star.resolve("Solo.out").resolve(name);
            }
            if (Files.isRegularFile(source)) {
                Files.copy(source, job.resolve(name));
            }
        }
    }

    private static void writeSettings(Path path, SingleCellRequest request, String version) throws IOException {
        StringBuilder settings = new StringBuilder()
                .append("Started UTC\t").append(Instant.now()).append('\n')
                .append("STAR version\t").append(version).append('\n')
                .append("Chemistry\t").append(request.chemistry).append('\n')
                .append("Count feature\t").append(request.feature()).append('\n')
                .append("Expected cells\t").append(request.expectedCells).append('\n')
                .append("Threads\t").append(request.threads).append('\n')
                .append("Index RAM (GiB)\t").append(request.indexRamGiB).append('\n')
                .append("Reference FASTA\t").append(request.fasta.toAbsolutePath()).append('\n')
                .append("Annotation GTF\t").append(request.gtf.toAbsolutePath()).append('\n')
                .append("Barcode whitelist\t").append(request.whitelist.toAbsolutePath()).append('\n');
        for (int i = 0; i < request.pairs.size(); i++) {
            SingleCellRequest.ReadPair pair = request.pairs.get(i);
            settings.append("Pair ").append(i + 1).append(" R1\t").append(pair.barcodeRead.toAbsolutePath()).append('\n');
            settings.append("Pair ").append(i + 1).append(" R2\t").append(pair.cdnaRead.toAbsolutePath()).append('\n');
        }
        Files.write(path, settings.toString().getBytes(StandardCharsets.UTF_8));
    }

    private static void writeSummary(Path destination, SingleCellMatrix.Dimensions raw,
            SingleCellMatrix.Dimensions filtered) throws IOException {
        String summary = "matrix\tgenes\tbarcodes\tnonzero_entries\tumis\n"
                + summaryRow("raw", raw) + summaryRow("filtered", filtered);
        Files.write(destination, summary.getBytes(StandardCharsets.UTF_8));
    }

    private static String summaryRow(String name, SingleCellMatrix.Dimensions matrix) {
        return name + "\t" + matrix.genes + "\t" + matrix.barcodes + "\t" + matrix.entries + "\t" + matrix.umis + "\n";
    }

    private static void report(Path log, Consumer<String> progress, String message) {
        try {
            append(log, message);
        } catch (IOException ex) {
            progress.accept("Could not write to " + log + ": " + ex.getMessage());
        }
        progress.accept(message);
    }

    private static void append(Path file, String text) throws IOException {
        Files.write(file, (text + "\n").getBytes(StandardCharsets.UTF_8),
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

    private static void discardScratch(Path dir) throws IOException {
        if (!Files.exists(dir)) {
            return;
        }
        Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path directory, IOException failure) throws IOException {
                if (failure != null) {
                    throw failure;
                }
                Files.delete(directory);
                return FileVisitResult.CONTINUE;
            }
        });
    }
}
