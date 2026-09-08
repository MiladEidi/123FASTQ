package Eidi._123Fastq.QualityControlPackage.Modules;

import Eidi._123Fastq.QualityControlPackage.Report.HTMLReportArchive;
import Eidi._123Fastq.QualityControlPackage.Sequence.Contaminant.ContaminantHit;
import Eidi._123Fastq.QualityControlPackage.Sequence.Contaminant.ContaminentFinder;
import Eidi._123Fastq.QualityControlPackage.Sequence.Sequence;
import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class OverRepresentedSeqs extends AbstractQCModule implements MergeableQCModule {

    protected HashMap<String, Integer> sequences = new HashMap<>();
    protected long count = 0;
    private OverrepresentedSeq[] overrepresntedSeqs = null;
    private boolean calculated = false;
    private boolean frozen = false;
    private DuplicationLevel duplicationModule;

    // This is the number of different sequences we want to track
    private final int OBSERVATION_CUTOFF = 100000;
    // This is a count of how many unique sequences we've seen so far
    // so we know when to stop adding them.
    private int uniqueSequenceCount = 0;
    // This was the total count at the point at which we saw our total
    // number of unique sequences, so we know what to correct by when
    // extrapolating to the whole file
    protected long countAtUniqueLimit = 0;
    private File barcode;

    public OverRepresentedSeqs() {
        duplicationModule = new DuplicationLevel(this);
    }

    public boolean ignoreInReport() {
        if (ModuleConfig.getParam("overrepresented", "ignore") > 0) {
            return true;
        }
        return false;
    }

    public String description() {
        return "Identifies sequences which are overrepresented in the set";
    }

    public boolean ignoreFilteredSequences() {
        return true;
    }

    public DuplicationLevel duplicationLevelModule() {
        return duplicationModule;
    }

//    public void barcodeSave() throws IOException {
//        JFileChooser SaveTo;
//        if (lastDirectory == null) {
//            SaveTo = new JFileChooser();
//        } else {
//            SaveTo = new JFileChooser(lastDirectory);
//        }
//        SaveTo.setMultiSelectionEnabled(false);
//        txtFileFilter sff = new txtFileFilter();
//        SaveTo.setFileFilter(sff);
//        if (SaveTo.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
//            barcode = SaveTo.getSelectedFile();
//            if (barcode.isFile()) {
//                int result = JOptionPane.showConfirmDialog(null, barcode.getName() + " already exists!\nDo you want to replace?", "Overwrite", JOptionPane.YES_NO_OPTION);
//                if (result == JOptionPane.NO_OPTION) {
//                    return;
//                }
//            }
//            lastDirectory = barcode.getParent();
//            writeBarcode(barcode);
//        }
//    }

//    public void writeBarcode(File barcode) throws IOException {
//        FileWriter write = new FileWriter(barcode);
//        PrintWriter printer = new PrintWriter(write);
//        printer.println("#This barcode splitter compatible text file created based on overrepresented sequences in your file.");
//        printer.println("#Change sample names if you want to specify barcode output files.");
//        printer.println("#Created by 123FASTQ");
//        printer.println("\n");
//        int index = 1;
//        for (OverrepresentedSeq overrepresntedSeq : overrepresntedSeqs) {
//            printer.println("Sample" + index + "\t" + overrepresntedSeq.seq());
//            index++;
//        }
//        printer.close();
//    }

    public JPanel getResultsPanel() {
        JPanel returnPanel = new JPanel();
        returnPanel.setLayout(new BorderLayout());
        returnPanel.add(new JLabel("Overrepresented Sequences", JLabel.CENTER), BorderLayout.NORTH);
        if (!calculated) {
            getOverrepresentedSeqs();
        }
       if (ignoreInReport()) {
            returnPanel.add(new JLabel("Ignore in report", JLabel.CENTER), BorderLayout.CENTER);
            return returnPanel;
        }
        if (overrepresntedSeqs.length > 0) {
            TableModel model = new ResultsTable(overrepresntedSeqs);
            JTable table = new JTable(model);
            table.setCellSelectionEnabled(true);
            returnPanel.add(new JScrollPane(table), BorderLayout.CENTER);
//            JButton btnMakeBarcodeFile = new JButton("Make Barcode File");
//            btnMakeBarcodeFile.setFont(new java.awt.Font("Segoe UI", 1, 18));
//            btnMakeBarcodeFile.addActionListener((ActionEvent e) -> {
//                try {
//                    barcodeSave();
//                } catch (IOException ex) {
//                    Logger.getLogger(OverRepresentedSeqs.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            });
//            returnPanel.add(btnMakeBarcodeFile, BorderLayout.SOUTH);
        } else {
            returnPanel.add(new JLabel("There are no overrepresented sequences", JLabel.CENTER), BorderLayout.CENTER);
        }
        return returnPanel;
    }

    public DuplicationLevel getDuplicationLevelModule() {
        return duplicationModule;
    }

    private synchronized void getOverrepresentedSeqs() {

        // If the duplication module hasn't already done
        // its calculation it needs to do it now before
        // we stomp all over the data
        duplicationModule.calculateLevels();

        Iterator<String> s = sequences.keySet().iterator();
        List<OverrepresentedSeq> keepers = new ArrayList<OverrepresentedSeq>();

        while (s.hasNext()) {
            String seq = s.next();
            double percentage = ((double) sequences.get(seq) / count) * 100;
            if (percentage > ModuleConfig.getParam("overrepresented", "warn")) {
                OverrepresentedSeq os = new OverrepresentedSeq(seq, sequences.get(seq), percentage);
                keepers.add(os);
            }
        }

        overrepresntedSeqs = keepers.toArray(new OverrepresentedSeq[0]);
        Arrays.sort(overrepresntedSeqs);
        calculated = true;
        sequences.clear();

    }

    public void mergeFrom(MergeableQCModule other) {
        OverRepresentedSeqs o = (OverRepresentedSeqs) other;
        count += o.count;

        // Merge the per-sequence observation counts.
        for (java.util.Map.Entry<String, Integer> entry : o.sequences.entrySet()) {
            Integer existing = sequences.get(entry.getKey());
            if (existing != null) {
                sequences.put(entry.getKey(), existing + entry.getValue());
            } else if (!frozen) {
                sequences.put(entry.getKey(), entry.getValue());
                ++uniqueSequenceCount;
                if (uniqueSequenceCount >= OBSERVATION_CUTOFF) {
                    frozen = true;
                }
            }
        }

        // After merging we treat all tracked sequences as fully observed.
        // This avoids complex inter-worker extrapolation and is equivalent
        // to the serial behaviour whenever the file has < OBSERVATION_CUTOFF
        // unique sequences (the common case).
        countAtUniqueLimit = count;
        calculated = false;
    }

    public void reset() {
        count = 0;
        sequences.clear();
    }

    public String name() {
        return "Overrepresented Sequences";
    }

    public void processSequence(Sequence sequence) {

        calculated = false;

        ++count;

        // Since we rely on identity to match sequences we can't trust really long
        // sequences, so anything over 75bp gets truncated to 50bp.
        String seq = sequence.getSequence();
        if (seq.length() > 75) {
            seq = new String(seq.substring(0, 50));
        }

        if (sequences.containsKey(seq)) {
            sequences.put(seq, sequences.get(seq) + 1);

            // We need to increment the count at unique limit just in case
            // we never hit the unique sequence limit, so we need to know
            // that we'd actually seen all of the sequences.
            if (!frozen) {
                countAtUniqueLimit = count;
            }
        } else {
            if (!frozen) {
                sequences.put(seq, 1);
                ++uniqueSequenceCount;
                countAtUniqueLimit = count;
                if (uniqueSequenceCount == OBSERVATION_CUTOFF) {
                    frozen = true;
                }

            }
        }

    }

    @SuppressWarnings("serial")
    private class ResultsTable extends AbstractTableModel {

        private OverrepresentedSeq[] seqs;

        public ResultsTable(OverrepresentedSeq[] seqs) {
            this.seqs = seqs;
        }

        // Sequence - Count - Percentage
        public int getColumnCount() {
            return 4;
        }

        public int getRowCount() {
            return seqs.length;
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return seqs[rowIndex].seq();
                case 1:
                    return seqs[rowIndex].count();
                case 2:
                    return seqs[rowIndex].percentage();
                case 3:
                    return seqs[rowIndex].contaminantHit();

            }
            return null;
        }

        public String getColumnName(int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return "Sequence";
                case 1:
                    return "Count";
                case 2:
                    return "Percentage";
                case 3:
                    return "Possible Source";
            }
            return null;
        }

        public Class<?> getColumnClass(int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return String.class;
                case 1:
                    return Integer.class;
                case 2:
                    return Double.class;
                case 3:
                    return String.class;
            }
            return null;

        }
    }

    private class OverrepresentedSeq implements Comparable<OverrepresentedSeq> {

        private String seq;
        private int count;
        private double percentage;
        private ContaminantHit contaminantHit;

        public OverrepresentedSeq(String seq, int count, double percentage) {
            this.seq = seq;
            this.count = count;
            this.percentage = percentage;
            this.contaminantHit = ContaminentFinder.findContaminantHit(seq);
        }

        public String seq() {
            return seq;
        }

        public int count() {
            return count;
        }

        public double percentage() {
            return percentage;
        }

        public String contaminantHit() {
            if (contaminantHit == null) {
                return "No Hit";
            } else {
                return contaminantHit.toString();
            }
        }

        public int compareTo(OverrepresentedSeq o) {
            return o.count - count;
        }
    }

    public boolean raisesError() {
        if (!calculated) {
            getOverrepresentedSeqs();
        }
        if (overrepresntedSeqs.length > 0) {
            if (overrepresntedSeqs[0].percentage > ModuleConfig.getParam("overrepresented", "error")) {
                return true;
            }
        }
        return false;
    }

    public boolean raisesWarning() {
        if (!calculated) {
            getOverrepresentedSeqs();
        }

        if (overrepresntedSeqs.length > 0) {
            return true;
        }
        return false;
    }

    public void makeReport(HTMLReportArchive report, int No) throws IOException, XMLStreamException {

        if (!calculated) {
            getOverrepresentedSeqs();
        }
        ResultsTable table = new ResultsTable(overrepresntedSeqs);

        if (overrepresntedSeqs.length == 0) {
            XMLStreamWriter w = report.xhtmlStream();
            w.writeStartElement("p");
            w.writeCharacters("No overrepresented sequences");
            w.writeEndElement();
        } else {
            //
            super.writeTable(report, table);
        }
    }
}
