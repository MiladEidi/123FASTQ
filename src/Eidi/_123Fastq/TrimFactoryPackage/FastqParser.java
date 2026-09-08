package Eidi._123Fastq.TrimFactoryPackage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;
import java.util.zip.ZipInputStream;
import javax.swing.JTextArea;
import org.itadaki.bzip2.BZip2InputStream;

public class FastqParser {

    private static final int PREREAD_COUNT = 10000;
    private int phredOffset;
    private ArrayDeque<FastqRecord> deque;
    int qualHistogram[];
    int patternHistogram[];
    private PositionTrackingInputStream posTrackInputStream;
    private BufferedReader reader;
    private FastqRecord current;
    private long fileLength;
    private AtomicBoolean atEOF;
    private JTextArea messenger;
    private LinkedList<String> Errors = new LinkedList<>();
    private int errorCounter;
    private int notRecords;

    public FastqParser(int phredOffset) {
        this.phredOffset = phredOffset;
        deque = new ArrayDeque<>(PREREAD_COUNT);
        this.atEOF = new AtomicBoolean();
    }

    public FastqParser(JTextArea Messenger, int phredOffset) {
        this.phredOffset = phredOffset;
        deque = new ArrayDeque<>(PREREAD_COUNT);
        this.atEOF = new AtomicBoolean();
        this.messenger = Messenger;
    }

    public void setPhredOffset(int phredOffset) {
        this.phredOffset = phredOffset;
        if (current != null) {
            current.setPhredOffset(phredOffset);
        }
    }

    public void parseOne() {
        try {
            current = null;
            String name;
            String sequence = null;
            String comment = null;
            String quality = null;
            String line;
            line = reader.readLine();
            if (line == null) {
                atEOF.set(true);
                return;
            }
            if (line.charAt(0) == '@') {
                name = line.substring(1);
                sequence = reader.readLine();
                if (sequence != null) {
                    line = reader.readLine();
                    if (line == null) {
                        Errors.add("Missing comment line from record: " + name + " in line: " + line + "\n");
                        errorCounter++;
                        current = new FastqRecord("Corrupted read", sequence, comment, quality, phredOffset, messenger);
                        errorHandler();
                    } else if (line.charAt(0) == '+') {
                        comment = line.substring(1);
                        quality = reader.readLine();
                        if (quality == null) {
                            Errors.add("Missing quality line from record: " + name + " in line: " + line + "\n");
                            errorCounter++;
                            current = new FastqRecord("Corrupted read", sequence, comment, quality, phredOffset, messenger);
                            return;
                        }
                        if (sequence.length() != quality.length()) {
                            Errors.add("Sequence and quality length don't match: '" + sequence + "'\n vs \n'" + quality + "'\n");
                            errorCounter++;
                            current = new FastqRecord("Corrupted read", sequence, comment, quality, phredOffset, messenger);
                            return;
                        }
                        current = new FastqRecord(name, sequence, comment, quality, phredOffset, messenger); //healthy read
                    } else {
                        Errors.add("Invalid FASTQ comment line: " + line + "\n");
                        errorCounter++;
                        current = new FastqRecord("Corrupted read", sequence, comment, quality, phredOffset, messenger);
                        errorHandler();
                    }
                } else {
                    Errors.add("Missing sequence line from record: " + name + " in line: " + line + "\n");
                    errorCounter++;
                    current = new FastqRecord("Corrupted read", sequence, comment, quality, phredOffset, messenger);
                    errorHandler();
                }
            } else {
                Errors.add("Invalid FASTQ name line: " + line + "\n");
                errorCounter++;
                current = new FastqRecord("Corrupted read", sequence, comment, quality, phredOffset, messenger);
                errorHandler();

            }
        } catch (IOException | RuntimeException ex) {
        }
    }

    public void errorHandler() throws IOException {
        String line;
        reader.mark(2000);
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            line = reader.readLine();
            if (line.charAt(0) == '@') {
                line = reader.readLine();
                if (Pattern.matches("([ATCGN]+)", line.toUpperCase())) {
                    reader.reset();
                    break;
                }
            }
            if (line == null) {
                atEOF.set(true);
                return;
            }
            reader.mark(2000);
        }
    }

    public void printErrors() {
        Errors.forEach((Error) -> {
            messenger.append(Error);
        });
    }

    public int getProgress() {
        if (atEOF.get()) {
            return 100;
        }
        long bytesRead = posTrackInputStream.getPosition();
        return (int) (((float) bytesRead / fileLength) * 100);
    }

    public long getFileLength() {
        return fileLength;
    }

    private void accumulateHistogram(FastqRecord rec) {
        int quals[] = rec.getQualityAsInteger(false);
        for (int i : quals) {
            qualHistogram[i]++;
        }
    }

    public int determinePhredOffset() {
        int phred33Total = 0;
        int phred64Total = 0;

        for (int i = 33; i <= 58; i++) {
            phred33Total += qualHistogram[i];
        }
        for (int i = 80; i <= 104; i++) {
            phred64Total += qualHistogram[i];
        }
        if (phred33Total == 0 && phred64Total > 0) {
            return 64;
        }
        if (phred64Total == 0 && phred33Total > 0) {
            return 33;
        }
        return 0;
    }

    public void parse(File file) throws InterruptedException {
        String name = file.getName();
        try {
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
            } catch (FileNotFoundException e) {
                messenger.append("\nInput file has a problem!\n\n");
            }
            if (phredOffset == 0) {
                deque.clear();
                qualHistogram = new int[256];
                for (int i = 0; i < PREREAD_COUNT; i++) {
                    parseOne();
                    if (current != null) {
                        deque.add(current);
                        if (!current.getName().equals("Corrupted read")) {
                            accumulateHistogram(current);
                        } else {
                            notRecords++;
                            if (notRecords >= 20) { //20 can be variable.
                                messenger.append("Input file seems not a Fastq file. At least, 20 file errors at the beginning of file!\n\n");
                                throw new RuntimeException();
                            }
                        }
                    }
                }
            }
            parseOne();
        } catch (IOException ex) {
            messenger.append("Input " + name + " file has a problem.\nProblem in .gz compression of this file.\n\n");
        }
    }

    public void close() throws IOException {
        reader.close();
    }

    public boolean hasNext() {
        return (!deque.isEmpty()) || (current != null);
    }

    public FastqRecord next() throws IOException, InterruptedException {
        if (deque.isEmpty()) {
            FastqRecord current = this.current;
            parseOne();
            if (current.getName().equals("Corrupted read")) {
                notRecords++;
                if (notRecords >= 60) { //60 can be variable.
                    messenger.append("More than 60 file errors have been detected. Trimming Failed. \n\n");
                    throw new RuntimeException();
                }
            }
            return current;
        } else {
            FastqRecord rec = deque.poll();
            if (rec != null) {
                rec.setPhredOffset(phredOffset);
            }
            return rec;
        }
    }

    public int getErrorCounter() {
        return errorCounter;
    }
}
