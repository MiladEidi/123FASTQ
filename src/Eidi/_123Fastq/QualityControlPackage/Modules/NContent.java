package Eidi._123Fastq.QualityControlPackage.Modules;

import Eidi._123Fastq.GUI.Statics;
import Eidi._123Fastq.QualityControlPackage.Graphs.BaseGroup;
import Eidi._123Fastq.QualityControlPackage.Graphs.LogarithmicPercentLineGraph;
import Eidi._123Fastq.QualityControlPackage.Graphs.PercentLineGraph;
import Eidi._123Fastq.QualityControlPackage.Report.HTMLReportArchive;
import Eidi._123Fastq.QualityControlPackage.Sequence.Sequence;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.io.IOException;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.xml.stream.XMLStreamException;
import org.knowm.xchart.BubbleChart;
import org.knowm.xchart.BubbleChartBuilder;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.style.Styler;

public class NContent extends AbstractQCModule implements MergeableQCModule {

    public long[] nCounts = new long[0];
    public long[] notNCounts = new long[0];
    public boolean calculated = false;
    public double[] percentages_logarithmic = null;
        public double[] percentages = null;

    public String[] xCategories = new String[0];

    public JPanel getResultsPanel() {
        if (!calculated) {
            getPercentages();
        }
        if (ignoreInReport()) {
            JPanel returnPanel = new JPanel();
            returnPanel.add(new JLabel("Ignore in report", JLabel.CENTER), BorderLayout.CENTER);
            return returnPanel;
        }
        return new LogarithmicPercentLineGraph(new double[][]{percentages_logarithmic}, Math.log10(1d), Math.log10(101d), "Position in read (bp)", new String[]{"%N"}, xCategories, "Logarithmic N content across all bases");
    }

    public boolean ignoreFilteredSequences() {
        return true;
    }

    public float getMeanNPercentage() {
        float Sum = 0;
        for (int i = 0; i < percentages_logarithmic.length; i++) {
            Sum += (double) percentages_logarithmic[i];
        }
        return Sum / percentages_logarithmic.length;
    }

    public boolean ignoreInReport() {
        return ModuleConfig.getParam("n_content", "ignore") > 0;
    }

    private synchronized void getPercentages() {
        BaseGroup[] groups = BaseGroup.makeBaseGroups(nCounts.length);
        xCategories = new String[groups.length];
        percentages_logarithmic = new double[groups.length];
        percentages = new double[groups.length];

        long total;
        long nCount;
        for (int i = 0; i < groups.length; i++) {
            xCategories[i] = groups[i].toString();
            nCount = 0;
            total = 0;
            for (int bp = groups[i].lowerCount() - 1; bp < groups[i].upperCount(); bp++) {
                nCount += nCounts[bp];
                total += nCounts[bp];
                total += notNCounts[bp];
            }
            percentages_logarithmic[i] = Math.log10((100 * (nCount / (double) total)) + 1);
            percentages[i] = 100 * (nCount / (double) total);
        }
        calculated = true;
    }

    public synchronized void processSequence(Sequence sequence) {
        calculated = false;
        char[] seq = sequence.getSequence().toCharArray();
        if (nCounts.length < seq.length) {
            long[] nCountsNew = new long[seq.length];
            long[] notNCountsNew = new long[seq.length];
            for (int i = 0; i < nCounts.length; i++) {
                nCountsNew[i] = nCounts[i];
                notNCountsNew[i] = notNCounts[i];
            }
            nCounts = nCountsNew;
            notNCounts = notNCountsNew;
        }

        for (int i = 0; i < seq.length; i++) {
            if (seq[i] == 'N') {
                ++nCounts[i];
            } else {
                ++notNCounts[i];
            }
        }
    }

    public synchronized void mergeFrom(MergeableQCModule other) {
        NContent source = (NContent) other;
        ensureLength(source.nCounts.length);
        for (int i = 0; i < source.nCounts.length; i++) {
            nCounts[i] += source.nCounts[i];
            notNCounts[i] += source.notNCounts[i];
        }
        percentages_logarithmic = null;
        percentages = null;
        xCategories = new String[0];
        calculated = false;
    }

    private void ensureLength(int length) {
        if (nCounts.length >= length) {
            return;
        }
        long[] nCountsNew = new long[length];
        long[] notNCountsNew = new long[length];
        for (int i = 0; i < nCounts.length; i++) {
            nCountsNew[i] = nCounts[i];
            notNCountsNew[i] = notNCounts[i];
        }
        nCounts = nCountsNew;
        notNCounts = notNCountsNew;
    }

    public void reset() {
        nCounts = new long[0];
        notNCounts = new long[0];
        percentages_logarithmic = null;
        percentages = null;
        xCategories = new String[0];
        calculated = false;
    }

    public String description() {
        return "Shows the percentage of bases at each position which are not being called";
    }

    public String name() {
        return "Per Base N Content";
    }

    public boolean raisesError() {
        if (!calculated) {
            getPercentages();
        }
        for (int i = 0; i < percentages.length; i++) {
            if (percentages[i] > ModuleConfig.getParam("n_content", "error")) {
                return true;
            }
        }
        return false;
    }

    public boolean raisesWarning() {
        if (!calculated) {
            getPercentages();
        }
        for (int i = 0; i < percentages.length; i++) {
            if (percentages[i] > ModuleConfig.getParam("n_content", "warn")) {
                return true;
            }
        }
        return false;
    }

    public void makeReport(HTMLReportArchive report, int No) throws XMLStreamException, IOException {
        if (!calculated) {
            getPercentages();
        }
        if (No == 1) {
            writeDefaultImage(report, "per_base_n_content.png", "N content graph", Math.max(800, percentages_logarithmic.length * 15), 600);
        } else {
            writeDefaultImage(report, "per_base_n_content2.png", "N content graph", Math.max(800, percentages_logarithmic.length * 15), 600);
        }
        StringBuffer sb = report.dataDocument();
        sb.append("#Base\tN-Count\n");
        for (int i = 0; i < xCategories.length; i++) {
            sb.append(xCategories[i]);
            sb.append("\t");
            sb.append(percentages_logarithmic[i]);
            sb.append("\n");
        }
    }
}
