package Eidi._123Fastq.QualityControlPackage.Modules;

import Eidi._123Fastq.QualityControlPackage.Graphs.BaseGroup;
import Eidi._123Fastq.QualityControlPackage.Graphs.QualityBoxPlot;
import Eidi._123Fastq.QualityControlPackage.Report.HTMLReportArchive;
import Eidi._123Fastq.QualityControlPackage.Sequence.QualityEncoding.PhredEncoding;
import Eidi._123Fastq.QualityControlPackage.Sequence.Sequence;
import Eidi._123Fastq.QualityControlPackage.Utilities.QualityCount;
import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.xml.stream.XMLStreamException;

public class PerBaseQualityScores extends AbstractQCModule implements MergeableQCModule {

    public QualityCount[] qualityCounts = new QualityCount[0];
    double[] means = null;
    double[] medians = null;
    double[] lowerQuartile = null;
    double[] upperQuartile = null;
    double[] lowest = null;
    double[] highest = null;
    String[] xLabels;
    int low = 0;
    int high = 0;
    PhredEncoding encodingScheme;
    private boolean calculated = false;

    public double[] getMeans() {
        return means;
    }

    public double[] getMedians() {
        return medians;
    }

    public double[] getLowerQuartile() {
        return lowerQuartile;
    }

    public double[] getUpperQuartile() {
        return upperQuartile;
    }

    public double[] getLowest() {
        return lowest;
    }

    public double[] getHighest() {
        return highest;
    }

    public JPanel getResultsPanel() {

        if (!calculated) {
            getPercentages();
        }

        if (ignoreInReport()) {
            JPanel returnPanel = new JPanel();
            returnPanel.add(new JLabel("Ignore in report", JLabel.CENTER), BorderLayout.CENTER);
            return returnPanel;
        }

        return new QualityBoxPlot(means, medians, lowest, highest, lowerQuartile, upperQuartile, low, high, 2d, xLabels, "Quality scores across all bases (" + encodingScheme + " encoding)");
    }

    public boolean ignoreFilteredSequences() {
        return true;
    }

    public boolean ignoreInReport() {
        // We don't show this if there is no quality data.
        return ModuleConfig.getParam("quality_base", "ignore") > 0 || qualityCounts.length == 0;
    }

    private synchronized void getPercentages() {

              char [] range = calculateOffsets();
		encodingScheme = PhredEncoding.getFastQEncodingOffset(range[0]);
		low = 0;
		high = range[1] - encodingScheme.offset();
		if (high < 35) {
			high = 35;
		}


        BaseGroup[] groups = BaseGroup.makeBaseGroups(qualityCounts.length);

        means = new double[groups.length];
        medians = new double[groups.length];
        lowest = new double[groups.length];
        highest = new double[groups.length];
        lowerQuartile = new double[groups.length];
        upperQuartile = new double[groups.length];
        xLabels = new String[groups.length];

        for (int i = 0; i < groups.length; i++) {
            xLabels[i] = groups[i].toString();
            int minBase = groups[i].lowerCount();
            int maxBase = groups[i].upperCount();
            lowest[i] = getPercentile(minBase, maxBase, encodingScheme.offset(), 10);
            highest[i] = getPercentile(minBase, maxBase, encodingScheme.offset(), 90);
            means[i] = getMean(minBase, maxBase, encodingScheme.offset());
            medians[i] = getPercentile(minBase, maxBase, encodingScheme.offset(), 50);
            lowerQuartile[i] = getPercentile(minBase, maxBase, encodingScheme.offset(), 25);
            upperQuartile[i] = getPercentile(minBase, maxBase, encodingScheme.offset(), 75);
        }
        calculated = true;
    }

    private char[] calculateOffsets() {

        char minChar = 0;
        char maxChar = 0;

        for (int q = 0; q < qualityCounts.length; q++) {
            if (q == 0) {
                minChar = qualityCounts[q].getMinChar();
                maxChar = qualityCounts[q].getMaxChar();
            } else {
                if (qualityCounts[q].getMinChar() < minChar) {
                    minChar = qualityCounts[q].getMinChar();
                }
                if (qualityCounts[q].getMaxChar() > maxChar) {
                    maxChar = qualityCounts[q].getMaxChar();
                }
            }
        }
        return new char[]{minChar, maxChar};
    }

    public void processSequence(Sequence sequence) {

        calculated = false;
        char[] qual = sequence.getQualityString().toCharArray();
        if (qualityCounts.length < qual.length) {
            QualityCount[] qualityCountsNew = new QualityCount[qual.length];
            for (int i = 0; i < qualityCounts.length; i++) {
                qualityCountsNew[i] = qualityCounts[i];
            }
            for (int i = qualityCounts.length; i < qualityCountsNew.length; i++) {
//                try{
                qualityCountsNew[i] = new QualityCount();
//                }catch(OutOfMemoryError e){
//                    System.out.println("Heap Overflow. Too long reads have been detected.");
//                    //when over heap space read length >> crash
            }
            qualityCounts = qualityCountsNew;
        }

        for (int i = 0; i < qual.length; i++) {
            qualityCounts[i].addValue(qual[i]);
        }

    }

    public synchronized void mergeFrom(MergeableQCModule other) {
        PerBaseQualityScores source = (PerBaseQualityScores) other;
        if (qualityCounts.length < source.qualityCounts.length) {
            QualityCount[] expanded = new QualityCount[source.qualityCounts.length];
            for (int i = 0; i < qualityCounts.length; i++) {
                expanded[i] = qualityCounts[i];
            }
            for (int i = qualityCounts.length; i < expanded.length; i++) {
                expanded[i] = new QualityCount();
            }
            qualityCounts = expanded;
        }
        for (int i = 0; i < source.qualityCounts.length; i++) {
            qualityCounts[i].mergeFrom(source.qualityCounts[i]);
        }
        calculated = false;
    }


    public void reset() {
        qualityCounts = new QualityCount[0];
    }

    public String description() {
        return "Shows the Quality scores of all bases at a given position in a sequencing run";
    }

    public String name() {
        return "Per Base Sequence Quality";
    }

    public boolean raisesError() {
        if (!calculated) {
            getPercentages();
        }

        for (int i = 0; i < lowerQuartile.length; i++) {
            if (Double.isNaN(lowerQuartile[i])) {
                // There wasn't enough data for this group to make an assessment
                continue;
            }
            if (lowerQuartile[i] < ModuleConfig.getParam("quality_base_lower", "error") || medians[i] < ModuleConfig.getParam("quality_base_median", "error")) {
                return true;
            }
        }

        return false;
    }

    public boolean raisesWarning() {
        if (!calculated) {
            getPercentages();
        }

        for (int i = 0; i < lowerQuartile.length; i++) {
            if (Double.isNaN(lowerQuartile[i])) {
                // There wasn't enough data for this group to make an assessment
                continue;
            }
            if (lowerQuartile[i] < ModuleConfig.getParam("quality_base_lower", "warn") || medians[i] < ModuleConfig.getParam("quality_base_median", "warn")) {
                return true;
            }
        }
        return false;
    }

    public void makeReport(HTMLReportArchive report, int No) throws IOException, XMLStreamException {
        if (!calculated) {
            getPercentages();
        }

        if(No == 1){
        writeDefaultImage(report, "per_base_quality.png", "Per Base Quality Graph", Math.max(800, means.length * 15), 600);
        }else{
        writeDefaultImage(report, "per_base_quality2.png", "Per Base Quality Graph", Math.max(800, means.length * 15), 600);
        }
        StringBuffer sb = report.dataDocument();
        sb.append("#Base\tMean\tMedian\tLower Quartile\tUpper Quartile\t10th Percentile\t90th Percentile\n");
        for (int i = 0; i < means.length; i++) {
            sb.append(xLabels[i]);
            sb.append("\t");

            sb.append(means[i]);
            sb.append("\t");

            sb.append(medians[i]);
            sb.append("\t");

            sb.append(lowerQuartile[i]);
            sb.append("\t");

            sb.append(upperQuartile[i]);
            sb.append("\t");

            sb.append(lowest[i]);
            sb.append("\t");

            sb.append(highest[i]);

            sb.append("\n");
        }
    }

    private double getPercentile(int minbp, int maxbp, int offset, int percentile) {
        int count = 0;
        double total = 0;

        for (int i = minbp - 1; i < maxbp; i++) {
            if (qualityCounts[i].getTotalCount() > 100) {
                count++;
                total += qualityCounts[i].getPercentile(offset, percentile);
            }
        }

        if (count > 0) {
            return total / count;
        }
        return Double.NaN;
    }

    private double getMean(int minbp, int maxbp, int offset) {
        int count = 0;
        double total = 0;

        for (int i = minbp - 1; i < maxbp; i++) {
            if (qualityCounts[i].getTotalCount() > 0) {
                count++;
                total += qualityCounts[i].getMean(offset);
            }
        }

        if (count > 0) {
            return total / count;
        }

        return 0;
    }
}
