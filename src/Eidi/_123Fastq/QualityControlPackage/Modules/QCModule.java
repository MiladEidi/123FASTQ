package Eidi._123Fastq.QualityControlPackage.Modules;

import Eidi._123Fastq.QualityControlPackage.Report.HTMLReportArchive;
import Eidi._123Fastq.QualityControlPackage.Sequence.Sequence;
import java.io.IOException;
import javax.swing.JPanel;
import javax.xml.stream.XMLStreamException;

public interface QCModule {

	public void processSequence(Sequence sequence);

	public JPanel getResultsPanel();

	public String name ();

	public String description ();

	public void reset ();

	public boolean raisesError();

	public boolean raisesWarning();

	public boolean ignoreFilteredSequences();

	public boolean ignoreInReport();

	public void makeReport(HTMLReportArchive report, int No) throws XMLStreamException,IOException;
}
