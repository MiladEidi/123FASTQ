package Eidi._123Fastq.TrimFactoryPackage;

import javax.swing.JTextArea;

public class GCContentTrimmer extends AbstractSingleRecordTrimmer {

    private Integer MaxGCPercent;
    private Integer MinGCPercent;

    public GCContentTrimmer(Integer MaxGC, Integer MinGC) {
        this.MaxGCPercent = MaxGC;
        this.MinGCPercent = MinGC;
    }

    @Override
    public FastqRecord processRecord(FastqRecord in, JTextArea messenger) {
        String inputRead = in.getSequence();
        int readLen = inputRead.length();
        char[] seq = inputRead.toCharArray();
        if (readLen == 0) {
            return null;
        }
        int GCnumberOfInputRead = 0;
        for (int i = 0; i < readLen; i++) {
            if (seq[i] == 'G' || seq[i] == 'C') {
                ++GCnumberOfInputRead;
            }
        }
        double GCcontentOfRead = ((double)GCnumberOfInputRead / readLen) * 100;
        if (MinGCPercent != null && MaxGCPercent != null) {
            if (GCcontentOfRead >= MinGCPercent && GCcontentOfRead <= MaxGCPercent) {
                return in;
            }
        } else if (MinGCPercent == null && MaxGCPercent != null) {
            if (GCcontentOfRead <= MaxGCPercent) {
                return in;
            }
        } else if (MinGCPercent != null && MaxGCPercent == null) {
            if (GCcontentOfRead >= MinGCPercent) {
                return in;
            }
        }
        return null;
    }
}
