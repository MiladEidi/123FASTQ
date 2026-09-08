package Eidi._123Fastq.QualityControlPackage.Results;

import Eidi._123Fastq.QualityControlPackage.Modules.ModuleFactory;
import Eidi._123Fastq.QualityControlPackage.Modules.QCModule;
import Eidi._123Fastq.QualityControlPackage.Sequence.Sequence;
import Eidi._123Fastq.QualityControlPackage.Sequence.SequenceFile;
import Eidi._123Fastq.QualityControlPackage.Sequence.SequenceFormatException;
import Eidi._123Fastq.QualityControlPackage.Utilities.QualityCount;
import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class QcRunnerSmokeTest {

    public static void main(String[] args) throws Exception {
        QCModule[] serialModules = ModuleFactory.getStandardModuleList("serial.fastq");
        QCModule[] parallelModules = ModuleFactory.getStandardModuleList("serial.fastq");

        QcAnalysisResult serial = new QcRunner().run(new InMemorySequenceFile("serial.fastq"), serialModules, 1, null);
        QcAnalysisResult parallel = new QcRunner().run(new InMemorySequenceFile("serial.fastq"), parallelModules, 4, null);

        assertEquals(serial.getSequenceCount(), parallel.getSequenceCount(), "sequence count");
        compareModules(serial.getModules(), parallel.getModules());
        System.out.println("QC runner serial/parallel smoke test passed.");
    }

    private static void compareModules(QCModule[] expected, QCModule[] actual) throws Exception {
        assertEquals(expected.length, actual.length, "module count");
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i].getClass(), actual[i].getClass(), "module class " + i);
            String className = expected[i].getClass().getSimpleName();
            if ("BasicStats".equals(className)) {
                compareFields(expected[i], actual[i], "actualCount", "filteredCount", "minLength", "maxLength", "gCount", "cCount", "aCount", "tCount", "lowestChar", "fileType");
            } else if ("PerBaseQualityScores".equals(className)) {
                compareQualityCountArrays((QualityCount[]) getField(expected[i], "qualityCounts"), (QualityCount[]) getField(actual[i], "qualityCounts"), className);
            } else if ("DistributionOfMeanQualitiesPerReadLengths".equals(className)) {
                compareFields(expected[i], actual[i], "MainHashMap", "lowestChar");
            } else if ("PerSequenceQualityScores".equals(className)) {
                compareFields(expected[i], actual[i], "averageScoreCounts", "lowestChar");
            } else if ("PerBaseSequenceContent".equals(className)) {
                compareFields(expected[i], actual[i], "gCounts", "aCounts", "cCounts", "tCounts");
            } else if ("PerSequenceGCContent".equals(className)) {
                compareFields(expected[i], actual[i], "gcDistribution");
            } else if ("NContent".equals(className)) {
                compareFields(expected[i], actual[i], "nCounts", "notNCounts");
            } else if ("SequenceLengthDistribution".equals(className)) {
                compareFields(expected[i], actual[i], "lengthCounts");
            } else if ("OverRepresentedSeqs".equals(className)) {
                compareFields(expected[i], actual[i], "sequences", "count", "uniqueSequenceCount", "countAtUniqueLimit", "frozen");
            } else if ("AdapterContent".equals(className)) {
                compareFields(expected[i], actual[i], "longestSequence", "longestAdapter", "totalCount");
                compareAdapterArrays(getField(expected[i], "adapters"), getField(actual[i], "adapters"));
            } else if ("PerTileQualityScores".equals(className)) {
                compareFields(expected[i], actual[i], "currentLength", "totalCount", "splitPosition", "ignoreInReport");
                compareTileQualityCounts(getField(expected[i], "perTileQualityCounts"), getField(actual[i], "perTileQualityCounts"));
            } else if ("KmerContent".equals(className)) {
                compareFields(expected[i], actual[i], "longestSequence", "totalKmerCounts", "skipCount");
                compareKmerTable(getField(expected[i], "kmers"), getField(actual[i], "kmers"));
            }
        }
    }

    private static void compareFields(Object expected, Object actual, String... fieldNames) throws Exception {
        for (String fieldName : fieldNames) {
            assertDeepEquals(getField(expected, fieldName), getField(actual, fieldName), expected.getClass().getSimpleName() + "." + fieldName);
        }
    }

    private static void compareAdapterArrays(Object expectedAdapters, Object actualAdapters) throws Exception {
        Object[] expected = (Object[]) expectedAdapters;
        Object[] actual = (Object[]) actualAdapters;
        assertEquals(expected.length, actual.length, "adapter count");
        for (int i = 0; i < expected.length; i++) {
            compareFields(expected[i], actual[i], "name", "sequence", "positions", "adapterCount");
        }
    }

    @SuppressWarnings("unchecked")
    private static void compareTileQualityCounts(Object expectedCounts, Object actualCounts) throws Exception {
        HashMap<Integer, QualityCount[]> expected = (HashMap<Integer, QualityCount[]>) expectedCounts;
        HashMap<Integer, QualityCount[]> actual = (HashMap<Integer, QualityCount[]>) actualCounts;
        assertEquals(expected.keySet(), actual.keySet(), "tile keys");
        for (Integer tile : expected.keySet()) {
            compareQualityCountArrays(expected.get(tile), actual.get(tile), "tile " + tile);
        }
    }

    @SuppressWarnings("unchecked")
    private static void compareKmerTable(Object expectedKmers, Object actualKmers) throws Exception {
        Hashtable<String, Object> expected = (Hashtable<String, Object>) expectedKmers;
        Hashtable<String, Object> actual = (Hashtable<String, Object>) actualKmers;
        assertEquals(expected.keySet(), actual.keySet(), "kmer keys");
        for (String kmer : expected.keySet()) {
            compareFields(expected.get(kmer), actual.get(kmer), "sequence", "count", "positions");
        }
    }

    private static void compareQualityCountArrays(QualityCount[] expected, QualityCount[] actual, String label) throws Exception {
        assertEquals(expected.length, actual.length, label + " quality count length");
        for (int i = 0; i < expected.length; i++) {
            compareFields(expected[i], actual[i], "actualCounts", "totalCounts");
        }
    }

    private static Object getField(Object target, String fieldName) throws Exception {
        Class<?> type = target.getClass();
        while (type != null) {
            try {
                Field field = type.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field.get(target);
            } catch (NoSuchFieldException e) {
                type = type.getSuperclass();
            }
        }
        throw new NoSuchFieldException(fieldName);
    }

    private static void assertDeepEquals(Object expected, Object actual, String label) {
        if (expected instanceof long[]) {
            if (!Arrays.equals((long[]) expected, (long[]) actual)) {
                throw new AssertionError(label);
            }
            return;
        }
        if (expected instanceof long[][]) {
            if (!Arrays.deepEquals((long[][]) expected, (long[][]) actual)) {
                throw new AssertionError(label);
            }
            return;
        }
        if (expected instanceof double[]) {
            if (!Arrays.equals((double[]) expected, (double[]) actual)) {
                throw new AssertionError(label);
            }
            return;
        }
        if (expected instanceof int[]) {
            if (!Arrays.equals((int[]) expected, (int[]) actual)) {
                throw new AssertionError(label);
            }
            return;
        }
        assertEquals(expected, actual, label);
    }

    private static void assertEquals(Object expected, Object actual, String label) {
        if (expected == null ? actual != null : !expected.equals(actual)) {
            throw new AssertionError(label + ": expected " + expected + " but got " + actual);
        }
    }

    private static class InMemorySequenceFile implements SequenceFile {

        private final List<Sequence> sequences = new ArrayList<>();
        private final String name;
        private int index = 0;

        private InMemorySequenceFile(String name) {
            this.name = name;
            for (int i = 0; i < 120; i++) {
                String sequence = buildSequence(i);
                String quality = buildQuality(sequence.length(), i);
                Sequence record = new Sequence(this, sequence, quality, "@INST:1:FC:1:" + (1101 + (i % 3)) + ":1000:" + i);
                if (i % 17 == 0) {
                    record.setIsFiltered(true);
                }
                sequences.add(record);
            }
        }

        public boolean hasNext() {
            return index < sequences.size();
        }

        public Sequence next() throws SequenceFormatException {
            if (!hasNext()) {
                throw new SequenceFormatException("No more sequences");
            }
            return sequences.get(index++);
        }

        public boolean isColorspace() {
            return false;
        }

        public String name() {
            return name;
        }

        public int getPercentComplete() {
            return (int) ((index / (double) sequences.size()) * 100);
        }

        public File getFile() {
            return new File(name);
        }

        private String buildSequence(int index) {
            String seed = index % 5 == 0 ? "ACGTACGTACGTNNNNACGTACGT" : "GATTACAGATTACACGTACGTACGT";
            StringBuilder builder = new StringBuilder(seed);
            for (int i = 0; i < index % 30; i++) {
                builder.append("ACGT".charAt((index + i) % 4));
            }
            return builder.toString();
        }

        private String buildQuality(int length, int index) {
            StringBuilder builder = new StringBuilder(length);
            for (int i = 0; i < length; i++) {
                builder.append((char) (33 + ((index + i) % 35)));
            }
            return builder.toString();
        }
    }
}
