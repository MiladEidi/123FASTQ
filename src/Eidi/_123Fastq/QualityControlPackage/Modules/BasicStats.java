package Eidi._123Fastq.QualityControlPackage.Modules;

import Eidi._123Fastq.QualityControlPackage.Report.HTMLReportArchive;
import Eidi._123Fastq.QualityControlPackage.Sequence.QualityEncoding.PhredEncoding;
import Eidi._123Fastq.QualityControlPackage.Sequence.Sequence;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import javax.xml.stream.XMLStreamException;

public class BasicStats extends AbstractQCModule implements MergeableQCModule {

    private String name = null;
    private long actualCount = 0;
    private long filteredCount = 0;
    private int minLength = 0;
    private int maxLength = 0;
    private long gCount = 0;
    private long cCount = 0;
    private long aCount = 0;
    private long tCount = 0;
    @SuppressWarnings("unused")
    private long nCount = 0;
    private char lowestChar = 126;
    private String fileType = null;
    private JTable modelTable;

    NumberFormat formatter = new DecimalFormat("###,###,###,###");

    public BasicStats(String name) {
        this.name = name;
    }

    public int getMinLength() {
        return minLength;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public char getLowestChar() {
        return lowestChar;
    }

    public String getName() {
        return name;
    }

    public String description() {
        return "Calculates some basic statistics about the file";
    }

    public boolean ignoreFilteredSequences() {
        return false;
    }

    private void updateRowHeights() {
        for (int row = 0; row < modelTable.getRowCount(); row++) {
            int rowHeight = modelTable.getRowHeight();
            for (int column = 0; column < modelTable.getColumnCount(); column++) {
                Component comp = modelTable.prepareRenderer(modelTable.getCellRenderer(row, column), row, column);
                rowHeight = Math.max(rowHeight, comp.getPreferredSize().height);
                //add scrollable cell for high height
                modelTable.scrollRectToVisible(modelTable.getCellRect(row, column, true));
            }
            modelTable.setRowHeight(row, rowHeight);
        }
    }

    public JPanel getResultsPanel() {
        JPanel returnPanel = new JPanel();
        returnPanel.setLayout(new BorderLayout());
        JLabel Title = new JLabel("Basic statistics", JLabel.CENTER);
        Title.setFont(new java.awt.Font("Segoe UI", 1, 20));
        Title.setForeground(new Color(30, 26, 52));
        returnPanel.add(Title, BorderLayout.NORTH);
        TableModel model = new ResultsTable();
        modelTable = new JTable(model);
        modelTable.setFont(new java.awt.Font("Segoe UI", 1, 18));
        updateRowHeights();
        returnPanel.add(new JScrollPane(modelTable), BorderLayout.CENTER);
        return returnPanel;
    }

    public void reset() {
        minLength = 0;
        maxLength = 0;
        gCount = 0;
        cCount = 0;
        aCount = 0;
        tCount = 0;
        nCount = 0;
    }

    public String name() {
        return "Basic Statistics";
    }

    public void processSequence(Sequence sequence) {

        if (name == null) {
            name = sequence.file().name();
        }

        name = name.replaceFirst("stdin:", "");

        // If this is a filtered sequence we simply count it and move on. for poor quality reads.
        if (sequence.isFiltered()) {
            filteredCount++;
            return;
        }

        if (fileType == null) {
            if (sequence.getColorspace() != null) {
                fileType = "Colorspace converted to bases";
            } else {
                fileType = "Conventional base calls";
            }
        }

        //find min and max reads lengths
        actualCount++;

        //min and max len calculation.
        if (actualCount == 1) {
            minLength = sequence.getSequence().length();
            maxLength = sequence.getSequence().length();
        } else {
            if (sequence.getSequence().length() < minLength) {
                minLength = sequence.getSequence().length();
            }
            if (sequence.getSequence().length() > maxLength) {
                maxLength = sequence.getSequence().length();
            }
        }

        //Counter of ATCGN Contents of each read.
        char[] chars = sequence.getSequence().toCharArray();
        for (int c = 0; c < chars.length; c++) {
            switch (chars[c]) {
                case 'G':
                    ++gCount;
                    break;
                case 'A':
                    ++aCount;
                    break;
                case 'T':
                    ++tCount;
                    break;
                case 'C':
                    ++cCount;
                    break;
                case 'N':
                    ++nCount;
                    break;
            }
        }

        //Lowest char is 126.  inja bad keyfiat tarin baz mire tooye lowest char.
        chars = sequence.getQualityString().toCharArray();
        for (int c = 0; c < chars.length; c++) {
            if (chars[c] < lowestChar) {
                lowestChar = chars[c];
            }
        }
    }

    public void mergeFrom(MergeableQCModule other) {
        BasicStats o = (BasicStats) other;
        actualCount += o.actualCount;
        filteredCount += o.filteredCount;
        gCount += o.gCount;
        cCount += o.cCount;
        aCount += o.aCount;
        tCount += o.tCount;
        nCount += o.nCount;
        if (o.minLength > 0 && (minLength == 0 || o.minLength < minLength)) {
            minLength = o.minLength;
        }
        if (o.maxLength > maxLength) {
            maxLength = o.maxLength;
        }
        // lowestChar is initialised to 126 (meaning "no data") — take the smallest real value
        if (o.lowestChar < lowestChar) {
            lowestChar = o.lowestChar;
        }
        if (fileType == null) {
            fileType = o.fileType;
        }
    }

    public boolean raisesError() {
        return false;
    }

    public boolean raisesWarning() {
        return false;
    }

    public boolean ignoreInReport() {
        return false;
    }

    public void makeReport(HTMLReportArchive report, int No) throws XMLStreamException, IOException {
        //Handle in the comprative-mode
        super.writeTable(report, new ResultsTable());
    }

    @SuppressWarnings("serial")
    private class ResultsTable extends AbstractTableModel {

        private String[] rowNames = new String[]{
            "File Name",
            "File Type",
            "Encoding",
            "Total Sequences",
            "Sequences flagged as poor quality",
            "Sequence length",
            "%GC",};

        // Sequence - Count - Percentage
        public int getColumnCount() {
            return 2;
        }

        public int getRowCount() {
            return rowNames.length;
        }

        public Object getValueAt(int rowIndex, int columnIndex) {

            switch (columnIndex) {
                case 0:
                    return rowNames[rowIndex];
                case 1:
                    switch (rowIndex) {
                        case 0:
                            return name;
                        case 1:
                            return fileType;
                        case 2:
                            return PhredEncoding.getFastQEncodingOffset(lowestChar);
                        case 3:
                            return "" + formatter.format(actualCount);
                        case 4:
                            return "" + formatter.format(filteredCount);
                        case 5:
                            if (minLength == maxLength) {
                                return "" + minLength;
                            } else {
                                return minLength + "-" + maxLength;
                            }
                        case 6:
                            if (aCount + tCount + gCount + cCount > 0) {
                                return "" + (((gCount + cCount) * 100) / (aCount + tCount + gCount + cCount));
                            } else {
                                return 0;
                            }
                    }
            }
            return null;
        }

        public String getColumnName(int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return "Measure";
                case 1:
                    return "Value";
            }
            return null;
        }

        public Class<?> getColumnClass(int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return String.class;
                case 1:
                    return String.class;
            }
            return null;

        }
    }
}
