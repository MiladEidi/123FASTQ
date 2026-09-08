package Eidi._123Fastq.TrimFactoryPackage;

import javax.swing.JTextArea;


public class LeadingTrimmer extends AbstractSingleRecordTrimmer {

    private int qual;

    public LeadingTrimmer(int SpnLeading) {
        this.qual = SpnLeading;
    }

    @Override
    public FastqRecord processRecord(FastqRecord in, JTextArea messenger)
    {
            String seq=in.getSequence();
            int quals[]=in.getQualityAsInteger(true);

            for(int i=0;i<seq.length();i++)
                    {
                    if(quals[i]>=qual)
                            return new FastqRecord(in,i,seq.length()-i,messenger);
                    }

            return null;
    }
}
