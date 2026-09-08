package Eidi._123Fastq.QualityControlPackage.Results;

import static Eidi._123Fastq.GUI.App123Fastq.ClickedTrim;
import Eidi._123Fastq.GUI.MovableTabbedPane;
import Eidi._123Fastq.GUI.MyJPanel;
import Eidi._123Fastq.GUI.Statics;
import Eidi._123Fastq.GUI.TabComponents;
import Eidi._123Fastq.GUI.TaskQueue;
import Eidi._123Fastq.QualityControlPackage.Modules.AdapterContent;
import Eidi._123Fastq.QualityControlPackage.Modules.BasicStats;
import Eidi._123Fastq.QualityControlPackage.Modules.IgnoresModule;
import Eidi._123Fastq.QualityControlPackage.Modules.ModuleFactory;
import Eidi._123Fastq.QualityControlPackage.Modules.NContent;
import Eidi._123Fastq.QualityControlPackage.Modules.PerBaseQualityScores;
import Eidi._123Fastq.QualityControlPackage.Modules.PerBaseSequenceContent;
import Eidi._123Fastq.QualityControlPackage.Modules.QCModule;
import Eidi._123Fastq.QualityControlPackage.Modules.SequenceLengthDistribution;
import Eidi._123Fastq.QualityControlPackage.Report.HTMLReportArchive;
import Eidi._123Fastq.QualityControlPackage.Sequence.QualityEncoding.PhredEncoding;
import Eidi._123Fastq.QualityControlPackage.Sequence.Sequence;
import Eidi._123Fastq.QualityControlPackage.Sequence.SequenceFile;
import Eidi._123Fastq.QualityControlPackage.Sequence.SequenceFormatException;
import Eidi._123Fastq.TrimFactoryPackage.TrimPanel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.xml.stream.XMLStreamException;
import org.apache.commons.io.FilenameUtils;

public class ResultsPanel extends MyJPanel {

    private String name1;
    private String name2;
    private int PercentComplete1;
    private int PercentComplete2;
    private int latestIndex = 0;
    private int automaticStatus;
    private boolean shouldRun;
    private TaskQueue mainQueue;
    private QCModule[] modules1;
    private QCModule[] modules2 = null;
    private SequenceFile sequenceFile1;
    private SequenceFile sequenceFile2;
    private JProgressBar progBar1;
    private JProgressBar progBar2;
    private File inputFile1;
    private File inputFile2;
    private JPanel Block1;
    private JPanel Block3;
    private JPanel Block2;
    private JPanel Block4;
    private JPanel UpPanel;
    private JPanel starterPanel;
    private JPanel bg;
    private JPanel currentPanel = null;
    private JPanel[] panels1;
    private JPanel[] panels2;
    private JComboBox moduleList1;
    private JComboBox moduleList2;
    private JRadioButton first;
    private JRadioButton second;
    private MovableTabbedPane tabPane;
    private Color BGprogress = Statics.UIcolor;
    private Dimension X = new Dimension(650, 220);
    private Dimension Y = new Dimension(50, 35);
    private Dimension Z = new Dimension(450, 35);
    private final int qcThreadCount;

    public ResultsPanel(SequenceFile sequenceFile, File file, TaskQueue MainQueue, boolean ShouldRun, MovableTabbedPane TabPane, JPanel StarterPanel, JPanel background, String FileName, int status, int qcThreadCount) {
        this.inputFile1 = file;
        this.sequenceFile1 = sequenceFile;
        this.shouldRun = ShouldRun;
        this.mainQueue = MainQueue;
        this.tabPane = TabPane;
        this.starterPanel = StarterPanel;
        this.bg = background;
        this.name1 = FileName;
        this.automaticStatus = status;
        this.qcThreadCount = normalizeThreadCount(qcThreadCount);
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        setBackground(BGprogress);
        Block1 = new JPanel(new BorderLayout());
        Block1.setPreferredSize(X);
        Block1.setBackground(BGprogress);
        add(Block1);
        Block2 = new JPanel(new BorderLayout());
        JPanel subBlock2 = new JPanel();
        subBlock2.setPreferredSize(Y);
        JPanel subBlock1 = new JPanel();
        subBlock1.setPreferredSize(Y);
        subBlock1.setBackground(BGprogress);
        subBlock2.setBackground(BGprogress);
        progBar1 = new JProgressBar();
        progBar1.setPreferredSize(Z);
        progBar1.setStringPainted(true);
        progBar1.setFont(new java.awt.Font("Segoe UI", 1, 28));
        progBar1.setString("Waiting to start...");
        Block2.add(subBlock1, BorderLayout.WEST);
        Block2.add(progBar1, BorderLayout.CENTER);
        Block2.add(subBlock2, BorderLayout.EAST);
        Block2.setBackground(BGprogress);
        add(Block2);
        Block3 = new JPanel();
        Block3.setPreferredSize(X);
        Block3.setBackground(BGprogress);
        add(Block3);
        startAnalysis();
    }

    //comprative
    public ResultsPanel(SequenceFile sequenceFile1, SequenceFile sequenceFile2, File file1, File file2, TaskQueue MainQueue, boolean ShouldRun, MovableTabbedPane TabPane, JPanel StarterPanel, JPanel background, String FileName1, String FileName2, int status, int qcThreadCount) {
        this.inputFile1 = file1;
        this.inputFile2 = file2;
        this.sequenceFile1 = sequenceFile1;
        this.sequenceFile2 = sequenceFile2;
        this.shouldRun = ShouldRun;
        this.mainQueue = MainQueue;
        this.tabPane = TabPane;
        this.starterPanel = StarterPanel;
        this.bg = background;
        this.name1 = FileName1;
        this.name2 = FileName2;
        this.automaticStatus = status;
        this.qcThreadCount = normalizeThreadCount(qcThreadCount);
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        setBackground(BGprogress);
        Block1 = new JPanel(new BorderLayout());
        Block1.setPreferredSize(X);
        Block1.setBackground(BGprogress);
        add(Block1);
        Block2 = new JPanel(new BorderLayout());
        JPanel subBlock2 = new JPanel();
        subBlock2.setPreferredSize(Y);
        JPanel subBlock1 = new JPanel();
        subBlock1.setPreferredSize(Y);
        subBlock1.setBackground(BGprogress);
        subBlock2.setBackground(BGprogress);
        progBar1 = new JProgressBar();
        progBar1.setPreferredSize(Z);
        progBar1.setStringPainted(true);
        progBar1.setFont(new java.awt.Font("Segoe UI", 1, 28));
        progBar1.setString("Waiting to start...");
        Block2.add(subBlock1, BorderLayout.WEST);
        Block2.add(progBar1, BorderLayout.CENTER);
        Block2.add(subBlock2, BorderLayout.EAST);
        Block2.setBackground(BGprogress);
        add(Block2);
        Block3 = new JPanel(new BorderLayout());
        JPanel subBlock3 = new JPanel();
        subBlock3.setPreferredSize(Y);
        JPanel subBlock4 = new JPanel();
        subBlock4.setPreferredSize(Y);
        subBlock3.setBackground(BGprogress);
        subBlock4.setBackground(BGprogress);
        progBar2 = new JProgressBar();
        progBar2.setPreferredSize(Z);
        progBar2.setStringPainted(true);
        progBar2.setFont(new java.awt.Font("Segoe UI", 1, 28));
        progBar2.setString("Waiting to start...");
        Block3.add(subBlock3, BorderLayout.WEST);
        Block3.add(progBar2, BorderLayout.CENTER);
        Block3.add(subBlock4, BorderLayout.EAST);
        Block3.setBackground(BGprogress);
        add(Block3);
        Block4 = new JPanel();
        Block4.setPreferredSize(X);
        Block4.setBackground(BGprogress);
        add(Block4);
        startAnalysis_Comparative();
    }

    public void setShouldRun(boolean shouldRun) {
        this.shouldRun = shouldRun;
    }

    private static int normalizeThreadCount(int threadCount) {
        return Math.max(1, threadCount);
    }

    private static int threadsForFile(int totalThreads, int fileIndex, int fileCount) {
        int normalizedThreads = normalizeThreadCount(totalThreads);
        if (fileCount <= 1) {
            return normalizedThreads;
        }
        int baseThreads = Math.max(1, normalizedThreads / fileCount);
        if (fileIndex == fileCount - 1) {
            return Math.max(1, normalizedThreads - (baseThreads * (fileCount - 1)));
        }
        return baseThreads;
    }

    private void setProgress(JProgressBar progressBar, int value, String message) {
        Runnable updater = () -> {
            if (value >= 0) {
                progressBar.setValue(value);
            }
            progressBar.setString(message);
        };
        if (SwingUtilities.isEventDispatchThread()) {
            updater.run();
        } else {
            SwingUtilities.invokeLater(updater);
        }
    }

    public void valueChanged(ItemEvent e) {
        int index = moduleList1.getSelectedIndex();
        if (index >= 0) {
            remove(currentPanel);
            currentPanel = panels1[index];
            add(currentPanel, BorderLayout.CENTER);
            validate();
            repaint();
        }
    }

    public void valueChanged_Comparative(int index) {
        if (index >= 0) {
            remove(currentPanel);
            if (first.isSelected()) {
                currentPanel = panels1[index];
            } else if (second.isSelected()) {
                currentPanel = panels2[index];
            }
            add(currentPanel, BorderLayout.CENTER);
            validate();
            repaint();
        }
        latestIndex = index;
    }

    private class ModuleRenderer extends DefaultListCellRenderer {

        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            if (!(value instanceof QCModule)) {
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
            QCModule module = (QCModule) value;
            ImageIcon icon = Statics.OK_ICON;
            if (module.raisesError()) {
                icon = Statics.ERROR_ICON;
            } else if (module.raisesWarning()) {
                icon = Statics.WARNING_ICON;
            }
            JLabel returnLabel = new JLabel(module.name(), icon, JLabel.LEFT);
            returnLabel.setOpaque(true);
            if (isSelected) {
                returnLabel.setBackground(Color.LIGHT_GRAY);
            } else {
                returnLabel.setBackground(Color.WHITE);
            }
            return returnLabel;
        }
    }

    public void analysisStarted(SequenceFile file, JProgressBar progbar) {
        setProgress(progbar, -1, "Initialization... QC threads: " + qcThreadCount);
    }

    public void analysisExceptionReceived(SequenceFile file, Exception e, JProgressBar progbar) {
        setProgress(progbar, -1, "Failed to process file: " + e.getLocalizedMessage());
    }

    public void analysisComplete(SequenceFile file, QCModule[] rawModules) {
        JLabel lblQCResults = new JLabel("Results:");
        JButton saveReport = new JButton("Save Report");
        JButton trimRecoms = new JButton("Single-End Trim");
        lblQCResults.setFont(new java.awt.Font("Segoe UI", 1, 22));
        lblQCResults.setForeground(new Color(194, 49, 38));
        saveReport.setFont(new java.awt.Font("Segoe UI", 1, 14));
        saveReport.addActionListener((ActionEvent e) -> {
            saveReport();
        });
        trimRecoms.setFont(new java.awt.Font("Segoe UI", 1, 14));
        trimRecoms.addActionListener((ActionEvent e) -> {
            Single_End_Automatic_Trim(e);
        });

        String key = "Save Report";
        Action ctrlS = new AbstractAction(key) {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveReport();
            }
        };
        saveReport.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK), key);
        saveReport.getActionMap().put(key, ctrlS);
        saveReport.setToolTipText("Ctrl+S");
        JPanel UpPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        Vector<QCModule> modulesToDisplay = new Vector<>();
        for (int m = 0; m < rawModules.length; m++) {
            if (!rawModules[m].ignoreInReport()) {
                modulesToDisplay.add(rawModules[m]);
            }
        }
        modules1 = modulesToDisplay.toArray(new QCModule[0]);
        panels1 = new JPanel[modules1.length];
        for (int m = 0; m < modules1.length; m++) {
            panels1[m] = modules1[m].getResultsPanel();
        }
        moduleList1 = new JComboBox(modules1);
        moduleList1.setRenderer(new ModuleRenderer());
        moduleList1.setSelectedIndex(0);
        moduleList1.addItemListener((ItemEvent e) -> {
            valueChanged(e);
        });
        remove(Block1);
        remove(Block2);
        remove(Block3);
        setLayout(new BorderLayout());
        UpPanel.add(lblQCResults);
        UpPanel.add(moduleList1);
        UpPanel.add(saveReport);
        if (automaticStatus != 1) {
            UpPanel.add(trimRecoms);
        }
        add(UpPanel, BorderLayout.NORTH);
        currentPanel = panels1[0];
        add(currentPanel, BorderLayout.CENTER);
        validate();
    }

    public void analysisComplete_Comparative(SequenceFile file, SequenceFile file2, QCModule[] rawModules1, QCModule[] rawModules2) {
        JLabel lblQCResults = new JLabel("Results:");
        first = new JRadioButton("First File", true);
        second = new JRadioButton("Second File", false);
        JPanel filesPanel = new JPanel(new BorderLayout());
        first.setFont(new java.awt.Font("Segoe UI", 1, 12));
        second.setFont(new java.awt.Font("Segoe UI", 1, 12));
        ButtonGroup rdioGroup = new ButtonGroup();
        rdioGroup.add(first);
        rdioGroup.add(second);
        filesPanel.add(first, BorderLayout.NORTH);
        filesPanel.add(second, BorderLayout.SOUTH);
        JButton saveReport = new JButton("Save Report");
        JButton trimRecoms = new JButton("Paired-End Trim");
        lblQCResults.setFont(new java.awt.Font("Segoe UI", 1, 22));
        lblQCResults.setForeground(new Color(194, 49, 38));
        saveReport.setFont(new java.awt.Font("Segoe UI", 1, 14));
        saveReport.addActionListener((ActionEvent e) -> {
            saveReport_Comprative();
        });
        trimRecoms.setFont(new java.awt.Font("Segoe UI", 1, 14));
        trimRecoms.addActionListener((ActionEvent e) -> {
            Paired_End_Automatic_Trim(e);
        });
        String key = "Save Report";
        Action ctrlS = new AbstractAction(key) {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveReport_Comprative();
            }
        };
        saveReport.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK), key);
        saveReport.getActionMap().put(key, ctrlS);
        saveReport.setToolTipText("Ctrl+S");
        UpPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        Vector<QCModule> modulesToDisplay1 = new Vector<>();
        Vector<QCModule> modulesToDisplay2 = new Vector<>();
        for (int m = 0; m < rawModules1.length; m++) {
            if (!rawModules1[m].ignoreInReport() && !rawModules2[m].ignoreInReport()) {
                modulesToDisplay1.add(rawModules1[m]);
                modulesToDisplay2.add(rawModules2[m]);
            }
            if (!rawModules1[m].ignoreInReport() && rawModules2[m].ignoreInReport()) {
                modulesToDisplay1.add(rawModules1[m]);
                modulesToDisplay2.add(new IgnoresModule(rawModules1[m].name()));
            }
            if (rawModules1[m].ignoreInReport() && !rawModules2[m].ignoreInReport()) {
                modulesToDisplay1.add(new IgnoresModule(rawModules2[m].name()));
                modulesToDisplay2.add(rawModules2[m]);
            }
        }
        modules1 = modulesToDisplay1.toArray(new QCModule[0]);
        modules2 = modulesToDisplay2.toArray(new QCModule[0]);
        panels1 = new JPanel[modules1.length];
        panels2 = new JPanel[modules2.length];
        for (int m = 0; m < modules1.length; m++) {
            panels1[m] = modules1[m].getResultsPanel();
        }
        for (int m = 0; m < modules2.length; m++) {
            panels2[m] = modules2[m].getResultsPanel();
        }
        moduleList1 = new JComboBox(modules1);
        moduleList2 = new JComboBox(modules2);
        moduleList1.setRenderer(new ModuleRenderer());
        moduleList2.setRenderer(new ModuleRenderer());
        moduleList1.setSelectedIndex(0);
        moduleList2.setSelectedIndex(0);
        currentPanel = panels1[0];
        moduleList1.addItemListener((ItemEvent e) -> {
            valueChanged_Comparative(moduleList1.getSelectedIndex());
        });
        moduleList2.addItemListener((ItemEvent e) -> {
            valueChanged_Comparative(moduleList2.getSelectedIndex());
        });
        first.addItemListener((ItemEvent evt) -> {
            firstRadioItemStateChanged();
        });
        second.addItemListener((ItemEvent evt) -> {
            secondRadioItemStateChanged();
        });
        remove(Block1);
        remove(Block2);
        remove(Block3);
        remove(Block4);
        repaint();
        revalidate();
        setLayout(new BorderLayout());
        UpPanel.add(lblQCResults);
        UpPanel.add(moduleList1);
        UpPanel.add(moduleList2);
        UpPanel.add(filesPanel);
        firstRadioItemStateChanged();
        UpPanel.add(saveReport);
        if (automaticStatus != 1) {
            UpPanel.add(trimRecoms);
        }
        add(UpPanel, BorderLayout.NORTH);
        add(currentPanel, BorderLayout.CENTER);
        validate();
    }

    private void firstRadioItemStateChanged() {
        if (first.isSelected()) {
            moduleList2.setVisible(false);
            moduleList1.setVisible(true);
            moduleList1.setSelectedIndex(latestIndex);
            remove(currentPanel);
            currentPanel = panels1[latestIndex];
            add(currentPanel, BorderLayout.CENTER);
            repaint();
            revalidate();
        }
    }

    private void secondRadioItemStateChanged() {
        if (second.isSelected()) {
            moduleList1.setVisible(false);
            moduleList2.setVisible(true);
            moduleList2.setSelectedIndex(latestIndex);
            remove(currentPanel);
            currentPanel = panels2[latestIndex];
            add(currentPanel, BorderLayout.CENTER);
            repaint();
            revalidate();
        }
    }

    public void startAnalysis() {
        this.modules1 = ModuleFactory.getStandardModuleList(name1);
        for (int i = 0; i < modules1.length; i++) {
            modules1[i].reset();
        }
        mainQueue.AddToQueue(QCThread);
    }

    public void startAnalysis_Comparative() {
        this.modules1 = ModuleFactory.getStandardModuleList(name1);
        this.modules2 = ModuleFactory.getStandardModuleList(name2);
        for (int i = 0; i < modules1.length; i++) {
            modules1[i].reset();
            modules2[i].reset();
        }
        mainQueue.AddToQueue(QCThread_Comprative);
    }

    public Thread getThread() {
        if (modules2 == null) {
            return QCThread;
        } else {
            return QCThread_Comprative;
        }
    }

    Thread QCThread = new Thread() {
        @Override
        public void run() {
            if (!shouldRun) {
                return;
            }
            analysisStarted(sequenceFile1, progBar1);
            try {
                QcAnalysisResult result = new QcRunner().run(sequenceFile1, modules1, qcThreadCount, (seqCount, percentComplete) -> {
                    if (percentComplete >= PercentComplete1 + 5) {
                        PercentComplete1 = percentComplete;
                        setProgress(progBar1, PercentComplete1, Statics.formatter.format(seqCount) + " Reads were Scanned... (" + Statics.percentFormatter2.format(percentComplete) + "%)");
                    }
                });
                modules1 = result.getModules();
                setProgress(progBar1, 100, "Scanning finished!  " + Statics.formatter.format(result.getSequenceCount()) + " Reads");
                analysisComplete(sequenceFile1, modules1);
            } catch (SequenceFormatException e) {
                analysisExceptionReceived(sequenceFile1, e, progBar1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                analysisExceptionReceived(sequenceFile1, e, progBar1);
            } catch (RuntimeException e) {
                analysisExceptionReceived(sequenceFile1, e, progBar1);
            }
        }
    };

    Thread QCThread_Comprative = new Thread() {
        @Override
        public void run() {
            if (!shouldRun) {
                return;
            }
            int threadsForFirstFile = threadsForFile(qcThreadCount, 0, 2);
            int threadsForSecondFile = threadsForFile(qcThreadCount, 1, 2);
            ExecutorService executor = Executors.newFixedThreadPool(Math.min(2, qcThreadCount));
            Future<QcAnalysisResult> firstResult = null;
            Future<QcAnalysisResult> secondResult = null;
            try {
                analysisStarted(sequenceFile1, progBar1);
                analysisStarted(sequenceFile2, progBar2);
                firstResult = executor.submit(new QcFileTask(sequenceFile1, modules1, threadsForFirstFile, progBar1, true));
                secondResult = executor.submit(new QcFileTask(sequenceFile2, modules2, threadsForSecondFile, progBar2, false));

                QcAnalysisResult result1 = waitForResult(firstResult, sequenceFile1, progBar1);
                QcAnalysisResult result2 = waitForResult(secondResult, sequenceFile2, progBar2);
                if (result1 == null || result2 == null) {
                    return;
                }
                modules1 = result1.getModules();
                modules2 = result2.getModules();
                setProgress(progBar1, 100, "First file scanning finished!  " + Statics.formatter.format(result1.getSequenceCount()) + " Reads");
                setProgress(progBar2, 100, "Second file scanning finished! " + Statics.formatter.format(result2.getSequenceCount()) + " Reads");
                analysisComplete_Comparative(sequenceFile1, sequenceFile2, modules1, modules2);
            } finally {
                if (firstResult != null) {
                    firstResult.cancel(true);
                }
                if (secondResult != null) {
                    secondResult.cancel(true);
                }
                executor.shutdownNow();
            }
        }
    };

    private QcAnalysisResult waitForResult(Future<QcAnalysisResult> result, SequenceFile file, JProgressBar progressBar) {
        try {
            return result.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            analysisExceptionReceived(file, e, progressBar);
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof Exception) {
                analysisExceptionReceived(file, (Exception) cause, progressBar);
            } else {
                analysisExceptionReceived(file, new RuntimeException(cause), progressBar);
            }
        }
        return null;
    }

    private class QcFileTask implements Callable<QcAnalysisResult> {

        private final SequenceFile sequenceFile;
        private final QCModule[] modules;
        private final int threadCount;
        private final JProgressBar progressBar;
        private final boolean firstFile;

        private QcFileTask(SequenceFile sequenceFile, QCModule[] modules, int threadCount, JProgressBar progressBar, boolean firstFile) {
            this.sequenceFile = sequenceFile;
            this.modules = modules;
            this.threadCount = threadCount;
            this.progressBar = progressBar;
            this.firstFile = firstFile;
        }

        public QcAnalysisResult call() throws SequenceFormatException, InterruptedException {
            return new QcRunner().run(sequenceFile, modules, threadCount, (seqCount, percentComplete) -> {
                if (firstFile) {
                    if (percentComplete >= PercentComplete1 + 5) {
                        PercentComplete1 = percentComplete;
                        setProgress(progressBar, PercentComplete1, Statics.formatter.format(seqCount) + " Reads were Scanned... (" + Statics.percentFormatter2.format(percentComplete) + "%)");
                    }
                } else {
                    if (percentComplete >= PercentComplete2 + 5) {
                        PercentComplete2 = percentComplete;
                        setProgress(progressBar, PercentComplete2, Statics.formatter.format(seqCount) + " Reads were Scanned... (" + Statics.percentFormatter2.format(percentComplete) + "%)");
                    }
                }
            });
        }
    }

    public void saveReport() {
        JFileChooser chooser;
        if (inputFile1 == null) {
            chooser = new JFileChooser();
        } else {
            chooser = new JFileChooser(inputFile1);
        }
        chooser.setSelectedFile(new File(sequenceFile1.getFile().getName().replaceAll("stdin:", "").replaceAll(".gz$", "").replaceAll(".bz2$", "").replaceAll(".txt$", "").replaceAll(".fastq$", "").replaceAll(".fq$", "").replaceAll(".sam$", "").replaceAll(".bam$", "") + "_123Fastq.html"));
        chooser.setMultiSelectionEnabled(false);
        chooser.setFileFilter(new FileFilter() {
            public String getDescription() {
                return "HTML files";
            }

            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".html");
            }
        });
        File reportFile;
        while (true) {
            int result = chooser.showSaveDialog(this);
            if (result == JFileChooser.CANCEL_OPTION) {
                return;
            }
            reportFile = chooser.getSelectedFile();
            if (!reportFile.getName().toLowerCase().endsWith(".html")) {
                reportFile = new File(reportFile.getAbsoluteFile() + ".html");
            }
            if (reportFile.exists()) {
                int reply = JOptionPane.showConfirmDialog(this, reportFile.getName() + " already exists.\nDo you want replace it?", "Overwrite Confirmation", JOptionPane.YES_NO_OPTION);
                if (reply == JOptionPane.NO_OPTION) {
                    continue;
                } else {
                    break;
                }
            } else {
                break;
            }
        }
        try {
            new HTMLReportArchive(sequenceFile1, modules1, reportFile);
        } catch (IOException | XMLStreamException e) {
            JOptionPane.showMessageDialog(this, "Failed to create archive: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void saveReport_Comprative() {
        JFileChooser chooser;
        if (inputFile1 == null) {
            chooser = new JFileChooser();
        } else {
            chooser = new JFileChooser(inputFile1);
        }
        chooser.setSelectedFile(new File("Comparative_Report_123Fastq.html"));
        chooser.setMultiSelectionEnabled(false);
        chooser.setFileFilter(new FileFilter() {
            public String getDescription() {
                return "HTML files";
            }

            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".html");
            }
        });
        File reportFile;
        while (true) {
            int result = chooser.showSaveDialog(this);
            if (result == JFileChooser.CANCEL_OPTION) {
                return;
            }
            reportFile = chooser.getSelectedFile();
            if (!reportFile.getName().toLowerCase().endsWith(".html")) {
                reportFile = new File(reportFile.getAbsoluteFile() + ".html");
            }
            if (reportFile.exists()) {
                int reply = JOptionPane.showConfirmDialog(this, reportFile.getName() + " already exists.\nDo you want replace it?", "Overwrite Confirmation", JOptionPane.YES_NO_OPTION);
                if (reply == JOptionPane.NO_OPTION) {
                } else {
                    break;
                }
            } else {
                break;
            }
        }
        try {
            new HTMLReportArchive(sequenceFile1, sequenceFile2, modules1, modules2, reportFile);
        } catch (IOException | XMLStreamException e) {
            JOptionPane.showMessageDialog(this, "Failed to create archive: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void Single_End_Automatic_Trim(java.awt.event.ActionEvent evt) {

        //check the eligibility of imported file for trimming
        if (!inputFile1.getName().toLowerCase().endsWith(".fq")
                && !inputFile1.getName().toLowerCase().endsWith(".txt.gz")
                && !inputFile1.getName().toLowerCase().endsWith(".fastq.gz")
                && !inputFile1.getName().toLowerCase().endsWith(".fq.gz")
                && !inputFile1.getName().toLowerCase().endsWith(".txt.bz2")
                && !inputFile1.getName().toLowerCase().endsWith(".fastq.bz2")
                && !inputFile1.getName().toLowerCase().endsWith(".txt")
                && !inputFile1.getName().toLowerCase().endsWith(".fastq")) {
            JOptionPane.showMessageDialog(null, "It seems the input file can not trim in the current format.\nIf it's a SAM, BAM or Fast5 file, convert it to Fastq file and then trim.");
            return;
        }
        JOptionPane.showMessageDialog(null, "Trim tab parameters will complete with 123FASTQ recommendations.\nChange them if you want.");
        ClickedTrim++;
        boolean shouldRunAutoTrim = true;
        TrimPanel trimTab = new TrimPanel(mainQueue, shouldRunAutoTrim, tabPane, starterPanel, bg);

        //Fetching QC modules
        SequenceLengthDistribution SLD = new SequenceLengthDistribution();
        BasicStats BS = new BasicStats(name1);
        PerBaseQualityScores PBQS = new PerBaseQualityScores();
        AdapterContent AC = new AdapterContent();
        NContent NC = new NContent();
        PerBaseSequenceContent PBSC = new PerBaseSequenceContent();
        for (QCModule module : modules1) {
            if (module.name().equals("Basic Statistics")) {
                BS = (BasicStats) module;
            }
            if (module.name().equals("Per Base Sequence Quality")) {
                PBQS = (PerBaseQualityScores) module;
            }
            if (module.name().equals("Adapter Content")) {
                AC = (AdapterContent) module;
            }
            if (module.name().equals("Sequence Length Distribution")) {
                SLD = (SequenceLengthDistribution) module;
            }
            if (module.name().equals("Per Base N Content")) {
                NC = (NContent) module;
            }
            if (module.name().equals("Per Base Sequence Content")) {
                PBSC = (PerBaseSequenceContent) module;
            }
        }

        //simple analysis to make decision for quality trimming
        PhredEncoding Phred = PhredEncoding.getFastQEncodingOffset(BS.getLowestChar());
        double[] Means = PBQS.getMeans();
        int meanOfMeans = 0;
        for (int i = 1; i < Means.length; i++) {
            meanOfMeans += Means[i];
        }
        meanOfMeans = meanOfMeans / Means.length;

        //fetching some data
        String Adapter = AC.mostIncidenceAdapterSequence();
        Integer EndCropBps = PBSC.getEndCropbps();
        Integer ThresholdEndCrop = PBSC.getEndCropThreshold();

        //setting some parameters of trim tab
        trimTab.setRdioSEMode();
        trimTab.setTxtImportFile1(inputFile1.getAbsolutePath());
        if (Phred.getOffset() == 33 || Phred.getOffset() == 64) {
            trimTab.setComboPhred("Phred" + Phred.getOffset());
        }

        //Leading and Trailing setting
        if (meanOfMeans > 33) {
            trimTab.setSpnLeading(33);
            trimTab.setSpnTrailing(33);
        } else {
            trimTab.setSpnLeading(meanOfMeans);
            trimTab.setSpnTrailing(meanOfMeans);
        }

        //Adapter seq setting
        if (Adapter != null) {
            trimTab.setChkAdapterTrim(true);
            trimTab.setRdioAdapterSeqApproach(true);
            trimTab.setTxtAdapterSeq(Adapter);
        }

        //End Crop setting
        if (EndCropBps != null) {
            trimTab.setChkEndCrop(true);
            trimTab.setSpnEndCrop(EndCropBps);
            trimTab.setSpnReadLenThreshold(ThresholdEndCrop);
        }

        //Sliding window setting
        trimTab.setSpnEachWindow(6);
        if (meanOfMeans > 35) {
            trimTab.setSpnMeanQualityOfEachWindow((float) 30);
        } else {
            trimTab.setSpnMeanQualityOfEachWindow((float) meanOfMeans - 5);
        }

        //MaxInfo parameters setting
        float NPercent = NC.getMeanNPercentage();
        if (NPercent > 0.01) {
            trimTab.setSpnTargetLength(SLD.getMeanLengthCounts());
            trimTab.setSpnStrictness((float) 0.6);
            if (NPercent > 1) {
                trimTab.setSpnStrictness((float) 0.8);
            }
        } else {
            trimTab.setChkAdaptive(false);
        }

        //deactive mean qual of each window
        trimTab.setChkMeanQual(false);

        //Phred convertor setting
        if (Phred.getOffset() != 33) {
            trimTab.setChkPhredConvertTo33(true);
        } else if (Phred.getOffset() == 33) {
            trimTab.setChkPhredConvert(false);
        }

        //finalization parameters setting
        trimTab.setCore(evt);
        String fileNameWithOutExt = FilenameUtils.removeExtension(inputFile1.getName());
        if (inputFile1.getParent().endsWith(Statics.SysFileSeprator)) {
            trimTab.setTxtSaveTo(inputFile1.getParent() + fileNameWithOutExt + "_Trimmed.fastq");
        } else {
            trimTab.setTxtSaveTo(inputFile1.getParent() + Statics.SysFileSeprator + fileNameWithOutExt + "_Trimmed.fastq");
        }
        JScrollPane scrollFrame = new JScrollPane(trimTab);
        trimTab.setAutoscrolls(true);
        scrollFrame.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollFrame.setPreferredSize(new Dimension(810, 650));
        tabPane.addTab("Trim Tab " + ClickedTrim, scrollFrame);
        int index = tabPane.indexOfTab("Trim Tab " + ClickedTrim);
        tabPane.setSelectedIndex(index);
        tabPane.setTabComponentAt(index, new TabComponents(tabPane, starterPanel, trimTab, mainQueue, bg));
    }

    public void Paired_End_Automatic_Trim(java.awt.event.ActionEvent evt) {

        //check the eligibility of imported files for trimming
        if ((!inputFile1.getName().toLowerCase().endsWith(".fq")
                && !inputFile1.getName().toLowerCase().endsWith(".txt.gz")
                && !inputFile1.getName().toLowerCase().endsWith(".fastq.gz")
                && !inputFile1.getName().toLowerCase().endsWith(".fq.gz")
                && !inputFile1.getName().toLowerCase().endsWith(".txt.bz2")
                && !inputFile1.getName().toLowerCase().endsWith(".fastq.bz2")
                && !inputFile1.getName().toLowerCase().endsWith(".txt")
                && !inputFile1.getName().toLowerCase().endsWith(".fastq")) || (!inputFile2.getName().toLowerCase().endsWith(".fq")
                && !inputFile2.getName().toLowerCase().endsWith(".txt.gz")
                && !inputFile2.getName().toLowerCase().endsWith(".fastq.gz")
                && !inputFile2.getName().toLowerCase().endsWith(".fq.gz")
                && !inputFile2.getName().toLowerCase().endsWith(".txt.bz2")
                && !inputFile2.getName().toLowerCase().endsWith(".fastq.bz2")
                && !inputFile2.getName().toLowerCase().endsWith(".txt")
                && !inputFile2.getName().toLowerCase().endsWith(".fastq"))) {
            JOptionPane.showMessageDialog(null, "It seems input files can not trim in current format.\nIf they are SAM, BAM or Fast5 files, convert them to Fastq file and then trim.");
            return;
        }
        JOptionPane.showMessageDialog(null, "Trim tab parameters will complete with 123FASTQ recommendations.\nChange them if you want.");
        ClickedTrim++;
        boolean shouldRunAutoTrim = true;
        TrimPanel trimTab = new TrimPanel(mainQueue, shouldRunAutoTrim, tabPane, starterPanel, bg);

        //Fetching QC modules
        SequenceLengthDistribution SLD1 = new SequenceLengthDistribution();
        BasicStats BS1 = new BasicStats(name1);
        PerBaseQualityScores PBQS1 = new PerBaseQualityScores();
        AdapterContent AC1 = new AdapterContent();
        NContent NC1 = new NContent();
        PerBaseSequenceContent PBSC1 = new PerBaseSequenceContent();
        SequenceLengthDistribution SLD2 = new SequenceLengthDistribution();
        BasicStats BS2 = new BasicStats(name2);
        PerBaseQualityScores PBQS2 = new PerBaseQualityScores();
        AdapterContent AC2 = new AdapterContent();
        NContent NC2 = new NContent();
        PerBaseSequenceContent PBSC2 = new PerBaseSequenceContent();
        for (QCModule module : modules1) {
            if (module.name().equals("Basic Statistics")) {
                BS1 = (BasicStats) module;
            }
            if (module.name().equals("Per Base Sequence Quality")) {
                PBQS1 = (PerBaseQualityScores) module;
            }
            if (module.name().equals("Adapter Content")) {
                AC1 = (AdapterContent) module;
            }
            if (module.name().equals("Sequence Length Distribution")) {
                SLD1 = (SequenceLengthDistribution) module;
            }
            if (module.name().equals("Per Base N Content")) {
                NC1 = (NContent) module;
            }
            if (module.name().equals("Per Base Sequence Content")) {
                PBSC1 = (PerBaseSequenceContent) module;
            }
        }
        for (QCModule module : modules2) {
            if (module.name().equals("Basic Statistics")) {
                BS2 = (BasicStats) module;
            }
            if (module.name().equals("Per Base Sequence Quality")) {
                PBQS2 = (PerBaseQualityScores) module;
            }
            if (module.name().equals("Adapter Content")) {
                AC2 = (AdapterContent) module;
            }
            if (module.name().equals("Sequence Length Distribution")) {
                SLD2 = (SequenceLengthDistribution) module;
            }
            if (module.name().equals("Per Base N Content")) {
                NC2 = (NContent) module;
            }
            if (module.name().equals("Per Base Sequence Content")) {
                PBSC2 = (PerBaseSequenceContent) module;
            }
        }

        //simple analysis to make decision for quality trimming
        PhredEncoding Phred1 = PhredEncoding.getFastQEncodingOffset(BS1.getLowestChar());
        PhredEncoding Phred2 = PhredEncoding.getFastQEncodingOffset(BS2.getLowestChar());
        double[] Means1 = PBQS1.getMeans();
        double[] Means2 = PBQS2.getMeans();
        int meanOfMeans1 = 0;
        for (int i = 1; i < Means1.length; i++) {
            meanOfMeans1 += Means1[i];
        }
        meanOfMeans1 = meanOfMeans1 / Means1.length;
        int meanOfMeans2 = 0;
        for (int i = 1; i < Means2.length; i++) {
            meanOfMeans2 += Means2[i];
        }
        meanOfMeans2 = meanOfMeans2 / Means2.length;
        int MainMean = (meanOfMeans1 + meanOfMeans2) / 2;

        //fetching some data
        String Adapter1 = AC1.mostIncidenceAdapterSequence();
        String Adapter2 = AC2.mostIncidenceAdapterSequence();
        Integer EndCropBps1 = PBSC1.getEndCropbps();
        Integer EndCropBps2 = PBSC2.getEndCropbps();
        Integer ThresholdEndCrop1 = PBSC1.getEndCropThreshold();
        Integer ThresholdEndCrop2 = PBSC2.getEndCropThreshold();

        //setting some parameters of trim tab
        trimTab.setRdioPEMode();
        trimTab.setTxtImportFile1(inputFile1.getAbsolutePath());
        trimTab.setTxtImportFile2(inputFile2.getAbsolutePath());
        if (Phred1.getOffset() == Phred2.getOffset()) {
            if (Phred1.getOffset() == 33 || Phred1.getOffset() == 64) {
                trimTab.setComboPhred("Phred" + Phred1.getOffset());
            }
        }

        //Leading and Trailing setting
        if (MainMean > 33) {
            trimTab.setSpnLeading(33);
            trimTab.setSpnTrailing(33);
        } else {
            trimTab.setSpnLeading(MainMean);
            trimTab.setSpnTrailing(MainMean);
        }

        //Adapter seq setting
        if (Adapter1 != null || Adapter2 != null) {
            if (AC1.getMostCount() >= AC2.getMostCount()) {
                trimTab.setChkAdapterTrim(true);
                trimTab.setRdioAdapterSeqApproach(true);
                trimTab.setTxtAdapterSeq(Adapter1);
            } else {
                trimTab.setChkAdapterTrim(true);
                trimTab.setRdioAdapterSeqApproach(true);
                trimTab.setTxtAdapterSeq(Adapter2);
            }
        }

        //End Crop setting
        int EndCrop = 0;
        int EndCropThreshold = 0;
        if (EndCropBps1 != null && EndCropBps2 != null) {

            EndCrop = (EndCropBps1 + EndCropBps2) / 2;

            if (ThresholdEndCrop1 < ThresholdEndCrop2) {
                EndCropThreshold = ThresholdEndCrop1;
            } else {
                EndCropThreshold = ThresholdEndCrop2;
            }
            trimTab.setChkEndCrop(true);
            trimTab.setSpnEndCrop(EndCrop);
            trimTab.setSpnReadLenThreshold(EndCropThreshold);
        }

        //Sliding window setting
        trimTab.setSpnEachWindow(6);
        if (MainMean > 31) {
            trimTab.setSpnMeanQualityOfEachWindow((float) 31);
        } else {
            trimTab.setSpnMeanQualityOfEachWindow((float) MainMean - 2);
        }

        //MaxInfo parameters setting
        int SLDmean = SLD1.getMeanLengthCounts() + SLD2.getMeanLengthCounts() / 2;
        float NPercent1 = NC1.getMeanNPercentage();
        float NPercent2 = NC2.getMeanNPercentage();
        float MeanNPercentage = (NPercent1 + NPercent2) / 2;
        if (MeanNPercentage > 0.5) {
            trimTab.setSpnTargetLength(SLDmean);
            trimTab.setSpnStrictness((float) 0.6);
            if (MeanNPercentage > 1) {
                trimTab.setSpnStrictness((float) 0.8);
            }
        } else {
            trimTab.setChkAdaptive(false);
        }

        //deactive mean qual of each window
        trimTab.setChkMeanQual(false);

        //Phred convertor setting
        if (Phred1.getOffset() != 33 || Phred2.getOffset() != 33) {
            trimTab.setChkPhredConvertTo33(true);
        } else {
            trimTab.setChkPhredConvert(false);
        }

        //finalization parameters setting
        trimTab.setCore(evt);
        trimTab.setTxtSaveTo(inputFile1.getParent());
        JScrollPane scrollFrame = new JScrollPane(trimTab);
        trimTab.setAutoscrolls(true);
        scrollFrame.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollFrame.setPreferredSize(new Dimension(810, 650));
        tabPane.addTab("Trim Tab " + ClickedTrim, scrollFrame);
        int index = tabPane.indexOfTab("Trim Tab " + ClickedTrim);
        tabPane.setSelectedIndex(index);
        tabPane.setTabComponentAt(index, new TabComponents(tabPane, starterPanel, trimTab, mainQueue, bg));
    }
}
