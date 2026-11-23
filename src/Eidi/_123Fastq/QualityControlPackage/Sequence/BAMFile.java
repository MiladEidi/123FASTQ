package Eidi._123Fastq.QualityControlPackage.Sequence;

import Eidi._123Fastq.ConvertorsPackage.SamToFastq.SamFileParser;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import net.sf.samtools.CigarElement;
import net.sf.samtools.CigarOperator;
import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMFormatException;
import net.sf.samtools.SAMRecord;

public class BAMFile implements SequenceFile {

	private File file;
	private boolean onlyMapped;
	private long fileSize = 0;
	private long recordSize = 0;

	// We keep the file stream around just so we can see how far through
	// the file we've got.  We don't read from this directly, but it's the
	// only way to access the file pointer.
	private FileInputStream fis;

	private SAMFileReader br;
	private String name;
	private Sequence nextSequence = null;
	private boolean closed;
	Iterator<SAMRecord> it;


	public BAMFile (File file, boolean onlyMapped) throws SequenceFormatException, IOException {
		this.file = file;
		fileSize = file.length();
		name = file.getName();
		this.onlyMapped = onlyMapped;
		SAMFileReader.setDefaultValidationStringency(SAMFileReader.ValidationStringency.SILENT);
		fis = new FileInputStream(file);
		try {
			br = new SAMFileReader(fis);
			it = br.iterator();
			readNext();
		} catch (SequenceFormatException | RuntimeException ex) {
			try {
				close();
			} catch (IOException | RuntimeException cleanup) {
				ex.addSuppressed(cleanup);
			}
			throw ex;
		}
	}

	public String name () {
		return name;
	}

	public int getPercentComplete() {
		if (!hasNext()) return 100;

		try {
			int percent = (int) (((double)fis.getChannel().position()/ fileSize)*100);
			return percent;
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public boolean isColorspace () {
		return false;
	}

	public boolean hasNext() {
		return nextSequence != null;
	}

	public Sequence next () throws SequenceFormatException {
		Sequence returnSeq = nextSequence;
		readNext();
		return returnSeq;
	}

	private void readNext() throws SequenceFormatException {

		SAMRecord record;

		while (true) {

			if (!it.hasNext()) {
				nextSequence = null;
				try {
					close();
				}
				catch (IOException ioe) {
					ioe.printStackTrace();
				}
				return;
			}

			try {
				record = it.next();
			}
			catch (SAMFormatException sfe) {
				throw new SequenceFormatException(sfe.getMessage());
			}

			// We skip over entries with no mapping if that's what the user asked for
			if (onlyMapped && record.getReadUnmappedFlag()) {
				continue;
			}
			else {
				break;
			}
		}

		// This is a very rough calculation of the record size so we can approximately track progress
		// through the file.
		if (recordSize == 0) {
			recordSize = (record.getReadLength()*2)+150;
			if (br.isBinary()) {
				recordSize /= 4;
			}
		}


		String sequence = record.getReadString();
		String qualities = record.getBaseQualityString();
		if (sequence.isEmpty() || sequence.equals("*") || qualities.equals("*")
				|| sequence.length() != qualities.length()) {
			throw new SequenceFormatException("Missing or inconsistent bases and qualities on SAM/BAM read " + record.getReadName());
		}


		// TODO: TEST THIS!!!
		// If we're only working with mapped data then we need to exclude any regions which have been either
		// hard or soft clipped by our aligner.
		if (onlyMapped) {
			List<CigarElement> elements = record.getCigar().getCigarElements();

			// We need to clip the 3' end first otherwise the numbers at the 5' end won't be right.
			if (!elements.isEmpty() && elements.get(elements.size()-1).getOperator().equals(CigarOperator.S)) {
				int value = elements.get(elements.size()-1).getLength();
				sequence = sequence.substring(0,sequence.length()-value);
				qualities = qualities.substring(0,qualities.length()-value);

			}


			if (!elements.isEmpty() && elements.get(0).getOperator().equals(CigarOperator.S)) {
				int value = elements.get(0).getLength();
				sequence = sequence.substring(value);
				qualities = qualities.substring(value);
			}


		}

		// BAM/SAM files always show sequence relative to the top strand of
		// the mapped reference so if this sequence maps to the reverse strand
		// we need to reverse complement the sequence and reverse the qualities
		// to get the original orientation of the read.
		if (record.getReadNegativeStrandFlag()) {
			sequence = SamFileParser.reverseComplement(sequence);
			qualities = new StringBuilder(qualities).reverse().toString();
		}

		nextSequence = new Sequence(this, sequence, qualities, record.getReadName());

	}


	public File getFile() {
		return file;
	}

	@Override
	public void close() throws IOException {
		if (!closed) {
			closed = true;
			try {
				if (br != null) {
					br.close();
				}
			} finally {
				fis.close();
			}
		}
	}

}
