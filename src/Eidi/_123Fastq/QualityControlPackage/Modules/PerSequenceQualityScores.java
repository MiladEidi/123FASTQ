package Eidi._123Fastq.QualityControlPackage.Modules;

import Eidi._123Fastq.QualityControlPackage.Graphs.QualityBarChart;
import Eidi._123Fastq.QualityControlPackage.Report.HTMLReportArchive;
import Eidi._123Fastq.QualityControlPackage.Sequence.QualityEncoding.PhredEncoding;
import Eidi._123Fastq.QualityControlPackage.Sequence.Sequence;
import java.awt.BorderLayout;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.xml.stream.XMLStreamException;

public class PerSequenceQualityScores extends AbstractQCModule implements MergeableQCModule {

    private HashMap<Integer, Long> averageScoreCounts = new HashMap<>();
    private double[] qualityDistribution = null;
    private int[] xCategories = new int[0];
    private char lowestChar = 126;
    private double maxCount = 0;
    private int mostFrequentScore;
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
        return new QualityBarChart(new double[][]{qualityDistribution}, 0d, maxCount, "Mean Sequence Quality (Phred Score)", xCategories, "Quality score distribution over all sequences");
    }

    public boolean ignoreInReport() {
        // We don't show this if they didn't have any quality data.
        return ModuleConfig.getParam("quality_sequence", "ignore") > 0 || averageScoreCounts.isEmpty();
    }

    private synchronized void calculateDistribution() {
        PhredEncoding encoding = PhredEncoding.getFastQEncodingOffset(lowestChar);
        Integer[] rawScores = averageScoreCounts.keySet().toArray(new Integer[0]);
        Arrays.sort(rawScores);
        // We'll run from the lowest to the highest
        qualityDistribution = new double[1 + (rawScores[rawScores.length - 1] - rawScores[0])];
        xCategories = new int[qualityDistribution.length];
        for (int i = 0; i < qualityDistribution.length; i++) {
            xCategories[i] = (rawScores[0] + i) - encoding.offset();
            if (averageScoreCounts.containsKey(rawScores[0] + i)) {
                qualityDistribution[i] = averageScoreCounts.get(rawScores[0] + i);
            }
        }
        for (int i = 0; i < qualityDistribution.length; i++) {
            if (qualityDistribution[i] > maxCount) {
                maxCount = (int) qualityDistribution[i];
                mostFrequentScore = xCategories[i];
            }
        }
        calculated = true;
    }

    public synchronized void processSequence(Sequence sequence) {

        char[] seq = sequence.getQualityString().toCharArray();
        int averageQuality = 0;
        for (int i = 0; i < seq.length; i++) {
            if (seq[i] < lowestChar) {
                lowestChar = seq[i];
            }
            averageQuality += seq[i];
        }
        if (seq.length > 0) {
            averageQuality /= seq.length;
            if (averageScoreCounts.containsKey(averageQuality)) {
                long currentCount = averageScoreCounts.get(averageQuality);
                currentCount++;
                averageScoreCounts.put(averageQuality, currentCount);
            } else {
                averageScoreCounts.put(averageQuality, 1L);
            }
        }
    }

    public synchronized void mergeFrom(MergeableQCModule other) {
        PerSequenceQualityScores source = (PerSequenceQualityScores) other;
        for (Integer score : source.averageScoreCounts.keySet()) {
            Long currentCount = averageScoreCounts.get(score);
            if (currentCount == null) {
                averageScoreCounts.put(score, source.averageScoreCounts.get(score));
            } else {
                averageScoreCounts.put(score, currentCount + source.averageScoreCounts.get(score));
            }
        }
        if (source.lowestChar < lowestChar) {
            lowestChar = source.lowestChar;
        }
        maxCount = 0;
        mostFrequentScore = 0;
        calculated = false;
    }

    public void reset() {
        averageScoreCounts.clear();
        lowestChar = 126;
        maxCount = 0;
        calculated = false;
    }

    public String description() {
        return "Shows the distribution of average quality scores for whole sequences";
    }

    public String name() {
        return "Per Sequence Quality Scores";
    }

    public boolean raisesError() {
        if (!calculated) {
            calculateDistribution();
        }
        return mostFrequentScore <= ModuleConfig.getParam("quality_sequence", "error");
    }

    public boolean raisesWarning() {
        if (!calculated) {
            calculateDistribution();
        }
        return mostFrequentScore <= ModuleConfig.getParam("quality_sequence", "warn");
    }

    public void makeReport(HTMLReportArchive report, int No) throws IOException, XMLStreamException {
        if (!calculated) {
            calculateDistribution();
        }
        if (No == 1) {
            writeDefaultImage(report, "per_sequence_quality.png", "Per Sequence quality graph", 800, 600);
        } else {
            writeDefaultImage(report, "per_sequence_quality2.png", "Per Sequence quality graph", 800, 600);
        }
        StringBuffer sb = report.dataDocument();
        sb.append("#Quality\tCount\n");
        for (int i = 0; i < xCategories.length; i++) {
            sb.append(xCategories[i]);
            sb.append("\t");
            sb.append(qualityDistribution[i]);
            sb.append("\n");
        }
    }

    public boolean ignoreFilteredSequences() {
        return true;
    }
}
