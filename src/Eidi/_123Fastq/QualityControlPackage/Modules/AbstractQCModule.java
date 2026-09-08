package Eidi._123Fastq.QualityControlPackage.Modules;

import Eidi._123Fastq.QualityControlPackage.Report.HTMLReportArchive;
import Eidi._123Fastq.QualityControlPackage.Utilities.ImageToBase64;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.table.TableModel;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public abstract class AbstractQCModule implements QCModule {

	protected void simpleXhtmlReport(HTMLReportArchive report,BufferedImage img,String alt) throws XMLStreamException {
		XMLStreamWriter xhtml = report.xhtmlStream();
		xhtml.writeStartElement("p");
		xhtml.writeEmptyElement("img");
		xhtml.writeAttribute("class", "indented");
		xhtml.writeAttribute("src", ImageToBase64.imageToBase64(img));
		xhtml.writeAttribute("alt", alt);

		if(img!=null){
			xhtml.writeAttribute("width",String.valueOf(img.getWidth()));
			xhtml.writeAttribute("height",String.valueOf(img.getHeight()));
		}
		xhtml.writeEndElement();//p
	}

	protected void writeDefaultImage (HTMLReportArchive report, String fileName, String imageTitle, int width, int height) throws IOException, XMLStreamException {
		ZipOutputStream zip = report.zipFile();
		zip.putNextEntry(new ZipEntry(report.folderName()+"/Images/"+fileName));
		BufferedImage b = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics g = b.createGraphics();



		JPanel resultsPanel = getResultsPanel();
		resultsPanel.setDoubleBuffered(false);
		resultsPanel.setSize(width,height);
		resultsPanel.addNotify();
		resultsPanel.validate();

		resultsPanel.print(g);

		g.dispose();

		ImageIO.write(b, "PNG", zip);
		zip.closeEntry();

		simpleXhtmlReport(report, b, imageTitle);

	}

	protected void writeSpecificImage (HTMLReportArchive report, JPanel resultsPanel, String fileName, String imageTitle, int width, int height) throws IOException, XMLStreamException {
		ZipOutputStream zip = report.zipFile();
		zip.putNextEntry(new ZipEntry(report.folderName()+"/Images/"+fileName));
		BufferedImage b = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics g = b.createGraphics();

		resultsPanel.setDoubleBuffered(false);
		resultsPanel.setSize(width,height);
		resultsPanel.addNotify();
		resultsPanel.validate();

		resultsPanel.print(g);

		g.dispose();

		ImageIO.write(b, "PNG", zip);
		zip.closeEntry();

		simpleXhtmlReport(report, b, imageTitle);

	}


	protected void writeTable(HTMLReportArchive report, TableModel table) throws IOException,XMLStreamException {
		writeXhtmlTable(report,table);
		writeTextTable(report,table);
	}

	protected void writeXhtmlTable(HTMLReportArchive report, TableModel table) throws IOException,XMLStreamException {
		XMLStreamWriter w=report.xhtmlStream();
		w.writeStartElement("table");
		w.writeStartElement("thead");
		w.writeStartElement("tr");

		for (int c=0;c<table.getColumnCount();c++) {
			w.writeStartElement("th");
			w.writeCharacters(table.getColumnName(c));
			w.writeEndElement();
		}

		w.writeEndElement();//tr
		w.writeEndElement();//thead
		w.writeStartElement("tbody");

		for (int r=0;r<table.getRowCount();r++) {
			w.writeStartElement("tr");
			for (int c=0;c<table.getColumnCount();c++) {
				w.writeStartElement("td");
                                String tmp = String.valueOf(table.getValueAt(r, c));
                                tmp = tmp.replaceAll("<html>", "");
                                tmp = tmp.replaceAll("</html>", "");
                                tmp = tmp.replaceAll("<br>", " / \n");
				w.writeCharacters(tmp);
				w.writeEndElement();//td
			}
			w.writeEndElement();//tr
		}
		w.writeEndElement();//tbody
		w.writeEndElement();
	}


	protected void writeTextTable(HTMLReportArchive report, TableModel table) throws IOException {
		StringBuffer d=report.dataDocument();
		d.append("#");

		for (int c=0;c<table.getColumnCount();c++) {
			if (c!=0) d.append("\t");
			d.append(table.getColumnName(c));
		}

		d.append("\n");

		// Do the rows
		for (int r=0;r<table.getRowCount();r++) {
			for (int c=0;c<table.getColumnCount();c++) {
				if (c!=0) d.append("\t");
				d.append(table.getValueAt(r, c));
			}
			d.append("\n");
		}

	}
}
