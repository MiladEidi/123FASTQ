package Eidi._123Fastq.ConvertorsPackage.SamToFastq;

import Eidi._123Fastq.GUI.Statics;
import Eidi._123Fastq.QualityControlPackage.Sequence.SequenceFormatException;
import Eidi._123Fastq.TrimFactoryPackage.*;
import java.io.File;
import java.io.IOException;
import javax.swing.JProgressBar;

public class SAMToFASTQTerminal extends Thread {

    public SAMToFASTQTerminal() {
    }

    private int allReads;
    private int percent;

    public void process(JProgressBar progBar, File input, File output)
            throws IOException, SequenceFormatException, InterruptedException {

        SamFileParser parser = new SamFileParser(input, progBar);
        if (!parser.hasNext()) {
            progBar.setString("we couldn't extract reads from input file.");
            throw new RuntimeException();
        }
        FastqSerializer serializer = new FastqSerializer(progBar);
        serializer.open_Progbar(output);
        FastqRecord recs[] = new FastqRecord[1];
        progBar.setString("Initialization...");
        while (parser.hasNext() && !Thread.currentThread().isInterrupted()) {
            recs[0] = parser.readNext();
            if (recs[0].getName().equals("Interrupt")) {
                return;
            }
            try {
                if (recs[0] != null) {
                    allReads++;
                    serializer.writeRecord(recs[0]);
                }
                if (allReads % 4000 == 0) {
                    progBar.setValue(parser.getPercentComplete());
                    if (parser.getPercentComplete() >= percent + 5) {
                        percent = parser.getPercentComplete();
                        progBar.setString(Statics.formatter.format(allReads) + " Reads were Converted... (" + Statics.percentFormatter2.format(percent) + "%)");
                    }
                }
            } catch (RuntimeException e) {
                progBar.setString("Exception processing read: " + recs[0].getName() + "\n");
                throw e;
            }
        }
        if (Thread.currentThread().isInterrupted()) {
            return;
        }
        serializer.close();
        progBar.setValue(parser.getPercentComplete());
        progBar.setString(Statics.formatter.format(allReads) + " Reads Converted To a Fastq File. Conversion Successfully Finished!");
    }

    public static void run(Pathways path, JProgressBar progBar) throws IOException, SequenceFormatException, InterruptedException {
        File input = new File(path.getImportPath1());
        File output = new File(path.getExportPath());
        SAMToFASTQTerminal tm = new SAMToFASTQTerminal();
        tm.process(progBar, input, output);
    }
}
