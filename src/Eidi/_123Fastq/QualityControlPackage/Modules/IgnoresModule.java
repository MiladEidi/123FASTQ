package Eidi._123Fastq.QualityControlPackage.Modules;

import Eidi._123Fastq.QualityControlPackage.Report.HTMLReportArchive;
import Eidi._123Fastq.QualityControlPackage.Sequence.Sequence;
import java.awt.BorderLayout;
import java.awt.Font;
import java.io.IOException;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.xml.stream.XMLStreamException;

public class IgnoresModule extends AbstractQCModule {

    private String name;
    public IgnoresModule(String Name) {
        this.name = Name;
    }

    @Override
    public void processSequence(Sequence sequence) {
    }

    @Override
    public JPanel getResultsPanel() {
        JPanel ignore = new JPanel(new BorderLayout());
        JLabel l = new JLabel("Nothing to show. This chart isn't suitable for inputed reads.", JLabel.CENTER);
        l.setFont(new Font("Segoe UI", 1, 18));
        ignore.add(l, BorderLayout.CENTER);
        return ignore;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String description() {
        return name;
    }

    @Override
    public void reset() {
    }

    @Override
    public boolean raisesError() {
        return true;
    }

    @Override
    public boolean raisesWarning() {
        return false;
    }

    @Override
    public boolean ignoreFilteredSequences() {
        return true;
    }

    @Override
    public boolean ignoreInReport() {
        return false;
    }

    @Override
    public void makeReport(HTMLReportArchive report, int No) throws XMLStreamException, IOException {
        if (No == 1) {
            writeDefaultImage(report, name + ".png", name, 800, 600);
        } else {
            writeDefaultImage(report, name + "2.png", name, 800, 600);
        }
    }
}
