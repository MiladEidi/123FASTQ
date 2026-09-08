package Eidi._123Fastq.TrimFactoryPackage;

import javax.swing.JTextArea;

public class MaxLenTrimmer extends AbstractSingleRecordTrimmer {

    private int maxLen;

    public MaxLenTrimmer(int maxLen) {
        this.maxLen = maxLen;
    }

    @Override
    public FastqRecord processRecord(FastqRecord in, JTextArea messenger) {
        if (in.getSequence().length() <= maxLen) {
            return in;
        }
        return null;
    }
}
