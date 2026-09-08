package Eidi._123Fastq.QualityControlPackage.Sequence;

import ch.systemsx.cisd.hdf5.HDF5Factory;
import ch.systemsx.cisd.hdf5.IHDF5SimpleReader;
import ch.systemsx.cisd.hdf5.h5ar.ArchiveEntry;
import ch.systemsx.cisd.hdf5.h5ar.HDF5ArchiverFactory;
import ch.systemsx.cisd.hdf5.h5ar.IHDF5ArchiveReader;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.swing.JOptionPane;

public class Fast5File implements SequenceFile {

    private Sequence nextSequence = null;
    private File file;
    private String name;
//    private boolean isMultiFast5 = false;

    private void readNext() throws SequenceFormatException {

        //MultiFast5 files can be handelled in the future versions....
//        HDF5ArchiverFactory archive = new HDF5ArchiverFactory();
//        IHDF5ArchiveReader arReader = archive.openForReading(file);
//        List<ArchiveEntry> list = arReader.list();
//        for (ArchiveEntry archiveEntry : list) {
//            String tmp = archiveEntry.getPath().toLowerCase();
//            if (tmp.startsWith("/read") || tmp.startsWith("read")) {
//                isMultiFast5 = true;
//                break;
//            }
//        }
        String[] rdfPaths = new String[]{
            "Analyses/Basecall_2D_000/BaseCalled_template/Fastq",
            "Analyses/Basecall_2D_000/BaseCalled_2D/Fastq",
            "Analyses/Basecall_1D_000/BaseCalled_template/Fastq",
            "Analyses/Basecall_1D_000/BaseCalled_1D/Fastq"
        };

//        if (isMultiFast5) {
//            System.out.println("File is MultiFast5.");
//            throw new SequenceFormatException("No valid fastq paths found in " + file);
//        } else {
        String[] sections = null;
        IHDF5SimpleReader reader = HDF5Factory.openForReading(file);
        boolean foundReadPath = false;
        for (int r = 0; r < rdfPaths.length; r++) {
            if (reader.exists(rdfPaths[r])) {
                foundReadPath = true;
                String fastq = reader.readString(rdfPaths[r]);
                sections = fastq.split("\\n");
                if (sections.length != 4) {
                    throw new SequenceFormatException("Didn't get 4 sections from " + fastq);
                }
                break;
            }
        }
        try {
            nextSequence = new Sequence(this, sections[1].toUpperCase(), sections[3], sections[0]);
        } catch (NullPointerException e) {
            JOptionPane.showMessageDialog(null, "123Fastq couldn't find any read in the fast5 file!", "Error", JOptionPane.ERROR_MESSAGE);
        }
        reader.close();
        if (!foundReadPath) {
            throw new SequenceFormatException("No valid fastq paths found in " + file);
        }
//        }
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
