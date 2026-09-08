package Eidi._123Fastq.ConvertorsPackage.Fast5ToFastq;

import Eidi._123Fastq.QualityControlPackage.Sequence.*;
import Eidi._123Fastq.TrimFactoryPackage.FastqRecord;
import ch.systemsx.cisd.hdf5.HDF5Factory;
import ch.systemsx.cisd.hdf5.IHDF5SimpleReader;
import java.io.File;
import java.io.IOException;

public class Fast5FileParser {

    public Fast5FileParser() {
    }

    private FastqRecord Read = null;
    private File file;
    private String name;

    public void Fast5FileParse(File file) throws SequenceFormatException, IOException {
        this.file = file;
        name = file.getName();
        IHDF5SimpleReader reader = HDF5Factory.openForReading(file);
        String[] rdfPaths = new String[]{
            "Analyses/Basecall_2D_000/BaseCalled_template/Fastq",
            "Analyses/Basecall_2D_000/BaseCalled_2D/Fastq",
            "Analyses/Basecall_1D_000/BaseCalled_template/Fastq",
            "Analyses/Basecall_1D_000/BaseCalled_1D/Fastq"
        };
        boolean foundPath = false;
        for (int r = 0; r < rdfPaths.length; r++) {
            if (reader.exists(rdfPaths[r])) {
                foundPath = true;
                String fastq = reader.readString(rdfPaths[r]);
                String[] sections = fastq.split("\\n");
                if (sections.length != 4) {
                    throw new SequenceFormatException("Didn't get 4 sections from " + fastq);
                }
                Read = new FastqRecord(sections[0], sections[1].toUpperCase(), sections[2], sections[3]);
                break;
            }
        }
        reader.close();
        if (!foundPath) {
            throw new SequenceFormatException("No valid fastq paths found in " + file);
        }
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

    public boolean hasNext() {
        return Read != null;
    }

    public FastqRecord next() throws SequenceFormatException {
        FastqRecord seq = Read;
        Read = null;
        return seq;
    }

    public File getFile() {
        return file;
    }
}
