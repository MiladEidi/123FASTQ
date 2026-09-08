package Eidi._123Fastq.TrimFactoryPackage;

import Eidi._123Fastq.GUI.Statics;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;

public class BlockOfWork implements Callable<BlockOfRecords> {

    private javax.swing.JTextArea messenger;
    private javax.swing.JProgressBar progbar;
    private Trimmer trimmers[];
    private BlockOfRecords bor;
    private boolean pe;
    private PairingValidator pv;
    private static int earlierFinished;
    private static int allEntered;
    private static int bothSurvived;
    private static int survivedF;
    private static int survivedR;

    public BlockOfWork(JProgressBar progBar, JTextArea messenger, Trimmer trimmers[], BlockOfRecords bor, boolean pe) {
        this.messenger = messenger;
        this.progbar = progBar;
        this.trimmers = trimmers;
        this.bor = bor;
        this.pe = pe;
    }

        public BlockOfWork(JProgressBar progBar, JTextArea messenger, Trimmer trimmers[], BlockOfRecords bor, boolean pe, PairingValidator pv) {
        this.messenger = messenger;
        this.progbar = progBar;
        this.trimmers = trimmers;
        this.bor = bor;
        this.pe = pe;
        this.pv = pv;
    }

    @Override
    public BlockOfRecords call() throws Exception {

        int BothLOCAL = 0;
        int ForLocal = 0;
        int RevLocal = 0;

        if (pe) {
            List<FastqRecord> originalRecs1 = bor.getOriginalRecs1();
            List<FastqRecord> originalRecs2 = bor.getOriginalRecs2();
            int len1 = originalRecs1.size();
            int len2 = originalRecs2.size();

            if (len1 == 0 && len2 != 0) {
                earlierFinished = 1;
//                                List<List<FastqRecord>> trimmedRecs = new ArrayList<>();
//                for (int i = 0; i < 4; i++) {
//                    trimmedRecs.add(null);
//                }
//                bor.setTrimmedRecs(trimmedRecs);
//                return bor;
            } else if (len2 == 0 && len1 != 0) {
                earlierFinished = 2;
//                                List<List<FastqRecord>> trimmedRecs = new ArrayList<>();
//                for (int i = 0; i < 4; i++) {
//                    trimmedRecs.add(null);
//                }
//                bor.setTrimmedRecs(trimmedRecs);
//                return bor;
            }

            if (len1 == 0 && len2 == 0) {
                List<List<FastqRecord>> trimmedRecs = new ArrayList<>();
                for (int i = 0; i < 4; i++) {
                    trimmedRecs.add(null);
                }
                bor.setTrimmedRecs(trimmedRecs);
                return bor;
            }

            int len = len1 < len2 ? len1 : len2;
            allEntered += len;

            if (allEntered % 100000 == 0) {
                    progbar.setString(Statics.formatter.format(allEntered * 2) + " Reads were processed... ");
            }

            FastqRecord originalRecs[] = new FastqRecord[2];
            List<FastqRecord> trimmedRecs1P = new ArrayList<>();
            List<FastqRecord> trimmedRecs1U = new ArrayList<>();
            List<FastqRecord> trimmedRecs2P = new ArrayList<>();
            List<FastqRecord> trimmedRecs2U = new ArrayList<>();
            for (int i = 0; i < len; i++) {
                originalRecs[0] = originalRecs1.get(i);
                originalRecs[1] = originalRecs2.get(i);
                if (originalRecs[0].getName().equals("Corrupted read") || originalRecs[1].getName().equals("Corrupted read")) {
                    continue;
                }
                if (pv != null) {
                    pv.validatePair(originalRecs[0] , originalRecs[1]);
                }
                FastqRecord recs[] = originalRecs;
                for (int j = 0; j < trimmers.length; j++) {
                    try {
                        recs = trimmers[j].processRecords(recs, messenger);
                    } catch (RuntimeException e) {
                        messenger.append("\nException processing reads: \n" + originalRecs[0].getName() + "\n and \n" + originalRecs[1].getName() + "\n");
                        throw e;
                    }
                }
                if (recs[0] != null && recs[1] != null) {
                    BothLOCAL++;
                    trimmedRecs1P.add(recs[0]);
                    trimmedRecs2P.add(recs[1]);
                } else if (recs[0] != null) {
                    ForLocal++;
                    trimmedRecs1U.add(recs[0]);
                } else if (recs[1] != null) {
                    RevLocal++;
                    trimmedRecs2U.add(recs[1]);
                }
            }
            List<List<FastqRecord>> trimmedRecsList = new ArrayList<>();
            trimmedRecsList.add(trimmedRecs1P);
            trimmedRecsList.add(trimmedRecs1U);
            trimmedRecsList.add(trimmedRecs2P);
            trimmedRecsList.add(trimmedRecs2U);
            bor.setTrimmedRecs(trimmedRecsList);

        } else //SE mode
        {
            List<FastqRecord> originalRecsL = bor.getOriginalRecs1();
            int len = originalRecsL.size();
            allEntered += len;

            if (allEntered % 100000 == 0) {
                    progbar.setString(Statics.formatter.format(allEntered) + " Reads were processed... ");
                }

            if (len == 0) {
                List<List<FastqRecord>> trimmedRecs = new ArrayList<>();
                for (int i = 0; i < 4; i++) {
                    trimmedRecs.add(null);
                }
                bor.setTrimmedRecs(trimmedRecs);
                return bor;
            }
            FastqRecord originalRecs[] = new FastqRecord[1];
            List<FastqRecord> trimmedRecs = new ArrayList<>();
            for (int i = 0; i < len; i++) {
                originalRecs[0] = originalRecsL.get(i);
                FastqRecord recs[] = originalRecs;
                if (originalRecs[0].getName().equals("Corrupted read")) {
                    continue;
                }
                for (int j = 0; j < trimmers.length; j++) {
                    try {
                        recs = trimmers[j].processRecords(recs, messenger);
                    } catch (RuntimeException e) {
                        messenger.append("\nException processing read: \n" + originalRecs[0].getName() + "\n");
                        throw e;
                    }
                }
                if (recs[0] != null) {
                    ForLocal++;
                    trimmedRecs.add(recs[0]);
                }
            }
            List<List<FastqRecord>> trimmedRecsList = new ArrayList<>();
            trimmedRecsList.add(trimmedRecs);
            bor.setTrimmedRecs(trimmedRecsList);
        }

        bothSurvived += BothLOCAL;
        survivedF += ForLocal;
        survivedR += RevLocal;
        return bor;
    }

    public int getBothSurvived() {
        int tempBoth = bothSurvived;
        bothSurvived = 0;
        return tempBoth;
    }

    public int getSurvivedF() {
        int tempF = survivedF;
        survivedF = 0;
        return tempF;
    }

    public int getSurvivedR() {
        int tempR = survivedR;
        survivedR = 0;
        return tempR;
    }

    public int getAllEntered() {
        int tempAll = allEntered;
        allEntered = 0;
        return tempAll;
    }

    public int getEarlierFinished() {
        int tmp = earlierFinished;
        earlierFinished = 0;
        return tmp;
    }

    public static void resetStatics() {
        allEntered = 0;
        bothSurvived = 0;
        survivedF = 0;
        survivedR = 0;
        earlierFinished = 0;
    }
}
