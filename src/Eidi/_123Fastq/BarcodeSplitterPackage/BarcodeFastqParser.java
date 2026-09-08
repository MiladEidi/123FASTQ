package Eidi._123Fastq.BarcodeSplitterPackage;

import Eidi._123Fastq.TrimFactoryPackage.ConcatGZIPInputStream;
import Eidi._123Fastq.TrimFactoryPackage.FastqRecord;
import Eidi._123Fastq.TrimFactoryPackage.PositionTrackingInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.ZipInputStream;
import javax.swing.JProgressBar;
import org.itadaki.bzip2.BZip2InputStream;

public class BarcodeFastqParser {

    private PositionTrackingInputStream posTrackInputStream;
    private BufferedReader reader;
    private FastqRecord current;
    private long fileLength;
    private AtomicBoolean atEOF;
    private JProgressBar progBar;

    public BarcodeFastqParser(JProgressBar ProgressBar) {
        this.atEOF = new AtomicBoolean();
        this.progBar = ProgressBar;
    }

    public void parseOne() {
        try {
            current = null;
            String name = null;
            String sequence;
            String comment = null;
            String quality;
            String line;
            line = reader.readLine();
            if (line == null) {
                atEOF.set(true);
                return;
            }
            if (line.charAt(0) == '@') {
                name = line.substring(1);
            } else {
                progBar.setString("Invalid FASTQ name line: " + line);
                throw new RuntimeException();
            }
            sequence = reader.readLine();
            if (sequence == null) {
                progBar.setString("Missing sequence line from record: " + name + " in line: " + line);
                throw new RuntimeException();
            }
            line = reader.readLine();
            if (line == null) {
                progBar.setString("Missing comment line from record: " + name + " in line: " + line);
                throw new RuntimeException();
            }
            if (line.charAt(0) == '+') {
                comment = line.substring(1);
            } else {
                progBar.setString("Invalid FASTQ comment line: " + line);
                throw new RuntimeException();
            }
            quality = reader.readLine();
            if (quality == null) {
                progBar.setString("Missing quality line from record: " + name + " in line: " + line);
                throw new RuntimeException();
            }
            current = new FastqRecord(name, sequence, comment, quality, progBar);
        } catch (IOException | RuntimeException ex) {
            current = new FastqRecord("Interrupt", "", "", "");
        }
    }

    public int getProgress() {
        if (atEOF.get()) {
            return 100;
        }
        long bytesRead = posTrackInputStream.getPosition();
        return (int) (((float) bytesRead / fileLength) * 100);
    }

    public void parse(File file) throws InterruptedException {
        String name = file.getName();
        try {
            fileLength = file.length();
            posTrackInputStream = new PositionTrackingInputStream(new FileInputStream(file));
            InputStream contentInputStream = posTrackInputStream;
            if (name.toLowerCase().endsWith(".gz")) {
                contentInputStream = new ConcatGZIPInputStream(posTrackInputStream);
            } else if (name.toLowerCase().endsWith(".bz2")) {
                contentInputStream = new BZip2InputStream(posTrackInputStream, false);
            } else if (name.toLowerCase().endsWith(".zip")) {
                contentInputStream = new ZipInputStream(posTrackInputStream);
            }
            reader = new BufferedReader(new InputStreamReader(contentInputStream), 32768);
            parseOne();
        } catch (Exception ex) {
            progBar.setString("Input file has a problem!");
        }
    }

    public void close() throws IOException {
        reader.close();
    }

    public boolean hasNext() {
        return (current != null);
    }

    public FastqRecord next() throws IOException, InterruptedException {
        FastqRecord current = this.current;
        parseOne();
        return current;
    }
}
