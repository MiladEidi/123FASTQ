package Eidi._123Fastq.TrimFactoryPackage;

import java.util.HashSet;
import java.util.Set;
import javax.swing.JTextArea;

public class AdapterRemover extends AbstractSingleRecordTrimmer {

    private static final int MAX_KMER_SIZE = 7;

    private final int kmerSize;
    private final Set<String> kmerSet;
    private final Set<String> kmerSet2;

    public AdapterRemover(String adapterSeq) {
        this.kmerSize = Math.min(adapterSeq.length(), MAX_KMER_SIZE);
        this.kmerSet = buildKmerSet(adapterSeq, kmerSize);
        this.kmerSet2 = null;
    }

    public AdapterRemover(String adapterSeq, String adapterSeq2) {
        this.kmerSize = Math.min(
                Math.min(adapterSeq.length(), adapterSeq2.length()),
                MAX_KMER_SIZE);
        this.kmerSet = buildKmerSet(adapterSeq, kmerSize);
        this.kmerSet2 = buildKmerSet(adapterSeq2, kmerSize);
    }

    private static Set<String> buildKmerSet(String seq, int k) {
        Set<String> kmers = new HashSet<>();
        for (int i = 0; i <= seq.length() - k; i++) {
            kmers.add(seq.substring(i, i + k));
        }
        return kmers;
    }

    @Override
    public FastqRecord processRecord(FastqRecord in, JTextArea messenger) {
        int trimPos = findFirstKmerMatch(in.getSequence());
        if (trimPos == -1) {
            return in;
        }
        if (trimPos == 0) {
            return null;
        }
        return new FastqRecord(in, 0, trimPos, messenger);
    }

    private int findFirstKmerMatch(String seq) {
        if (seq.length() < kmerSize) {
            return -1;
        }
        for (int i = 0; i <= seq.length() - kmerSize; i++) {
            String kmer = seq.substring(i, i + kmerSize);
            if (kmerSet.contains(kmer) || (kmerSet2 != null && kmerSet2.contains(kmer))) {
                return i;
            }
        }
        return -1;
    }
}
