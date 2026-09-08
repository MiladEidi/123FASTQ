package Eidi._123Fastq.TrimFactoryPackage;

import javax.swing.JTextArea;

public class HeadCropTrimmer extends AbstractSingleRecordTrimmer {

    private int bases;

    public HeadCropTrimmer(int spnHeadCrop) {
        bases = spnHeadCrop;
    }

    @Override
    public FastqRecord processRecord(FastqRecord in, JTextArea messenger) {
        int len = in.getSequence().length();
        int toTrim = bases;
        if (len <= toTrim) {
            return null;
        }
        if (toTrim == 0) {
            return in;
        }
        return new FastqRecord(in, toTrim, len - toTrim, messenger);
    }
}
