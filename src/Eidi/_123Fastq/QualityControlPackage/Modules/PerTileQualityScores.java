package Eidi._123Fastq.QualityControlPackage.Modules;

import Eidi._123Fastq.QualityControlPackage.Graphs.BaseGroup;
import Eidi._123Fastq.QualityControlPackage.Graphs.TileGraph;
import Eidi._123Fastq.QualityControlPackage.Report.HTMLReportArchive;
import Eidi._123Fastq.QualityControlPackage.Sequence.QualityEncoding.PhredEncoding;
import Eidi._123Fastq.QualityControlPackage.Sequence.Sequence;
import Eidi._123Fastq.QualityControlPackage.Utilities.QualityCount;
import java.awt.BorderLayout;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.xml.stream.XMLStreamException;

public class PerTileQualityScores extends AbstractQCModule implements MergeableQCModule {

    public HashMap<Integer, QualityCount[]> perTileQualityCounts = new HashMap<>();
    private int currentLength = 0;
    private double[][] means = null;
    private String[] xLabels;
    private int[] tiles;
    private int high = 0;
    PhredEncoding encodingScheme;
    private boolean calculated = false;

    private long totalCount = 0;

    private int splitPosition = -1;

    private double maxDeviation = 0;

    private boolean ignoreInReport = false;

    public JPanel getResultsPanel() {

        if (!calculated) {
            getPercentages();
        }
        if (ignoreInReport()) {
            JPanel returnPanel = new JPanel();
            returnPanel.add(new JLabel("Ignore in report", JLabel.CENTER), BorderLayout.CENTER);
            return returnPanel;
        }
        return new TileGraph(xLabels, tiles, means);

    }

    public boolean ignoreFilteredSequences() {
        return true;
    }

    public boolean ignoreInReport() {
        if (ignoreInReport || ModuleConfig.getParam("tile", "ignore") > 0 || currentLength == 0) {
            return true;
        }
        return false;
    }

    private synchronized void getPercentages() {

        char[] range = calculateOffsets();
        encodingScheme = PhredEncoding.getFastQEncodingOffset(range[0]);
        high = range[1] - encodingScheme.offset();
        if (high < 35) {
            high = 35;
        }

        BaseGroup[] groups = BaseGroup.makeBaseGroups(currentLength);

        Integer[] tileNumbers = perTileQualityCounts.keySet().toArray(new Integer[0]);

        Arrays.sort(tileNumbers);

        tiles = new int[tileNumbers.length];
        for (int i = 0; i < tiles.length; i++) {
            tiles[i] = tileNumbers[i];
        }

        means = new double[tileNumbers.length][groups.length];
        xLabels = new String[groups.length];

        for (int t = 0; t < tileNumbers.length; t++) {
            for (int i = 0; i < groups.length; i++) {
                if (t == 0) {
                    xLabels[i] = groups[i].toString();
                }

                int minBase = groups[i].lowerCount();
                int maxBase = groups[i].upperCount();
                means[t][i] = getMean(tileNumbers[t], minBase, maxBase, encodingScheme.offset());
            }
        }

        // Now we normalise across each column to see if there are any tiles with unusually
        // high or low quality.
        double maxDeviation = 0;

        double[] averageQualitiesPerGroup = new double[groups.length];

        for (int t = 0; t < tileNumbers.length; t++) {
            for (int i = 0; i < groups.length; i++) {
                averageQualitiesPerGroup[i] += means[t][i];
            }
        }

        for (int i = 0; i < averageQualitiesPerGroup.length; i++) {
            averageQualitiesPerGroup[i] /= tileNumbers.length;
        }

        for (int i = 0; i < groups.length; i++) {
            for (int t = 0; t < tileNumbers.length; t++) {
                means[t][i] -= averageQualitiesPerGroup[i];
                if (Math.abs(means[t][i]) > maxDeviation) {
                    maxDeviation = Math.abs(means[t][i]);
                }
            }
        }

        this.maxDeviation = maxDeviation;

        calculated = true;

    }

    private char[] calculateOffsets() {
        // Works out from the set of chars what is the most
        // likely encoding scale for this file.

        char minChar = 0;
        char maxChar = 0;

        // Use the data from the first tile
        QualityCount[] qualityCounts = perTileQualityCounts.get(perTileQualityCounts.keySet().toArray()[0]);

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

        // Check if we can skip counting because the module is being ignored anyway
        if (totalCount == 0) {
            if (ModuleConfig.getParam("tile", "ignore") > 0) {
                ignoreInReport = true;
            }
        }

        // Don't waste time calculating this if we're not going to use it anyway
        if (ignoreInReport) {
            return;
        }

        calculated = false;

        // Try to find the tile id.  This can come in one of two forms:
        //		@HWI-1KL136:211:D1LGAACXX:1:1101:18518:48851 3:N:0:ATGTCA
        //                                ^
        //		@HWUSI-EAS493_0001:2:1:1000:16900#0/1
        //                         ^
        // These would appear at sections 2 or 3 of an array split on :
        // This module does quite a lot of work and ends up being the limiting
        // step when calculating.  We'll therefore take only a sample of the
        // sequences to try to get a representative selection.
        ++totalCount;
        if (totalCount % 10 != 0) {
            return;
        }

        // First try to split the id by :
        int tile = 0;

        String[] splitID = sequence.getID().split(":");

        // If there are 7 or more fields then it's a 1.8+ file
        try {

            if (splitPosition >= 0) {
                // We've found a split position before so let's try to use it again

                if (splitID.length <= splitPosition) {
                    // There isn't enough data in this header to split the way we did before
                    throw new NumberFormatException("Can't extract a number - not enough data");
                }

                tile = Integer.parseInt(splitID[splitPosition]);
            } else if (splitID.length >= 7) {
                splitPosition = 4;
                tile = Integer.parseInt(splitID[4]);
            } else if (splitID.length >= 5) {
                splitPosition = 2;
                // We can try the older format
                tile = Integer.parseInt(splitID[2]);
            } else {
                // We're not going to get a tile out of this
                ignoreInReport = true;
                return;
            }

        } catch (NumberFormatException nfe) {
            // This doesn't conform
            ignoreInReport = true;
            return;
        }

        char[] qual = sequence.getQualityString().toCharArray();
        if (currentLength < qual.length) {

            Iterator<Integer> tiles = perTileQualityCounts.keySet().iterator();
            while (tiles.hasNext()) {
                int thisTile = tiles.next();

                QualityCount[] qualityCounts = perTileQualityCounts.get(thisTile);
                QualityCount[] qualityCountsNew = new QualityCount[qual.length];

                for (int i = 0; i < qualityCounts.length; i++) {
                    qualityCountsNew[i] = qualityCounts[i];
                }
                for (int i = qualityCounts.length; i < qualityCountsNew.length; i++) {
                    qualityCountsNew[i] = new QualityCount();
                }
                perTileQualityCounts.put(thisTile, qualityCountsNew);
            }

            currentLength = qual.length;

        }

        if (!perTileQualityCounts.containsKey(tile)) {

            if (perTileQualityCounts.size() > 1000) {
                // There are too many tiles, so we're probably parsing this wrong.
                // Let's give up
                System.err.println("Too many tiles (>1000) so giving up trying to do per-tile qualities since we're probably parsing the file wrongly");
                ignoreInReport = true;
                perTileQualityCounts.clear();
                return;
            }

            QualityCount[] qualityCounts = new QualityCount[currentLength];
            for (int i = 0; i < currentLength; i++) {
                qualityCounts[i] = new QualityCount();
            }

            perTileQualityCounts.put(tile, qualityCounts);
        }

        QualityCount[] qualityCounts = perTileQualityCounts.get(tile);

        for (int i = 0; i < qual.length; i++) {
            qualityCounts[i].addValue(qual[i]);
        }

    }

    public void mergeFrom(MergeableQCModule other) {
        PerTileQualityScores o = (PerTileQualityScores) other;
        totalCount += o.totalCount;

        // If the other worker decided to ignore this module, propagate that.
        if (o.ignoreInReport) {
            ignoreInReport = true;
            return;
        }

        // Expand existing tile arrays if the other worker saw longer reads.
        if (o.currentLength > currentLength) {
            for (java.util.Map.Entry<Integer, QualityCount[]> entry : perTileQualityCounts.entrySet()) {
                QualityCount[] existing = entry.getValue();
                QualityCount[] expanded = new QualityCount[o.currentLength];
                for (int i = 0; i < existing.length; i++) {
                    expanded[i] = existing[i];
                }
                for (int i = existing.length; i < expanded.length; i++) {
                    expanded[i] = new QualityCount();
                }
                perTileQualityCounts.put(entry.getKey(), expanded);
            }
            currentLength = o.currentLength;
        }

        // Merge per-tile quality counts.
        for (java.util.Map.Entry<Integer, QualityCount[]> entry : o.perTileQualityCounts.entrySet()) {
            int tile = entry.getKey();
            QualityCount[] incoming = entry.getValue();

            if (perTileQualityCounts.containsKey(tile)) {
                QualityCount[] existing = perTileQualityCounts.get(tile);
                int len = Math.min(existing.length, incoming.length);
                for (int i = 0; i < len; i++) {
                    existing[i].mergeFrom(incoming[i]);
                }
            } else {
                // New tile: pad to currentLength if necessary.
                if (incoming.length < currentLength) {
                    QualityCount[] padded = new QualityCount[currentLength];
                    for (int i = 0; i < incoming.length; i++) {
                        padded[i] = incoming[i];
                    }
                    for (int i = incoming.length; i < currentLength; i++) {
                        padded[i] = new QualityCount();
                    }
                    perTileQualityCounts.put(tile, padded);
                } else {
                    perTileQualityCounts.put(tile, incoming);
                }
            }
        }

        // Adopt a valid split-position if we don't have one yet.
        if (splitPosition < 0) {
            splitPosition = o.splitPosition;
        }
        calculated = false;
    }

    public void reset() {
        totalCount = 0;
        perTileQualityCounts = new HashMap<>();
    }

    public String description() {
        return "Shows the perl tile Quality scores of all bases at a given position in a sequencing run";
    }

    public String name() {
        return "Per Tile Sequence Quality";
    }

    public boolean raisesError() {
        if (!calculated) {
            getPercentages();
        }

        if (maxDeviation > ModuleConfig.getParam("tile", "error")) {
            return true;
        }
        return false;
    }

    public boolean raisesWarning() {
        if (!calculated) {
            getPercentages();
        }

        if (maxDeviation > ModuleConfig.getParam("tile", "warn")) {
            return true;
        }
        return false;
    }

    public void makeReport(HTMLReportArchive report, int No) throws IOException, XMLStreamException {
        if (!calculated) {
            getPercentages();
        }

        if (No == 1) {
            writeDefaultImage(report, "per_tile_quality.png", "Per Base Quality Graph", Math.max(800, xLabels.length * 15), 600);
        } else {
            writeDefaultImage(report, "per_tile_quality2.png", "Per Base Quality Graph", Math.max(800, xLabels.length * 15), 600);
        }

        StringBuffer sb = report.dataDocument();
        sb.append("#Tile\tBase\tMean\n");

        for (int t = 0; t < tiles.length; t++) {
            for (int i = 0; i < means[t].length; i++) {

                sb.append(tiles[t]);
                sb.append("\t");

                sb.append(xLabels[i]);
                sb.append("\t");

                sb.append(means[t][i]);

                sb.append("\n");
            }
        }
    }

    private double getMean(int tile, int minbp, int maxbp, int offset) {
        int count = 0;
        double total = 0;

        QualityCount[] qualityCounts = perTileQualityCounts.get(tile);
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
