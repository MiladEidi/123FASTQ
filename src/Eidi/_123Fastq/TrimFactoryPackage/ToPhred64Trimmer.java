package Eidi._123Fastq.TrimFactoryPackage;

import javax.swing.JTextArea;

public class ToPhred64Trimmer extends AbstractSingleRecordTrimmer {

    public ToPhred64Trimmer() {
    }

    @Override
    public FastqRecord processRecord(FastqRecord in, JTextArea messenger) {
        if (in.getPhredOffset() == 64) {
            return in;
        }

        String sequence = in.getSequence();
        String quality = in.getQuality();

        StringBuilder newQuality = new StringBuilder();

        for (int i = 0; i < quality.length(); i++) {
            char newCh = (char) (quality.charAt(i) + 31);
            newQuality.append(newCh);
        }
        return new FastqRecord(in, sequence, newQuality.toString(), 64);
    }
}
