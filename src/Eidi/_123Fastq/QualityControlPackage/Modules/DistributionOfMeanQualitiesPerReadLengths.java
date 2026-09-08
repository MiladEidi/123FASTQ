package Eidi._123Fastq.QualityControlPackage.Modules;

import Eidi._123Fastq.GUI.Statics;
import Eidi._123Fastq.QualityControlPackage.Report.HTMLReportArchive;
import Eidi._123Fastq.QualityControlPackage.Sequence.QualityEncoding.PhredEncoding;
import Eidi._123Fastq.QualityControlPackage.Sequence.Sequence;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.xml.stream.XMLStreamException;
import org.knowm.xchart.BubbleChart;
import org.knowm.xchart.BubbleChartBuilder;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.style.Styler;

public class DistributionOfMeanQualitiesPerReadLengths extends AbstractQCModule implements MergeableQCModule {

    private HashMap<Integer, HashMap<Integer, Integer>> MainHashMap = new HashMap<>();

    private double[] ReadLengths;
    private double[] MeanQualities;
    private double[] BubbleSize;
    private LinkedList<Integer> length = new LinkedList<>();
    private LinkedList<Integer> qual = new LinkedList<>();
    private LinkedList<Integer> bubble = new LinkedList<>();
    private int maxQualities = 0;
    private char lowestChar = 126;
    private boolean calculated = false;
    private int mostFrequentLen;
    private int mostFrequentQual;

    public JPanel getResultsPanel() {
        if (!calculated) {
            FinalProcessing();
        }
        if (ignoreInReport()) {
            JPanel returnPanel = new JPanel();
            returnPanel.add(new JLabel("Ignore in report", JLabel.CENTER), BorderLayout.CENTER);
            return returnPanel;
        }
        BubbleChart chart = new BubbleChartBuilder().width(810).height(650).xAxisTitle("Read Length").yAxisTitle("Mean Quality").build();
        chart.getStyler().setChartTitleVisible(true);
        chart.getStyler().setLegendVisible(false);
        chart.getStyler().setMarkerSize(3);
        chart.getStyler().setChartBackgroundColor(Statics.UIcolor);
        chart.getStyler().setAxisTitleFont(Statics.Graph_Mid_Font);
        chart.getStyler().setChartTitleVisible(false);
        chart.getStyler().setToolTipsEnabled(true);
        chart.getStyler().setToolTipHighlightColor(Color.red);
        chart.getStyler().setToolTipFont(Statics.Graph_Min_Font);
        chart.getStyler().setToolTipType(Styler.ToolTipType.xAndYLabels);
        if (maxQualities > 40) {
            chart.getStyler().setYAxisMax((double) maxQualities + 5);
        } else {
            chart.getStyler().setYAxisMax((double) 40);
        }
        chart.getStyler().setYAxisMin((double) 0);
        chart.addSeries("QL", ReadLengths, MeanQualities, BubbleSize);
        JPanel graph = new JPanel();
        graph.setLayout(new BorderLayout());
        JPanel CT = new JPanel(new BorderLayout());
        JPanel chartTitle = new JPanel(new BorderLayout());
        JPanel emptySpace = new JPanel();
        XChartPanel<BubbleChart> chartPane = new XChartPanel<>(chart);
        JLabel up = new JLabel("<html><center>Distributions of Mean Qualities per Reads Length</html>");
        up.setFont(Statics.Graph_Max_Font);
        JLabel down = new JLabel("<html><center>(Bigger circles mean that more reads have that mean quality and length.)</html>");
        down.setFont(Statics.Graph_Mid_Font);
        down.setForeground(Color.GRAY);
        chartTitle.add(up, BorderLayout.NORTH);
        chartTitle.add(down, BorderLayout.SOUTH);
        emptySpace.setPreferredSize(new Dimension(50, 25));
        CT.add(emptySpace, BorderLayout.WEST);
        CT.add(chartTitle, BorderLayout.CENTER);
        graph.add(CT, BorderLayout.NORTH);
        graph.add(chartPane, BorderLayout.CENTER);
        return graph;
    }

    public boolean ignoreInReport() {
        if (!calculated) {
            FinalProcessing();
        }

        boolean ReadLen = new HashSet<>(length).size() <= 3;
        boolean MeanQual = new HashSet<>(qual).size() <= 3;
        return ReadLen || MeanQual;
    }

    //Implement better in the future versions
    private synchronized void FinalProcessing() {
        PhredEncoding encoding = PhredEncoding.getFastQEncodingOffset(lowestChar);
        int maxBubble = Integer.MIN_VALUE;
        int minBubble = Integer.MAX_VALUE;
        Iterator<Map.Entry<Integer, HashMap<Integer, Integer>>> parent = MainHashMap.entrySet().iterator();
        while (parent.hasNext()) {
            Map.Entry<Integer, HashMap<Integer, Integer>> parentPair = parent.next();
            Iterator<Map.Entry<Integer, Integer>> child = (parentPair.getValue()).entrySet().iterator();
            while (child.hasNext()) {
                Map.Entry childPair = child.next();
                length.add(parentPair.getKey()); //read Len into its list
                int tmp = (int) childPair.getKey() - encoding.offset();
                qual.add((Integer) tmp);
                if (tmp > maxQualities) {
                    maxQualities = tmp;
                }
                int frequency = (int) childPair.getValue();
                bubble.add((Integer) frequency);
                if (maxBubble < frequency) {
                    maxBubble = frequency;
                    mostFrequentLen = parentPair.getKey();
                    mostFrequentQual = tmp;
                }
                if (minBubble > frequency) {
                    minBubble = frequency;
                }
            }
        }

        ReadLengths = length.stream().mapToDouble(i -> i).toArray();
        MeanQualities = qual.stream().mapToDouble(i -> i).toArray();
        Set<Integer> s = new HashSet<>(bubble);
        if (s.size() != 1) {
            BubbleSize = bubble.stream().mapToDouble(i -> i).toArray();
            for (int i = 0; i < BubbleSize.length; i++) {
                BubbleSize[i] = (((BubbleSize[i] - minBubble) / (maxBubble - minBubble)) * 100) + 2; //Normalization + 2
            }
        } else {
            BubbleSize = new double[bubble.size()];
            for (int i = 0; i < BubbleSize.length; i++) {
                BubbleSize[i] = 40;
            }
        }
        calculated = true;
    }

    public synchronized void processSequence(Sequence sequence) {
        char[] seq = sequence.getQualityString().toCharArray();
        Integer readLen = seq.length;
        int averageQuality = 0;
        for (int i = 0; i < readLen; i++) {
            if (seq[i] < lowestChar) {
                lowestChar = seq[i];
            }
            averageQuality += seq[i];
        }
        if (readLen > 0) {
            averageQuality /= readLen;
            if (!MainHashMap.containsKey(readLen)) {
                MainHashMap.put(readLen, new HashMap<>());
            }
            if (!MainHashMap.get(readLen).containsKey(averageQuality)) {
                MainHashMap.get(readLen).put(averageQuality, 1);
            } else {
                MainHashMap.get(readLen).put(averageQuality, MainHashMap.get(readLen).get(averageQuality) + 1);
            }
        }
    }

    public synchronized void mergeFrom(MergeableQCModule other) {
        DistributionOfMeanQualitiesPerReadLengths source = (DistributionOfMeanQualitiesPerReadLengths) other;
        for (Map.Entry<Integer, HashMap<Integer, Integer>> lengthEntry : source.MainHashMap.entrySet()) {
            HashMap<Integer, Integer> targetQualities = MainHashMap.get(lengthEntry.getKey());
            if (targetQualities == null) {
                targetQualities = new HashMap<>();
                MainHashMap.put(lengthEntry.getKey(), targetQualities);
            }
            for (Map.Entry<Integer, Integer> qualityEntry : lengthEntry.getValue().entrySet()) {
                Integer currentCount = targetQualities.get(qualityEntry.getKey());
                if (currentCount == null) {
                    targetQualities.put(qualityEntry.getKey(), qualityEntry.getValue());
                } else {
                    targetQualities.put(qualityEntry.getKey(), currentCount + qualityEntry.getValue());
                }
            }
        }
        if (source.lowestChar < lowestChar) {
            lowestChar = source.lowestChar;
        }
        clearCalculatedData();
    }

    private void clearCalculatedData() {
        ReadLengths = null;
        MeanQualities = null;
        BubbleSize = null;
        length.clear();
        qual.clear();
        bubble.clear();
        maxQualities = 0;
        mostFrequentLen = 0;
        mostFrequentQual = 0;
        calculated = false;
    }

    public void reset() {
        MainHashMap.clear();
        lowestChar = 126;
        clearCalculatedData();
    }

    public String description() {
        return "Shows the distribution of sequence length per average quality scores";
    }

    public String name() {
        return "Quality Per Length Distributions";
    }

    public boolean raisesError() {
        if (!calculated) {
            FinalProcessing();
        }
        return mostFrequentLen < 20 && mostFrequentQual < 23;
    }

    public boolean raisesWarning() {
        if (!calculated) {
            FinalProcessing();
        }
        return mostFrequentLen < 20 || mostFrequentQual < 23;
    }

    public void makeReport(HTMLReportArchive report, int No) throws IOException, XMLStreamException {
        if (!calculated) {
            FinalProcessing();
        }
        if (No == 1) {
            writeDefaultImage(report, "Weighted_Lengths_Per_Qualities.png", "Weighted Lengths Per Qualities", 800, 600);
        } else {
            writeDefaultImage(report, "Weighted_Lengths_Per_Qualities2.png", "Weighted Lengths Per Qualities", 800, 600);
        }
    }

    public boolean ignoreFilteredSequences() {
        return true;
    }
}
