package Eidi._123Fastq.BarcodeSplitterPackage;

import Eidi._123Fastq.GUI.Statics;
import Eidi._123Fastq.TrimFactoryPackage.FastqRecord;
import Eidi._123Fastq.TrimFactoryPackage.FastqSerializer;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import org.apache.commons.io.FilenameUtils;

public class BarcodeTerminal {

    private BufferedReader reader;
    private JProgressBar progBar;
    private String outputParentDirectory;
    private HashMap<aPatternBitapFactory, FastqSerializer> outputFiles = new HashMap<>();
    private LinkedList<aPatternBitapFactory> PatternList;
    private FastqSerializer unmatchesSerializer;
    private int penalty;
    private int Mode;
    private File mainFastqFile;
    private int allReads;
    private int trashes;
    private int percent;

    public BarcodeTerminal(JProgressBar progressBar, BarcodeParameterCollector X) {
        this.progBar = progressBar;
        this.mainFastqFile = new File(X.getFastqFilePath());
        this.Mode = X.getMode3or5();
        this.outputParentDirectory = X.getOutputPath() + "/";
        this.penalty = X.getPenalty();
    }

    public LinkedList<aPatternBitapFactory> barcodeParser(File file) throws IOException {
        try {
            reader = new BufferedReader(new InputStreamReader(new BufferedInputStream(new FileInputStream(file), 1000000)));
            PatternList = new LinkedList<>();
            String Line;
            int lineNumber = 0;
            while ((Line = reader.readLine()) != null) {
                lineNumber++;
                if (Line.startsWith("#")) {
                    continue;
                }
                if (Line.trim().length() == 0) {
                    continue;
                }
                String[] sections = Line.split("\\t+");
                if (sections.length != 2) {
                    progBar.setString("Expected 2 sections for each sample's barcode but got " + sections.length + " from line " + lineNumber);
                    throw new RuntimeException();
                }
                sections[1] = sections[1].toUpperCase();
                if (!Pattern.matches("([ATCG]+)", sections[1])) {
                    progBar.setString("Barcode sequences should contain only A, T, C or G. An inconsistency on Line " + lineNumber + ".");
                    throw new RuntimeException();
                }
                aPatternBitapFactory pattern = new aPatternBitapFactory(sections[0], sections[1], penalty, Mode);
                PatternList.add(pattern);
            }
            reader.close();
            return PatternList;
        } catch (Exception e) {
            reader.close();
            return null;
        }
    }

    public void FileMaker(LinkedList<aPatternBitapFactory> barcodeList) {
        barcodeList.forEach((BR) -> {
            String outputFileName = BR.getPatternName() + ".fq";
            File outputFile = new File(outputParentDirectory + outputFileName);
            FastqSerializer outputFileSerializer = new FastqSerializer(progBar);
            outputFileSerializer.open_Progbar(outputFile);
            outputFiles.put(BR, outputFileSerializer);
        });
        File unmatchesOutputFile = new File(outputParentDirectory + "Undetermines.fq");
        unmatchesSerializer = new FastqSerializer(progBar);
        unmatchesSerializer.open_Progbar(unmatchesOutputFile);
    }

    public void process(BarcodeParameterCollector X) throws IOException, InterruptedException {
        BarcodeFastqParser input = new BarcodeFastqParser(progBar);
        input.parse(mainFastqFile);
        LinkedList<aPatternBitapFactory> barcodeList = barcodeParser(new File(X.getBarcodeFilePath()));
        if (barcodeList == null) {
            return;
        }
        FileMaker(barcodeList);
        FastqRecord[] recs = new FastqRecord[1];
        boolean found = false;
        while (input.hasNext() && !Thread.currentThread().isInterrupted()) {
            recs[0] = input.next();
            if (recs[0].getName().equals("Interrupt")) {
                progBar.setString("The input Fastq file has a problem. (Incorrect format)");
                return;
            }
            allReads++;
            switch (Mode) {
                case 5:
                    HashMap<Map.Entry<aPatternBitapFactory, FastqSerializer>, Map.Entry<Integer, Long>> sum5 = new HashMap<>();
                    for (Map.Entry<aPatternBitapFactory, FastqSerializer> barcodeRecord : outputFiles.entrySet()) {
                        //search for each barcode return best leftmost match
                        Map.Entry<Integer, Long> PositionBit = barcodeRecord.getKey().bitapFuzzyMatch5Ends(recs[0].getSequence());
                        if (PositionBit != null) {
                            sum5.put(barcodeRecord, PositionBit);
                        }
                    }
                    if (!sum5.isEmpty()) {
                        found = true;
                        Map.Entry<Map.Entry<aPatternBitapFactory, FastqSerializer>, Map.Entry<Integer, Long>> BestMatchEntry = sum5.entrySet().iterator().next();
                        for (Entry<Entry<aPatternBitapFactory, FastqSerializer>, Map.Entry<Integer, Long>> entry : sum5.entrySet()) {
                            //choosing the best barcode in the matching based on bit array
                            if ((long) BestMatchEntry.getValue().getValue() > (long) entry.getValue().getValue()) {
                                BestMatchEntry = entry;
                            }
                        }
                        //add a hit for that barcode
                        PatternList.get(PatternList.indexOf(BestMatchEntry.getKey().getKey())).Hit();
                        //trim and write reads based on detected barcodes
                        if (X.isClipBarcodes()) {
                            int cutPoint = (int) BestMatchEntry.getValue().getKey() + BestMatchEntry.getKey().getKey().getPatternSequence5().length();
                            int readLen = recs[0].getSequence().length();
                            //can add a length limitter
                            if (cutPoint < readLen) {
                                recs[0] = new FastqRecord(recs[0], cutPoint, readLen - cutPoint, progBar);
                                BestMatchEntry.getKey().getValue().writeRecord(recs[0]);
                            } else {
                                trashes++;
                            }
                        } else {
                            BestMatchEntry.getKey().getValue().writeRecord(recs[0]);
                        }
                    }
                    break;
                    //hidden in the current version
                case 3:
                    HashMap<Map.Entry<aPatternBitapFactory, FastqSerializer>, Map.Entry<Integer, Long>> sum3 = new HashMap<>();
                    for (Map.Entry<aPatternBitapFactory, FastqSerializer> barcodeRecord : outputFiles.entrySet()) {
                        Map.Entry<Integer, Long> PositionBit = barcodeRecord.getKey().bitapFuzzyMatch3Ends(recs[0].getSequence());
                        if (PositionBit != null) {
                            sum3.put(barcodeRecord, PositionBit);
                        }
                    }
                    if (!sum3.isEmpty()) {
                        found = true;
                        Map.Entry<Map.Entry<aPatternBitapFactory, FastqSerializer>, Map.Entry<Integer, Long>> BestMatchEntry = sum3.entrySet().iterator().next();
                        for (Entry<Entry<aPatternBitapFactory, FastqSerializer>, Map.Entry<Integer, Long>> entry : sum3.entrySet()) {
                            if ((long) BestMatchEntry.getValue().getValue() > (long) entry.getValue().getValue()) {
                                BestMatchEntry = entry;
                            }
                        }
                        PatternList.get(PatternList.indexOf(BestMatchEntry.getKey().getKey())).Hit();
                        if (X.isClipBarcodes()) {
                            int cutPoint = (int) BestMatchEntry.getValue().getKey();
                            int readLen = recs[0].getSequence().length();
                            if (cutPoint < readLen) {
                                recs[0] = new FastqRecord(recs[0], 0, cutPoint, progBar);
                                BestMatchEntry.getKey().getValue().writeRecord(recs[0]);
                            }else{
                                trashes++;
                            }
                        } else {
                            BestMatchEntry.getKey().getValue().writeRecord(recs[0]);
                        }
                    }
                    break;
            }
            if (!found) {
                unmatchesSerializer.writeRecord(recs[0]);
            }
            found = false;
            if (allReads % 2000 == 0) {
                int progress = input.getProgress();
                if (progress >= percent + 5) {
                    percent = progress;
                    progBar.setString(Statics.formatter.format(allReads) + " Reads were processed... (" + Statics.percentFormatter2.format(percent) + " %)");
                    progBar.setValue(progress);
                }
            }
        }
        for (FastqSerializer value : outputFiles.values()) {
            value.close();
            value.deleteEmptyfiles();
        }
        unmatchesSerializer.close();
        unmatchesSerializer.deleteEmptyfiles();
        if (Thread.currentThread().isInterrupted()) {
            progBar.setString("Splitting Stopped!");
            return;
        }
        if (X.makeReport()) {
            reportMaker();
        }
        progBar.setValue(input.getProgress());
        input.close();
        progBar.setString("Splitting succesfully completed! " + Statics.formatter.format(allReads - trashes) + " reads distributed to " + (outputFiles.size() + 1 - FastqSerializer.emptyFiles) + " files.");
        FastqSerializer.emptyFiles = 0;
        if (trashes != 0) {
            JOptionPane.showMessageDialog(null, Statics.formatter.format(allReads) + " reads were processed.\n" + Statics.formatter.format(trashes) + " reads were discarded due to their small sizes.");
        }
    }

    public void reportMaker() throws IOException {
        File report = new File(outputParentDirectory + "/" + FilenameUtils.removeExtension(mainFastqFile.getName()) + "_BarcodeSplitter_Report.txt");
        FileWriter write = new FileWriter(report);
        try (PrintWriter printer = new PrintWriter(write)) {
            String DateLog = new SimpleDateFormat("EEE d MMM yyyy").format(Calendar.getInstance().getTime());
            String TimeLog = new SimpleDateFormat("HH:mm").format(Calendar.getInstance().getTime());
            printer.println("                   Barcode Splitter Report by 123Fastq");
            printer.println("                             " + DateLog);
            printer.println("                                 " + TimeLog);
            printer.println("__________________________________________________________________________");
            printer.println("\n");
            printer.println("Main Fastq File: " + mainFastqFile.getName());
            printer.println("All input reads: " + Statics.formatter.format(allReads));
            printer.print("Mode: ");
            if (Mode == 5) {
                printer.println("5' barcode exploration");
            } else {
                printer.println("3' barcode exploration");
            }
            printer.println("__________________________________________________________________________");
            printer.println("                       Report of barcodes");
            printer.println("\n");
            printer.printf("%-25s%-25s%-25s\n", "Sample name", "Number of hits ", "Percent of input reads");
            printer.println("\n");
            printer.println("--------------------------------------------------------------------------");
            double allhits = 0;
            double tAllReads = allReads;
            for (int i = 0; i < PatternList.size(); i++) {
                allhits += PatternList.get(i).getHits();
                printer.printf("%-25s%-25s%-25s\n", PatternList.get(i).getPatternName(), Statics.formatter.format(PatternList.get(i).getHits()), Statics.percentFormatter.format((PatternList.get(i).getHits() * 100) / tAllReads) + " %");
                printer.println("\n");
            }
            printer.printf("%-25s%-25s%-25s\n", "Undetermine reads", Statics.formatter.format(tAllReads - allhits), Statics.percentFormatter.format(((tAllReads - allhits) * 100) / tAllReads) + " %");
            printer.println("\n");
            printer.println("__________________________________________________________________________");
            printer.println("Hit Rate((all hits/all input reads)*100): " + Statics.percentFormatter.format((allhits * 100) / tAllReads) + " %");
            printer.println("\n");
            if (trashes != 0) {
                printer.println("Discarded reads due to small size: " + Statics.formatter.format(trashes));
            }
            printer.println("\n");
            printer.println("--------------------------------------------------------------------------");
            printer.println("Easier!");
            printer.println("123Fastq");
        }
    }

    public static void run(BarcodeParameterCollector X, JProgressBar progressBar) throws IOException, InterruptedException {
        progressBar.setString("Initialization...");
        BarcodeTerminal BP = new BarcodeTerminal(progressBar, X);
        BP.process(X);
    }
}
