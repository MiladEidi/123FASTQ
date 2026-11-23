package Eidi._123Fastq.SingleCellPackage;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/** Inputs for a single 10x 3′ gene-expression library. */
public final class SingleCellRequest {
    public enum Chemistry {
        THREE_PRIME_V2("10x 3′ v2 (16 bp CB + 10 bp UMI)", 10),
        THREE_PRIME_V3("10x 3′ v3 / v3.1 (16 bp CB + 12 bp UMI)", 12);

        static final int BARCODE_LENGTH = 16;
        final int umiLength;
        private final String label;

        Chemistry(String label, int umiLength) {
            this.label = label;
            this.umiLength = umiLength;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    public static final class ReadPair {
        public final Path barcodeRead;
        public final Path cdnaRead;

        public ReadPair(Path barcodeRead, Path cdnaRead) {
            this.barcodeRead = Objects.requireNonNull(barcodeRead, "R1 FASTQ");
            this.cdnaRead = Objects.requireNonNull(cdnaRead, "R2 FASTQ");
        }
    }

    final List<ReadPair> pairs;
    final Path fasta;
    final Path gtf;
    final Path whitelist;
    final Path outputParent;
    final Path starExecutable;
    final Chemistry chemistry;
    final boolean includeIntrons;
    final int expectedCells;
    final int threads;
    final int indexRamGiB;

    /** A null starExecutable selects STAR on PATH. */
    public SingleCellRequest(List<ReadPair> pairs, Path fasta, Path gtf, Path whitelist,
            Path outputParent, Path starExecutable, Chemistry chemistry, boolean includeIntrons,
            int expectedCells, int threads, int indexRamGiB) {
        if (pairs.isEmpty() || pairs.contains(null)) {
            throw new IllegalArgumentException("Add at least one paired R1/R2 FASTQ lane.");
        }
        if (expectedCells < 1 || threads < 1 || indexRamGiB < 1) {
            throw new IllegalArgumentException("Expected cells, threads, and index RAM must be positive.");
        }
        this.pairs = Collections.unmodifiableList(new ArrayList<>(pairs));
        this.fasta = Objects.requireNonNull(fasta, "reference FASTA");
        this.gtf = Objects.requireNonNull(gtf, "annotation GTF");
        this.whitelist = Objects.requireNonNull(whitelist, "barcode whitelist");
        this.outputParent = Objects.requireNonNull(outputParent, "output directory");
        this.starExecutable = starExecutable;
        this.chemistry = Objects.requireNonNull(chemistry, "chemistry");
        this.includeIntrons = includeIntrons;
        this.expectedCells = expectedCells;
        this.threads = threads;
        this.indexRamGiB = indexRamGiB;
    }

    String feature() {
        return includeIntrons ? "GeneFull" : "Gene";
    }
}
