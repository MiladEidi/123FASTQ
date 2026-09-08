package Eidi._123Fastq.TrimFactoryPackage;

import javax.swing.JTextArea;

public class EndCropTrimmer extends AbstractSingleRecordTrimmer {

    private int len;
    private int readLenThreshold;

    public EndCropTrimmer(int spnEndCrop, int readLenThreshold) {
        this.len = spnEndCrop;
        this.readLenThreshold = readLenThreshold;
    }

    @Override
    public FastqRecord processRecord(FastqRecord in, JTextArea messenger) {
        int seqlen = in.getSequence().length();
        if (seqlen > readLenThreshold) {
            if (seqlen <= len) {
                return null;
            }
            if (len == 0) {
                return in;
            }
            return new FastqRecord(in, 0, seqlen - len, messenger);
        } else {
            return in;
        }
    }
}
