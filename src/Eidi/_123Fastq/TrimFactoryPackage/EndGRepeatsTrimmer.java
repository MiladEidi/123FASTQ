package Eidi._123Fastq.TrimFactoryPackage;

import javax.swing.JTextArea;

/**
 * @author Milad Eidi
 */

public class EndGRepeatsTrimmer extends AbstractSingleRecordTrimmer {

    private int repeats;

    public EndGRepeatsTrimmer(int Repeats) {
        this.repeats = Repeats;
    }

    @Override
    public FastqRecord processRecord(FastqRecord in, JTextArea messenger) {
        char seq[] = in.getSequence().toCharArray();
        int repeat = 1;

        int endPointOfRepeats = 0;
        for (int i = seq.length - 1; i > 0; i--) {
            if (seq[i] == 'G' && seq[i - 1] == 'G') {
            repeat++;
            }else{
                endPointOfRepeats = i;
                break;
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
