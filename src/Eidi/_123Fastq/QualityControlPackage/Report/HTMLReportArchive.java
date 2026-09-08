package Eidi._123Fastq.QualityControlPackage.Report;

import Eidi._123Fastq.GUI.Statics;
import Eidi._123Fastq.QualityControlPackage.Modules.IgnoresModule;
import Eidi._123Fastq.QualityControlPackage.Modules.QCModule;
import Eidi._123Fastq.QualityControlPackage.Sequence.SequenceFile;
import Eidi._123Fastq.QualityControlPackage.Utilities.ImageToBase64;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class HTMLReportArchive {

    private XMLStreamWriter xhtml = null;
    private StringBuffer data = new StringBuffer();
    private QCModule[] modules1;
    private QCModule[] modules2;

    private ZipOutputStream zip;
    private SequenceFile sequenceFile1;
    private SequenceFile sequenceFile2;

    private byte[] buffer = new byte[1024];
    private File htmlFile;
    private File zipFile;

    public HTMLReportArchive(SequenceFile sequenceFile, QCModule[] modules, File htmlFile) throws IOException, XMLStreamException {
        this.sequenceFile1 = sequenceFile;
        this.modules1 = modules;
        this.htmlFile = htmlFile;
        this.zipFile = new File(htmlFile.getAbsoluteFile().toString().replaceAll("\\.html$", "") + ".zip");
        StringWriter htmlStr = new StringWriter();
        XMLOutputFactory xmlfactory = XMLOutputFactory.newInstance();
        this.xhtml = xmlfactory.createXMLStreamWriter(htmlStr);

        zip = new ZipOutputStream(new FileOutputStream(zipFile));
        zip.putNextEntry(new ZipEntry(folderName() + "/"));
        zip.putNextEntry(new ZipEntry(folderName() + "/Icons/"));
        zip.putNextEntry(new ZipEntry(folderName() + "/Images/"));

        startDocument();

        for (int m = 0; m < modules.length; m++) {

            if (modules[m].ignoreInReport()) {
                continue;
            }

            xhtml.writeStartElement("div");
            xhtml.writeAttribute("class", "module");
            xhtml.writeStartElement("h2");
            xhtml.writeAttribute("id", "M" + m);

            // Add an icon before the module name
            if (modules[m].raisesError()) {
                xhtml.writeEmptyElement("img");
                xhtml.writeAttribute("src", base64ForIcon("Icons/error.png"));
                xhtml.writeAttribute("alt", "[FAIL]");
            } else if (modules[m].raisesWarning()) {
                xhtml.writeEmptyElement("img");
                xhtml.writeAttribute("src", base64ForIcon("Icons/warning.png"));
                xhtml.writeAttribute("alt", "[WARN]");
            } else {
                xhtml.writeEmptyElement("img");
                xhtml.writeAttribute("src", base64ForIcon("Icons/tick.png"));
                xhtml.writeAttribute("alt", "[OK]");
            }

            xhtml.writeCharacters(modules[m].name());
            data.append(">>");
            data.append(modules[m].name());
            data.append("\t");
            if (modules[m].raisesError()) {
                data.append("fail");
            } else if (modules[m].raisesWarning()) {
                data.append("warn");
            } else {
                data.append("pass");
            }
            data.append("\n");
            xhtml.writeEndElement();

            modules[m].makeReport(this, 1);

            data.append(">>END_MODULE\n");

            xhtml.writeEndElement();
        }

        closeDocument();

        zip.putNextEntry(new ZipEntry(folderName() + "/123Fastq_report.html"));
        xhtml.flush();
        xhtml.close();
        zip.write(htmlStr.toString().getBytes());
        zip.closeEntry();
        zip.putNextEntry(new ZipEntry(folderName() + "/123Fastq_data.txt"));
        zip.write(data.toString().getBytes());
        zip.closeEntry();

        //XSL-FO
        try {
            DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            domFactory.setNamespaceAware(false);
            DocumentBuilder builder = domFactory.newDocumentBuilder();
            Document src = builder.parse(new InputSource(new StringReader(htmlStr.toString())));
            InputStream rsrc = getClass().getResourceAsStream("/Eidi/_123Fastq/QualityControlPackage/Templates/123Fastq2fo.xsl");
            if (rsrc != null) {
                domFactory.setNamespaceAware(true);
                builder = domFactory.newDocumentBuilder();
                Document html2fo = builder.parse(rsrc);
                rsrc.close();

                TransformerFactory tf = TransformerFactory.newInstance();
                Templates templates = tf.newTemplates(new DOMSource(html2fo));
                zip.putNextEntry(new ZipEntry(folderName() + "/123Fastq.fo"));
                templates.newTransformer().transform(new DOMSource(src), new StreamResult(zip));
                zip.closeEntry();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        zip.close();

        PrintWriter pr = new PrintWriter(new FileWriter(htmlFile));
        pr.print(htmlStr.toString());
        pr.close();

    }

    //comprative
    public HTMLReportArchive(SequenceFile sequenceFile1, SequenceFile sequenceFile2, QCModule[] modules1, QCModule[] modules2, File htmlFile) throws IOException, XMLStreamException {
        this.sequenceFile1 = sequenceFile1;
        this.modules1 = modules1;
        this.sequenceFile2 = sequenceFile2;
        this.modules2 = modules2;
        this.htmlFile = htmlFile;
        this.zipFile = new File(htmlFile.getAbsoluteFile().toString().replaceAll("\\.html$", "") + ".zip");
        StringWriter htmlStr = new StringWriter();
        XMLOutputFactory xmlfactory = XMLOutputFactory.newInstance();
        this.xhtml = xmlfactory.createXMLStreamWriter(htmlStr);

        zip = new ZipOutputStream(new FileOutputStream(zipFile));
        zip.putNextEntry(new ZipEntry(folderName() + "/"));
        zip.putNextEntry(new ZipEntry(folderName() + "/Icons/"));
        zip.putNextEntry(new ZipEntry(folderName() + "/Images/"));

        startDocument_Comprative();

        for (int m = 0; m < modules1.length; m++) {

            if (modules1[m].ignoreInReport() && modules2[m].ignoreInReport()) {
                continue;
            } else if (!modules1[m].ignoreInReport() && modules2[m].ignoreInReport()) {
                modules2[m] = new IgnoresModule(modules2[m].name());
            } else if (modules1[m].ignoreInReport() && !modules2[m].ignoreInReport()) {
                modules1[m] = new IgnoresModule(modules1[m].name());
            }

            xhtml.writeStartElement("div");
            xhtml.writeAttribute("class", "module");
            xhtml.writeStartElement("h2");
            xhtml.writeAttribute("id", "M" + m);
            xhtml.writeCharacters(modules1[m].name());
            data.append(">>");
            data.append(modules1[m].name());
            data.append("\t");
            xhtml.writeEndElement();

            xhtml.writeStartElement("h3");
            xhtml.writeAttribute("id", "M" + m);
            if (modules1[m].raisesError()) {
                xhtml.writeEmptyElement("img");
                xhtml.writeAttribute("src", base64ForIcon("Icons/error.png"));
                xhtml.writeAttribute("alt", "[FAIL]");
            } else if (modules1[m].raisesWarning()) {
                xhtml.writeEmptyElement("img");
                xhtml.writeAttribute("src", base64ForIcon("Icons/warning.png"));
                xhtml.writeAttribute("alt", "[WARN]");
            } else {
                xhtml.writeEmptyElement("img");
                xhtml.writeAttribute("src", base64ForIcon("Icons/tick.png"));
                xhtml.writeAttribute("alt", "[OK]");
            }
            xhtml.writeCharacters("Forward File(s):");
            data.append(">>");
            data.append("Forward File(s):");
            data.append("\t");
            if (modules1[m].raisesError()) {
                data.append("fail");
            } else if (modules1[m].raisesWarning()) {
                data.append("warn");
            } else {
                data.append("pass");
            }
            data.append("\n");
            xhtml.writeEndElement();
            modules1[m].makeReport(this, 1);

            xhtml.writeStartElement("h3");
            xhtml.writeAttribute("id", "M" + m);
            if (modules2[m].raisesError()) {
                xhtml.writeEmptyElement("img");
                xhtml.writeAttribute("src", base64ForIcon("Icons/error.png"));
                xhtml.writeAttribute("alt", "[FAIL]");
            } else if (modules2[m].raisesWarning()) {
                xhtml.writeEmptyElement("img");
                xhtml.writeAttribute("src", base64ForIcon("Icons/warning.png"));
                xhtml.writeAttribute("alt", "[WARN]");
            } else {
                xhtml.writeEmptyElement("img");
                xhtml.writeAttribute("src", base64ForIcon("Icons/tick.png"));
                xhtml.writeAttribute("alt", "[OK]");
            }
            xhtml.writeCharacters("Reverse File(s):");
            data.append(">>");
            data.append("Reverse File(s):");
            data.append("\t");
            if (modules2[m].raisesError()) {
                data.append("fail");
            } else if (modules2[m].raisesWarning()) {
                data.append("warn");
            } else {
                data.append("pass");
            }
            data.append("\n");
            xhtml.writeEndElement();
            modules2[m].makeReport(this, 2);

            data.append(">>END_MODULE\n");

            xhtml.writeEndElement();
        }

        closeDocument();

        zip.putNextEntry(new ZipEntry(folderName() + "/123Fastq_report.html"));
        xhtml.flush();
        xhtml.close();
        zip.write(htmlStr.toString().getBytes());
        zip.closeEntry();
        zip.putNextEntry(new ZipEntry(folderName() + "/123Fastq_data.txt"));
        zip.write(data.toString().getBytes());
        zip.closeEntry();

        //XSL-FO
        try {
            DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            domFactory.setNamespaceAware(false);
            DocumentBuilder builder = domFactory.newDocumentBuilder();
            Document src = builder.parse(new InputSource(new StringReader(htmlStr.toString())));
            InputStream rsrc = getClass().getResourceAsStream("/Eidi/_123Fastq/QualityControlPackage/Templates/123Fastq2fo.xsl");
            if (rsrc != null) {
                domFactory.setNamespaceAware(true);
                builder = domFactory.newDocumentBuilder();
                Document html2fo = builder.parse(rsrc);
                rsrc.close();

                TransformerFactory tf = TransformerFactory.newInstance();
                Templates templates = tf.newTemplates(new DOMSource(html2fo));
                zip.putNextEntry(new ZipEntry(folderName() + "/123Fastq.fo"));
                templates.newTransformer().transform(new DOMSource(src), new StreamResult(zip));
                zip.closeEntry();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        zip.close();

        // Save the HTML file at the same level as the zip file
        PrintWriter pr = new PrintWriter(new FileWriter(htmlFile));

        pr.print(htmlStr.toString());

        pr.close();
    }

    public XMLStreamWriter xhtmlStream() {
        return this.xhtml;
    }

    public StringBuffer dataDocument() {
        return data;
    }

    public String folderName() {
        return htmlFile.getName().replaceAll("\\.html$", "");
    }

    public ZipOutputStream zipFile() {
        return zip;
    }

    private void startDocument() throws IOException, XMLStreamException {

        data.append("##123FASTQ\t");
        data.append(Statics.VERSION);
        data.append("\n");
        for (String icnName : new String[]{
            "123Fastq_icon.png",
            "warning.png",
            "error.png",
            "tick.png"}) {
            InputStream in = getClass().getResourceAsStream("/Eidi/_123Fastq/QualityControlPackage/Templates/Icons/" + icnName);
            if (in == null) {
                continue;
            }
            zip.putNextEntry(new ZipEntry(folderName() + "/Icons/" + icnName));
            int len;
            while ((len = in.read(buffer)) > 0) {
                zip.write(buffer, 0, len);
            }
            in.close();
            zip.closeEntry();
        }
        SimpleDateFormat df = new SimpleDateFormat("EEE d MMM yyyy");
        xhtml.writeStartElement("html");
        xhtml.writeStartElement("head");
        xhtml.writeStartElement("title");
        xhtml.writeCharacters(sequenceFile1.name());
        xhtml.writeCharacters(" 123Fastq Report");
        xhtml.writeEndElement();
        InputStream rsrc = getClass().getResourceAsStream("/Eidi/_123Fastq/QualityControlPackage/Templates/header_template.html");
        if (rsrc != null) {
            xhtml.writeStartElement("style");
            xhtml.writeAttribute("type", "text/css");
            byte array[] = new byte[128];
            int nRead;
            while ((nRead = rsrc.read(array)) != -1) {
                xhtml.writeCharacters(new String(array, 0, nRead));
            }
            rsrc.close();
            xhtml.writeEndElement();//style
        }

        xhtml.writeEndElement();//head

        xhtml.writeStartElement("body");

        xhtml.writeStartElement("div");
        xhtml.writeAttribute("class", "header");

        xhtml.writeStartElement("div");
        xhtml.writeAttribute("id", "header_title");

        xhtml.writeEmptyElement("img");
        xhtml.writeAttribute("src", base64ForIcon("Icons/123Fastq_icon.png"));
        xhtml.writeAttribute("alt", "123Fastq");
        xhtml.writeCharacters("123Fastq Report");
        xhtml.writeEndElement();//div

        xhtml.writeStartElement("div");
        xhtml.writeAttribute("id", "header_filename");
        xhtml.writeCharacters(df.format(new Date()));
        xhtml.writeEmptyElement("br");
        xhtml.writeCharacters(sequenceFile1.name());
        xhtml.writeEndElement();//div
        xhtml.writeEndElement();//div

        xhtml.writeStartElement("div");
        xhtml.writeAttribute("class", "summary");

        xhtml.writeStartElement("h2");
        xhtml.writeCharacters("Results");
        xhtml.writeEndElement();

        xhtml.writeStartElement("ul");

        StringBuffer summaryText = new StringBuffer();

        for (int m = 0; m < modules1.length; m++) {

            if (modules1[m].ignoreInReport()) {
                continue;
            }
            xhtml.writeStartElement("li");
            xhtml.writeEmptyElement("img");
            if (modules1[m].raisesError()) {
                xhtml.writeAttribute("src", base64ForIcon("Icons/error.png"));
                xhtml.writeAttribute("alt", "[FAIL]");
                summaryText.append("FAIL");
            } else if (modules1[m].raisesWarning()) {
                xhtml.writeAttribute("src", base64ForIcon("Icons/warning.png"));
                xhtml.writeAttribute("alt", "[WARNING]");
                summaryText.append("WARN");
            } else {
                xhtml.writeAttribute("src", base64ForIcon("Icons/tick.png"));
                xhtml.writeAttribute("alt", "[PASS]");
                summaryText.append("PASS");
            }
            summaryText.append("\t");
            summaryText.append(modules1[m].name());
            summaryText.append("\t");
            summaryText.append(sequenceFile1.name());
            summaryText.append(System.getProperty("line.separator"));

            xhtml.writeStartElement("a");
            xhtml.writeAttribute("href", "#M" + m);
            xhtml.writeCharacters(modules1[m].name());
            xhtml.writeEndElement();//a
            xhtml.writeEndElement();//li

        }
        xhtml.writeEndElement();//ul
        xhtml.writeEndElement();//div

        xhtml.writeStartElement("div");
        xhtml.writeAttribute("class", "main");

        zip.putNextEntry(new ZipEntry(folderName() + "/summary.txt"));
        zip.write(summaryText.toString().getBytes());

    }

    private void startDocument_Comprative() throws IOException, XMLStreamException {

        data.append("##123FASTQ\t");
        data.append(Statics.VERSION);
        data.append("\n");
        for (String icnName : new String[]{
            "123Fastq_icon.png",
            "warning.png",
            "error.png",
            "tick.png"}) {
            InputStream in = getClass().getResourceAsStream("/Eidi/_123Fastq/QualityControlPackage/Templates/Icons/" + icnName);
            if (in == null) {
                continue;
            }
            zip.putNextEntry(new ZipEntry(folderName() + "/Icons/" + icnName));
            int len;
            while ((len = in.read(buffer)) > 0) {
                zip.write(buffer, 0, len);
            }
            in.close();
            zip.closeEntry();
        }
        SimpleDateFormat df = new SimpleDateFormat("EEE d MMM yyyy");
        String TimeLog = new SimpleDateFormat("HH:mm").format(Calendar.getInstance().getTime());

        xhtml.writeStartElement("html");
        xhtml.writeStartElement("head");
        xhtml.writeStartElement("title");

        xhtml.writeCharacters("Comprative-Mode");
        xhtml.writeCharacters(" 123Fastq Report");
        xhtml.writeEndElement();
        InputStream rsrc = getClass().getResourceAsStream("/Eidi/_123Fastq/QualityControlPackage/Templates/header_template.html");
        if (rsrc != null) {
            xhtml.writeStartElement("style");
            xhtml.writeAttribute("type", "text/css");
            byte array[] = new byte[128];
            int nRead;
            while ((nRead = rsrc.read(array)) != -1) {
                xhtml.writeCharacters(new String(array, 0, nRead));
            }
            rsrc.close();
            xhtml.writeEndElement();//style
        }

        xhtml.writeEndElement();//head

        xhtml.writeStartElement("body");

        xhtml.writeStartElement("div");
        xhtml.writeAttribute("class", "header");

        xhtml.writeStartElement("div");
        xhtml.writeAttribute("id", "header_title");

        xhtml.writeEmptyElement("img");
        xhtml.writeAttribute("src", base64ForIcon("Icons/123Fastq_icon.png"));
        xhtml.writeAttribute("alt", "123Fastq");
        xhtml.writeCharacters("123Fastq Report");
        xhtml.writeEndElement();//div

        xhtml.writeStartElement("div");
        xhtml.writeAttribute("id", "header_filename");
        xhtml.writeCharacters(df.format(new Date()));
        xhtml.writeEmptyElement("br");

        xhtml.writeCharacters(TimeLog);

        xhtml.writeEndElement();//div
        xhtml.writeEndElement();//div

        xhtml.writeStartElement("div");
        xhtml.writeAttribute("class", "summary");

        xhtml.writeStartElement("h2");
        xhtml.writeCharacters("Results");
        xhtml.writeEndElement();

        xhtml.writeStartElement("ul");

        StringBuffer summaryText = new StringBuffer();

        for (int m = 0; m < modules1.length; m++) {

            if (modules1[m].ignoreInReport() && modules2[m].ignoreInReport()) {
                continue;
            }

            xhtml.writeStartElement("li");
            summaryText.append("\t");
            summaryText.append(modules1[m].name());
            summaryText.append("\t");
            summaryText.append(sequenceFile1.name());
            summaryText.append(System.getProperty("line.separator"));
            xhtml.writeStartElement("a");
            xhtml.writeAttribute("href", "#M" + m);
            xhtml.writeCharacters(modules1[m].name());
            xhtml.writeEndElement();//a
            xhtml.writeEndElement();//li

        }

        xhtml.writeEndElement();//ul
        xhtml.writeEndElement();//div

        xhtml.writeStartElement("div");
        xhtml.writeAttribute("class", "main");

        zip.putNextEntry(new ZipEntry(folderName() + "/summary.txt"));
        zip.write(summaryText.toString().getBytes());

    }

    private String base64ForIcon(String path) {
        try {
            BufferedImage b = ImageIO.read(ClassLoader.getSystemResource("Eidi/_123Fastq/QualityControlPackage/Templates/" + path));
            return (ImageToBase64.imageToBase64(b));
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return "Failed";
        }
    }

    private void closeDocument() throws XMLStreamException {
        xhtml.writeEndElement();//div
        xhtml.writeStartElement("div");
        xhtml.writeAttribute("class", "footer");
        xhtml.writeCharacters("Produced by ");
        xhtml.writeStartElement("a");
        xhtml.writeAttribute("href", "https://sourceforge.net/projects/project-123ngs/");
        xhtml.writeCharacters("123FASTQ");
        xhtml.writeEndElement();//a
        xhtml.writeCharacters("  (version " + Statics.VERSION + ")");
        xhtml.writeEndElement();//div
        xhtml.writeEndElement();//body
        xhtml.writeEndElement();//html
    }
}
