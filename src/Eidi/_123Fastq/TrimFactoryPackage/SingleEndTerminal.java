package Eidi._123Fastq.TrimFactoryPackage;

import Eidi._123Fastq.GUI.Statics;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import org.apache.commons.io.FilenameUtils;

public class SingleEndTerminal extends Thread {

    public SingleEndTerminal() {
    }

    private int allReads = 0;
    private int survivedReads = 0;
    private boolean interruptionFlag = false;

    public void processSingleThreaded(JProgressBar progBar, JTextArea messenger, FastqParser parser, FastqSerializer serializer,
            Trimmer[] TrimmerQueue, boolean deleteComments) {
        try {
            FastqRecord recs[] = new FastqRecord[1];
            FastqRecord originalRecs[] = new FastqRecord[1];
            progBar.setString(Statics.formatter.format(allReads) + " Reads were processed...");
            while (parser.hasNext() && !Thread.currentThread().isInterrupted()) {
                allReads++;
                originalRecs[0] = recs[0] = parser.next();
                if (recs[0].getName().equals("Corrupted read")) {
                    continue;
                }
                if (allReads % 100000 == 0) {
                    progBar.setValue(parser.getProgress());
                    progBar.setString(Statics.formatter.format(allReads) + " Reads were processed...");
                }
                try {
                    for (int i = 0; i < TrimmerQueue.length; i++) {
                        recs = TrimmerQueue[i].processRecords(recs, messenger);
                        if (recs == null) {
                            break;
                        }
                    }
                    if (recs[0] != null) {
                        survivedReads++;
                        if(deleteComments){
                        serializer.digestedWriteRecord(recs[0]);
                        }else{
                        serializer.writeRecord(recs[0]);
                        }
                    }
                } catch (IOException e) {
                    messenger.append("\nException processing read: \n" + originalRecs[0].getName() + "\n\n");
                    interruptionFlag = true;
                    throw e;
                }
            }
            progBar.setValue(parser.getProgress());
            if (Thread.currentThread().isInterrupted()) {
                interruptionFlag = true;
                return;
            }
            progBar.setString(Statics.formatter.format(allReads) + " Reads were processed");
            if (parser.getErrorCounter() > 0) {
                if (parser.getErrorCounter() == 1) {
                    messenger.append("\n" + parser.getErrorCounter() + " input file error were detected:\n");
                } else {
                    messenger.append("\n" + parser.getErrorCounter() + " input file errors were detected. Listed below:\n");
                }
                parser.printErrors();
            }
            if (survivedReads == 0) {
                JOptionPane.showMessageDialog(null, "Are you sure there isn't a problem? all your input reads (if they were reads) thrown away.", "Warning", JOptionPane.OK_OPTION);
            }
            messenger.append("\nAll Processed Reads: " + Statics.formatter.format(allReads) + "\n");
            messenger.append("Survived Reads: " + Statics.formatter.format(survivedReads) + "\n");
            messenger.append("Dropped Reads: " + Statics.formatter.format(allReads - survivedReads) + "\n\n");
            Statics.percentFormatter.setRoundingMode(RoundingMode.DOWN);
            double SurviveRate = ((survivedReads * 100.0) / allReads);
            messenger.append("Survive Rate: " + Statics.percentFormatter.format(SurviveRate) + " %\n\n");
        } catch (IOException | InterruptedException e) {
            interruptionFlag = true;
        }
    }

    public void processMultiThreaded(JProgressBar progBar, JTextArea messenger, FastqParser parser, FastqSerializer serializer,
            Trimmer[] TrimmerQueue, int threads, boolean deleteComments) {
        try {
            progBar.setString(Statics.formatter.format(0) + " Reads were processed...");
            ArrayBlockingQueue<List<FastqRecord>> parserQueue = new ArrayBlockingQueue<>(threads);
            ArrayBlockingQueue<Runnable> taskQueue = new ArrayBlockingQueue<>(threads * 2);
            ArrayBlockingQueue<Future<BlockOfRecords>> serializerQueue = new ArrayBlockingQueue<>(threads * 5);
            ParserWorker parserWorker = new ParserWorker(progBar, parser, parserQueue, true);
            Thread parserThread = new Thread(parserWorker);
            ThreadPoolExecutor taskExec = new ThreadPoolExecutor(threads, threads, 0, TimeUnit.SECONDS, taskQueue);
            SerializerWorker serializerWorker = new SerializerWorker(serializer, serializerQueue, 0, deleteComments);
            Thread serializerThread = new Thread(serializerWorker);
            parserThread.start();
            serializerThread.start();
            boolean done = false;
            List<FastqRecord> recs1 = null;
            BlockOfWork work = null;
            while (!done && !Thread.currentThread().isInterrupted()) {
                recs1 = null;
                while (recs1 == null) {
                    recs1 = parserQueue.poll(1, TimeUnit.SECONDS);
                }
                if (recs1.isEmpty()) {
                    done = true;
                }
                BlockOfRecords bor = new BlockOfRecords(recs1, null);
                work = new BlockOfWork(progBar, messenger, TrimmerQueue, bor, false);
                while (taskQueue.remainingCapacity() < 1) {
                    Thread.sleep(100);
                }
                Future<BlockOfRecords> future = taskExec.submit(work);
                serializerQueue.put(future);
            }
            if (Thread.currentThread().isInterrupted()) {
                interruptionFlag = true;
                return;
            }
            parserThread.join();
            parser.close();
            taskExec.shutdown();
            taskExec.awaitTermination(1, TimeUnit.HOURS);
            serializerThread.join();
            allReads = work.getAllEntered();
            survivedReads = work.getSurvivedF();
            double SurviveRate = ((survivedReads * 100.0) / allReads);
            progBar.setString(Statics.formatter.format(allReads) + " Reads were processed");
            if (parser.getErrorCounter() > 0) {
                if (parser.getErrorCounter() == 1) {
                    messenger.append("\n" + parser.getErrorCounter() + " input file error were detected:\n");
                } else {
                    messenger.append("\n" + parser.getErrorCounter() + " input file errors were detected. Listed below:\n");
                }
                parser.printErrors();
            }
            if (survivedReads == 0) {
                JOptionPane.showMessageDialog(null, "Are you sure there isn't a problem? all your input reads (if they were reads) thrown away.", "Warning", JOptionPane.OK_OPTION);
            }
            messenger.append("\nAll Processed Reads: " + Statics.formatter.format(allReads) + "\n");
            messenger.append("Survived Reads: " + Statics.formatter.format(survivedReads) + "\n");
            messenger.append("Dropped Reads: " + Statics.formatter.format(allReads - survivedReads) + "\n\n");
            Statics.percentFormatter.setRoundingMode(RoundingMode.DOWN);
            messenger.append("Survive Rate: " + Statics.percentFormatter.format(SurviveRate) + " %\n\n");
        } catch (InterruptedException | IOException ex) {
            interruptionFlag = true;
        }
    }

    public void process(TrimmerMaker params, JProgressBar progBar, JTextArea messenger, Trimmer[] TrimmerQueue, File input, File output, int phredOffset, int threads) {
        try {
            if (threads == 1) {
                messenger.append("Single-End Mode Of 123Fastq Trimmer Was Started in one thread.\nSpeed up trimming by using multi-thread option.\n\n");
            } else {
                messenger.append("Single-End Mode Of 123Fastq Trimmer Was Started in " + threads + " threads.\n\n");
            }
            FastqParser parser = new FastqParser(messenger, phredOffset);
            parser.parse(input);
            if (phredOffset == 0) {
                int phred = parser.determinePhredOffset();
                phredOffset = phred;
                if (phred != 0) {
                    messenger.append("Quality encoding detected as phred" + phred + ".\n");
                    parser.setPhredOffset(phred);
                } else {
                    messenger.append("Error: Unable to detect quality encoding.\n Select quality encoding your own and run trimmer again.\n");
                    interruptionFlag = true;
                    throw new RuntimeException();
                }
            } else {
                messenger.append("Phred " + phredOffset + " selected as quality encoding format.\n");
            }
            FastqSerializer serializer = new FastqSerializer(messenger);
            serializer.open_Messenger(output);
            if (threads == 1) {
                processSingleThreaded(progBar, messenger, parser, serializer, TrimmerQueue, params.getDeleteComments());
            } else {
                processMultiThreaded(progBar, messenger, parser, serializer, TrimmerQueue, threads, params.getDeleteComments() );
            }
            serializer.close();
            if (params.isReportSheet() && interruptionFlag != true) {
                reportMaker(input, output, params, phredOffset);
            }
        } catch (InterruptedException | RuntimeException | IOException ex) {
            interruptionFlag = true;
        }
    }

    public void reportMaker(File Input, File output, TrimmerMaker params, int phred) throws IOException {

        String outputName = FilenameUtils.removeExtension(Input.getName());
        File report = new File(output.getParent() + "/" + outputName + "_Trim_Report.txt");
        FileWriter write = new FileWriter(report);
        try (PrintWriter printer = new PrintWriter(write)) {
            String DateLog = new SimpleDateFormat("EEE d MMM yyyy").format(Calendar.getInstance().getTime());
            String TimeLog = new SimpleDateFormat("HH:mm").format(Calendar.getInstance().getTime());
            printer.println("                      Trimmer Report by 123Fastq");
            printer.println("                             " + DateLog);
            printer.println("                                 " + TimeLog);
            printer.println("__________________________________________________________________________");
            printer.println("\n");
            printer.println("Mode: Single-End");
            printer.println("Input file: " + Input.getName());
            printer.println("Input file encoding: " + phred + " Phred Scale");
            printer.println("\n");
            printer.println("All processed reads: " + Statics.formatter.format(allReads));
            printer.println("Survived Reads: " + Statics.formatter.format(survivedReads));
            printer.println("Dropped Reads: " + Statics.formatter.format(allReads - survivedReads));
            double SurviveRate = ((survivedReads * 100.0) / allReads);
            printer.println("Survive Rate: " + Statics.percentFormatter.format(SurviveRate) + " %");
            printer.println("\n");
            printer.println("__________________________________________________________________________");
            printer.println("                       Trim Parameters");
            printer.println("\n");

            if (params.getAdapterSeq() != null) {
                printer.println("* Adapter sequence: " + params.getAdapterSeq());
            }
            if (params.getSeedMaxMiss() != null) {
                printer.println("* Adapter sequences in the " + params.getAdapterFileName() + " file were used to find adapter contamination.");
                printer.println("  Minimum matches to detect adapter contamination: " + params.getMinSequenceLikelihood());
                printer.println("  Maximum mismatches to detect adapter contamination: " + params.getSeedMaxMiss());
            }
            if (params.getHeadCrop() != null && params.getHeadCrop() != 0) {
                printer.println("* " + params.getHeadCrop() + " bases have cutted from 5' of all reads.");
            }
            if (params.getEndCrop() != null && params.getEndCrop() != 0) {
                printer.println("* " + params.getEndCrop() + " bases have cutted from 3' of all reads that were longer than " + params.getReadLenEndCropThreshold() + " bases.");
            }
            if (params.getEndGRepeat() != null) {
                printer.println("* All the G repeats at 3' ends more than "
                        + params.getEndGRepeat() + " were trimmed.");
            }
            if (params.getEndRepeat() != null) {
                printer.println("* All the repeats(A or T or C or G) at 3' ends more than "
                        + params.getEndGRepeat() + " were trimmed.");
            }
            if (params.getLeading() != null && params.getLeading() != 0) {
                printer.println("* 5' bases of reads were trimmed to get a base with " + params.getLeading() + " quality threshold.");
            }
            if (params.getTrailing() != null && params.getTrailing() != 0) {
                printer.println("* 3' bases of reads were trimmed to get a base with " + params.getTrailing() + " quality threshold.");
            }
            if (params.getEachWindowLen() != null) {
                printer.println("* A " + params.getEachWindowLen() + " bases sliding window scanned all the reads from 5' to 3' ends,");
                printer.println("  when the quality mean of a window was lower than " + Math.round(params.getMeanQualEachWindow()) + ", rest of bases of that read were discarded.");
            }
            if (params.getTargetLen() != null) {
                printer.println("* To maximise the value of each read, all the reads shorter than " + params.getTargetLen()
                        + " were hardly punished for their errors.");
                printer.println("  Degree of strictness was " + params.getStrictness() + " (0 to 1).");
            }
            if (params.getMaxLen() != null) {
                printer.println("* All the reads longer than " + params.getMaxLen() + " bases were dropped.");
            }
            if (params.getMinLen() != null) {
                printer.println("* All the reads shorter than " + params.getMinLen() + " bases were dropped.");
            }
            if (params.getAvgQual() != null) {
                printer.println("* All the reads that have quality mean lower than " + params.getAvgQual() + " were dropped.");
            }
            if (params.getMaxGC() != null) {
                printer.println("* All the reads that have GC content more than " + params.getMaxGC() + "% were dropped.");
            }
            if (params.getMinGC() != null) {
                printer.println("* All the reads that have GC content lower than " + params.getMaxGC() + "% were dropped.");
            }
            if (params.getPhredConvertor() != null) {
                if (params.getPhredConvertor() == 33) {
                    printer.println("* Reads quality encoding were coverted to Phred 33.");
                } else if (params.getPhredConvertor() == 64) {
                    printer.println("* Reads quality encoding were coverted to Phred 64.");
                }
            }
            printer.println("\n");
            printer.println("--------------------------------------------------------------------------");
            printer.println("Easier way!");
            printer.println("123Fastq");
        }
    }

    public static int run(Pathways path, TrimmerMaker params, JTextArea messenger, JProgressBar progBar) throws IOException {
        int phredOffset = params.getSelectedPhred();
        int threads = params.getThreadNO();
        File input = new File(path.getImportPath1());
        File output = new File(path.getExportPath());
        Trimmer[] TrimmerQueue = params.Trimmers();
        SingleEndTerminal SE = new SingleEndTerminal();
        SE.process(params, progBar, messenger, TrimmerQueue, input, output, phredOffset, threads);
        if (SE.interruptionFlag) {
            progBar.setString("Trimming Stopped...");
            messenger.append("\nTrimming Stopped!\n");
            BlockOfWork.resetStatics();
            return 0;
        }
        messenger.append("Trim Cascade Completed successfully!\n");
        return 1;
    }
}
