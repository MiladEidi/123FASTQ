package Eidi._123Fastq.QualityControlPackage.Sequence;

import Eidi._123Fastq.ConvertorsPackage.Fast5ToFastq.Fast5FileParser;
import Eidi._123Fastq.TrimFactoryPackage.FastqRecord;
import java.io.File;
import java.io.IOException;

public class Fast5File implements SequenceFile {

    private Sequence nextSequence = null;
    private File file;
    private String name;

    private void readNext() throws SequenceFormatException, IOException {
        Fast5FileParser parser = new Fast5FileParser();
        parser.Fast5FileParse(file);
        FastqRecord read = parser.next();
        nextSequence = new Sequence(this, read.getSequence(), read.getQuality(), read.getName());
    }

    protected Fast5File(File file) throws SequenceFormatException, IOException {
        this.file = file;
        name = file.getName();
        readNext();
    }

    public String name() {
        return name;
    }

    public int getPercentComplete() {
        if (!hasNext()) {
            return 100;
        }
        return 0;
    }

    public boolean isColorspace() {
        return false;
    }

    public boolean hasNext() {
        return nextSequence != null;
    }

    public Sequence next() throws SequenceFormatException {

        Sequence seq = nextSequence;
        nextSequence = null;
        return seq;
    }

    public void remove() {
        // No action here
    }

    public File getFile() {
        return file;
    }
}
