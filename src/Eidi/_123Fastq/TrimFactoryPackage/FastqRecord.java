package Eidi._123Fastq.TrimFactoryPackage;

import javax.swing.JProgressBar;
import javax.swing.JTextArea;

public class FastqRecord {

    private String name;
    private String sequence;
    private String comment;
    private String quality;
    private String barcodeLabel = null;
    private int phredOffset;
    private int headPos;
    private JTextArea messenger;
    private JProgressBar progBar;


    public FastqRecord(String name, String sequence, String comment, String quality, JProgressBar ProgBar) {
        try {
            this.name = name;
            this.sequence = sequence;
            this.comment = comment;
            this.quality = quality;
            this.progBar = ProgBar;

            headPos = 0;

            if (sequence.length() != quality.length()) {
                progBar.setString("Sequence and quality length don't match: '" + sequence + "' vs '" + quality + "'\n");
                throw new RuntimeException();
            }
        } catch (RuntimeException e) {
        }
    }

    public FastqRecord(String name, String sequence, String comment, String quality, JTextArea Messenger) {
        try {
            this.name = name;
            this.sequence = sequence;
            this.comment = comment;
            this.quality = quality;
            this.messenger = Messenger;
            headPos = 0;
            if (sequence.length() != quality.length()) {
                messenger.append("Sequence and quality length don't match: '" + sequence + "' vs '" + quality + "'");
                throw new RuntimeException();
            }
        } catch (RuntimeException e) {
        }
    }

    public FastqRecord(String name, String sequence, String comment, String quality) {
        this.name = name;
        this.sequence = sequence;
        this.comment = comment;
        this.quality = quality;
    }

    public FastqRecord(String name, String sequence, String comment, String quality, int phredOffset, JTextArea Messenger) {
        try {
            this.name = name;
            this.sequence = sequence;
            this.comment = comment;
            this.quality = quality;
            this.phredOffset = phredOffset;
            this.messenger = Messenger;
            headPos = 0;
        } catch (RuntimeException ex) {
        }
    }

    public FastqRecord(FastqRecord base, int headPos, int length, JTextArea Messenger) {
        try {
            this.messenger = Messenger;
            if (headPos < 0) {
                messenger.append("Attempting invalid trim on " + base.name + " with length " + base.sequence.length() + ":\n Wanted " + headPos + " to " + (headPos + length) + "\n");
                throw new RuntimeException();
            }
            int availableLength = base.getSequence().length();
            if (headPos + length > availableLength) {
                length = availableLength - headPos;
            }
            this.sequence = base.sequence.substring(headPos, headPos + length);
            this.quality = base.quality.substring(headPos, headPos + length);
            this.name = base.name;
            this.comment = base.comment;
            this.phredOffset = base.phredOffset;
            this.headPos = base.headPos + headPos;
            this.barcodeLabel = base.barcodeLabel;
        } catch (RuntimeException e) {
        }
    }

    public FastqRecord(FastqRecord base, int headPos, int length, JProgressBar progbar) {
        try {
            this.progBar = progbar;

            if (headPos < 0) {
                progBar.setString("Attempting invalid trim on " + base.name + " with length " + base.sequence.length() + ": Wanted " + headPos + " to " + (headPos + length));
                throw new RuntimeException();
            }

            int availableLength = base.getSequence().length();
            if (headPos + length > availableLength) {
                length = availableLength - headPos;
            }

            this.sequence = base.sequence.substring(headPos, headPos + length);
            this.quality = base.quality.substring(headPos, headPos + length);
            this.name = base.name;
            this.comment = base.comment;
            this.phredOffset = base.phredOffset;
            this.headPos = base.headPos + headPos;

            this.barcodeLabel = base.barcodeLabel;
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    public FastqRecord(FastqRecord base, String sequence, String quality, int phredOffset) {
        this.sequence = sequence;
        this.quality = quality;
        this.name = base.name;
        this.comment = base.comment;
        this.headPos = base.headPos;
        this.phredOffset = phredOffset;

        this.barcodeLabel = base.barcodeLabel;
    }

    public String getName() {
        return name;
    }

    public String getSequence() {
        return sequence;
    }

    public String getBarcodeLabel() {
        return barcodeLabel;
    }

    public void setBarcodeLabel(String barcodeLabel) {
        this.barcodeLabel = barcodeLabel;
    }

    public String getComment() {
        return comment;
    }

    public String getQuality() {
        return quality;
    }

    public int getPhredOffset() {
        return phredOffset;
    }

    void setPhredOffset(int phredOffset) {
        this.phredOffset = phredOffset;
    }

    public int getHeadPos() {
        return headPos;
    }

    public int[] getQualityAsInteger(boolean zeroNs) {
        int arr[] = new int[quality.length()];
        for (int i = 0; i < quality.length(); i++) {
            if (zeroNs && sequence.charAt(i) == 'N') {
                arr[i] = 0;
            } else {
                arr[i] = quality.charAt(i) - phredOffset;
            }
        }
        return arr;
    }
}
