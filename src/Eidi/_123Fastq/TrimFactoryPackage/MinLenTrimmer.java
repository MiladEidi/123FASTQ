package Eidi._123Fastq.TrimFactoryPackage;

import javax.swing.JTextArea;

public class MinLenTrimmer extends AbstractSingleRecordTrimmer {

    private int minLen;

    public MinLenTrimmer(int minLen) {
        this.minLen = minLen;
    }

    @Override
    public FastqRecord processRecord(FastqRecord in, JTextArea messenger) {
        if (in.getSequence().length() >= minLen) {
            return in;
        }
        return null;
    }
}
