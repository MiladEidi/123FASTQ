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

public class PairedEndTerminal extends Thread {

    private int allReads;
    private int bothSurvived;
    private int survivedReadsF;
    private int survivedReadsR;
    private boolean interruptionFlag;
    private boolean anomaly;

    public PairedEndTerminal() {
    }

    public void processSingleThreaded(JProgressBar progBar, JTextArea messenger,
            FastqParser parser1, FastqParser parser2, FastqSerializer serializer1P,
            FastqSerializer serializer1U, FastqSerializer serializer2P, FastqSerializer serializer2U,
            Trimmer[] TrimmerQueue, PairingValidator pairingValidator, boolean deleteComments) {
        try {
            FastqRecord originalRecs[] = new FastqRecord[2];
            FastqRecord recs[] = new FastqRecord[2];
            progBar.setString(Statics.formatter.format(allReads) + " Reads were processed...");

            while (parser1.hasNext() && parser2.hasNext() && !Thread.currentThread().isInterrupted()) {
                allReads++;
                originalRecs[0] = recs[0] = parser1.next();
                originalRecs[1] = recs[1] = parser2.next();
                if (originalRecs[0].getName().equals("Corrupted read") || originalRecs[1].getName().equals("Corrupted read")) {
                    continue;
                }
                if (allReads % 100000 == 0) {
                    progBar.setValue(parser1.getProgress());
                    progBar.setString(Statics.formatter.format(allReads * 2) + " Reads were processed... ");
                }
                if (pairingValidator != null) {
                    pairingValidator.validatePair(recs[0], recs[1]);
                }
                try {
                    for (int i = 0; i < TrimmerQueue.length; i++) {
                        recs = TrimmerQueue[i].processRecords(recs, messenger);
                    }
                } catch (Exception e) {
                    messenger.append("Exception processing reads: \n" + originalRecs[0].getName() + "\n and \n"
                            + originalRecs[1].getName() + "\n\n");
                    interruptionFlag = true;
                    throw e;
                }
                if (deleteComments) {
                    if (recs[0] != null && recs[1] != null) {
                        serializer1P.digestedWriteRecord(recs[0]);
                        serializer2P.digestedWriteRecord(recs[1]);
                        bothSurvived++;
                    } else if (recs[0] != null) {
                        serializer1U.digestedWriteRecord(recs[0]);
                        survivedReadsF++;
                    } else if (recs[1] != null) {
                        serializer2U.digestedWriteRecord(recs[1]);
                        survivedReadsR++;
                    }
                } else {
                    if (recs[0] != null && recs[1] != null) {
                        serializer1P.writeRecord(recs[0]);
                        serializer2P.writeRecord(recs[1]);
                        bothSurvived++;
                    } else if (recs[0] != null) {
                        serializer1U.writeRecord(recs[0]);
                        survivedReadsF++;
                    } else if (recs[1] != null) {
                        serializer2U.writeRecord(recs[1]);
                        survivedReadsR++;
                    }
                }
            }
            progBar.setValue(parser1.getProgress());
            if (Thread.currentThread().isInterrupted()) {
                interruptionFlag = true;
                return;
            }
            if (anomaly) {
                if (parser1.hasNext()) {
                    messenger.append("End of forward file was reached, but some lines in reverse file remain.\n\n");
                } else if (parser2.hasNext()) {
                    messenger.append("End of reverse file was reached, but some lines in forward file remain.\n\n");
                }
            } else {
                if (parser2.hasNext()) {
                    messenger.append("End of forward file was reached, but some lines in reverse file remain.\n\n");
                } else if (parser1.hasNext()) {
                    messenger.append("End of reverse file was reached, but some lines in forward file remain.\n\n");
                }
            }
            double pairedSurvivedRate = ((bothSurvived * 100.0) / allReads);
            double overalSurviveRate = ((((bothSurvived * 2) + survivedReadsF + survivedReadsR) * 100.0) / (allReads * 2));
            progBar.setString(Statics.formatter.format(allReads * 2) + " Reads were processed");
            if (anomaly) {
                if (parser2.getErrorCounter() > 0) {
                    if (parser2.getErrorCounter() == 1) {
                        messenger.append(parser2.getErrorCounter() + " forward file error were detected:\n");
                    } else if (parser2.getErrorCounter() > 1) {
                        messenger.append(parser2.getErrorCounter() + " forward file errors were detected. Listed below:\n");
                    }
                    parser2.printErrors();
                }
                if (parser1.getErrorCounter() > 0) {
                    if (parser1.getErrorCounter() == 1) {
                        messenger.append(parser1.getErrorCounter() + " reverse file error were detected:\n");
                    } else if (parser1.getErrorCounter() > 1) {
                        messenger.append(parser1.getErrorCounter() + " reverse file errors were detected. Listed below:\n");
                    }
                    parser1.printErrors();
                }
            } else {
                if (parser1.getErrorCounter() > 0) {
                    if (parser1.getErrorCounter() == 1) {
                        messenger.append(parser1.getErrorCounter() + " forward file error were detected:\n");
                    } else if (parser1.getErrorCounter() > 1) {
                        messenger.append(parser1.getErrorCounter() + " forward file errors were detected. Listed below:\n");
                    }
                    parser1.printErrors();
                }
                if (parser2.getErrorCounter() > 0) {
                    if (parser2.getErrorCounter() == 1) {
                        messenger.append(parser2.getErrorCounter() + " reverse file error were detected:\n");
                    } else if (parser2.getErrorCounter() > 1) {
                        messenger.append(parser2.getErrorCounter() + " reverse file errors were detected. Listed below:\n");
                    }
                    parser2.printErrors();
                }
            }
            if (bothSurvived == 0 && survivedReadsF == 0 && survivedReadsR == 0) {
                JOptionPane.showMessageDialog(null, "Are you sure there isn't a problem? all your input reads (if they were reads) thrown away.", "Warning", JOptionPane.OK_OPTION);
            }
            messenger.append("\nAll processed reads(F & R reads) : " + Statics.formatter.format(allReads * 2) + "\n");
            messenger.append("Survived reads of both files : " + Statics.formatter.format(bothSurvived * 2) + "\n");
            messenger.append("Survived reads of Forward file only (written in Forward Unpairs file) : " + Statics.formatter.format(survivedReadsF) + "\n");
            messenger.append("Survived reads of Reverse file only (written in Reverse Unpairs file) : " + Statics.formatter.format(survivedReadsR) + "\n");
            messenger.append("All dropped reads(F & R reads) : " + Statics.formatter.format((allReads * 2) - ((bothSurvived * 2) + survivedReadsF + survivedReadsR)) + "\n\n");
            Statics.percentFormatter.setRoundingMode(RoundingMode.DOWN);
            messenger.append("Paired Survive Rate: " + Statics.percentFormatter.format(pairedSurvivedRate) + " %\n");
            messenger.append("Overal Survive Rate: " + Statics.percentFormatter.format(overalSurviveRate) + " %\n\n");
        } catch (IOException | InterruptedException e) {
            interruptionFlag = true;
        }
    }

    public void processMultiThreaded(JProgressBar progBar, JTextArea messenger,
            FastqParser parser1, FastqParser parser2, FastqSerializer serializer1P,
            FastqSerializer serializer1U, FastqSerializer serializer2P, FastqSerializer serializer2U,
            Trimmer[] TrimmerQueue, PairingValidator pairingValidator, int threads, boolean deleteComments) {
        try {
            ArrayBlockingQueue<List<FastqRecord>> parser1Queue = new ArrayBlockingQueue<>(threads);
            ArrayBlockingQueue<List<FastqRecord>> parser2Queue = new ArrayBlockingQueue<>(threads);
            ArrayBlockingQueue<Runnable> taskQueue = new ArrayBlockingQueue<>(threads);
            ArrayBlockingQueue<Future<BlockOfRecords>> serializerQueue1P = new ArrayBlockingQueue<>(threads);
            ArrayBlockingQueue<Future<BlockOfRecords>> serializerQueue1U = new ArrayBlockingQueue<>(threads);
            ArrayBlockingQueue<Future<BlockOfRecords>> serializerQueue2P = new ArrayBlockingQueue<>(threads);
            ArrayBlockingQueue<Future<BlockOfRecords>> serializerQueue2U = new ArrayBlockingQueue<>(threads);
            ParserWorker parserWorker1 = new ParserWorker(progBar, parser1, parser1Queue, true);
            ParserWorker parserWorker2 = new ParserWorker(progBar, parser2, parser2Queue, false);
            Thread parser1Thread = new Thread(parserWorker1);
            Thread parser2Thread = new Thread(parserWorker2);
            ThreadPoolExecutor taskExec = new ThreadPoolExecutor(threads, threads, 0, TimeUnit.SECONDS, taskQueue);
            SerializerWorker serializerWorker1P = new SerializerWorker(serializer1P, serializerQueue1P, 0, deleteComments);
            SerializerWorker serializerWorker1U = new SerializerWorker(serializer1U, serializerQueue1U, 1, deleteComments);
            SerializerWorker serializerWorker2P = new SerializerWorker(serializer2P, serializerQueue2P, 2, deleteComments);
            SerializerWorker serializerWorker2U = new SerializerWorker(serializer2U, serializerQueue2U, 3, deleteComments);
            Thread serializer1PThread = new Thread(serializerWorker1P);
            Thread serializer1UThread = new Thread(serializerWorker1U);
            Thread serializer2PThread = new Thread(serializerWorker2P);
            Thread serializer2UThread = new Thread(serializerWorker2U);
            parser1Thread.start();
            parser2Thread.start();
            serializer1PThread.start();
            serializer1UThread.start();
            serializer2PThread.start();
            serializer2UThread.start();
            boolean done1 = false, done2 = false;
            List<FastqRecord> recs1 = null;
            List<FastqRecord> recs2 = null;
            BlockOfWork work = null;
            BlockOfRecords bor = null;
            while (!done1 && !done2 && !Thread.currentThread().isInterrupted()) {
                if (!done1) {
                    recs1 = null;
                    while (recs1 == null) {
                        recs1 = parser1Queue.poll(1, TimeUnit.SECONDS);
                    }
                    if (recs1.isEmpty()) {
                        done1 = true;
                    }
                }
                if (!done2) {
                    recs2 = null;
                    while (recs2 == null) {
                        recs2 = parser2Queue.poll(1, TimeUnit.SECONDS);
                    }
                    if (recs2.isEmpty()) {
                        done2 = true;
                    }
                }
                bor = new BlockOfRecords(recs1, recs2);
                work = new BlockOfWork(progBar, messenger, TrimmerQueue, bor, true, pairingValidator);
                while (taskQueue.remainingCapacity() < 1) {
                    Thread.sleep(100);
                }
                Future<BlockOfRecords> future = taskExec.submit(work);
                serializerQueue1P.put(future);
                serializerQueue1U.put(future);
                serializerQueue2P.put(future);
                serializerQueue2U.put(future);
            }
            //need better handling
            if (parserWorker1.isComplete() && !parserWorker2.isComplete()) {
                parser1.close();
                parser2.close();
                taskExec.shutdown();
                taskExec.awaitTermination(1, TimeUnit.HOURS);
            } else {
                parser1Thread.join();
                parser2Thread.join();
                parser1.close();
                parser2.close();
                taskExec.shutdown();
                taskExec.awaitTermination(1, TimeUnit.HOURS);
                serializer1PThread.join();
                serializer1UThread.join();
                serializer2PThread.join();
                serializer2UThread.join();
            }

            if (Thread.currentThread().isInterrupted()) {
                interruptionFlag = true;
                return;
            }
            allReads = work.getAllEntered();
            bothSurvived = work.getBothSurvived();
            survivedReadsF = work.getSurvivedF();
            survivedReadsR = work.getSurvivedR();
            double pairedSurvivedRate = ((bothSurvived * 100.0) / allReads);
            double overalSurviveRate = ((((bothSurvived * 2) + survivedReadsF + survivedReadsR) * 100.0) / (allReads * 2));
            progBar.setString(Statics.formatter.format(allReads * 2) + " Reads were processed");
            int Earlier = work.getEarlierFinished();
            if (Earlier != 0) {
                if (anomaly) {
                    if (Earlier == 2) {
                        messenger.append("End of forward file was reached, but some lines in reverse file remain.\n\n");
                    } else if (Earlier == 1) {
                        messenger.append("End of reverse file was reached, but some lines in forward file remain.\n\n");
                    }
                } else {
                    if (Earlier == 1) {
                        messenger.append("End of forward file was reached, but some lines in reverse file remain.\n\n");
                    } else if (Earlier == 2) {
                        messenger.append("End of reverse file was reached, but some lines in forward file remain.\n\n");
                    }
                }
            }
            if (anomaly) {
                if (parser2.getErrorCounter() > 0) {
                    if (parser2.getErrorCounter() == 1) {
                        messenger.append(parser2.getErrorCounter() + " forward file error were detected:\n");
                    } else if (parser2.getErrorCounter() > 1) {
                        messenger.append(parser2.getErrorCounter() + " forward file errors were detected. Listed below:\n");
                    }
                    parser2.printErrors();
                }
                if (parser1.getErrorCounter() > 0) {
                    if (parser1.getErrorCounter() == 1) {
                        messenger.append(parser1.getErrorCounter() + " reverse file error were detected:\n");
                    } else if (parser1.getErrorCounter() > 1) {
                        messenger.append(parser1.getErrorCounter() + " reverse file errors were detected. Listed below:\n");
                    }
                    parser1.printErrors();
                }
            } else {
                if (parser1.getErrorCounter() > 0) {
                    if (parser1.getErrorCounter() == 1) {
                        messenger.append(parser1.getErrorCounter() + " forward file error were detected:\n");
                    } else if (parser1.getErrorCounter() > 1) {
                        messenger.append(parser1.getErrorCounter() + " forward file errors were detected. Listed below:\n");
                    }
                    parser1.printErrors();
                }
                if (parser2.getErrorCounter() > 0) {
                    if (parser2.getErrorCounter() == 1) {
                        messenger.append(parser2.getErrorCounter() + " reverse file error were detected:\n");
                    } else if (parser2.getErrorCounter() > 1) {
                        messenger.append(parser2.getErrorCounter() + " reverse file errors were detected. Listed below:\n");
                    }
                    parser2.printErrors();
                }
            }
            if (bothSurvived == 0 && survivedReadsF == 0 && survivedReadsR == 0) {
                JOptionPane.showMessageDialog(null, "Are you sure there isn't a problem? all your input reads (if they were reads) thrown away.", "Warning", JOptionPane.OK_OPTION);
            }
            messenger.append("\nAll processed reads(F & R reads) : " + Statics.formatter.format(allReads * 2) + "\n");
            messenger.append("Survived reads of both files : " + Statics.formatter.format(bothSurvived * 2) + "\n");
            messenger.append("Survived reads of Forward file only (written in Forward Unpairs file) : " + Statics.formatter.format(survivedReadsF) + "\n");
            messenger.append("Survived reads of Reverse file only (written in Reverse Unpairs file) : " + Statics.formatter.format(survivedReadsR) + "\n");
            messenger.append("All dropped reads(F & R reads) : " + Statics.formatter.format((allReads * 2) - ((bothSurvived * 2) + survivedReadsF + survivedReadsR)) + "\n\n");
            Statics.percentFormatter.setRoundingMode(RoundingMode.DOWN);
            messenger.append("Paired Survive Rate: " + Statics.percentFormatter.format(pairedSurvivedRate) + " %\n");
            messenger.append("Overal Survive Rate: " + Statics.percentFormatter.format(overalSurviveRate) + " %\n\n");
        } catch (InterruptedException | IOException e) {
            interruptionFlag = true;
        }
    }

    public void process(TrimmerMaker params, JProgressBar progBar, JTextArea messenger, File input1, File input2,
            File output1P, File output1U, File output2P, File output2U, boolean validatePairing, int threads, boolean deleteComments) {
        try {
            if (threads == 1) {
                messenger.append("Paired-End Mode of 123Fastq Trimmer Was Started in only one thread.\n");
                messenger.append("Speed up the trimming by using multi-thread option.\n\n");
            } else {
                messenger.append("Paired-End Mode of 123Fastq Trimmer Was Started in " + threads + " threads.\n\n");
            }
            int phredOffset = params.getSelectedPhred();
            FastqParser parser1 = new FastqParser(messenger, phredOffset);
            FastqParser parser2 = new FastqParser(messenger, phredOffset);
            if (input1.length() > input2.length()) {
                parser1.parse(input2);
                parser2.parse(input1);
                anomaly = true;
            } else {
                parser1.parse(input1);
                parser2.parse(input2);
            }
            if (phredOffset == 0) {
                int phred1 = parser1.determinePhredOffset();
                int phred2 = parser2.determinePhredOffset();
                if (phred1 == phred2 && phred1 != 0) {
                    messenger.append("Quality encoding detected as phred" + phred1 + ".\n\n");
                    parser1.setPhredOffset(phred1);
                    parser2.setPhredOffset(phred1);
                    phredOffset = phred1;
                } else {
                    messenger.append("Error: Unable to detect quality encoding.\n\n");
                    interruptionFlag = true;
                    return;
                }
            } else {
                messenger.append("Phred " + phredOffset + " selected as quality encoding format.\n\n");
            }
            Trimmer[] TrimmerQueue = params.Trimmers();
            FastqSerializer serializer1P = new FastqSerializer(messenger);
            serializer1P.open_Messenger(output1P);
            FastqSerializer serializer1U = new FastqSerializer(messenger);
            serializer1U.open_Messenger(output1U);
            FastqSerializer serializer2P = new FastqSerializer(messenger);
            serializer2P.open_Messenger(output2P);
            FastqSerializer serializer2U = new FastqSerializer(messenger);
            serializer2U.open_Messenger(output2U);
            PairingValidator pairingValidator = null;
            if (validatePairing) {
                pairingValidator = new PairingValidator(messenger);
            }
            if (threads == 1) {
                processSingleThreaded(progBar, messenger, parser1, parser2, serializer1P, serializer1U, serializer2P, serializer2U, TrimmerQueue, pairingValidator, deleteComments);
            } else {
                processMultiThreaded(progBar, messenger, parser1, parser2, serializer1P, serializer1U, serializer2P, serializer2U, TrimmerQueue,
                        pairingValidator, threads, deleteComments);
            }
            serializer1P.close();
            serializer1U.close();
            serializer2P.close();
            serializer2U.close();
            if (params.isReportSheet() && interruptionFlag != true) {
                reportMaker(input1, input2, output1P, params, phredOffset);
            }
            parser1.close();
            parser2.close();
        } catch (InterruptedException | IOException ex) {
            interruptionFlag = true;
        }
    }

    public void reportMaker(File Input1, File Input2, File output, TrimmerMaker params, int phred) throws IOException {
        String BaseName1 = getCore(Input1.getAbsolutePath());
        String BaseName2 = getCore(Input2.getAbsolutePath());
        String BaseName;

        if (BaseName1 == null && BaseName2 != null) {
            BaseName = BaseName2;
        } else if (BaseName2 == null && BaseName1 != null) {
            BaseName = BaseName1;
        } else if (BaseName1 == null && BaseName2 == null) {
            BaseName = null;
        } else if (BaseName1.equals(BaseName2)) {
            BaseName = BaseName1;
        } else {
            BaseName = BaseName1 + "_" + BaseName2;
        }
        File report;
        if (BaseName == null) {
            report = new File(output.getParent() + "/Paired_End_Trim_Report.txt");
        } else {
            report = new File(output.getParent() + "/" + BaseName + "_Trim_Report.txt");
        }
        FileWriter write = new FileWriter(report);
        try (PrintWriter printer = new PrintWriter(write)) {
            String DateLog = new SimpleDateFormat("EEE d MMM yyyy").format(Calendar.getInstance().getTime());
            String TimeLog = new SimpleDateFormat("HH:mm").format(Calendar.getInstance().getTime());
            printer.println("                      Trimmer Report by 123Fastq");
            printer.println("                             " + DateLog);
            printer.println("                                 " + TimeLog);
            printer.println("__________________________________________________________________________");
            printer.println("\n");
            printer.println("Mode: Paired-End");
            if (BaseName == null) {
                printer.println("Input files: " + Input1.getName() + " and " + Input2.getName());
            } else if (BaseName1.equals(BaseName2)) {
                printer.println("Input files: " + BaseName + " group");
            } else {
                printer.println("Input files: " + BaseName1 + " and " + BaseName2);
            }
            printer.println("Input file encoding: " + phred + " Phred Scale");
            printer.println("\n");
            double pairedSurvivedRate = ((bothSurvived * 100.0) / allReads);
            double overalSurviveRate = ((((bothSurvived * 2) + survivedReadsF + survivedReadsR) * 100.0) / (allReads * 2));
            printer.println("\nAll processed reads(F & R reads) : " + Statics.formatter.format(allReads * 2) + "\n");
            printer.println("Survived reads of both files : " + Statics.formatter.format(bothSurvived * 2) + "\n");
            printer.println("Survived reads of Forward file only (written in Forward Unpairs file) : " + Statics.formatter.format(survivedReadsF) + "\n");
            printer.println("Survived reads of Reverse file only (written in Reverse Unpairs file) : " + Statics.formatter.format(survivedReadsR) + "\n");
            printer.println("All dropped reads(F & R reads) : " + Statics.formatter.format((allReads * 2) - ((bothSurvived * 2) + survivedReadsF + survivedReadsR)) + "\n\n");
            Statics.percentFormatter.setRoundingMode(RoundingMode.DOWN);
            printer.println("Paired Survive Rate: " + Statics.percentFormatter.format(pairedSurvivedRate) + " %\n");
            printer.println("Overal Survive Rate: " + Statics.percentFormatter.format(overalSurviveRate) + " %\n\n");
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
                printer.println("  Minimum matches between the two 'adapter ligated' reads: " + params.getMinPalindromeLikelihood());
                printer.println("  Minimum adapter length in each read: " + params.getMinAdapterLen());
                if (params.getKeepBoth() == true) {
                    printer.println("  Reverse reads were kept.");
                } else {
                    printer.println("  Reverse reads were discarded.");
                }
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

    public static int run(Pathways pathPE, TrimmerMaker params, JTextArea messenger, JProgressBar progBar) {
        int threads = params.getThreadNO();
        boolean validatePairs = pathPE.getPairValidator();
        File inputs[], outputs[];
        inputs = new File[2];
        inputs[0] = new File(pathPE.getImportPath1());
        inputs[1] = new File(pathPE.getImportPath2());
        outputs = new File[4];
        outputs[0] = new File(pathPE.getFP());
        outputs[1] = new File(pathPE.getFU());
        outputs[2] = new File(pathPE.getRP());
        outputs[3] = new File(pathPE.getRU());
        PairedEndTerminal PE = new PairedEndTerminal();
        PE.process(params, progBar, messenger, inputs[0], inputs[1], outputs[0], outputs[1], outputs[2], outputs[3], validatePairs, threads, params.getDeleteComments());
        if (PE.interruptionFlag) {
            progBar.setString("Trimming Stopped...");
            messenger.append("\nTrimming Stopped!\n");
            BlockOfWork.resetStatics();
            return 0;
        }
        messenger.append("Paired-End Trimming Completed successfully!\n");
        return 1;
    }

    public String coreFinder(String str, String first, String second) {
        int idx = str.lastIndexOf(first);
        if (idx == -1) {
            idx = str.lastIndexOf(second);
            if (idx == -1) {
                return null;
            }
        }
        return str.substring(0, idx);
    }

    public String getCore(String inputPath) {
        String specifiers[][] = {{"_R1_", "_R2_"}, {"_f", "_r"}, {".f", ".r"}, {"_1", "_2"}, {".1", ".2"}, {"_R1", "_R2"}};
        File fileBase = new File(inputPath);
        String baseName = FilenameUtils.removeExtension(fileBase.getName());
        for (String pair[] : specifiers) {
            String core = coreFinder(baseName, pair[0], pair[1]);
            if (core != null) {
                return core;
            }
        }
        return null;
    }
}
