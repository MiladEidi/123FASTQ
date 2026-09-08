package Eidi._123Fastq.ConvertorsPackage.SamToFastq;

import Eidi._123Fastq.QualityControlPackage.Sequence.*;
import Eidi._123Fastq.TrimFactoryPackage.FastqRecord;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import javax.swing.JProgressBar;
import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMRecord;

public class SamFileParser {

    private File file;
    private long fileSize = 0;
    private FileInputStream fis;
    private SAMFileReader br;
    private String name;
    private Sequence nextSequence = null;
    private Iterator<SAMRecord> it;
    private JProgressBar progBar;

    public SamFileParser(File file, JProgressBar progBar) throws SequenceFormatException, IOException {
        this.file = file;
        fileSize = file.length();
        name = file.getName();
        SAMFileReader.setDefaultValidationStringency(SAMFileReader.ValidationStringency.SILENT);
        fis = new FileInputStream(file);
        br = new SAMFileReader(fis);
        it = br.iterator();
        this.progBar = progBar;
    }

    public String name() {
        return name;
    }

    public int getPercentComplete() {
        if (!hasNext()) {
            return 100;
        }
        try {
            int percent = (int) (((double) fis.getChannel().position() / fileSize) * 100);
            return percent;
        } catch (Exception e) {
        }
        return 0;
    }

    public boolean isColorspace() {
        return false;
    }

    public boolean hasNext() {
        return it.hasNext();
    }

    public FastqRecord next() throws SequenceFormatException {
        FastqRecord X = readNext();
        return X;
    }

    public FastqRecord readNext() {

        SAMRecord record = null;
        try {

            record = it.next();

        } catch (Exception e) {
            progBar.setString("The input file has a problem. (Incorrect format)");
            return new FastqRecord("Interrupt", "", "", "");
        }

        String sequence = record.getReadString();
        String qualities = record.getBaseQualityString();

        if (record.getReadNegativeStrandFlag()) {
            sequence = reverseComplement(sequence);
            qualities = reverse(qualities);
        }
        return new FastqRecord(record.getReadName(), sequence, "", qualities);
    }

    private String reverseComplement(String sequence) {

        char[] letters = reverse(sequence).toUpperCase().toCharArray();
        char[] rc = new char[letters.length];
        for (int i = 0; i < letters.length; i++) {
            switch (letters[i]) {
                case 'G':
                    rc[i] = 'C';
                    break;
                case 'A':
                    rc[i] = 'T';
                    break;
                case 'T':
                    rc[i] = 'A';
                    break;
                case 'C':
                    rc[i] = 'G';
                    break;
                default:
                    rc[i] = letters[i];
            }
        }
        return new String(rc);
    }

    private String reverse(String sequence) {
        char[] starting = sequence.toCharArray();
        char[] reversed = new char[starting.length];
        for (int i = 0; i < starting.length; i++) {
            reversed[reversed.length - (1 + i)] = starting[i];
        }
        return new String(reversed);
    }

    public File getFile() {
        return file;
    }
}
