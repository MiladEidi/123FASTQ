package Eidi._123Fastq.ConvertorsPackage.Fast5ToFastq;

import Eidi._123Fastq.GUI.Statics;
import Eidi._123Fastq.QualityControlPackage.Sequence.SequenceFormatException;
import Eidi._123Fastq.TrimFactoryPackage.*;
import java.io.File;
import java.io.IOException;
import javax.swing.JProgressBar;

public class Fast5ToFastqTerminal extends Thread {

    public Fast5ToFastqTerminal() {
    }

    private int allReads = 0;

    public void process(JProgressBar progBar, File[] input, File output)
            throws IOException, SequenceFormatException, InterruptedException {

        progBar.setString("Initialization...");
        FastqSerializer serializer = new FastqSerializer(progBar);
        serializer.open_Progbar(output);
        int fileNo = input.length;
        int x = 0;
        for (int i = 0; i < fileNo; i++) {
            x++;
            Fast5FileParser parser = new Fast5FileParser();
            try {
                parser.Fast5FileParse(input[i]);
            } catch (Exception e) {
                if (fileNo == 1) {
                    progBar.setString("The input file has a problem. (Incorrect format)");
                } else {
                    progBar.setString("The input files have problem. (Incorrect format)");
                }
                return;
            }
            if (!parser.hasNext()) {
                progBar.setString("We couldn't extract read from " + input[i].getName() + " file.");
                throw new RuntimeException();
            }
            FastqRecord recs[] = new FastqRecord[1];
            while (parser.hasNext() && !Thread.currentThread().isInterrupted()) {
                recs[0] = parser.next();
                try {
                    if (recs[0] != null) {
                        allReads++;
                        serializer.writeRecordFromFast5(recs[0]);
                    }
                    progBar.setString(Statics.formatter.format(allReads) + " Reads were Converted...");
                    progBar.setValue(x * 100 / fileNo);
                } catch (RuntimeException e) {
                    progBar.setString("Exception processing read: " + recs[0].getName() + "\n");
                    throw e;
                }
            }
            if (Thread.currentThread().isInterrupted()) {
                return;
            }
        }
        serializer.close();
        if (allReads == 1) {
            progBar.setString(Statics.formatter.format(allReads) + " Fast5 file Converted To a Fastq File. Conversion Successfully Finished!");
        } else {
            progBar.setString(Statics.formatter.format(allReads) + " Fast5 files Converted To a Fastq File. Conversion Successfully Finished!");
        }
    }

    public static void run(File[] files, String outut, JProgressBar progBar) throws IOException, SequenceFormatException, InterruptedException {
        File output = new File(outut);
        Fast5ToFastqTerminal tm = new Fast5ToFastqTerminal();
        tm.process(progBar, files, output);
    }
}
