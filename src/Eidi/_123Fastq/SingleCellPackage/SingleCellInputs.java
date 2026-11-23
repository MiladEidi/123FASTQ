package Eidi._123Fastq.SingleCellPackage;

import static Eidi._123Fastq.SingleCellPackage.SingleCellRequest.Chemistry.BARCODE_LENGTH;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

final class SingleCellInputs {
    private static final Pattern FASTQ_BASES = Pattern.compile("[ACGTNacgtn]+");
    private static final Pattern FASTA_BASES = Pattern.compile("[ACGTRYSWKMBDHVNacgtryswkmbdhvn]+");
    private static final Pattern QUALITY = Pattern.compile("[!-~]+");
    private static final Pattern GENE_ID = Pattern.compile("(?:^|;)\\s*gene_id\\s+\"[^\";]+\"\\s*(?=;|$)");
    private static final Pattern TRANSCRIPT_ID = Pattern.compile("(?:^|;)\\s*transcript_id\\s+\"[^\";]+\"\\s*(?=;|$)");
    private static final int BARCODE_SAMPLE = 20_000;

    static final class Reference {
        long bases;
        int contigs;
        int longestRead;
    }

    private SingleCellInputs() {
    }

    static Reference stage(SingleCellRequest request, Path scratch, Consumer<String> log)
            throws IOException, InterruptedException {
        checkFiles(request);
        log.accept("Checking reference FASTA");
        Map<String, Long> contigs = stageFasta(request.fasta, scratch.resolve("genome.fa"));
        log.accept("Checking annotation GTF against FASTA contigs");
        long exons = stageGtf(request.gtf, scratch.resolve("genes.gtf"), contigs);
        Reference reference = new Reference();
        reference.contigs = contigs.size();
        for (long length : contigs.values()) {
            reference.bases += length;
        }
        log.accept(String.format(Locale.ROOT, "Reference: %,d bases across %,d contigs; %,d annotated exons.",
                reference.bases, reference.contigs, exons));

        log.accept("Checking barcode whitelist");
        int[] barcodes = stageWhitelist(request.whitelist, scratch.resolve("whitelist.txt"));
        log.accept(String.format(Locale.ROOT, "Validated %,d whitelist barcodes.", barcodes.length));
        stageReads(request, scratch, barcodes, reference, log);
        if (reference.longestRead < SingleCellRunner.MIN_ALIGNMENT_SCORE) {
            throw new IOException("All R2 reads are shorter than " + SingleCellRunner.MIN_ALIGNMENT_SCORE
                    + " bases. Select the cDNA FASTQs for R2.");
        }
        return reference;
    }

    private static void checkFiles(SingleCellRequest request) throws IOException {
        Set<Path> paths = new HashSet<>();
        Set<Object> fileKeys = new HashSet<>();
        long bytes = checkFile(request.fasta, paths, fileKeys)
                + checkFile(request.gtf, paths, fileKeys) + checkFile(request.whitelist, paths, fileKeys);
        for (SingleCellRequest.ReadPair pair : request.pairs) {
            bytes += checkFile(pair.barcodeRead, paths, fileKeys);
            bytes += checkFile(pair.cdnaRead, paths, fileKeys);
        }
        if (Files.getFileStore(request.outputParent).getUsableSpace() < bytes) {
            throw new IOException("The output volume has too little free space to stage the inputs. "
                    + "Allow room for uncompressed input files, the STAR index, and both matrices.");
        }
    }

    private static long checkFile(Path path, Set<Path> paths, Set<Object> keys) throws IOException {
        if (!Files.isRegularFile(path) || !Files.isReadable(path)) {
            throw new IOException("Cannot read input file " + path);
        }
        Path real = path.toRealPath();
        BasicFileAttributes attrs = Files.readAttributes(real, BasicFileAttributes.class);
        if (!paths.add(real) || (attrs.fileKey() != null && !keys.add(attrs.fileKey()))) {
            throw new IOException("The same file was selected more than once: " + path);
        }
        if (attrs.size() == 0) {
            throw new IOException("Input file is empty: " + path);
        }
        return attrs.size();
    }

    static BufferedReader reader(Path path) throws IOException {
        BufferedInputStream input = new BufferedInputStream(Files.newInputStream(path), 64 * 1024);
        try {
            input.mark(2);
            boolean gzip = input.read() == 0x1f && input.read() == 0x8b;
            input.reset();
            InputStream decoded = gzip ? new GZIPInputStream(input, 64 * 1024) : input;
            return new BufferedReader(new InputStreamReader(decoded, StandardCharsets.UTF_8.newDecoder()), 64 * 1024);
        } catch (IOException ex) {
            input.close();
            throw ex;
        }
    }

    private static Map<String, Long> stageFasta(Path source, Path target)
            throws IOException, InterruptedException {
        Map<String, Long> contigs = new HashMap<>();
        try (BufferedReader in = reader(source); BufferedWriter out = Files.newBufferedWriter(target)) {
            String name = null;
            long bases = 0;
            long lineNumber = 0;
            String line;
            while ((line = in.readLine()) != null) {
                SingleCellRunner.checkInterrupted();
                lineNumber++;
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }
                if (line.charAt(0) == '>') {
                    if (name != null) {
                        addContig(contigs, name, bases);
                    }
                    name = firstToken(line.substring(1).trim());
                    if (name.isEmpty()) {
                        throw new IOException("FASTA header has no contig name at line " + lineNumber + ".");
                    }
                    bases = 0;
                    writeLine(out, ">" + name);
                } else {
                    if (name == null || !FASTA_BASES.matcher(line).matches()) {
                        throw new IOException("Expected nucleotide FASTA sequence at line " + lineNumber + ".");
                    }
                    bases += line.length();
                    writeLine(out, line);
                }
            }
            if (name == null) {
                throw new IOException("FASTA contains no contigs.");
            }
            addContig(contigs, name, bases);
        } catch (IOException ex) {
            throw fileError(source, ex);
        }
        return contigs;
    }

    private static void addContig(Map<String, Long> contigs, String name, long bases) throws IOException {
        if (bases == 0 || contigs.putIfAbsent(name, bases) != null) {
            throw new IOException("Empty or duplicate FASTA contig: " + name);
        }
    }

    private static long stageGtf(Path source, Path target, Map<String, Long> contigs)
            throws IOException, InterruptedException {
        long exons = 0;
        try (BufferedReader in = reader(source); BufferedWriter out = Files.newBufferedWriter(target)) {
            long lineNumber = 0;
            String line;
            while ((line = in.readLine()) != null) {
                SingleCellRunner.checkInterrupted();
                lineNumber++;
                if (line.trim().isEmpty() || line.charAt(0) == '#') {
                    continue;
                }
                String[] fields = line.split("\t", -1);
                if (fields.length != 9) {
                    throw new IOException("Expected 9 tab-separated GTF fields at line " + lineNumber + ".");
                }
                Long length = contigs.get(fields[0]);
                if (length == null) {
                    throw new IOException("GTF contig '" + fields[0] + "' is absent from FASTA at line "
                            + lineNumber + ". Use FASTA and GTF from the same assembly.");
                }
                long start = coordinate(fields[3], lineNumber);
                long end = coordinate(fields[4], lineNumber);
                if (start < 1 || end < start || end > length) {
                    throw new IOException("GTF coordinates fall outside the FASTA contig at line " + lineNumber + ".");
                }
                if ("exon".equals(fields[2])) {
                    if (!("+".equals(fields[6]) || "-".equals(fields[6]))
                            || !GENE_ID.matcher(fields[8]).find() || !TRANSCRIPT_ID.matcher(fields[8]).find()) {
                        throw new IOException("GTF exon needs a strand (+/-), gene_id and transcript_id at line "
                                + lineNumber + ".");
                    }
                    exons++;
                }
                writeLine(out, line);
            }
            if (exons == 0) {
                throw new IOException("GTF contains no exon features.");
            }
        } catch (IOException ex) {
            throw fileError(source, ex);
        }
        return exons;
    }

    private static long coordinate(String value, long line) throws IOException {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException ex) {
            throw new IOException("Invalid GTF coordinate at line " + line + ".", ex);
        }
    }

    private static int[] stageWhitelist(Path source, Path target) throws IOException, InterruptedException {
        int[] barcodes = new int[8192];
        int count = 0;
        try (BufferedReader in = reader(source); BufferedWriter out = Files.newBufferedWriter(target)) {
            String line;
            long lineNumber = 0;
            while ((line = in.readLine()) != null) {
                SingleCellRunner.checkInterrupted();
                lineNumber++;
                String barcode = line.trim().toUpperCase(Locale.ROOT);
                if (barcode.isEmpty()) {
                    continue;
                }
                if (barcode.length() != BARCODE_LENGTH || !FASTQ_BASES.matcher(barcode).matches()
                        || barcode.indexOf('N') >= 0) {
                    throw new IOException("Expected one " + BARCODE_LENGTH + "-base A/C/G/T barcode at line "
                            + lineNumber + ".");
                }
                if (count == barcodes.length) {
                    barcodes = Arrays.copyOf(barcodes, barcodes.length * 2);
                }
                barcodes[count++] = encodeBarcode(barcode);
                writeLine(out, barcode);
            }
            if (count == 0) {
                throw new IOException("Barcode whitelist has no entries.");
            }
            Arrays.sort(barcodes, 0, count);
            for (int i = 1; i < count; i++) {
                if (barcodes[i] == barcodes[i - 1]) {
                    throw new IOException("Barcode whitelist contains a duplicate sequence.");
                }
            }
        } catch (IOException ex) {
            throw fileError(source, ex);
        }
        return Arrays.copyOf(barcodes, count);
    }

    private static int encodeBarcode(String barcode) {
        int result = 0;
        for (int i = 0; i < BARCODE_LENGTH; i++) {
            result = (result << 2) | ("ACGT".indexOf(barcode.charAt(i)));
        }
        return result;
    }

    private static void stageReads(SingleCellRequest request, Path scratch, int[] barcodes,
            Reference reference, Consumer<String> log) throws IOException, InterruptedException {
        try (BufferedWriter barcodeOut = Files.newBufferedWriter(scratch.resolve("barcode.fastq"));
                BufferedWriter cdnaOut = Files.newBufferedWriter(scratch.resolve("cdna.fastq"))) {
            long total = 0;
            int barcodeLength = BARCODE_LENGTH + request.chemistry.umiLength;
            for (int i = 0; i < request.pairs.size(); i++) {
                SingleCellRequest.ReadPair pair = request.pairs.get(i);
                String lane = "Pair " + (i + 1) + " of " + request.pairs.size();
                log.accept(lane + " — checking FASTQ records and mate synchronization");
                total += stagePair(pair, barcodeOut, cdnaOut, barcodes, barcodeLength, reference, lane, log);
            }
            log.accept(String.format(Locale.ROOT, "Validated %,d read pairs.", total));
        }
    }

    private static long stagePair(SingleCellRequest.ReadPair pair, BufferedWriter barcodeOut,
            BufferedWriter cdnaOut, int[] barcodes, int barcodeLength, Reference reference,
            String lane, Consumer<String> log) throws IOException, InterruptedException {
        long records = 0;
        long extraCycles = 0;
        int matching = 0;
        try (BufferedReader barcodeIn = reader(pair.barcodeRead); BufferedReader cdnaIn = reader(pair.cdnaRead)) {
            while (true) {
                SingleCellRunner.checkInterrupted();
                Read barcode = readRecord(barcodeIn, 1, records + 1);
                Read cdna = readRecord(cdnaIn, 2, records + 1);
                if (barcode == null && cdna == null) {
                    break;
                }
                records++;
                if (barcode == null || cdna == null) {
                    throw new IOException("R1 and R2 have different record counts (at record " + records + ").");
                }
                if (!barcode.id.equals(cdna.id)) {
                    throw new IOException("R1 and R2 read identifiers differ at record " + records + ".");
                }
                if (barcode.sequence.length() < barcodeLength) {
                    throw new IOException("R1 record " + records + " has " + barcode.sequence.length()
                            + " bases. This chemistry requires at least " + barcodeLength + ".");
                }
                if (barcode.sequence.length() > barcodeLength) {
                    extraCycles++;
                }
                if (records <= BARCODE_SAMPLE) {
                    String cb = barcode.sequence.substring(0, BARCODE_LENGTH);
                    if (cb.indexOf('N') < 0 && Arrays.binarySearch(barcodes, encodeBarcode(cb)) >= 0) {
                        matching++;
                    }
                }
                reference.longestRead = Math.max(reference.longestRead, cdna.sequence.length());
                barcode.write(barcodeOut, barcodeLength);
                cdna.write(cdnaOut, cdna.sequence.length());
                if (records % 1_000_000 == 0) {
                    log.accept(String.format(Locale.ROOT, "%s — checked %,d read pairs.", lane, records));
                }
            }
            if (records == 0) {
                throw new IOException("The FASTQ pair contains no reads.");
            }
        } catch (IOException ex) {
            throw new IOException(lane + " (" + pair.barcodeRead + " and " + pair.cdnaRead + "): "
                    + ex.getMessage(), ex);
        }

        int sampled = (int) Math.min(records, BARCODE_SAMPLE);
        double matchRate = (double) matching / sampled;
        log.accept(String.format(Locale.ROOT, "%s — %,d reads; %.1f%% exact whitelist matches in the first %,d.",
                lane, records, 100 * matchRate, sampled));
        if (matching == 0 || matchRate < 0.1) {
            log.accept("Warning: low exact barcode match rate. Check the chemistry, whitelist and R1 assignment.");
        }
        if (extraCycles > 0) {
            log.accept(String.format(Locale.ROOT, "Ignored trailing R1 cycles on %,d records.", extraCycles));
        }
        return records;
    }

    private static Read readRecord(BufferedReader in, int mate, long record) throws IOException {
        String header = in.readLine();
        if (header == null) {
            return null;
        }
        String sequence = in.readLine();
        String separator = in.readLine();
        String quality = in.readLine();
        if (header.length() < 2 || header.charAt(0) != '@' || Character.isWhitespace(header.charAt(1))
                || sequence == null || !FASTQ_BASES.matcher(sequence).matches()
                || separator == null || !separator.startsWith("+")
                || quality == null || quality.length() != sequence.length() || !QUALITY.matcher(quality).matches()) {
            throw new IOException("Malformed four-line FASTQ record " + record + " in R" + mate
                    + ". Check its header, sequence, separator and quality.");
        }

        String readName = header.substring(1);
        String id = firstToken(readName);
        if (!id.chars().allMatch(c -> c >= 33 && c <= 126)) {
            throw new IOException("Invalid FASTQ identifier at R" + mate + " record " + record + ".");
        }
        String description = readName.substring(id.length()).trim();
        boolean slashMate = id.endsWith("/1") || id.endsWith("/2");
        boolean casavaMate = description.startsWith("1:Y:") || description.startsWith("1:N:")
                || description.startsWith("2:Y:") || description.startsWith("2:N:");
        if ((slashMate && id.charAt(id.length() - 1) != '0' + mate)
                || (casavaMate && description.charAt(0) != '0' + mate)) {
            throw new IOException("Unexpected mate designation in R" + mate + " at record " + record + ".");
        }
        if (separator.length() > 1 && !firstToken(separator.substring(1)).equals(id)) {
            throw new IOException("FASTQ '+' identifier differs from the header in R" + mate + " at record " + record + ".");
        }
        if (slashMate) {
            id = id.substring(0, id.length() - 2);
        }
        if (id.isEmpty()) {
            throw new IOException("Missing FASTQ identifier in R" + mate + " at record " + record + ".");
        }
        return new Read(id, sequence.toUpperCase(Locale.ROOT), quality);
    }

    private static String firstToken(String value) {
        int end = 0;
        while (end < value.length() && !Character.isWhitespace(value.charAt(end))) {
            end++;
        }
        return value.substring(0, end);
    }

    private static IOException fileError(Path path, IOException cause) {
        return new IOException(path + ": " + cause.getMessage(), cause);
    }

    private static void writeLine(BufferedWriter out, String line) throws IOException {
        out.write(line);
        out.write('\n');
    }

    private static final class Read {
        final String id;
        final String sequence;
        final String quality;

        Read(String id, String sequence, String quality) {
            this.id = id;
            this.sequence = sequence;
            this.quality = quality;
        }

        void write(BufferedWriter out, int length) throws IOException {
            writeLine(out, "@" + id);
            out.write(sequence, 0, length);
            out.write("\n+\n");
            out.write(quality, 0, length);
            out.write('\n');
        }
    }
}
