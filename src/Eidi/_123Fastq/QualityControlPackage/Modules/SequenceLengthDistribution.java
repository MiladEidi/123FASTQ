package Eidi._123Fastq.QualityControlPackage.Modules;

import Eidi._123Fastq.QualityControlPackage.Config;
import Eidi._123Fastq.QualityControlPackage.Graphs.ReadLengthBarChart;
import Eidi._123Fastq.QualityControlPackage.Report.HTMLReportArchive;
import Eidi._123Fastq.QualityControlPackage.Sequence.Sequence;
import java.awt.BorderLayout;
import java.io.IOException;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.xml.stream.XMLStreamException;

public class SequenceLengthDistribution extends AbstractQCModule implements MergeableQCModule {

    private long[] lengthCounts = new long[0];
    private double[] graphCounts = null;
    private String[] xCategories = new String[0];
    private double max = 0;
    private boolean calculated = false;

    public JPanel getResultsPanel() {
        if (!calculated) {
            calculateDistribution();
        }
        if (ignoreInReport()) {
            JPanel returnPanel = new JPanel();
            returnPanel.add(new JLabel("Ignore in report", JLabel.CENTER), BorderLayout.CENTER);
            return returnPanel;
        }
        return new ReadLengthBarChart(new double[][]{graphCounts}, 0d, max, "Sequence Length (bp)", new String[]{"Sequence Length"}, xCategories, "Distribution of sequence lengths over all sequences");
    }

    public int getMeanLengthCounts() {
        int WeightedSum = 0;
        int all = 0;
        for (int i = 0; i < lengthCounts.length; i++) {
            WeightedSum += (int) lengthCounts[i] * i;
            all += (int) lengthCounts[i];
        }
        return WeightedSum / all;
    }

    public boolean ignoreFilteredSequences() {
        return true;
    }

    public boolean ignoreInReport() {
        return ModuleConfig.getParam("sequence_length", "ignore") > 0;
    }

    private synchronized void calculateDistribution() {
        int maxLen = 0;
        int minLen = -1;
        max = 0;

        // Find the min and max lengths
        for (int i = 0; i < lengthCounts.length; i++) {
            if (lengthCounts[i] > 0) {
                if (minLen < 0) {
                    minLen = i;
                }
                maxLen = i;
            }
        }

        // We put one extra category either side of the actual size
        if (minLen > 0) {
            minLen--;
        }
        maxLen++;
        int[] startAndInterval = getSizeDistribution(minLen, maxLen);

        // Work out how many categories we need
        int categories = 0;
        int currentValue = startAndInterval[0];
        while (currentValue <= maxLen) {
            ++categories;
            currentValue += startAndInterval[1];
        }

        graphCounts = new double[categories];
        xCategories = new String[categories];

        for (int i = 0; i < graphCounts.length; i++) {

            int minValue = startAndInterval[0] + (startAndInterval[1] * i);
            int maxValue = (startAndInterval[0] + (startAndInterval[1] * (i + 1))) - 1;

            if (maxValue > maxLen) {
                maxValue = maxLen;
            }

            //a bug in big length reads...
            for (int bp = minValue; bp <= maxValue; bp++) {
                if (bp < lengthCounts.length) {
                    graphCounts[i] += lengthCounts[bp];
                }
            }

            if (startAndInterval[1] == 1) {
                xCategories[i] = "" + minValue;
            } else {
                xCategories[i] = minValue + "-" + maxValue;
            }

            if (graphCounts[i] > max) {
                max = graphCounts[i];
            }
        }
        calculated = true;
    }

    public synchronized void processSequence(Sequence sequence) {
        int seqLen = sequence.getSequence().length();

        if (seqLen + 2 > lengthCounts.length) {
            long[] newLengthCounts = new long[seqLen + 2];
            for (int i = 0; i < lengthCounts.length; i++) {
                newLengthCounts[i] = lengthCounts[i];
            }
            lengthCounts = newLengthCounts;
        }

        ++lengthCounts[seqLen];

    }

    public synchronized void mergeFrom(MergeableQCModule other) {
        SequenceLengthDistribution source = (SequenceLengthDistribution) other;
        ensureLength(source.lengthCounts.length);
        for (int i = 0; i < source.lengthCounts.length; i++) {
            lengthCounts[i] += source.lengthCounts[i];
        }
        graphCounts = null;
        xCategories = new String[0];
        max = 0;
        calculated = false;
    }

    private void ensureLength(int length) {
        if (lengthCounts.length >= length) {
            return;
        }
        long[] newLengthCounts = new long[length];
        for (int i = 0; i < lengthCounts.length; i++) {
            newLengthCounts[i] = lengthCounts[i];
        }
        lengthCounts = newLengthCounts;
    }

    private int[] getSizeDistribution(int min, int max) {

        // We won't group if they've asked us not to
        if (Config.getInstance().nogroup) {
            return (new int[]{min, 1});
        }

        int base = 1;

        while (base > (max - min)) {
            base /= 10;
        }

        int interval;
        int starting;

        int[] divisions = new int[]{1, 2, 5};

        OUTER:
        while (true) {

            for (int d = 0; d < divisions.length; d++) {
                int tester = base * divisions[d];
                if (((max - min) / tester) <= 50) {
                    interval = tester;
                    break OUTER;
                }
            }

            base *= 10;

        }

        // Now we work out the first value to be plotted
        int basicDivision = (int) (min / interval);

        int testStart = basicDivision * interval;

        starting = testStart;

        return new int[]{starting, interval};

    }

    public void reset() {
        lengthCounts = new long[0];
        graphCounts = null;
        xCategories = new String[0];
        max = 0;
        calculated = false;
    }

    public String description() {
        return "Shows the distribution of sequence length over all sequences";
    }

    public String name() {
        return "Sequence Length Distribution";
    }

    public boolean raisesError() {
        if (!calculated) {
            calculateDistribution();
        }

        // See if they've turned this test off
        if (ModuleConfig.getParam("sequence_length", "error") == 0) {
            return false;
        }

        if (lengthCounts[0] > 0) {
            return true;
        }
        return false;
    }

    public boolean raisesWarning() {
        if (!calculated) {
            calculateDistribution();
        }

        // See if they've turned this test off
        if (ModuleConfig.getParam("sequence_length", "warn") == 0) {
            return false;
        }

        // Warn if they're not all the same length
        boolean seenLength = false;
        for (int i = 0; i < lengthCounts.length; i++) {
            if (lengthCounts[i] > 0) {
                if (seenLength) {
                    return true;
                } else {
                    seenLength = true;
                }
            }
        }
        return false;
    }

    public void makeReport(HTMLReportArchive report, int No) throws IOException, XMLStreamException {
        if (!calculated) {
            calculateDistribution();
        }
        if (No == 1) {
            writeDefaultImage(report, "sequence_length_distribution.png", "Sequence length distribution", Math.max(800, graphCounts.length * 15), 600);
        } else {
            writeDefaultImage(report, "sequence_length_distribution2.png", "Sequence length distribution", Math.max(800, graphCounts.length * 15), 600);
        }

        StringBuffer sb = report.dataDocument();
        sb.append("#Length\tCount\n");
        for (int i = 0; i < xCategories.length; i++) {
            // Remove any padding we added to make the graph look better
            if ((i == 0 || i == xCategories.length - 1) && graphCounts[i] == 0) {
                continue;
            }
            sb.append(xCategories[i]);
            sb.append("\t");
            sb.append(graphCounts[i]);
            sb.append("\n");
        }
    }
}
