package Eidi._123Fastq.TrimFactoryPackage;

import javax.swing.JTextArea;

public abstract class AbstractSingleRecordTrimmer implements Trimmer
{
	@Override
	public FastqRecord[] processRecords(FastqRecord[] in, JTextArea messenger) {
		if(in==null)
			return null;

		FastqRecord out[]=new FastqRecord[in.length];

		for(int i=0;i<in.length;i++)
			{
			if(in[i]!=null)
				out[i]=processRecord(in[i], messenger);
			}

		return out;
	}

	public abstract FastqRecord processRecord(FastqRecord in, JTextArea messenger);

}
