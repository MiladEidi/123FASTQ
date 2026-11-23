package Eidi._123Fastq.ConvertorsPackage.Fast5ToFastq;

import Eidi._123Fastq.QualityControlPackage.Sequence.SequenceFormatException;
import Eidi._123Fastq.TrimFactoryPackage.FastqRecord;
import ch.systemsx.cisd.hdf5.HDF5Factory;
import ch.systemsx.cisd.hdf5.IHDF5SimpleReader;
import java.io.File;
import java.io.IOException;
import java.util.Locale;

public class Fast5FileParser {
    private static final String[] FASTQ_PATHS = {
        "Analyses/Basecall_2D_000/BaseCalled_template/Fastq",
        "Analyses/Basecall_2D_000/BaseCalled_2D/Fastq",
        "Analyses/Basecall_1D_000/BaseCalled_template/Fastq",
        "Analyses/Basecall_1D_000/BaseCalled_1D/Fastq"
    };

    private FastqRecord read;
    private File file;

    public void Fast5FileParse(File file) throws SequenceFormatException, IOException {
        this.file = file;
        read = null;
        try (IHDF5SimpleReader reader = HDF5Factory.openForReading(file)) {
            for (String path : FASTQ_PATHS) {
                if (reader.exists(path)) {
                    String[] lines = reader.readString(path).split("\\r?\\n");
                    if (lines.length != 4 || !lines[0].startsWith("@") || lines[0].length() < 2
                            || lines[1].isEmpty() || !lines[2].startsWith("+")
                            || lines[1].length() != lines[3].length()) {
                        throw new SequenceFormatException("Invalid embedded FASTQ in " + file);
                    }
                    read = new FastqRecord(lines[0], lines[1].toUpperCase(Locale.ROOT), lines[2], lines[3]);
                    return;
                }
            }
        }
        throw new SequenceFormatException("No embedded single-read basecall FASTQ found in " + file);
    }

    public String name() {
        return file.getName();
    }

    public int getPercentComplete() {
        return hasNext() ? 0 : 100;
    }

    public boolean hasNext() {
        return read != null;
    }

    public FastqRecord next() throws SequenceFormatException {
        FastqRecord result = read;
        read = null;
        return result;
    }

    public File getFile() {
        return file;
    }
}
