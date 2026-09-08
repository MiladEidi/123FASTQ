package Eidi._123Fastq.QualityControlPackage.Sequence;

import Eidi._123Fastq.QualityControlPackage.Utilities.CasavaBasename;
import Eidi._123Fastq.QualityControlPackage.Utilities.NameFormatException;
import java.io.File;
import java.io.IOException;

public class SequenceFileGroup implements SequenceFile {

    private File[] files;
    private SequenceFile sequenceFile;
    private File groupFile;
    private int currentIndex = 0;

    public SequenceFileGroup(File[] files) throws IOException, SequenceFormatException {
        this.files = files;
        sequenceFile = SequenceFactory.getSequenceFile(files[0]);
        String baseName;
        try {

            baseName = CasavaBasename.getCasavaBasename(sequenceFile.name());
            if (sequenceFile.getFile().getParent() == null) {
                groupFile = new File(baseName);
            } else {
                groupFile = new File(sequenceFile.getFile().getParent() + "/" + baseName);
            }
        } catch (NameFormatException nfe) {
            groupFile = sequenceFile.getFile();
        }
    }

    public File getFile() {
        return groupFile;
    }

    public int getPercentComplete() {
        return ((100 * currentIndex) / files.length)
                + (sequenceFile.getPercentComplete() / files.length);
    }

    public boolean hasNext() {
        if (sequenceFile.hasNext()) {
            return true;
        } else {
            while (currentIndex < files.length - 1) {
                ++currentIndex;
                try {
                    sequenceFile = SequenceFactory.getSequenceFile(files[currentIndex]);
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
                if (sequenceFile.hasNext()) {
                    break;
                }
            }
            return sequenceFile.hasNext();
        }
    }

    public boolean isColorspace() {
        return sequenceFile.isColorspace();
    }

    public String name() {
        return groupFile.getName();
    }

    public Sequence next() throws SequenceFormatException {
        return sequenceFile.next();
    }
}
