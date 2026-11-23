package Eidi._123Fastq.ConvertorsPackage.SamToFastq;

import Eidi._123Fastq.QualityControlPackage.Sequence.*;
import Eidi._123Fastq.TrimFactoryPackage.FastqRecord;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Locale;
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

        return toFastqRecord(record);
    }

    public static FastqRecord toFastqRecord(SAMRecord record) {
        String sequence = record.getReadString();
        String qualities = record.getBaseQualityString();

        if (record.getReadNegativeStrandFlag()) {
            sequence = reverseComplement(sequence);
            qualities = new StringBuilder(qualities).reverse().toString();
        }
        return new FastqRecord(record.getReadName(), sequence, "", qualities);
    }

    public static String reverseComplement(String sequence) {
        String bases = "ACGTRYSWKMBDHVN";
        String complements = "TGCAYRSWMKVHDBN";
        String upper = sequence.toUpperCase(Locale.ROOT);
        StringBuilder result = new StringBuilder(sequence.length());
        for (int i = upper.length() - 1; i >= 0; i--) {
            char base = upper.charAt(i);
            int index = bases.indexOf(base);
            result.append(index < 0 ? base : complements.charAt(index));
        }
        return result.toString();
    }

    public File getFile() {
        return file;
    }
}
