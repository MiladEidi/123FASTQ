package Eidi._123Fastq.SingleCellPackage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.StringTokenizer;
import java.util.zip.GZIPOutputStream;

final class SingleCellMatrix {
    private static final String HEADER = "%%MatrixMarket matrix coordinate integer general";

    static final class Dimensions {
        final long genes;
        final long barcodes;
        final long entries;
        final long umis;

        Dimensions(long genes, long barcodes, long entries, long umis) {
            this.genes = genes;
            this.barcodes = barcodes;
            this.entries = entries;
            this.umis = umis;
        }
    }

    private SingleCellMatrix() {
    }

    static Dimensions export(Path source, Path target) throws IOException, InterruptedException {
        Files.createDirectory(target);
        long genes = copyAxis(source, target, "features.tsv", true);
        long barcodes = copyAxis(source, target, "barcodes.tsv", false);
        Path matrix = source.resolve("matrix.mtx");
        Dimensions dimensions = validate(matrix, genes, barcodes);
        try (BufferedReader in = Files.newBufferedReader(matrix);
                BufferedWriter out = gzipWriter(target.resolve("matrix.mtx.gz"))) {
            readDimensions(in, matrix, genes, barcodes);
            writeHeader(out, dimensions);
            String line;
            while ((line = in.readLine()) != null) {
                SingleCellRunner.checkInterrupted();
                // STAR can emit explicit zero counts after removing ambiguous UMIs.
                if (triplet(line)[2] != 0) {
                    writeLine(out, line);
                }
            }
        }
        return dimensions;
    }

    static Dimensions emptyFiltered(Path rawMatrix, Path target, long genes) throws IOException {
        Files.createDirectory(target);
        Files.copy(rawMatrix.resolve("features.tsv.gz"), target.resolve("features.tsv.gz"));
        try (GZIPOutputStream out = new GZIPOutputStream(Files.newOutputStream(target.resolve("barcodes.tsv.gz")))) {
            out.finish();
        }
        Dimensions empty = new Dimensions(genes, 0, 0, 0);
        try (BufferedWriter out = gzipWriter(target.resolve("matrix.mtx.gz"))) {
            writeHeader(out, empty);
        }
        return empty;
    }

    private static Dimensions validate(Path matrix, long genes, long barcodes) throws IOException, InterruptedException {
        try (BufferedReader in = Files.newBufferedReader(matrix)) {
            long entries = readDimensions(in, matrix, genes, barcodes);
            long observed = 0;
            long nonzero = 0;
            long umis = 0;
            String line;
            while ((line = in.readLine()) != null) {
                SingleCellRunner.checkInterrupted();
                long[] triplet = triplet(line);
                observed++;
                if (triplet[0] < 1 || triplet[0] > genes || triplet[1] < 1 || triplet[1] > barcodes
                        || triplet[2] < 0 || observed > entries) {
                    throw new IOException("Invalid count at matrix entry " + observed + " in " + matrix);
                }
                if (triplet[2] > 0) {
                    nonzero++;
                    umis = Math.addExact(umis, triplet[2]);
                }
            }
            if (observed != entries) {
                throw new IOException("Matrix entry count differs from its header: " + matrix);
            }
            return new Dimensions(genes, barcodes, nonzero, umis);
        } catch (ArithmeticException | NumberFormatException ex) {
            throw new IOException("Invalid integer counts in " + matrix, ex);
        }
    }

    private static long readDimensions(BufferedReader in, Path matrix, long genes, long barcodes) throws IOException {
        if (!HEADER.equalsIgnoreCase(in.readLine())) {
            throw new IOException("Expected a sparse integer Matrix Market count matrix: " + matrix);
        }
        String line = in.readLine();
        while (line != null && line.startsWith("%")) {
            line = in.readLine();
        }
        return dimensionEntries(line, genes, barcodes);
    }

    private static void writeHeader(BufferedWriter out, Dimensions dimensions) throws IOException {
        writeLine(out, HEADER);
        writeLine(out, "%");
        writeLine(out, dimensions.genes + " " + dimensions.barcodes + " " + dimensions.entries);
    }

    private static long dimensionEntries(String line, long genes, long barcodes) throws IOException {
        if (line == null) {
            throw new IOException("Count matrix has no dimensions.");
        }
        long[] dimensions = triplet(line);
        if (genes < 1 || dimensions[0] != genes || dimensions[1] != barcodes || dimensions[2] < 0) {
            throw new IOException("Matrix dimensions disagree with the feature or barcode lists.");
        }
        return dimensions[2];
    }

    private static long[] triplet(String line) throws IOException {
        StringTokenizer fields = new StringTokenizer(line);
        if (fields.countTokens() != 3) {
            throw new IOException("Expected three fields per count matrix row.");
        }
        return new long[] {Long.parseLong(fields.nextToken()), Long.parseLong(fields.nextToken()),
            Long.parseLong(fields.nextToken())};
    }

    private static long copyAxis(Path source, Path target, String fileName, boolean features)
            throws IOException, InterruptedException {
        Path path = source.resolve(fileName);
        long rows = 0;
        try (BufferedReader in = Files.newBufferedReader(path);
                BufferedWriter out = gzipWriter(target.resolve(fileName + ".gz"))) {
            String line;
            while ((line = in.readLine()) != null) {
                SingleCellRunner.checkInterrupted();
                if (features) {
                    String[] fields = line.split("\t", -1);
                    if (fields.length < 2 || fields.length > 3 || fields[0].isEmpty() || fields[1].isEmpty()) {
                        throw new IOException("Invalid feature row " + (rows + 1) + " in " + path);
                    }
                    line = fields[0] + "\t" + fields[1] + "\tGene Expression";
                } else if (line.length() != SingleCellRequest.Chemistry.BARCODE_LENGTH
                        || !line.chars().allMatch(c -> c == 'A' || c == 'C' || c == 'G' || c == 'T')) {
                    throw new IOException("Invalid cell barcode at row " + (rows + 1) + " in " + path);
                }
                rows++;
                writeLine(out, line);
            }
        }
        return rows;
    }

    private static BufferedWriter gzipWriter(Path file) throws IOException {
        return new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(Files.newOutputStream(file),
                64 * 1024), StandardCharsets.UTF_8), 64 * 1024);
    }

    private static void writeLine(BufferedWriter out, String line) throws IOException {
        out.write(line);
        out.write('\n');
    }
}
