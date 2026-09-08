package Eidi._123Fastq.TrimFactoryPackage;

import javax.swing.JTextArea;


public class AvgQualTrimmer extends AbstractSingleRecordTrimmer
{
    private int qual;

    public AvgQualTrimmer(int qual) {
        this.qual = qual;
    }

    @Override
    public FastqRecord processRecord(FastqRecord in, JTextArea messenger)
    {
            String seq=in.getSequence();
            int quals[]=in.getQualityAsInteger(true);

            int total=0;
            for(int i=0;i<seq.length();i++)
                       total+=quals[i];

            if(total<qual*seq.length())
                      return null;

            return in;
    }
}
