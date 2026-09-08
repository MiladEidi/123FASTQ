package Eidi._123Fastq.QualityControlPackage.Modules;

import Eidi._123Fastq.QualityControlPackage.Graphs.LogarithmicAdapterDeduplicatedLineGraph;
import Eidi._123Fastq.QualityControlPackage.Graphs.BaseGroup;
import Eidi._123Fastq.QualityControlPackage.Report.HTMLReportArchive;
import Eidi._123Fastq.QualityControlPackage.Sequence.Contaminant.ContaminentFinder;
import Eidi._123Fastq.QualityControlPackage.Sequence.Sequence;
import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Vector;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.table.AbstractTableModel;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class AdapterContent extends AbstractQCModule implements MergeableQCModule {

    private int longestSequence = 0;
    private int longestAdapter = 0;
    private long totalCount = 0;
    public boolean calculated = false;
    // This is the full set of Kmers to be reported
    private Adapter[] adapters;
    // This is the data for the Kmers which are going to be placed on the graph
    private double[][] enrichments_Logarithmic = null;
    private double[][] enrichments = null;

    private String[] labels;
    private String[] xLabels = new String[0];
    BaseGroup[] groups;
    private int mostCount;

    public AdapterContent() {

        Vector<Adapter> c = new Vector<>();
        Vector<String> l = new Vector<>();
        InputStream rsrc;
        try {

            //For Compiled Version
            try {
                String filesDirectory = (new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath()).getParent() + "/").replace("%20", " ");
                File adaptersFile = new File(filesDirectory + "dependencies/adapter_list.txt");
                rsrc = new FileInputStream(adaptersFile.getAbsolutePath());
            } catch (Exception e) {
                //for src version
                System.out.println("Embedded adapter list file has been used.");
                rsrc = ContaminentFinder.class.getResourceAsStream("/Eidi/_123Fastq/QualityControlPackage/QC_Configs/adapter_list.txt");
            }

            if (rsrc == null) {
                //add here progbar string
                throw new FileNotFoundException("cannot find \"adapter_list.txt\" file.");
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(rsrc));
            String line;
            while ((line = br.readLine()) != null) {

                if (line.startsWith("#")) {
                    continue; // Skip comments
                }
                if (line.trim().length() == 0) {
                    continue; // Skip blank lines
                }
                String[] sections = line.split("\\t+");
                if (sections.length != 2) {
                    System.err.println("Expected 2 sections for contaminant line but got " + sections.length + " from " + line);
                    continue;
                }
                Adapter adapter = new Adapter(sections[0], sections[1]);
                c.add(adapter);
                l.add(adapter.name());
                if (adapter.sequence().length() > longestAdapter) {
                    longestAdapter = adapter.sequence().length();
                }
            }
            labels = l.toArray(new String[0]);

            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        adapters = c.toArray(new Adapter[0]);
    }

    public boolean ignoreFilteredSequences() {
        return true;
    }

    public boolean ignoreInReport() {
        return ModuleConfig.getParam("adapter", "ignore") > 0;
    }

    public JPanel getResultsPanel() {

        if (longestAdapter > longestSequence) {
            // We can't display sensible results
            JPanel failPanel = new JPanel();
            failPanel.setLayout(new BorderLayout());
            failPanel.add(new JLabel("Can't analyse adapters as read length is too short (" + longestAdapter + " vs " + longestSequence + ")", JLabel.CENTER), BorderLayout.CENTER);
            return failPanel;
        }

        if (!calculated) {
            calculateEnrichment();
        }

        if (ignoreInReport()) {
            JPanel returnPanel = new JPanel();
            returnPanel.add(new JLabel("Ignore in report", JLabel.CENTER), BorderLayout.CENTER);
            return returnPanel;
        }
        return (new LogarithmicAdapterDeduplicatedLineGraph(enrichments_Logarithmic, Math.log10(1d), Math.log10(101d), "Position in read (bp)", labels, xLabels, "Logarithmic Adapter existence percentage in reads positions"));

    }

    public void processSequence(Sequence sequence) {
        calculated = false;
        ++totalCount;
        // We need to be careful about making sure that a sequence is not only longer
        // than we've seen before, but also that the last position we could find a hit
        // is a positive position.
        if (sequence.getSequence().length() > longestSequence && sequence.getSequence().length() - longestAdapter > 0) {
            longestSequence = sequence.getSequence().length();
            for (int a = 0; a < adapters.length; a++) {
                adapters[a].expandLengthTo(longestSequence - longestAdapter);
            }
        }

        // Now we go through all of the Adapters to see where they occur
        for (int a = 0; a < adapters.length; a++) {
            int index = sequence.getSequence().indexOf(adapters[a].sequence());
            if (index >= 0) {
//                adapters[a].setExistenceFlag(true);
                adapters[a].Counter();
                for (int i = index; i <= longestSequence - longestAdapter; i++) {
                    adapters[a].incrementCount(i);
                }
            }
        }
    }

    public int getMostCount() {
        return mostCount;
    }

    public String mostIncidenceAdapter() {
        Adapter most = adapters[0];
        for (int i = 0; i < adapters.length; i++) {
            if (adapters[i].adapterCount > most.adapterCount) {
                most = adapters[i];
            }
        }
        if (most.adapterCount < (totalCount / 1000)) {
            return null;
        }
        mostCount = most.adapterCount;
        return most.name;
    }

    public String mostIncidenceAdapterSequence() {
        Adapter most = adapters[0];
        for (int i = 0; i < adapters.length; i++) {
            if (adapters[i].adapterCount > most.adapterCount) {
                most = adapters[i];
            }
        }
        //false-positive adapters exclusion
        if (totalCount <= 10000) {
            return null;
        }
        //adapter sequence hits below 0.1% of reads are skipped.
        if (most.adapterCount < (totalCount / 1000)) {
            return null;
        }
        mostCount = most.adapterCount;
        return most.sequence;
    }

    public synchronized void calculateEnrichment() {
        int maxLength = 0;
        for (int a = 0; a < adapters.length; a++) {
            if (adapters[a].getPositions().length > maxLength) {
                maxLength = adapters[a].getPositions().length;
            }
        }
        groups = BaseGroup.makeBaseGroups(maxLength);
        xLabels = new String[groups.length];
        for (int i = 0; i < xLabels.length; i++) {
            xLabels[i] = groups[i].toString();
        }
        enrichments_Logarithmic = new double[adapters.length][groups.length];
        enrichments = new double[adapters.length][groups.length];
        for (int a = 0; a < adapters.length; a++) {
            long[] positions = adapters[a].positions;
            for (int g = 0; g < groups.length; g++) {
                for (int p = groups[g].lowerCount() - 1; p < groups[g].upperCount() && p < positions.length; p++) {
                    enrichments[a][g] += (positions[p] * 100d) / totalCount;
                    enrichments_Logarithmic[a][g] += Math.log10(((positions[p] * 100d) / totalCount) + 1);
                }
                enrichments_Logarithmic[a][g] /= (groups[g].upperCount() - groups[g].lowerCount()) + 1;
                enrichments[a][g] /= (groups[g].upperCount() - groups[g].lowerCount()) + 1;
            }
        }
        calculated = true;
    }

    public void mergeFrom(MergeableQCModule other) {
        AdapterContent o = (AdapterContent) other;
        totalCount += o.totalCount;
        if (o.longestSequence > longestSequence) {
            longestSequence = o.longestSequence;
        }
        for (int a = 0; a < adapters.length; a++) {
            adapters[a].mergeFrom(o.adapters[a]);
        }
        calculated = false;
    }

    public void reset() {
        calculated = false;
        totalCount = 0;
        longestSequence = 0;
        for (int a = 0; a < adapters.length; a++) {
            adapters[a].reset();
        }
    }

    public String description() {
        return "Searches for specific adapter sequences in a library";
    }

    public String name() {
        return "Adapter Content";
    }

    public boolean raisesError() {
        if (!calculated) {
            calculateEnrichment();
        }

        for (int i = 0; i < enrichments.length; i++) {
            for (int j = 0; j < enrichments[i].length; j++) {
                if (enrichments[i][j] > ModuleConfig.getParam("adapter", "error")) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean raisesWarning() {

        // We warn if we just couldn't run the analysis
        if (longestAdapter > longestSequence) {
            return true;
        }

        if (!calculated) {
            calculateEnrichment();
        }

        for (int i = 0; i < enrichments.length; i++) {
            for (int j = 0; j < enrichments[i].length; j++) {
                if (enrichments[i][j] > ModuleConfig.getParam("adapter", "warn")) {
                    return true;
                }
            }
        }
        return false;
    }

    public void makeReport(HTMLReportArchive report, int No) throws IOException, XMLStreamException {

        //should be handled in the comprative-mode
        if (longestAdapter > longestSequence) {
            XMLStreamWriter xhtml = report.xhtmlStream();
            xhtml.writeStartElement("p");
            xhtml.writeCharacters("Can't analyse adapters as read length is too short (" + longestAdapter + " vs " + longestSequence + ")");
            xhtml.writeEndElement();
        } else {

            if (!calculated) {
                calculateEnrichment();
            }

            if (No == 1) {
                writeDefaultImage(report, "adapter_content.png", "Adapter graph", Math.max(800, groups.length * 15), 600);
            } else {
                writeDefaultImage(report, "adapter_content2.png", "Adapter graph", Math.max(800, groups.length * 15), 600);
            }

            StringBuffer sb = report.dataDocument();

            ResultsTable table = new ResultsTable();
            // Header
            sb.append("#");
            for (int i = 0; i < table.getColumnCount(); i++) {
                if (i > 0) {
                    sb.append("\t");
                }
                sb.append(table.getColumnName(i));
            }
            sb.append("\n");

            for (int r = 0; r < table.getRowCount(); r++) {
                for (int c = 0; c < table.getColumnCount(); c++) {
                    if (c > 0) {
                        sb.append("\t");
                    }
                    sb.append(table.getValueAt(r, c));
                }
                sb.append("\n");
            }
        }
    }

    private class Adapter {

        private String name;
        private String sequence;
        private long[] positions = new long[0];
        private int adapterCount;

        public Adapter(String name, String sequence) {
            this.name = name;
            this.sequence = sequence;
            positions = new long[1];
        }

        public void Counter() {
            adapterCount++;
        }

        public void incrementCount(int position) {
            if (position >= positions.length) {
                expandLengthTo(position + 1);
            }
            ++positions[position];
        }

        public void expandLengthTo(int newLength) {
            long[] newPositions = new long[newLength];
            for (int i = 0; i < positions.length; i++) {
                newPositions[i] = positions[i];
                //System.err.println("Copied value "+positions[i]+" at position "+i);
            }
            // Copy the current longest value to the newly added slots
            if (positions.length > 0) {
                for (int i = positions.length; i < newPositions.length; i++) {
                    newPositions[i] = positions[positions.length - 1];
                }
            }
            positions = newPositions;
        }

        public long[] getPositions() {
            return positions;
        }

        public String sequence() {
            return sequence;
        }

        public void mergeFrom(Adapter other) {
            adapterCount += other.adapterCount;
            // Expand with zeros (not cumulative copy) so we sum cleanly.
            if (other.positions.length > positions.length) {
                long[] expanded = new long[other.positions.length];
                for (int i = 0; i < positions.length; i++) {
                    expanded[i] = positions[i];
                }
                positions = expanded;
            }
            for (int i = 0; i < other.positions.length; i++) {
                positions[i] += other.positions[i];
            }
        }

        public void reset() {
            positions = new long[0];
        }

        public String name() {
            return name;
        }
    }

    private class ResultsTable extends AbstractTableModel {

        private static final long serialVersionUID = 1L;

        public ResultsTable() {
        }

        // Sequence - Count - Obs/Exp
        public int getColumnCount() {
            return adapters.length + 1;
        }

        public int getRowCount() {
            return enrichments_Logarithmic[0].length;
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            if (columnIndex == 0) {
                return xLabels[rowIndex];
            }
            return enrichments_Logarithmic[columnIndex - 1][rowIndex];
        }

        public String getColumnName(int columnIndex) {
            if (columnIndex == 0) {
                return "Position";
            }
            return (labels[columnIndex - 1]);
        }

        public Class<?> getColumnClass(int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return String.class;
            }
            return Double.class;
        }
    }
}
