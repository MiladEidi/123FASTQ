package Eidi._123Fastq.TrimFactoryPackage;

import javax.swing.JTextArea;

public class EndComplexityTrimmer extends AbstractSingleRecordTrimmer {

    private int repeats;

    public EndComplexityTrimmer(int Repeats) {
        this.repeats = Repeats;
    }

    @Override
    public FastqRecord processRecord(FastqRecord in, JTextArea messenger) {
        char seq[] = in.getSequence().toCharArray();
        int repeat = 1;

        int endPointOfRepeats = 0;
        for (int i = seq.length - 1; i > 0; i--) {
            if (seq[i] != seq[i - 1]) {
                endPointOfRepeats = i;
                break;
            }else{
            repeat++;
            }
        }
        if (repeat >= repeats) {
            return new FastqRecord(in, 0, endPointOfRepeats, messenger);
        } else if (repeat == seq.length) {
            return null;
        }
        return in;
    }
}
