package Eidi._123Fastq.TrimFactoryPackage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.zip.GZIPOutputStream;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;

import org.itadaki.bzip2.BZip2OutputStream;

public class FastqSerializer {

    private BufferedWriter stream;
    private File inputFile;
    private JTextArea messenger;
    private JProgressBar progBar;
    public static int emptyFiles;

    public FastqSerializer() {
    }

    public FastqSerializer(JTextArea Messenger) {
        this.messenger = Messenger;
    }

    public FastqSerializer(JProgressBar ProgBar) {
        this.progBar = ProgBar;
    }

    public void open_Messenger(File file) {
        String name = file.getName();
        this.inputFile = file;
        try {
            OutputStream gStream = new FileOutputStream(file);
            if (name.endsWith(".gz")) {
                gStream = new GZIPOutputStream(gStream);
            } else if (name.endsWith(".bz2")) {
                gStream = new BZip2OutputStream(gStream);
            }
            stream = new BufferedWriter(new OutputStreamWriter(gStream), 32768);
        } catch (FileNotFoundException ex) {
            messenger.append("You didn't determine output file, correctly.\n");
        } catch (IOException ex) {
            messenger.append("An error occured in compression of output file.\n");
        }
    }

    public void open_Progbar(File file) {
        String name = file.getName();
        this.inputFile = file;
        try {
            OutputStream gStream = new FileOutputStream(file);
            if (name.endsWith(".gz")) {
                gStream = new GZIPOutputStream(gStream);
            } else if (name.endsWith(".bz2")) {
                gStream = new BZip2OutputStream(gStream);
            }
            stream = new BufferedWriter(new OutputStreamWriter(gStream), 32768);
        } catch (FileNotFoundException ex) {
            progBar.setString("You didn't determine Output File.");
        } catch (IOException ex) {
            progBar.setString("An error occured in compression of output file.");
        }
    }

    public void close() throws IOException {
        stream.close();
    }

    public void deleteEmptyfiles() {
        if (inputFile.length() == 0) {
            inputFile.delete();
            emptyFiles++;
        }
    }

    public void writeRecord(FastqRecord record) throws IOException {
        StringBuilder sb = new StringBuilder(500);
        sb.append('@');
        sb.append(record.getName());
        sb.append('\n');
        sb.append(record.getSequence());
        sb.append("\n+");
        sb.append(record.getComment());
        sb.append('\n');
        sb.append(record.getQuality());
        sb.append('\n');
        stream.write(sb.toString());
    }

        public void digestedWriteRecord(FastqRecord record) throws IOException {
        StringBuilder sb = new StringBuilder(500);
        sb.append('@');
        sb.append(record.getName());
        sb.append('\n');
        sb.append(record.getSequence());
        sb.append("\n+");
        sb.append('\n');
        sb.append(record.getQuality());
        sb.append('\n');
        stream.write(sb.toString());
    }

    public void writeRecordFromFast5(FastqRecord record) throws IOException {
        StringBuilder sb = new StringBuilder(500);
        sb.append(record.getName());
        sb.append('\n');
        sb.append(record.getSequence());
        sb.append('\n');
        sb.append(record.getComment());
        sb.append('\n');
        sb.append(record.getQuality());
        sb.append('\n');
        stream.write(sb.toString());
    }

    public File getInputFile() {
        return inputFile;
    }

    public void setInputFile(File file) {
        this.inputFile = file;
    }
}
