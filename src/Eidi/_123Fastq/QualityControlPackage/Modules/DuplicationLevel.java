package Eidi._123Fastq.QualityControlPackage.Modules;

import Eidi._123Fastq.QualityControlPackage.Graphs.AdapterDeduplicatedLineGraph;
import Eidi._123Fastq.QualityControlPackage.Report.HTMLReportArchive;
import Eidi._123Fastq.QualityControlPackage.Sequence.Sequence;
import java.awt.BorderLayout;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.xml.stream.XMLStreamException;

public class DuplicationLevel extends AbstractQCModule implements MergeableQCModule {

    private OverRepresentedSeqs overrepresentedModule;
    private double[] deduplicatedPercentages = null;
    private double[] totalPercentages = null;
    private double maxCount = 100;
    private double percentDifferentSeqs = 0;
    private String[] labels;
    private static final DecimalFormat df = new DecimalFormat("#.##");

    protected DuplicationLevel(OverRepresentedSeqs overrepresentedModule) {
        this.overrepresentedModule = overrepresentedModule;
    }

    public String description() {
        return "Plots the number of sequences which are duplicated to different levels";
    }

    public boolean ignoreFilteredSequences() {
        if (ModuleConfig.getParam("duplication", "ignore") > 0) {
            return true;
        }
        return false;
    }

    public boolean ignoreInReport() {
        if (ModuleConfig.getParam("duplication", "ignore") > 0) {
            return true;
        }
        return false;
    }

    protected synchronized void calculateLevels() {

        if (deduplicatedPercentages != null) {
            return;
        }

        deduplicatedPercentages = new double[16];
        totalPercentages = new double[16];

        HashMap<Integer, Integer> collatedCounts = new HashMap<>();

        Iterator<String> it = overrepresentedModule.sequences.keySet().iterator();

        while (it.hasNext()) {
            int thisCount = overrepresentedModule.sequences.get(it.next());

            if (collatedCounts.containsKey(thisCount)) {
                collatedCounts.put(thisCount, collatedCounts.get(thisCount) + 1);
            } else {
                collatedCounts.put(thisCount, 1);
            }
        }

        // Now we can correct each of these
        HashMap<Integer, Double> correctedCounts = new HashMap<>();

        Iterator<Integer> itr = collatedCounts.keySet().iterator();

        while (itr.hasNext()) {
            int dupLevel = itr.next();
            int count = collatedCounts.get(dupLevel);

            correctedCounts.put(dupLevel, getCorrectedCount(overrepresentedModule.countAtUniqueLimit, overrepresentedModule.count, dupLevel, count));

        }

        // From the corrected counts we can now work out the raw and deduplicated proportions
        double dedupTotal = 0;
        double rawTotal = 0;

        Iterator<Integer> itc = correctedCounts.keySet().iterator();

        while (itc.hasNext()) {
            int dupLevel = itc.next();
            double count = correctedCounts.get(dupLevel);

            dedupTotal += count;
            rawTotal += count * dupLevel;

            int dupSlot = dupLevel - 1;

            if (dupSlot > 9999) {
                dupSlot = 15;
            } else if (dupSlot > 4999) {
                dupSlot = 14;
            } else if (dupSlot > 999) {
                dupSlot = 13;
            } else if (dupSlot > 499) {
                dupSlot = 12;
            } else if (dupSlot > 99) {
                dupSlot = 11;
            } else if (dupSlot > 49) {
                dupSlot = 10;
            } else if (dupSlot > 9) {
                dupSlot = 9;
            }

            deduplicatedPercentages[dupSlot] += count;
            totalPercentages[dupSlot] += count * dupLevel;

        }

//		System.err.println("True total = "+overrepresentedModule.count+" inferred total is "+rawTotal+" dedup total is "+dedupTotal);
        labels = new String[16];
        for (int i = 0; i < deduplicatedPercentages.length; i++) {
            if (i < 9) {
                labels[i] = "" + (i + 1);
            } else if (i == 9) {
                labels[i] = ">10";
            } else if (i == 10) {
                labels[i] = ">50";
            } else if (i == 11) {
                labels[i] = ">100";
            } else if (i == 12) {
                labels[i] = ">500";
            } else if (i == 13) {
                labels[i] = ">1k";
            } else if (i == 14) {
                labels[i] = ">5k";
            } else if (i == 15) {
                labels[i] = ">10k";
            }

            deduplicatedPercentages[i] /= dedupTotal;
            totalPercentages[i] /= rawTotal;
            deduplicatedPercentages[i] *= 100;
            totalPercentages[i] *= 100;
        }

        percentDifferentSeqs = (dedupTotal / rawTotal) * 100;

    }

    private static double getCorrectedCount(long countAtLimit, long totalCount, int duplicationLevel, int numberOfObservations) {

//		System.err.println("Count at limit = "+countAtLimit+" total = "+totalCount+" Dup level = "+duplicationLevel+" no obs = "+numberOfObservations);
        // See if we can bail out early
        if (countAtLimit == totalCount) {
            return numberOfObservations;
        }

        // If there aren't enough sequences left to hide another sequence with this count then
        // we can also skip the calculation
        if (totalCount - numberOfObservations < countAtLimit) {
            return numberOfObservations;
        }

        // If not then we need to see what the likelihood is that we had another sequence
        // with this number of observations which we would have missed.
        // We'll start by working out the probability of NOT seeing a sequence with this duplication level
        // within the first countAtLimit sequences of numberOfObservations.  This is easier than calculating
        // the probability of seeing it.
        double pNotSeeingAtLimit = 1;

        // To save doing long calculations which are never going to produce anything meaningful
        // we'll set a limit to our p-value calculation.  This is the probability below which we
        // won't increase our count by 0.01 of an observation.  Once we're below this we stop caring
        // about the corrected value since it's going to be so close to the observed value that
        // we can just return that instead.
        double limitOfCaring = 1d - (numberOfObservations / (numberOfObservations + 0.01d));

        for (int i = 0; i < countAtLimit; i++) {
            pNotSeeingAtLimit *= ((totalCount - i) - duplicationLevel) / (double) (totalCount - i);

            if (pNotSeeingAtLimit < limitOfCaring) {
                pNotSeeingAtLimit = 0;
                break;
            }

        }

        // Now we can invert this to get the chance of seeing a sequence with this count
        double pSeeingAtLimit = 1 - pNotSeeingAtLimit;

        // Now we can assume that the number we observed can be scaled up by this proportion
        double trueCount = numberOfObservations / pSeeingAtLimit;

        return trueCount;

    }

    public JPanel getResultsPanel() {
        if (deduplicatedPercentages == null) {
            calculateLevels();
        }
        if (ignoreInReport()) {
            JPanel returnPanel = new JPanel();
            returnPanel.add(new JLabel("Ignore in report", JLabel.CENTER), BorderLayout.CENTER);
            return returnPanel;
        }
        return new AdapterDeduplicatedLineGraph(new double[][]{deduplicatedPercentages, totalPercentages}, 0d, maxCount, "Sequence Duplication Level", new String[]{"% Deduplicated sequences", "% Total sequences"}, labels, "Percent of seqs remaining if deduplicated " + df.format(percentDifferentSeqs) + "%");
    }

    public void makeReport(HTMLReportArchive report, int No) throws IOException, XMLStreamException {
        if (deduplicatedPercentages == null) {
            calculateLevels();
        }

        if (No == 1) {
        writeDefaultImage(report, "duplication_levels.png", "Duplication level graph", 800, 600);
        }else{
        writeDefaultImage(report, "duplication_levels2.png", "Duplication level graph", 800, 600);
        }

        StringBuffer sb = report.dataDocument();

        sb.append("#Total Deduplicated Percentage\t");
        sb.append(percentDifferentSeqs);
        sb.append("\n");

        sb.append("#Duplication Level\tPercentage of deduplicated\tPercentage of total\n");
        for (int i = 0; i < labels.length; i++) {
            sb.append(labels[i]);
            if (i == labels.length - 1) {
                sb.append("+");
            }
            sb.append("\t");
            sb.append(deduplicatedPercentages[i]);
            sb.append("\t");
            sb.append(totalPercentages[i]);
            sb.append("\n");
        }

    }

    public String name() {
        return "Sequence Duplication Levels";
    }

    public void mergeFrom(MergeableQCModule other) {
        // All data is derived lazily from the linked OverRepresentedSeqs instance.
        // That module handles its own merging; nothing to do here.
    }

    public void processSequence(Sequence sequence) {
        // We don't need to do anything since we use
        // the data structure from the overrepresented sequences
        // module.
    }

    public boolean raisesError() {
        if (deduplicatedPercentages == null) {
            calculateLevels();
        }

        // Anything over 50% duplicate gets us a error
        if (percentDifferentSeqs < ModuleConfig.getParam("duplication", "error")) {
            return true;
        }

        return false;
    }

    public boolean raisesWarning() {
        if (deduplicatedPercentages == null) {
            calculateLevels();
        }

        // Anything over 20% duplicate gets us a warning
        if (percentDifferentSeqs < ModuleConfig.getParam("duplication", "warn")) {
            return true;
        }

        return false;
    }

    public void reset() {
        deduplicatedPercentages = null;
    }
}
