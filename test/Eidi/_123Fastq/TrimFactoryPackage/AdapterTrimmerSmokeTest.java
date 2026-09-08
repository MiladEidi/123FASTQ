package Eidi._123Fastq.TrimFactoryPackage;

import java.util.ArrayList;
import java.util.HashSet;
import javax.swing.JTextArea;

public class AdapterTrimmerSmokeTest {

    public static void main(String[] args) throws Exception {
        trimsWhenOptionalMinAdapterLengthIsUnset();
        trimsAdapterWithOneLowQualityMismatch();
    }

    private static void trimsWhenOptionalMinAdapterLengthIsUnset() throws Exception {
        JTextArea log = new JTextArea();
        AdapterTrimmer loader = new AdapterTrimmer(log);
        loader.loadSequences("src/Eidi/_123Fastq/TrimFactoryPackage/adapters/TruSeq3-SE.fa", null);

        TrimmerMaker maker = new TrimmerMaker(
                false, log, null, false, 33,
                null, null, null, null, null, null, null, null, null,
                "TruSeq3-SE.fa", 2, 30, 10, null, null,
                loader.getPrefixPairs(), loader.getForwardSeqs(), loader.getReverseSeqs(), loader.getCommonSeqs(),
                null, null, null, null, null, null, null, null,
                1, false, false);

        Trimmer[] trimmers = maker.Trimmers();
        assertTrue(trimmers.length == 1, "Trimmomatic adapter trimmer should be added with default min adapter length");

        String insert = "TTGACCGTACGTTAGCCTAAGGCTTACGTA";
        String adapter = "AGATCGGAAGAGCACACGTCTGAACTCCAGTCAC";
        FastqRecord rec = record(insert + adapter, repeat('I', insert.length() + adapter.length()));
        FastqRecord[] result = trimmers[0].processRecords(new FastqRecord[]{rec}, log);

        assertTrue(result[0] != null, "Read should survive after adapter trimming");
        assertEquals(insert, result[0].getSequence(), "Adapter should be removed");
    }

    private static void trimsAdapterWithOneLowQualityMismatch() {
        JTextArea log = new JTextArea();
        AdapterTrimmer trimmer = new AdapterTrimmer(
                log, 2, 30, 10, null, false,
                new ArrayList<AdapterTrimmer.IlluminaPrefixPair>(),
                new HashSet<AdapterTrimmer.IlluminaClippingSeq>(),
                new HashSet<AdapterTrimmer.IlluminaClippingSeq>(),
                new HashSet<AdapterTrimmer.IlluminaClippingSeq>());
        AdapterTrimmer.IlluminaClippingSeq adapter = trimmer.new IlluminaLongClippingSeq("AGATCGGAAGAGCACACGTCTGAACTCCAGTCAC");
        trimmer.addClippingSeq(adapter, true, true);

        String insert = "TTGACCGTACGTTAGCCTAAGGCTTACGTA";
        String adapterPrefixWithMismatch = "AGATCGGAAGTGCACACGTCTG";
        String seq = insert + adapterPrefixWithMismatch;
        FastqRecord rec = record(seq, repeat('5', seq.length()));
        FastqRecord[] result = trimmer.processRecords(new FastqRecord[]{rec}, log);

        assertTrue(result[0] != null, "Read should survive after adapter trimming");
        assertEquals(insert, result[0].getSequence(), "Adapter scoring should tolerate the allowed low-quality mismatch");
    }

    private static FastqRecord record(String sequence, String quality) {
        return new FastqRecord("read", sequence, "+", quality, 33, new JTextArea());
    }

    private static String repeat(char c, int len) {
        StringBuilder builder = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            builder.append(c);
        }
        return builder.toString();
    }

    private static void assertEquals(String expected, String actual, String message) {
        if (!expected.equals(actual)) {
            throw new AssertionError(message + ": expected '" + expected + "' but got '" + actual + "'");
        }
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
