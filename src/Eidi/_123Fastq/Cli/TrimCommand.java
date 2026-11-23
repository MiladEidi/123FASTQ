package Eidi._123Fastq.Cli;

import Eidi._123Fastq.TrimFactoryPackage.AdapterRemover;
import Eidi._123Fastq.TrimFactoryPackage.AdapterTrimmer;
import Eidi._123Fastq.TrimFactoryPackage.AvgQualTrimmer;
import Eidi._123Fastq.TrimFactoryPackage.EndComplexityTrimmer;
import Eidi._123Fastq.TrimFactoryPackage.EndCropTrimmer;
import Eidi._123Fastq.TrimFactoryPackage.EndGRepeatsTrimmer;
import Eidi._123Fastq.TrimFactoryPackage.FastqRecord;
import Eidi._123Fastq.TrimFactoryPackage.FastqSerializer;
import Eidi._123Fastq.TrimFactoryPackage.GCContentTrimmer;
import Eidi._123Fastq.TrimFactoryPackage.HeadCropTrimmer;
import Eidi._123Fastq.TrimFactoryPackage.LeadingTrimmer;
import Eidi._123Fastq.TrimFactoryPackage.MaxLenTrimmer;
import Eidi._123Fastq.TrimFactoryPackage.MaximumInformationTrimmer;
import Eidi._123Fastq.TrimFactoryPackage.MinLenTrimmer;
import Eidi._123Fastq.TrimFactoryPackage.SlidingWindowTrimmer;
import Eidi._123Fastq.TrimFactoryPackage.ToPhred33Trimmer;
import Eidi._123Fastq.TrimFactoryPackage.ToPhred64Trimmer;
import Eidi._123Fastq.TrimFactoryPackage.TrailingTrimmer;
import Eidi._123Fastq.TrimFactoryPackage.Trimmer;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

final class TrimCommand {
    private static final int BATCH_SIZE = 256;
    static final String HELP = "Usage: java -jar 123Fastq.jar trim --input FILE --output FILE [options]\n"
            + "   or: java -jar 123Fastq.jar trim --r1 FILE --r2 FILE --out-r1 FILE --out-r2 FILE [options]\n"
            + "\nInput and output accept plain FASTQ, .gz or .bz2. Paired reads must have synchronized IDs."
            + "\n  --orphans-r1 FILE   Save surviving R1 reads whose mate was removed."
            + "\n  --orphans-r2 FILE   Save surviving R2 reads whose mate was removed."
            + "\n  --phred 33|64       Input quality encoding (33 by default)."
            + "\n  --threads N        Trimming workers (1 by default). Output order is preserved."
            + "\n  --drop-comments     Clear text on the '+' line."
            + "\n  --report FILE       Write tab-separated read or pair totals."
            + "\n  --adapter-file FILE Trim with a FASTA of Illumina adapters before any --step."
            + "\n  --adapter-seed-mismatches N  Adapter seed mismatches (2 by default)."
            + "\n  --adapter-palindrome N      Minimum palindrome score (30 by default)."
            + "\n  --adapter-simple N          Minimum simple clipping score (10 by default)."
            + "\n  --adapter-min-overlap N     Minimum palindrome adapter overlap (8 by default)."
            + "\n  --keep-both-adapter-reads    Retain reverse reads in palindrome mode."
            + "\n  --step STEP         Repeat to specify operations in order. Supply at least one step or adapter file."
            + "\n\nSteps:"
            + "\n  leading:Q, trailing:Q, mean-quality:Q, sliding-window:SIZE:Q"
            + "\n  head-crop:N, end-crop:N[:READ_LENGTH_THRESHOLD], min-length:N, max-length:N"
            + "\n  poly-g:N, poly-base:N, gc:MIN:MAX, max-info:TARGET:STRICTNESS"
            + "\n  adapter:SEQUENCE (short k-mer adapter clipping), to-phred33, to-phred64"
            + "\n  max-info accepts reads up to 1000 bases and strictness from 0 to 1.\n"
            + "\nExample: trim --input reads.fq.gz --output trimmed.fq.gz --step sliding-window:4:20 --step min-length:35\n";

    private TrimCommand() {
    }

    static void run(String[] options, PrintStream out, PrintStream err) throws Exception {
        Arguments args = Arguments.parse(options, new String[] {"input", "output", "r1", "r2", "out-r1", "out-r2",
            "orphans-r1", "orphans-r2", "phred", "threads", "step", "report", "adapter-file", "adapter-seed-mismatches",
            "adapter-palindrome", "adapter-simple", "adapter-min-overlap"},
                new String[] {"drop-comments", "keep-both-adapter-reads"}, "step");
        boolean paired = args.has("r1") || args.has("r2");
        if ((paired && (args.has("input") || args.has("output")))
                || (!paired && (args.has("out-r1") || args.has("out-r2") || args.has("orphans-r1") || args.has("orphans-r2")))) {
            throw new Arguments.Usage("Choose either single-end (--input, --output) or paired-end (--r1, --r2, --out-r1, --out-r2).");
        }
        int phred = args.integer("phred", 33, 33, 64);
        if (phred != 33 && phred != 64) {
            throw new Arguments.Usage("--phred must be 33 or 64.");
        }
        int threads = args.integer("threads", 1, 1, 64);
        List<Trimmer> steps = steps(args);
        Path r1 = Cli.inputFile(args.path(paired ? "r1" : "input"));
        Path r2 = paired ? Cli.inputFile(args.path("r2")) : null;
        if (paired && Files.isSameFile(r1, r2)) {
            throw new IOException("R1 and R2 must be distinct FASTQ files.");
        }
        Path out1 = args.path(paired ? "out-r1" : "output");
        Path out2 = paired ? args.path("out-r2") : null;
        Path orphan1 = args.optionalPath("orphans-r1");
        Path orphan2 = args.optionalPath("orphans-r2");
        Path report = args.optionalPath("report");
        Totals totals;
        try (StagedOutputs outputs = new StagedOutputs()) {
            Path pendingReport = stage(outputs, report);
            try (FastqInput first = new FastqInput(r1, phred);
                    FastqInput second = paired ? new FastqInput(r2, phred) : null;
                    FastqSerializer firstOut = Cli.writer(outputs.stage(out1));
                    FastqSerializer secondOut = writer(outputs, out2);
                    FastqSerializer firstOrphans = writer(outputs, orphan1);
                    FastqSerializer secondOrphans = writer(outputs, orphan2)) {
                totals = trim(first, second, firstOut, secondOut, firstOrphans, secondOrphans, steps,
                        args.flag("drop-comments"), threads);
            }
            if (pendingReport != null) {
                Files.write(pendingReport, totals.rows(paired).getBytes(StandardCharsets.UTF_8));
            }
            outputs.publish();
        }
        if (!args.flag("quiet")) {
            err.print(totals.rows(paired));
        }
        Cli.output(out, paired ? "paired_r1" : "fastq", out1);
        printOutput(out, "paired_r2", out2);
        printOutput(out, "orphans_r1", orphan1);
        printOutput(out, "orphans_r2", orphan2);
        printOutput(out, "report", report);
    }

    private static Totals trim(FastqInput first, FastqInput second, FastqSerializer firstOut,
            FastqSerializer secondOut, FastqSerializer firstOrphans, FastqSerializer secondOrphans,
            List<Trimmer> steps, boolean dropComments, int threads) throws Exception {
        Totals totals = new Totals();
        ExecutorService workers = threads == 1 ? null : Executors.newFixedThreadPool(threads);
        Deque<Future<List<FastqRecord[]>>> pending = new ArrayDeque<>();
        try {
            while (true) {
                long firstRecord = totals.input + 1;
                List<FastqRecord[]> batch = new ArrayList<>(BATCH_SIZE);
                for (int i = 0; i < BATCH_SIZE; i++) {
                    FastqRecord[] record = nextRecord(first, second, totals.input + 1);
                    if (record == null) {
                        break;
                    }
                    totals.input++;
                    batch.add(record);
                }
                if (batch.isEmpty()) {
                    break;
                }
                if (workers == null) {
                    writeBatch(apply(batch, steps, firstRecord), totals, firstOut, secondOut, firstOrphans,
                            secondOrphans, dropComments);
                } else {
                    pending.addLast(workers.submit(() -> apply(batch, steps, firstRecord)));
                    if (pending.size() >= 2 * threads) {
                        writeBatch(pending.removeFirst().get(), totals, firstOut, secondOut, firstOrphans,
                                secondOrphans, dropComments);
                    }
                }
            }
            while (!pending.isEmpty()) {
                writeBatch(pending.removeFirst().get(), totals, firstOut, secondOut, firstOrphans,
                        secondOrphans, dropComments);
            }
        } catch (ExecutionException ex) {
            throw new IOException("Trimming worker failed: " + ex.getCause().getMessage(), ex.getCause());
        } finally {
            if (workers != null) {
                workers.shutdownNow();
            }
        }
        return totals;
    }

    private static FastqRecord[] nextRecord(FastqInput first, FastqInput second, long number)
            throws IOException, InterruptedException {
        FastqRecord r1 = first.next();
        FastqRecord r2 = second == null ? null : second.next();
        if (r1 == null && r2 == null) {
            return null;
        }
        if (second == null) {
            return new FastqRecord[] {r1};
        }
        if (r1 == null || r2 == null || !FastqInput.id(r1).equals(FastqInput.id(r2))) {
            throw new IOException("Paired FASTQ identifiers or record counts differ at pair " + number + ".");
        }
        if (!FastqInput.mateMatches(r1, '1') || !FastqInput.mateMatches(r2, '2')) {
            throw new IOException("R1 or R2 has an unexpected mate designation at pair " + number + ".");
        }
        return new FastqRecord[] {r1, r2};
    }

    private static List<FastqRecord[]> apply(List<FastqRecord[]> batch, List<Trimmer> steps, long firstRecord)
            throws IOException, InterruptedException {
        for (int i = 0; i < batch.size(); i++) {
            Cli.checkInterrupted();
            FastqRecord[] reads = batch.get(i);
            for (Trimmer step : steps) {
                if (step instanceof MaximumInformationTrimmer) {
                    for (FastqRecord read : reads) {
                        if (read != null && read.getSequence().length() > MaximumInformationTrimmer.LONGEST_READ) {
                            throw new IOException("max-info supports reads up to 1000 bases. A longer read occurs at record "
                                    + (firstRecord + i) + ".");
                        }
                    }
                }
                reads = step.processRecords(reads, null);
                for (int mate = 0; mate < reads.length; mate++) {
                    if (reads[mate] != null && reads[mate].getSequence().isEmpty()) {
                        reads[mate] = null;
                    }
                }
            }
            batch.set(i, reads);
        }
        return batch;
    }

    private static void writeBatch(List<FastqRecord[]> batch, Totals totals, FastqSerializer firstOut,
            FastqSerializer secondOut, FastqSerializer firstOrphans, FastqSerializer secondOrphans,
            boolean dropComments) throws IOException, InterruptedException {
        for (FastqRecord[] reads : batch) {
            Cli.checkInterrupted();
            if (reads[0] != null && (reads.length == 1 || reads[1] != null)) {
                write(firstOut, reads[0], dropComments);
                if (secondOut != null) {
                    write(secondOut, reads[1], dropComments);
                }
                totals.kept++;
            } else if (reads[0] != null) {
                write(firstOrphans, reads[0], dropComments);
                totals.firstOnly++;
            } else if (reads.length == 2 && reads[1] != null) {
                write(secondOrphans, reads[1], dropComments);
                totals.secondOnly++;
            }
        }
    }

    private static void write(FastqSerializer writer, FastqRecord record, boolean dropComments) throws IOException {
        if (writer == null) {
            return;
        }
        if (record.getQuality().chars().anyMatch(q -> q > 126)) {
            throw new IOException("A quality score is out of range for the requested output Phred encoding.");
        }
        if (dropComments) {
            writer.digestedWriteRecord(record);
        } else {
            writer.writeRecord(record);
        }
    }

    private static List<Trimmer> steps(Arguments args) throws IOException {
        List<Trimmer> steps = new ArrayList<>();
        if (args.has("adapter-file")) {
            Path file = Cli.inputFile(args.path("adapter-file"));
            int minimum = args.integer("adapter-min-overlap", 8, 1, 64);
            AdapterTrimmer adapter = new AdapterTrimmer(null, args.integer("adapter-seed-mismatches", 2, 0, 16),
                    args.integer("adapter-palindrome", 30, 1, 1000), args.integer("adapter-simple", 10, 1, 1000),
                    minimum, args.flag("keep-both-adapter-reads"), null, null, null, null);
            adapter.loadSequences(file.toString(), minimum);
            if (adapter.getPrefixPairs().isEmpty() && adapter.getCommonSeqs().isEmpty()
                    && adapter.getForwardSeqs().isEmpty() && adapter.getReverseSeqs().isEmpty()) {
                throw new IOException("No adapters found in " + file);
            }
            steps.add(adapter);
        } else if (args.has("adapter-min-overlap") || args.has("adapter-seed-mismatches")
                || args.has("adapter-palindrome") || args.has("adapter-simple") || args.flag("keep-both-adapter-reads")) {
            throw new Arguments.Usage("Adapter scoring options require --adapter-file.");
        }
        for (String step : args.all("step")) {
            steps.add(step(step));
        }
        if (steps.isEmpty()) {
            throw new Arguments.Usage("Specify at least one --step or --adapter-file.");
        }
        return steps;
    }

    private static Trimmer step(String spec) {
        String[] parts = spec.split(":", -1);
        switch (parts[0]) {
            case "leading": return new LeadingTrimmer(one(parts, 0, 93));
            case "trailing": return new TrailingTrimmer(one(parts, 0, 93));
            case "mean-quality": return new AvgQualTrimmer(one(parts, 0, 93));
            case "head-crop": return new HeadCropTrimmer(one(parts, 0, Integer.MAX_VALUE));
            case "min-length": return new MinLenTrimmer(one(parts, 1, Integer.MAX_VALUE));
            case "max-length": return new MaxLenTrimmer(one(parts, 1, Integer.MAX_VALUE));
            case "poly-g": return new EndGRepeatsTrimmer(one(parts, 2, Integer.MAX_VALUE));
            case "poly-base": return new EndComplexityTrimmer(one(parts, 2, Integer.MAX_VALUE));
            case "end-crop":
                count(parts, 2, 3);
                return new EndCropTrimmer(number(parts, 1, 0, Integer.MAX_VALUE),
                        parts.length == 3 ? number(parts, 2, 0, Integer.MAX_VALUE) : 0);
            case "sliding-window":
                count(parts, 3, 3);
                return new SlidingWindowTrimmer(number(parts, 1, 1, 1_000_000), decimal(parts, 2, 93));
            case "max-info":
                count(parts, 3, 3);
                return new MaximumInformationTrimmer(number(parts, 1, 1, 700), decimal(parts, 2, 1));
            case "gc":
                count(parts, 3, 3);
                int min = number(parts, 1, 0, 100);
                int max = number(parts, 2, min, 100);
                return new GCContentTrimmer(max, min);
            case "adapter":
                count(parts, 2, 2);
                if (!parts[1].matches("[ACGTNacgtn]+")) {
                    throw new Arguments.Usage("adapter:SEQUENCE needs a nucleotide sequence.");
                }
                return new AdapterRemover(parts[1].toUpperCase(Locale.ROOT));
            case "to-phred33":
                count(parts, 1, 1);
                return new ToPhred33Trimmer();
            case "to-phred64":
                count(parts, 1, 1);
                return new ToPhred64Trimmer();
            default:
                throw new Arguments.Usage("Unknown trimming step '" + parts[0] + "'.");
        }
    }

    private static int one(String[] parts, int min, int max) {
        count(parts, 2, 2);
        return number(parts, 1, min, max);
    }

    private static int number(String[] parts, int column, int min, int max) {
        return Arguments.number(parts[0] + " parameter " + column, parts[column], min, max);
    }

    private static void count(String[] parts, int min, int max) {
        if (parts.length < min || parts.length > max) {
            throw new Arguments.Usage("Wrong number of parameters for trimming step '" + parts[0] + "'.");
        }
    }

    private static float decimal(String[] parts, int column, int max) {
        try {
            float value = Float.parseFloat(parts[column]);
            if (value >= 0 && value <= max) {
                return value;
            }
        } catch (NumberFormatException ignored) {
        }
        throw new Arguments.Usage(parts[0] + " parameter " + column + " must be from 0 to " + max + ".");
    }

    private static Path stage(StagedOutputs outputs, Path path) throws IOException {
        return path == null ? null : outputs.stage(path);
    }

    private static FastqSerializer writer(StagedOutputs outputs, Path path) throws IOException {
        return path == null ? null : Cli.writer(outputs.stage(path));
    }

    private static void printOutput(PrintStream out, String label, Path path) {
        if (path != null) {
            Cli.output(out, label, path);
        }
    }

    private static final class Totals {
        long input;
        long kept;
        long firstOnly;
        long secondOnly;

        String rows(boolean paired) {
            String header = "metric\tcount\n";
            if (paired) {
                return header + "input_pairs\t" + input + "\nkept_pairs\t" + kept + "\nr1_only\t" + firstOnly
                        + "\nr2_only\t" + secondOnly + "\nboth_removed\t" + (input - kept - firstOnly - secondOnly) + "\n";
            }
            return header + "input_reads\t" + input + "\nkept_reads\t" + kept + "\nremoved_reads\t" + (input - kept) + "\n";
        }
    }
}
