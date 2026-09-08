package Eidi._123Fastq.TrimFactoryPackage;

import javax.swing.JTextArea;

public class TrailingTrimmer extends AbstractSingleRecordTrimmer {

    private int qual;

    public TrailingTrimmer(int qual) {
        this.qual = qual;
    }

    @Override
    public FastqRecord processRecord(FastqRecord in, JTextArea messenger) {
        int quals[] = in.getQualityAsInteger(true);

        for (int i = quals.length - 1; i > 0; i--) {
            if (quals[i] >= qual) {
                return new FastqRecord(in, 0, i + 1, messenger);
            }
        }
        return null;
    }
}
