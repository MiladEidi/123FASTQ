package Eidi._123Fastq.TrimFactoryPackage;

import java.util.List;


public class BlockOfRecords
{
	private List<FastqRecord>originalRecs1;
	private List<FastqRecord>originalRecs2;

	private List<List<FastqRecord>> trimmedRecs;

	public BlockOfRecords(List<FastqRecord> originalRecs1, List<FastqRecord> originalRecs2)
	{
		this.originalRecs1=originalRecs1;
		this.originalRecs2=originalRecs2;
	}

	public List<List<FastqRecord>> getTrimmedRecs()
	{
		return trimmedRecs;
	}

	public void setTrimmedRecs(List<List<FastqRecord>> trimmedRecs)
	{
		this.trimmedRecs = trimmedRecs;
	}

	public List<FastqRecord> getOriginalRecs1()
	{
		return originalRecs1;
	}

	public List<FastqRecord> getOriginalRecs2()
	{
		return originalRecs2;
	}
}
