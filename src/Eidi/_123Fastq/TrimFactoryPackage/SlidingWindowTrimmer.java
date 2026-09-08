package Eidi._123Fastq.TrimFactoryPackage;

import javax.swing.JTextArea;

public class SlidingWindowTrimmer extends AbstractSingleRecordTrimmer {

    private int windowLength;
    private float requiredQuality;
    private float totalRequiredQuality;

    public SlidingWindowTrimmer(int spnEachWindow, float spnMeanQualityOfEachWindow) {
        windowLength = spnEachWindow;
        requiredQuality = spnMeanQualityOfEachWindow;
        totalRequiredQuality = requiredQuality * windowLength;
    }

    @Override
    public FastqRecord processRecord(FastqRecord in, JTextArea messenger) {
        int quals[] = in.getQualityAsInteger(true);

        if (quals.length < windowLength) {
            return null;
        }

        int total = 0;
        for (int i = 0; i < windowLength; i++) {
            total += quals[i];
        }

        if (total < totalRequiredQuality) {
            return null;
        }

        int lengthToKeep = quals.length;

        for (int i = 0; i < quals.length - windowLength; i++) {
            total = total - quals[i] + quals[i + windowLength];
            if (total < totalRequiredQuality) {
                lengthToKeep = i + windowLength;
                break;
            }
        }

        int i = lengthToKeep;

        int lastBaseQuality = quals[i - 1];
        while (lastBaseQuality < requiredQuality && i > 1) {
            i--;
            lastBaseQuality = quals[i - 1];
        }

        if (i < 1) {
            return null;
        }

        if (i < quals.length) {
            return new FastqRecord(in, 0, i, messenger);
        }

        return in;
    }
}
