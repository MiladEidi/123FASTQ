package Eidi._123Fastq.TrimFactoryPackage;

import javax.swing.JTextArea;

public class AdapterRemover extends AbstractSingleRecordTrimmer {

    private String AdapterSeq;
    private String AdapterSeq2 = null;
    int bestPosOfAdapterStart;

    public AdapterRemover(String AdapterSeq) {
        this.AdapterSeq = AdapterSeq;
    }

    public AdapterRemover(String AdapterSeq, String AdapterSeq2) {
        this.AdapterSeq = AdapterSeq;
        this.AdapterSeq2 = AdapterSeq2;
    }

    @Override
    public FastqRecord processRecord(FastqRecord in, JTextArea messenger) {
        if (AdapterSeq2 == null) {
            bestPosOfAdapterStart = in.getSequence().indexOf(AdapterSeq);
        } else {
            int one = in.getSequence().indexOf(AdapterSeq);
            int two = in.getSequence().indexOf(AdapterSeq2);
            if (one != -1 && two != -1) {
                if (one < two) {
                    bestPosOfAdapterStart = one;
                } else if (two < one) {
                    bestPosOfAdapterStart = two;
                }
            } else if (one == -1 && two != -1) {
                bestPosOfAdapterStart = two;
            } else if (one != -1 && two == -1) {
                bestPosOfAdapterStart = one;
            } else {
                bestPosOfAdapterStart = -1;
            }
        }
        if (bestPosOfAdapterStart == -1) {
            return in;
        }
        if (bestPosOfAdapterStart == 0) {
            return null;
        }
        return new FastqRecord(in, 0, bestPosOfAdapterStart + 1, messenger);
    }
}
