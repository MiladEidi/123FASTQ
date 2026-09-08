package Eidi._123Fastq.QualityControlPackage.Modules;

import Eidi._123Fastq.QualityControlPackage.Graphs.BaseGroup;
import Eidi._123Fastq.QualityControlPackage.Graphs.PercentLineGraph;
import Eidi._123Fastq.QualityControlPackage.Report.HTMLReportArchive;
import Eidi._123Fastq.QualityControlPackage.Sequence.Sequence;
import java.awt.BorderLayout;
import java.io.IOException;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.xml.stream.XMLStreamException;

public class PerBaseSequenceContent extends AbstractQCModule implements MergeableQCModule {

    public long[] gCounts = new long[0];
    public long[] aCounts = new long[0];
    public long[] cCounts = new long[0];
    public long[] tCounts = new long[0];
    private double[][] percentages = null;
    private String[] xCategories = new String[0];
    private boolean calculated = false;
    private Integer EndCropbps = null;
    private Integer EndCropThreshold = null;
    private boolean FlagEndCrop;

    public JPanel getResultsPanel() {

        if (!calculated) {
            getPercentages();
        }
        if (ignoreInReport()) {
            JPanel returnPanel = new JPanel();
            returnPanel.add(new JLabel("Ignore in report", JLabel.CENTER), BorderLayout.CENTER);
            return returnPanel;
        }
        return new PercentLineGraph(percentages, 0d, 100d, "Position in read (bp)", new String[]{"%T", "%C", "%A", "%G"}, xCategories, "Sequence content across all bases");
    }

    public boolean ignoreFilteredSequences() {
        return true;
    }

    public boolean ignoreInReport() {
        if (ModuleConfig.getParam("sequence", "ignore") > 0) {
            return true;
        }
        return false;
    }

    private synchronized void getPercentages() {

        BaseGroup[] groups = BaseGroup.makeBaseGroups(gCounts.length);
        xCategories = new String[groups.length];
        double[] gPercent = new double[groups.length];
        double[] aPercent = new double[groups.length];
        double[] tPercent = new double[groups.length];
        double[] cPercent = new double[groups.length];
        long total;
        long gCount;
        long aCount;
        long tCount;
        long cCount;
        for (int i = 0; i < groups.length; i++) {
            xCategories[i] = groups[i].toString();
            gCount = 0;
            aCount = 0;
            tCount = 0;
            cCount = 0;
            total = 0;
            for (int bp = groups[i].lowerCount() - 1; bp < groups[i].upperCount(); bp++) {
                total += gCounts[bp];
                total += cCounts[bp];
                total += aCounts[bp];
                total += tCounts[bp];
                aCount += aCounts[bp];
                tCount += tCounts[bp];
                cCount += cCounts[bp];
                gCount += gCounts[bp];
            }
            gPercent[i] = (gCount / (double) total) * 100;
            aPercent[i] = (aCount / (double) total) * 100;
            tPercent[i] = (tCount / (double) total) * 100;
            cPercent[i] = (cCount / (double) total) * 100;
        }
        percentages = new double[][]{tPercent, cPercent, aPercent, gPercent};
        calculated = true;
    }

    public void processSequence(Sequence sequence) {
        calculated = false;
        char[] seq = sequence.getSequence().toCharArray();
        if (gCounts.length < seq.length) {
            long[] gCountsNew = new long[seq.length];
            long[] aCountsNew = new long[seq.length];
            long[] cCountsNew = new long[seq.length];
            long[] tCountsNew = new long[seq.length];
            for (int i = 0; i < gCounts.length; i++) {
                gCountsNew[i] = gCounts[i];
                aCountsNew[i] = aCounts[i];
                tCountsNew[i] = tCounts[i];
                cCountsNew[i] = cCounts[i];
            }
            gCounts = gCountsNew;
            aCounts = aCountsNew;
            tCounts = tCountsNew;
            cCounts = cCountsNew;
        }
        for (int i = 0; i < seq.length; i++) {
            switch (seq[i]) {
                case 'G':
                    ++gCounts[i];
                    break;
                case 'A':
                    ++aCounts[i];
                    break;
                case 'T':
                    ++tCounts[i];
                    break;
                case 'C':
                    ++cCounts[i];
                    break;
                default:
                    break;
            }
        }
    }

    public synchronized void mergeFrom(MergeableQCModule other) {
        PerBaseSequenceContent source = (PerBaseSequenceContent) other;
        ensureLength(source.gCounts.length);
        for (int i = 0; i < source.gCounts.length; i++) {
            gCounts[i] += source.gCounts[i];
            aCounts[i] += source.aCounts[i];
            cCounts[i] += source.cCounts[i];
            tCounts[i] += source.tCounts[i];
        }
        percentages = null;
        xCategories = new String[0];
        EndCropbps = null;
        EndCropThreshold = null;
        FlagEndCrop = false;
        calculated = false;
    }

    private void ensureLength(int length) {
        if (gCounts.length >= length) {
            return;
        }
        long[] gCountsNew = new long[length];
        long[] aCountsNew = new long[length];
        long[] cCountsNew = new long[length];
        long[] tCountsNew = new long[length];
        for (int i = 0; i < gCounts.length; i++) {
            gCountsNew[i] = gCounts[i];
            aCountsNew[i] = aCounts[i];
            tCountsNew[i] = tCounts[i];
            cCountsNew[i] = cCounts[i];
        }
        gCounts = gCountsNew;
        aCounts = aCountsNew;
        tCounts = tCountsNew;
        cCounts = cCountsNew;
    }

    public void reset() {
        gCounts = new long[0];
        aCounts = new long[0];
        tCounts = new long[0];
        cCounts = new long[0];
        percentages = null;
        xCategories = new String[0];
        EndCropbps = null;
        EndCropThreshold = null;
        FlagEndCrop = false;
        calculated = false;
    }

    public String description() {
        return "Shows the relative amounts of each base at each position in a sequencing run";
    }

    public String name() {
        return "Per Base Sequence Content";
    }

    public boolean raisesError() {
        if (!calculated) {
            getPercentages();
        }
        for (int i = 0; i < percentages[0].length; i++) {
            double gcDiff = Math.abs(percentages[1][i] - percentages[3][i]);
            double atDiff = Math.abs(percentages[0][i] - percentages[2][i]);
            if (gcDiff > ModuleConfig.getParam("sequence", "error") || atDiff > ModuleConfig.getParam("sequence", "error")) {
                if (!FlagEndCrop) {
                    CalculationOfStartOfError();
                }
                return true;
            }
        }
        return false;
    }

    private void CalculationOfStartOfError() {
        for (int i = 0; i < gCounts.length; i++) {
            double total = gCounts[i] + cCounts[i] + aCounts[i] + tCounts[i];
            //upper 20% non-tolerable
            if (Math.abs((((gCounts[i] + cCounts[i]) / total) * 100)
                    - ((((aCounts[i] + tCounts[i]) / total) * 100))) > 20) {
                //if errors occur under 60 percent of reads. It's better to have no suggestion.
                if ((double) i / (double) gCounts.length * 100 <= 60) {
                    FlagEndCrop = true;
                    return;
                }
                //if error came from last 15% of reads
                if ((double) i / (double) gCounts.length * 100 >= 85) {
                    EndCropbps = gCounts.length - i;
                    EndCropThreshold = i - 10;
                    if (EndCropThreshold < 0) {
                        EndCropThreshold = 0;
                    }
                    FlagEndCrop = true;
                    return;
                }
            }
        }
    }

    public boolean raisesWarning() {
        if (!calculated) {
            getPercentages();
        }
        for (int i = 0; i < percentages[0].length; i++) {
            double gcDiff = Math.abs(percentages[1][i] - percentages[3][i]);
            double atDiff = Math.abs(percentages[0][i] - percentages[2][i]);
            if (gcDiff > ModuleConfig.getParam("sequence", "warn") || atDiff > ModuleConfig.getParam("sequence", "warn")) {
                return true;
            }
        }
        return false;
    }

    public void makeReport(HTMLReportArchive report, int No) throws IOException, XMLStreamException {

        if (!calculated) {
            getPercentages();
        }

        if (No == 1) {
        writeDefaultImage(report, "per_base_sequence_content.png", "Per base sequence content", Math.max(800, xCategories.length * 15), 600);
        }else{
        writeDefaultImage(report, "per_base_sequence_content2.png", "Per base sequence content", Math.max(800, xCategories.length * 15), 600);
        }

        StringBuffer sb = report.dataDocument();
        sb.append("#Base\tG\tA\tT\tC\n");
        for (int i = 0; i < xCategories.length; i++) {
            sb.append(xCategories[i]);
            sb.append("\t");
            sb.append(percentages[3][i]);
            sb.append("\t");
            sb.append(percentages[2][i]);
            sb.append("\t");
            sb.append(percentages[0][i]);
            sb.append("\t");
            sb.append(percentages[1][i]);
            sb.append("\n");
        }
    }

    public Integer getEndCropThreshold() {
        return EndCropThreshold;
    }

    public Integer getEndCropbps() {
        return EndCropbps;
    }
}
