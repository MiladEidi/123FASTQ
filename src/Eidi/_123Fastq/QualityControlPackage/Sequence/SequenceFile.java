package Eidi._123Fastq.QualityControlPackage.Sequence;

import java.io.File;
import java.io.Closeable;
import java.io.IOException;

public interface SequenceFile extends Closeable {

	public boolean hasNext();
	public Sequence next() throws SequenceFormatException;
	public boolean isColorspace();
	public String name();
	public int getPercentComplete();
	public File getFile();
	@Override
	default void close() throws IOException {
	}

}
