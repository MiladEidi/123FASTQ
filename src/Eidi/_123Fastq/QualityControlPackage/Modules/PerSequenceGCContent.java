package Eidi._123Fastq.QualityControlPackage.Modules;

import Eidi._123Fastq.QualityControlPackage.Graphs.GCContentLineGraph;
import Eidi._123Fastq.QualityControlPackage.Modules.GCModel.GCModel;
import Eidi._123Fastq.QualityControlPackage.Modules.GCModel.GCModelValue;
import Eidi._123Fastq.QualityControlPackage.Report.HTMLReportArchive;
import Eidi._123Fastq.QualityControlPackage.Sequence.Sequence;
import Eidi._123Fastq.QualityControlPackage.Statistics.NormalDistribution;
import java.awt.BorderLayout;
import java.io.IOException;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.xml.stream.XMLStreamException;

public class PerSequenceGCContent extends AbstractQCModule implements MergeableQCModule {

    private double[] gcDistribution = new double[101];
    private double[] theoreticalDistribution = new double[101];
    private int[] xCategories = new int[0];
    private double max = 0;
    private double deviationPercent;
    private boolean calculated = false;

    private GCModel[] cachedModels = new GCModel[200];

    public JPanel getResultsPanel() {

        if (!calculated) {
            calculateDistribution();
        }
        if (ignoreInReport()) {
            JPanel returnPanel = new JPanel();
            returnPanel.add(new JLabel("Ignore in report", JLabel.CENTER), BorderLayout.CENTER);
            return returnPanel;
        }
        return new GCContentLineGraph(new double[][]{gcDistribution, theoreticalDistribution}, 0d, max, "Mean GC content (%)", new String[]{"GC count per read", "Theoretical Distribution"}, xCategories, "GC distribution over all sequences");
    }

    public boolean ignoreFilteredSequences() {
        return true;
    }

    public boolean ignoreInReport() {
        if (ModuleConfig.getParam("gc_sequence", "ignore") > 0) {
            return true;
        }
        return false;
    }

    private synchronized void calculateDistribution() {
        max = 0;
        xCategories = new int[gcDistribution.length];
        double totalCount = 0;

        // We use the mode to calculate the theoretical distribution
        // so that we cope better with skewed distributions.
        int firstMode = 0;
        double modeCount = 0;

        for (int i = 0; i < gcDistribution.length; i++) {
            xCategories[i] = i;
            totalCount += gcDistribution[i];

            if (gcDistribution[i] > modeCount) {
                modeCount = gcDistribution[i];
                firstMode = i;
            }
            if (gcDistribution[i] > max) {
                max = gcDistribution[i];
            }
        }

        // The mode might not be a very good measure of the centre
        // of the distribution either due to duplicated vales or
        // several very similar values next to each other.  We therefore
        // average over adjacent points which stay above 95% of the modal
        // value
        double mode = 0;
        int modeDuplicates = 0;

        boolean fellOffTop = true;

        for (int i = firstMode; i < gcDistribution.length; i++) {
            if (gcDistribution[i] > gcDistribution[firstMode] - (gcDistribution[firstMode] / 10)) {
                mode += i;
                modeDuplicates++;
            } else {
                fellOffTop = false;
                break;
            }
        }

        boolean fellOffBottom = true;

        for (int i = firstMode - 1; i >= 0; i--) {
            if (gcDistribution[i] > gcDistribution[firstMode] - (gcDistribution[firstMode] / 10)) {
                mode += i;
                modeDuplicates++;
            } else {
                fellOffBottom = false;
                break;
            }
        }

        if (fellOffBottom || fellOffTop) {
            // If the distribution is so skewed that 95% of the mode
            // is off the 0-100% scale then we keep the mode as the
            // centre of the model
            mode = firstMode;
        } else {
            mode /= modeDuplicates;
        }

        // We can now work out a theoretical distribution
        double stdev = 0;

        for (int i = 0; i < gcDistribution.length; i++) {
            stdev += Math.pow((i - mode), 2) * gcDistribution[i];
        }

        stdev /= totalCount - 1;

        stdev = Math.sqrt(stdev);

        NormalDistribution nd = new NormalDistribution(mode, stdev);

        deviationPercent = 0;

        for (int i = 0; i < theoreticalDistribution.length; i++) {
            double probability = nd.getZScoreForValue(i);
            theoreticalDistribution[i] = probability * totalCount;

            if (theoreticalDistribution[i] > max) {
                max = theoreticalDistribution[i];
            }

            deviationPercent += Math.abs(theoreticalDistribution[i] - gcDistribution[i]);
        }

        deviationPercent /= totalCount;
        deviationPercent *= 100;
        calculated = true;
    }

    public void processSequence(Sequence sequence) {

        // Because we keep a model around for every possible sequence length we
        // encounter we need to reduce the number of models.  We can do this by
        // rounding off the sequence once we get above a certain size
        char[] seq = truncateSequence(sequence);

        if (seq.length == 0) {
            return;
        }

        int thisSeqGCCount = 0;
        for (int i = 0; i < seq.length; i++) {
            if (seq[i] == 'G' || seq[i] == 'C') {
                ++thisSeqGCCount;
            }
        }

        if (seq.length >= cachedModels.length) {
            GCModel[] longerModels = new GCModel[seq.length + 1];
            for (int i = 0; i < cachedModels.length; i++) {
                longerModels[i] = cachedModels[i];
            }
            cachedModels = longerModels;
        }
        if (cachedModels[seq.length] == null) {
            cachedModels[seq.length] = new GCModel(seq.length);
        }
        GCModelValue[] values = cachedModels[seq.length].getModelValues(thisSeqGCCount);
        for (int i = 0; i < values.length; i++) {
            gcDistribution[values[i].percentage()] += values[i].increment();
        }
    }

    public synchronized void mergeFrom(MergeableQCModule other) {
        PerSequenceGCContent source = (PerSequenceGCContent) other;
        for (int i = 0; i < gcDistribution.length; i++) {
            gcDistribution[i] += source.gcDistribution[i];
        }
        theoreticalDistribution = new double[101];
        xCategories = new int[0];
        max = 0;
        deviationPercent = 0;
        calculated = false;
    }

    private char[] truncateSequence(Sequence sequence) {
        String seq = sequence.getSequence();
        if (seq.length() > 1000) {
            int length = (seq.length() / 1000) * 1000;
            return seq.substring(0, length).toCharArray();
        }
        if (seq.length() > 100) {
            int length = (seq.length() / 100) * 100;
            return seq.substring(0, length).toCharArray();
        }
        return seq.toCharArray();
    }

    public void reset() {
        gcDistribution = new double[101];
        theoreticalDistribution = new double[101];
        xCategories = new int[0];
        max = 0;
        deviationPercent = 0;
        calculated = false;
    }

    public String description() {
        return "Shows the distribution of GC contents for whole sequences";
    }

    public String name() {
        return "Per Sequence GC Content";
    }

    public boolean raisesError() {
        if (!calculated) {
            calculateDistribution();
        }

        return deviationPercent > ModuleConfig.getParam("gc_sequence", "error");
    }

    public boolean raisesWarning() {
        if (!calculated) {
            calculateDistribution();
        }

        return deviationPercent > ModuleConfig.getParam("gc_sequence", "warn");
    }

    public void makeReport(HTMLReportArchive report, int No) throws IOException, XMLStreamException {

        if (No == 1) {
            writeDefaultImage(report, "per_sequence_gc_content.png", "Per sequence GC content graph", 800, 600);
        } else {
            writeDefaultImage(report, "per_sequence_gc_content2.png", "Per sequence GC content graph", 800, 600);
        }

        StringBuffer sb = report.dataDocument();
        sb.append("#GC Content\tCount\n");
        for (int i = 0; i < xCategories.length; i++) {
            sb.append(xCategories[i]);
            sb.append("\t");
            sb.append(gcDistribution[i]);
            sb.append("\n");
        }
    }
}
