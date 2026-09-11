package Eidi._123Fastq.TrimFactoryPackage;

import static Eidi._123Fastq.GUI.App123Fastq.Single_ModeQC;
import static Eidi._123Fastq.GUI.App123Fastq.lastDirectory;
import Eidi._123Fastq.GUI.MovableTabbedPane;
import Eidi._123Fastq.GUI.MyJPanel;
import Eidi._123Fastq.GUI.Statics;
import Eidi._123Fastq.GUI.TabComponents;
import Eidi._123Fastq.GUI.TaskQueue;
import Eidi._123Fastq.QualityControlPackage.Results.ResultsPanel;
import Eidi._123Fastq.QualityControlPackage.Results.QcRunner;
import Eidi._123Fastq.QualityControlPackage.Sequence.SequenceFactory;
import Eidi._123Fastq.QualityControlPackage.Sequence.SequenceFile;
import Eidi._123Fastq.QualityControlPackage.Sequence.SequenceFormatException;
import java.awt.Container;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.text.DefaultCaret;

public class TrimPanel extends MyJPanel {

    private static final int MIN_TRIM_THREADS = 1;

    private TaskQueue mainQueue;
    private boolean shouldRun;
    private boolean endOptionsFlag;
    private Thread trimPanelThread = new Thread();
    private MovableTabbedPane tabPane;
    private JPanel starterPanel;
    private JPanel bg;

    public TrimPanel(TaskQueue MainQueue, boolean ShouldRun, MovableTabbedPane TabPane, JPanel StarterPanel, JPanel background) {
        this.mainQueue = MainQueue;
        this.shouldRun = ShouldRun;
        this.tabPane = TabPane;
        this.starterPanel = StarterPanel;
        this.bg = background;
        initComponents();
    }

    public void setShouldRun(boolean shouldRun) {
        this.shouldRun = shouldRun;
    }

    public Thread getThread() {
        return trimPanelThread;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnGroupConvertEncoding = new javax.swing.ButtonGroup();
        btnGroupTrimmingModes = new javax.swing.ButtonGroup();
        btnGroupAdapters = new javax.swing.ButtonGroup();
        popupAdapterSeq = new javax.swing.JPopupMenu();
        menuCopy = new javax.swing.JMenuItem();
        menuPaste = new javax.swing.JMenuItem();
        jPanel2 = new javax.swing.JPanel();
        rdioSEMode = new javax.swing.JRadioButton();
        rdioPEMode = new javax.swing.JRadioButton();
        jLabel1 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        lblLeading = new javax.swing.JLabel();
        lblTrailing = new javax.swing.JLabel();
        lblHeadCrop = new javax.swing.JLabel();
        lblEndCrop = new javax.swing.JLabel();
        spnLeading = new javax.swing.JSpinner();
        spnTrailing = new javax.swing.JSpinner();
        spnHeadCrop = new javax.swing.JSpinner();
        spnEndCrop = new javax.swing.JSpinner();
        chkLeading = new javax.swing.JCheckBox();
        chkTrailing = new javax.swing.JCheckBox();
        chkHeadCrop = new javax.swing.JCheckBox();
        chkEndCrop = new javax.swing.JCheckBox();
        spnMeanQualityOfEachWindow = new javax.swing.JSpinner();
        spnEachWindow = new javax.swing.JSpinner();
        chkSlidingWindow = new javax.swing.JCheckBox();
        lblEachWindow = new javax.swing.JLabel();
        lblMeanQualEachWindow = new javax.swing.JLabel();
        lblWarnSlidingWindow = new javax.swing.JLabel();
        lblQualitySlidingWindows = new javax.swing.JLabel();
        lblWarnHeadCrop = new javax.swing.JLabel();
        lblWarnEndCrop = new javax.swing.JLabel();
        lblWarnLeading = new javax.swing.JLabel();
        lblWarnTrailing = new javax.swing.JLabel();
        lblEndRepeat = new javax.swing.JLabel();
        chkEndRepeat = new javax.swing.JCheckBox();
        spnEndRepeat = new javax.swing.JSpinner();
        lblWarnBaseRepeats = new javax.swing.JLabel();
        spnGEndRepeats = new javax.swing.JSpinner();
        lblWarnGEndRepeats = new javax.swing.JLabel();
        lblGEndRepeat = new javax.swing.JLabel();
        chkGEndRepeat = new javax.swing.JCheckBox();
        lblSimpleTrimOptions = new javax.swing.JLabel();
        lblIfreadLongerThan = new javax.swing.JLabel();
        spnEndCropThreshold = new javax.swing.JSpinner();
        jPanel6 = new javax.swing.JPanel();
        btnLetsTrim = new javax.swing.JButton();
        lblThreads = new javax.swing.JLabel();
        spnThreads = new javax.swing.JSpinner();
        btnSaveTo = new javax.swing.JButton();
        txtSaveTo = new javax.swing.JTextField();
        lblSaveTo = new javax.swing.JLabel();
        btnCoreCalculation = new javax.swing.JButton();
        lblFinalizeTrim = new javax.swing.JLabel();
        lblSpecify = new javax.swing.JLabel();
        btnStop = new javax.swing.JButton();
        chkReportSheet = new javax.swing.JCheckBox();
        chkDeleteComments = new javax.swing.JCheckBox();
        pnlAdapterTrim = new javax.swing.JPanel();
        lblAdapterParameter = new javax.swing.JLabel();
        lblSelectAdapter = new javax.swing.JLabel();
        lblSeedMismatch = new javax.swing.JLabel();
        lblPalindromeLikelihood = new javax.swing.JLabel();
        lblMinPrefix = new javax.swing.JLabel();
        lblSequenceLikelihood = new javax.swing.JLabel();
        lblKeepBoth = new javax.swing.JLabel();
        comboAdaptersFiles = new javax.swing.JComboBox<>();
        spnSeedMismatchAdapters = new javax.swing.JSpinner();
        spnPalindromeLikelihood = new javax.swing.JSpinner();
        spnMinprefix = new javax.swing.JSpinner();
        spnSequenceLikelihood = new javax.swing.JSpinner();
        comboKeepBoth = new javax.swing.JComboBox<>();
        chkAdapterTrim = new javax.swing.JCheckBox();
        lblWarnsSeedMismatches = new javax.swing.JLabel();
        lblOptionalField = new javax.swing.JLabel();
        txtAdapterSeq = new javax.swing.JTextField();
        lblAdapterSeq = new javax.swing.JLabel();
        lblWarnAdapterSeq = new javax.swing.JLabel();
        rdioAdapterSeqApproach = new javax.swing.JRadioButton();
        rdioTrimmomaticApproach = new javax.swing.JRadioButton();
        lblAdapterSeq1 = new javax.swing.JLabel();
        lblTrimmomaticApproach = new javax.swing.JLabel();
        chkMinAdapterLen = new javax.swing.JCheckBox();
        chkReverseComplement = new javax.swing.JCheckBox();
        lblWarnsPalindromeLikelihood = new javax.swing.JLabel();
        lblWarnsSimpleLikelihood = new javax.swing.JLabel();
        lblWarnsMinAdapterLen = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        lblMaxLen = new javax.swing.JLabel();
        chkMaxLen = new javax.swing.JCheckBox();
        chkMinLen = new javax.swing.JCheckBox();
        chkMeanQual = new javax.swing.JCheckBox();
        lblMeanQual = new javax.swing.JLabel();
        lblMinLen = new javax.swing.JLabel();
        spnMaxLen = new javax.swing.JSpinner();
        spnMinLen = new javax.swing.JSpinner();
        spnMeanQual = new javax.swing.JSpinner();
        rdioPhred33 = new javax.swing.JRadioButton();
        rdioPhred64 = new javax.swing.JRadioButton();
        chk3364 = new javax.swing.JCheckBox();
        lblEndTrim = new javax.swing.JLabel();
        lblWarnEachReadMeanQual = new javax.swing.JLabel();
        lblWarnMaxLen = new javax.swing.JLabel();
        lblWarnMinLen = new javax.swing.JLabel();
        lblStrictness = new javax.swing.JLabel();
        chkAdaptive = new javax.swing.JCheckBox();
        spnStrictness = new javax.swing.JSpinner();
        lblWarnStrictness = new javax.swing.JLabel();
        lblWarnTargetLen = new javax.swing.JLabel();
        lblTargetLength = new javax.swing.JLabel();
        spnTargetLength = new javax.swing.JSpinner();
        lblAdaptiveErrorCorrector = new javax.swing.JLabel();
        lblMaxGCcontent = new javax.swing.JLabel();
        lblWarnMaxGC = new javax.swing.JLabel();
        chkMaxGC = new javax.swing.JCheckBox();
        spnMaxGC = new javax.swing.JSpinner();
        lblMinGCcontent = new javax.swing.JLabel();
        chkMinGC = new javax.swing.JCheckBox();
        lblWarnMinGC = new javax.swing.JLabel();
        spnMinGC = new javax.swing.JSpinner();
        lblConvertQualities = new javax.swing.JLabel();

        menuCopy.setText("Copy");
        menuCopy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuCopyActionPerformed(evt);
            }
        });
        popupAdapterSeq.add(menuCopy);

        menuPaste.setText("Paste");
        menuPaste.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuPasteActionPerformed(evt);
            }
        });
        popupAdapterSeq.add(menuPaste);

        setMinimumSize(new java.awt.Dimension(810, 1950));
        setPreferredSize(new java.awt.Dimension(810, 1919));
        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel2.setBackground(new java.awt.Color(109, 112, 137));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnGroupTrimmingModes.add(rdioSEMode);
        rdioSEMode.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        rdioSEMode.setForeground(new java.awt.Color(153, 204, 255));
        rdioSEMode.setText("Single-End Mode");
        rdioSEMode.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                rdioSEModeItemStateChanged(evt);
            }
        });
        rdioSEMode.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                rdioSEModeMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                rdioSEModeMouseExited(evt);
            }
        });
        jPanel2.add(rdioSEMode, new org.netbeans.lib.awtextra.AbsoluteConstraints(167, 39, -1, -1));

        btnGroupTrimmingModes.add(rdioPEMode);
        rdioPEMode.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        rdioPEMode.setForeground(new java.awt.Color(153, 204, 255));
        rdioPEMode.setText("Paired-End Mode");
        rdioPEMode.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                rdioPEModeItemStateChanged(evt);
            }
        });
        rdioPEMode.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                rdioPEModeMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                rdioPEModeMouseExited(evt);
            }
        });
        jPanel2.add(rdioPEMode, new org.netbeans.lib.awtextra.AbsoluteConstraints(449, 39, -1, -1));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Select trimming mode");
        jPanel2.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(7, 3, -1, -1));

        add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 810, 80));

        jPanel3.setBackground(new java.awt.Color(109, 112, 137));
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 80, 810, 120));

        jPanel5.setBackground(new java.awt.Color(45, 51, 81));
        jPanel5.setMinimumSize(new java.awt.Dimension(810, 418));
        jPanel5.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblLeading.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblLeading.setForeground(new java.awt.Color(170, 204, 255));
        lblLeading.setText("Quality threshold from 5':");
        lblLeading.setToolTipText("Leading\n");
        lblLeading.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblLeadingMouseClicked(evt);
            }
        });
        jPanel5.add(lblLeading, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 220, -1, 30));

        lblTrailing.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblTrailing.setForeground(new java.awt.Color(170, 204, 255));
        lblTrailing.setText("Quality threshold from 3':");
        lblTrailing.setToolTipText("Trailing\n");
        lblTrailing.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblTrailingMouseClicked(evt);
            }
        });
        jPanel5.add(lblTrailing, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 250, -1, 40));

        lblHeadCrop.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblHeadCrop.setForeground(new java.awt.Color(170, 204, 255));
        lblHeadCrop.setText("Cut bps from 5':");
        lblHeadCrop.setEnabled(false);
        lblHeadCrop.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblHeadCropMouseClicked(evt);
            }
        });
        jPanel5.add(lblHeadCrop, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 60, 145, 30));

        lblEndCrop.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblEndCrop.setForeground(new java.awt.Color(170, 204, 255));
        lblEndCrop.setText("Cut bps from 3':");
        lblEndCrop.setEnabled(false);
        lblEndCrop.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblEndCropMouseClicked(evt);
            }
        });
        jPanel5.add(lblEndCrop, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 100, 145, 30));

        spnLeading.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        spnLeading.setModel(new javax.swing.SpinnerNumberModel(25, 0, 40, 1));
        spnLeading.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spnLeadingStateChanged(evt);
            }
        });
        jPanel5.add(spnLeading, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 220, 70, -1));

        spnTrailing.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        spnTrailing.setModel(new javax.swing.SpinnerNumberModel(25, 0, 40, 1));
        spnTrailing.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spnTrailingStateChanged(evt);
            }
        });
        jPanel5.add(spnTrailing, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 260, 70, -1));

        spnHeadCrop.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        spnHeadCrop.setModel(new javax.swing.SpinnerNumberModel(0, 0, 70, 1));
        spnHeadCrop.setEnabled(false);
        spnHeadCrop.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spnHeadCropStateChanged(evt);
            }
        });
        jPanel5.add(spnHeadCrop, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 60, 70, 30));

        spnEndCrop.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        spnEndCrop.setModel(new javax.swing.SpinnerNumberModel(0, 0, 70, 1));
        spnEndCrop.setEnabled(false);
        spnEndCrop.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spnEndCropStateChanged(evt);
            }
        });
        jPanel5.add(spnEndCrop, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 100, 70, 30));

        chkLeading.setSelected(true);
        chkLeading.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                chkLeadingStateChanged(evt);
            }
        });
        jPanel5.add(chkLeading, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 220, -1, 30));

        chkTrailing.setSelected(true);
        chkTrailing.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                chkTrailingStateChanged(evt);
            }
        });
        jPanel5.add(chkTrailing, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 260, -1, -1));

        chkHeadCrop.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                chkHeadCropStateChanged(evt);
            }
        });
        jPanel5.add(chkHeadCrop, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 60, -1, 30));

        chkEndCrop.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                chkEndCropStateChanged(evt);
            }
        });
        jPanel5.add(chkEndCrop, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 100, -1, 30));

        spnMeanQualityOfEachWindow.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        spnMeanQualityOfEachWindow.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(15.0f), Float.valueOf(0.0f), Float.valueOf(40.0f), Float.valueOf(1.0f)));
        spnMeanQualityOfEachWindow.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spnMeanQualityOfEachWindowStateChanged(evt);
            }
        });
        jPanel5.add(spnMeanQualityOfEachWindow, new org.netbeans.lib.awtextra.AbsoluteConstraints(711, 390, 72, -1));

        spnEachWindow.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        spnEachWindow.setModel(new javax.swing.SpinnerNumberModel(5, 3, 20, 1));
        jPanel5.add(spnEachWindow, new org.netbeans.lib.awtextra.AbsoluteConstraints(711, 350, 72, -1));

        chkSlidingWindow.setSelected(true);
        chkSlidingWindow.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                chkSlidingWindowStateChanged(evt);
            }
        });
        jPanel5.add(chkSlidingWindow, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 370, -1, -1));

        lblEachWindow.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblEachWindow.setForeground(new java.awt.Color(170, 204, 255));
        lblEachWindow.setText("Size of each window:");
        lblEachWindow.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblEachWindowMouseClicked(evt);
            }
        });
        jPanel5.add(lblEachWindow, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 350, -1, 30));

        lblMeanQualEachWindow.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblMeanQualEachWindow.setForeground(new java.awt.Color(170, 204, 255));
        lblMeanQualEachWindow.setText("Mean quality of each windows:");
        lblMeanQualEachWindow.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblMeanQualEachWindowMouseClicked(evt);
            }
        });
        jPanel5.add(lblMeanQualEachWindow, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 390, -1, 30));

        lblWarnSlidingWindow.setBackground(new java.awt.Color(170, 204, 255));
        lblWarnSlidingWindow.setFont(new java.awt.Font("Segoe UI", 3, 12)); // NOI18N
        lblWarnSlidingWindow.setForeground(new java.awt.Color(255, 51, 102));
        lblWarnSlidingWindow.setFocusable(false);
        jPanel5.add(lblWarnSlidingWindow, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 390, 391, 30));

        lblQualitySlidingWindows.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblQualitySlidingWindows.setForeground(new java.awt.Color(204, 204, 255));
        lblQualitySlidingWindows.setText("Quality sliding window");
        lblQualitySlidingWindows.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblQualitySlidingWindowsMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lblQualitySlidingWindowsMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lblQualitySlidingWindowsMouseExited(evt);
            }
        });
        jPanel5.add(lblQualitySlidingWindows, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 310, -1, -1));

        lblWarnHeadCrop.setBackground(new java.awt.Color(170, 204, 255));
        lblWarnHeadCrop.setFont(new java.awt.Font("Segoe UI", 3, 12)); // NOI18N
        lblWarnHeadCrop.setForeground(new java.awt.Color(255, 51, 102));
        lblWarnHeadCrop.setFocusable(false);
        jPanel5.add(lblWarnHeadCrop, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 60, 391, 30));

        lblWarnEndCrop.setBackground(new java.awt.Color(170, 204, 255));
        lblWarnEndCrop.setFont(new java.awt.Font("Segoe UI", 3, 12)); // NOI18N
        lblWarnEndCrop.setForeground(new java.awt.Color(255, 51, 102));
        lblWarnEndCrop.setFocusable(false);
        jPanel5.add(lblWarnEndCrop, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 100, 270, 30));

        lblWarnLeading.setBackground(new java.awt.Color(170, 204, 255));
        lblWarnLeading.setFont(new java.awt.Font("Segoe UI", 3, 12)); // NOI18N
        lblWarnLeading.setForeground(new java.awt.Color(255, 51, 102));
        lblWarnLeading.setFocusable(false);
        jPanel5.add(lblWarnLeading, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 220, 391, 30));

        lblWarnTrailing.setBackground(new java.awt.Color(170, 204, 255));
        lblWarnTrailing.setFont(new java.awt.Font("Segoe UI", 3, 12)); // NOI18N
        lblWarnTrailing.setForeground(new java.awt.Color(255, 51, 102));
        lblWarnTrailing.setFocusable(false);
        jPanel5.add(lblWarnTrailing, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 260, 391, 20));

        lblEndRepeat.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblEndRepeat.setForeground(new java.awt.Color(170, 204, 255));
        lblEndRepeat.setText("Maximum allowed repeated bps at 3' of each read:");
        lblEndRepeat.setEnabled(false);
        lblEndRepeat.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblEndRepeatMouseClicked(evt);
            }
        });
        jPanel5.add(lblEndRepeat, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 180, -1, 30));

        chkEndRepeat.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                chkEndRepeatStateChanged(evt);
            }
        });
        jPanel5.add(chkEndRepeat, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 180, -1, 30));

        spnEndRepeat.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        spnEndRepeat.setModel(new javax.swing.SpinnerNumberModel(8, 2, 30, 1));
        spnEndRepeat.setEnabled(false);
        spnEndRepeat.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spnEndRepeatStateChanged(evt);
            }
        });
        jPanel5.add(spnEndRepeat, new org.netbeans.lib.awtextra.AbsoluteConstraints(708, 181, 70, -1));

        lblWarnBaseRepeats.setFont(new java.awt.Font("Segoe UI", 3, 12)); // NOI18N
        lblWarnBaseRepeats.setForeground(new java.awt.Color(255, 51, 102));
        jPanel5.add(lblWarnBaseRepeats, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 180, 170, 30));

        spnGEndRepeats.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        spnGEndRepeats.setModel(new javax.swing.SpinnerNumberModel(8, 2, 30, 1));
        spnGEndRepeats.setEnabled(false);
        spnGEndRepeats.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spnGEndRepeatsStateChanged(evt);
            }
        });
        jPanel5.add(spnGEndRepeats, new org.netbeans.lib.awtextra.AbsoluteConstraints(708, 141, 70, 30));

        lblWarnGEndRepeats.setFont(new java.awt.Font("Segoe UI", 3, 12)); // NOI18N
        lblWarnGEndRepeats.setForeground(new java.awt.Color(255, 51, 102));
        jPanel5.add(lblWarnGEndRepeats, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 140, 170, 30));

        lblGEndRepeat.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblGEndRepeat.setForeground(new java.awt.Color(170, 204, 255));
        lblGEndRepeat.setText("Maximum allowed \"G\" repeats at 3' of each read:");
        lblGEndRepeat.setEnabled(false);
        lblGEndRepeat.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblGEndRepeatMouseClicked(evt);
            }
        });
        jPanel5.add(lblGEndRepeat, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 140, -1, 30));

        chkGEndRepeat.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                chkGEndRepeatStateChanged(evt);
            }
        });
        jPanel5.add(chkGEndRepeat, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 140, -1, 30));

        lblSimpleTrimOptions.setFont(new java.awt.Font("Segoe UI", 1, 22)); // NOI18N
        lblSimpleTrimOptions.setForeground(new java.awt.Color(204, 204, 255));
        lblSimpleTrimOptions.setText("Trim options");
        lblSimpleTrimOptions.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        lblSimpleTrimOptions.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblSimpleTrimOptionsMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lblSimpleTrimOptionsMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lblSimpleTrimOptionsMouseExited(evt);
            }
        });
        jPanel5.add(lblSimpleTrimOptions, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 10, -1, 30));

        lblIfreadLongerThan.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblIfreadLongerThan.setForeground(new java.awt.Color(170, 204, 255));
        lblIfreadLongerThan.setText("if read longer than:");
        lblIfreadLongerThan.setEnabled(false);
        jPanel5.add(lblIfreadLongerThan, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 100, 150, 30));

        spnEndCropThreshold.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        spnEndCropThreshold.setModel(new javax.swing.SpinnerNumberModel(0, 0, null, 1));
        spnEndCropThreshold.setEnabled(false);
        jPanel5.add(spnEndCropThreshold, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 100, 70, 30));

        add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 600, 810, 430));

        jPanel6.setBackground(new java.awt.Color(5, 25, 25));
        jPanel6.setMinimumSize(new java.awt.Dimension(810, 198));
        jPanel6.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnLetsTrim.setBackground(new java.awt.Color(246, 36, 89));
        btnLetsTrim.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnLetsTrim.setForeground(new java.awt.Color(255, 255, 255));
        btnLetsTrim.setText("Let's Trim");
        btnLetsTrim.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLetsTrimActionPerformed(evt);
            }
        });
        jPanel6.add(btnLetsTrim, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 150, 166, -1));

        lblThreads.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblThreads.setForeground(new java.awt.Color(170, 204, 255));
        lblThreads.setText("Multi-Threading:");
        jPanel6.add(lblThreads, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 111, 140, 20));

        spnThreads.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        spnThreads.setModel(new javax.swing.SpinnerNumberModel(1, MIN_TRIM_THREADS, null, 1));
        spnThreads.setEnabled(false);
        jPanel6.add(spnThreads, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 110, 60, -1));

        btnSaveTo.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnSaveTo.setText("Save to");
        btnSaveTo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveToActionPerformed(evt);
            }
        });
        jPanel6.add(btnSaveTo, new org.netbeans.lib.awtextra.AbsoluteConstraints(696, 63, -1, -1));

        txtSaveTo.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jPanel6.add(txtSaveTo, new org.netbeans.lib.awtextra.AbsoluteConstraints(138, 67, 530, -1));

        lblSaveTo.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblSaveTo.setForeground(new java.awt.Color(170, 204, 255));
        lblSaveTo.setText("Output file:");
        jPanel6.add(lblSaveTo, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 70, 110, -1));

        btnCoreCalculation.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnCoreCalculation.setText("Use recommended thread count");
        btnCoreCalculation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCoreCalculationActionPerformed(evt);
            }
        });
        jPanel6.add(btnCoreCalculation, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 106, 280, -1));

        lblFinalizeTrim.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblFinalizeTrim.setForeground(new java.awt.Color(255, 255, 255));
        lblFinalizeTrim.setText("Finalize Trimming");
        jPanel6.add(lblFinalizeTrim, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, -1));

        lblSpecify.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblSpecify.setForeground(new java.awt.Color(170, 204, 255));
        lblSpecify.setText("Specify number of threads > ");
        lblSpecify.setEnabled(false);
        jPanel6.add(lblSpecify, new org.netbeans.lib.awtextra.AbsoluteConstraints(458, 114, 240, -1));

        btnStop.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnStop.setText("Stop!");
        btnStop.setEnabled(false);
        btnStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStopActionPerformed(evt);
            }
        });
        jPanel6.add(btnStop, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 170, 130, -1));

        chkReportSheet.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        chkReportSheet.setForeground(new java.awt.Color(170, 204, 255));
        chkReportSheet.setText("Make report sheet");
        chkReportSheet.setToolTipText("Make report sheet in the output path.");
        jPanel6.add(chkReportSheet, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 180, 180, -1));

        chkDeleteComments.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        chkDeleteComments.setForeground(new java.awt.Color(170, 204, 255));
        chkDeleteComments.setSelected(true);
        chkDeleteComments.setText("Delete comments");
        chkDeleteComments.setToolTipText("To reduce output file size");
        jPanel6.add(chkDeleteComments, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 150, 180, -1));

        add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 1450, 810, 470));

        pnlAdapterTrim.setBackground(new java.awt.Color(75, 70, 109));
        pnlAdapterTrim.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblAdapterParameter.setFont(new java.awt.Font("Segoe UI", 1, 22)); // NOI18N
        lblAdapterParameter.setForeground(new java.awt.Color(204, 204, 255));
        lblAdapterParameter.setText("Adapter decontaminators");
        lblAdapterParameter.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        lblAdapterParameter.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblAdapterParameterMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lblAdapterParameterMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lblAdapterParameterMouseExited(evt);
            }
        });
        pnlAdapterTrim.add(lblAdapterParameter, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 40, -1, 30));

        lblSelectAdapter.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblSelectAdapter.setForeground(new java.awt.Color(170, 204, 255));
        lblSelectAdapter.setText("Select Adapter File:");
        lblSelectAdapter.setEnabled(false);
        lblSelectAdapter.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblSelectAdapterMouseClicked(evt);
            }
        });
        pnlAdapterTrim.add(lblSelectAdapter, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 160, -1, 30));

        lblSeedMismatch.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblSeedMismatch.setForeground(new java.awt.Color(170, 204, 255));
        lblSeedMismatch.setText("Maximum seed mismatches:");
        lblSeedMismatch.setEnabled(false);
        lblSeedMismatch.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblSeedMismatchMouseClicked(evt);
            }
        });
        pnlAdapterTrim.add(lblSeedMismatch, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 200, -1, 30));

        lblPalindromeLikelihood.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblPalindromeLikelihood.setForeground(new java.awt.Color(170, 204, 255));
        lblPalindromeLikelihood.setText("Palindrome clip threshold:");
        lblPalindromeLikelihood.setEnabled(false);
        lblPalindromeLikelihood.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblPalindromeLikelihoodMouseClicked(evt);
            }
        });
        pnlAdapterTrim.add(lblPalindromeLikelihood, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 240, -1, 28));

        lblMinPrefix.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblMinPrefix.setForeground(new java.awt.Color(170, 204, 255));
        lblMinPrefix.setText("Min adapter length:");
        lblMinPrefix.setEnabled(false);
        lblMinPrefix.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblMinPrefixMouseClicked(evt);
            }
        });
        pnlAdapterTrim.add(lblMinPrefix, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 320, -1, 28));

        lblSequenceLikelihood.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblSequenceLikelihood.setForeground(new java.awt.Color(170, 204, 255));
        lblSequenceLikelihood.setText("Simple clip threshold:");
        lblSequenceLikelihood.setEnabled(false);
        lblSequenceLikelihood.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblSequenceLikelihoodMouseClicked(evt);
            }
        });
        pnlAdapterTrim.add(lblSequenceLikelihood, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 280, -1, 28));

        lblKeepBoth.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblKeepBoth.setForeground(new java.awt.Color(170, 204, 255));
        lblKeepBoth.setText("Keep both:");
        lblKeepBoth.setEnabled(false);
        lblKeepBoth.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblKeepBothMouseClicked(evt);
            }
        });
        pnlAdapterTrim.add(lblKeepBoth, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 360, -1, 28));

        comboAdaptersFiles.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        comboAdaptersFiles.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Nextera-PE.fa", "TruSeq2-PE.fa", "TruSeq2-SE.fa", "TruSeq3-PE.fa", "TruSeq3-SE.fa" }));
        comboAdaptersFiles.setEnabled(false);
        pnlAdapterTrim.add(comboAdaptersFiles, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 160, 150, 30));

        spnSeedMismatchAdapters.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        spnSeedMismatchAdapters.setModel(new javax.swing.SpinnerNumberModel(3, 0, 8, 1));
        spnSeedMismatchAdapters.setEnabled(false);
        spnSeedMismatchAdapters.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spnSeedMismatchAdaptersStateChanged(evt);
            }
        });
        pnlAdapterTrim.add(spnSeedMismatchAdapters, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 200, 70, -1));

        spnPalindromeLikelihood.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        spnPalindromeLikelihood.setModel(new javax.swing.SpinnerNumberModel(25, 7, 60, 1));
        spnPalindromeLikelihood.setEnabled(false);
        spnPalindromeLikelihood.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spnPalindromeLikelihoodStateChanged(evt);
            }
        });
        pnlAdapterTrim.add(spnPalindromeLikelihood, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 240, 70, -1));

        spnMinprefix.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        spnMinprefix.setModel(new javax.swing.SpinnerNumberModel(8, 1, 35, 1));
        spnMinprefix.setEnabled(false);
        spnMinprefix.setRequestFocusEnabled(false);
        spnMinprefix.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spnMinprefixStateChanged(evt);
            }
        });
        pnlAdapterTrim.add(spnMinprefix, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 320, 70, -1));

        spnSequenceLikelihood.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        spnSequenceLikelihood.setModel(new javax.swing.SpinnerNumberModel(10, 3, 40, 1));
        spnSequenceLikelihood.setEnabled(false);
        spnSequenceLikelihood.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spnSequenceLikelihoodStateChanged(evt);
            }
        });
        pnlAdapterTrim.add(spnSequenceLikelihood, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 280, 70, -1));

        comboKeepBoth.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        comboKeepBoth.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Yes", "No" }));
        comboKeepBoth.setEnabled(false);
        pnlAdapterTrim.add(comboKeepBoth, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 360, 70, 28));

        chkAdapterTrim.setBackground(new java.awt.Color(255, 0, 204));
        chkAdapterTrim.setVisible(false);
        chkAdapterTrim.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                chkAdapterTrimStateChanged(evt);
            }
        });
        pnlAdapterTrim.add(chkAdapterTrim, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 40, -1, 30));

        lblWarnsSeedMismatches.setFont(new java.awt.Font("Segoe UI", 3, 12)); // NOI18N
        lblWarnsSeedMismatches.setForeground(new java.awt.Color(255, 51, 102));
        pnlAdapterTrim.add(lblWarnsSeedMismatches, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 200, 260, 30));

        lblOptionalField.setFont(new java.awt.Font("Segoe UI", 1, 28)); // NOI18N
        lblOptionalField.setForeground(new java.awt.Color(255, 255, 255));
        lblOptionalField.setText("Trim cascade");
        pnlAdapterTrim.add(lblOptionalField, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, -1, -1));

        txtAdapterSeq.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        txtAdapterSeq.setEnabled(false);
        txtAdapterSeq.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                txtAdapterSeqCaretUpdate(evt);
            }
        });
        txtAdapterSeq.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                txtAdapterSeqMouseReleased(evt);
            }
        });
        pnlAdapterTrim.add(txtAdapterSeq, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 100, 210, 30));

        lblAdapterSeq.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblAdapterSeq.setForeground(new java.awt.Color(51, 204, 255));
        lblAdapterSeq.setText("Kmer-based approach");
        lblAdapterSeq.setToolTipText("");
        lblAdapterSeq.setEnabled(false);
        lblAdapterSeq.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblAdapterSeqMouseClicked(evt);
            }
        });
        pnlAdapterTrim.add(lblAdapterSeq, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 70, 200, 40));

        lblWarnAdapterSeq.setFont(new java.awt.Font("Segoe UI", 3, 12)); // NOI18N
        lblWarnAdapterSeq.setForeground(new java.awt.Color(255, 51, 102));
        pnlAdapterTrim.add(lblWarnAdapterSeq, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 100, 350, 30));

        btnGroupAdapters.add(rdioAdapterSeqApproach);
        rdioAdapterSeqApproach.setToolTipText("K-mer base decontaminator");
        rdioAdapterSeqApproach.setEnabled(false);
        rdioAdapterSeqApproach.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                rdioAdapterSeqApproachItemStateChanged(evt);
            }
        });
        pnlAdapterTrim.add(rdioAdapterSeqApproach, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 70, -1, 40));

        btnGroupAdapters.add(rdioTrimmomaticApproach);
        rdioTrimmomaticApproach.setToolTipText("Trimmomatic decontaminator");
        rdioTrimmomaticApproach.setEnabled(false);
        rdioTrimmomaticApproach.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                rdioTrimmomaticApproachItemStateChanged(evt);
            }
        });
        pnlAdapterTrim.add(rdioTrimmomaticApproach, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 130, -1, 30));

        lblAdapterSeq1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblAdapterSeq1.setForeground(new java.awt.Color(170, 204, 255));
        lblAdapterSeq1.setText("Adapter Sequence:");
        lblAdapterSeq1.setEnabled(false);
        lblAdapterSeq1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblAdapterSeq1MouseClicked(evt);
            }
        });
        pnlAdapterTrim.add(lblAdapterSeq1, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 100, 160, 30));

        lblTrimmomaticApproach.setFont(new java.awt.Font("Segoe UI", 1, 16)); // NOI18N
        lblTrimmomaticApproach.setForeground(new java.awt.Color(51, 204, 255));
        lblTrimmomaticApproach.setText("Trimmomatic approach");
        lblTrimmomaticApproach.setEnabled(false);
        lblTrimmomaticApproach.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblTrimmomaticApproachMouseClicked(evt);
            }
        });
        pnlAdapterTrim.add(lblTrimmomaticApproach, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 130, -1, 30));

        chkMinAdapterLen.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                chkMinAdapterLenStateChanged(evt);
            }
        });
        pnlAdapterTrim.add(chkMinAdapterLen, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 320, -1, 30));

        chkReverseComplement.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        chkReverseComplement.setForeground(new java.awt.Color(170, 204, 255));
        chkReverseComplement.setSelected(true);
        chkReverseComplement.setText("Consider reverse complement");
        chkReverseComplement.setVisible(false);
        pnlAdapterTrim.add(chkReverseComplement, new org.netbeans.lib.awtextra.AbsoluteConstraints(543, 80, 240, 20));

        lblWarnsPalindromeLikelihood.setFont(new java.awt.Font("Segoe UI", 3, 12)); // NOI18N
        lblWarnsPalindromeLikelihood.setForeground(new java.awt.Color(255, 51, 102));
        pnlAdapterTrim.add(lblWarnsPalindromeLikelihood, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 240, 260, 30));

        lblWarnsSimpleLikelihood.setFont(new java.awt.Font("Segoe UI", 3, 12)); // NOI18N
        lblWarnsSimpleLikelihood.setForeground(new java.awt.Color(255, 51, 102));
        pnlAdapterTrim.add(lblWarnsSimpleLikelihood, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 280, 260, 30));

        lblWarnsMinAdapterLen.setFont(new java.awt.Font("Segoe UI", 3, 12)); // NOI18N
        lblWarnsMinAdapterLen.setForeground(new java.awt.Color(255, 51, 102));
        pnlAdapterTrim.add(lblWarnsMinAdapterLen, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 320, 260, 30));

        add(pnlAdapterTrim, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 200, 810, 400));

        jPanel4.setBackground(new java.awt.Color(45, 51, 81));
        jPanel4.setMinimumSize(new java.awt.Dimension(810, 398));
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblMaxLen.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblMaxLen.setForeground(new java.awt.Color(170, 204, 255));
        lblMaxLen.setText("Maximum length of each read:");
        lblMaxLen.setEnabled(false);
        lblMaxLen.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblMaxLenMouseClicked(evt);
            }
        });
        jPanel4.add(lblMaxLen, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 170, -1, 40));

        chkMaxLen.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                chkMaxLenStateChanged(evt);
            }
        });
        jPanel4.add(chkMaxLen, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 170, -1, 40));

        chkMinLen.setSelected(true);
        chkMinLen.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                chkMinLenStateChanged(evt);
            }
        });
        jPanel4.add(chkMinLen, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 220, -1, 30));

        chkMeanQual.setSelected(true);
        chkMeanQual.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                chkMeanQualStateChanged(evt);
            }
        });
        jPanel4.add(chkMeanQual, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 260, -1, 30));

        lblMeanQual.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblMeanQual.setForeground(new java.awt.Color(170, 204, 255));
        lblMeanQual.setText("Minimum average quality of each read:");
        lblMeanQual.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblMeanQualMouseClicked(evt);
            }
        });
        jPanel4.add(lblMeanQual, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 260, 350, 30));

        lblMinLen.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblMinLen.setForeground(new java.awt.Color(170, 204, 255));
        lblMinLen.setText("Minimum length of each read:");
        lblMinLen.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblMinLenMouseClicked(evt);
            }
        });
        jPanel4.add(lblMinLen, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 220, -1, 30));

        spnMaxLen.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        spnMaxLen.setModel(new javax.swing.SpinnerNumberModel(50, 20, null, 1));
        spnMaxLen.setEnabled(false);
        spnMaxLen.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spnMaxLenStateChanged(evt);
            }
        });
        jPanel4.add(spnMaxLen, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 180, 72, -1));

        spnMinLen.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        spnMinLen.setModel(new javax.swing.SpinnerNumberModel(20, 10, 50, 1));
        spnMinLen.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spnMinLenStateChanged(evt);
            }
        });
        jPanel4.add(spnMinLen, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 220, 70, -1));

        spnMeanQual.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        spnMeanQual.setModel(new javax.swing.SpinnerNumberModel(15, 0, 40, 1));
        spnMeanQual.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spnMeanQualStateChanged(evt);
            }
        });
        jPanel4.add(spnMeanQual, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 260, 70, -1));

        btnGroupConvertEncoding.add(rdioPhred33);
        rdioPhred33.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        rdioPhred33.setForeground(new java.awt.Color(170, 204, 255));
        rdioPhred33.setText("Phred 33");
        rdioPhred33.setEnabled(false);
        jPanel4.add(rdioPhred33, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 380, -1, -1));

        btnGroupConvertEncoding.add(rdioPhred64);
        rdioPhred64.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        rdioPhred64.setForeground(new java.awt.Color(170, 204, 255));
        rdioPhred64.setText("Phred 64");
        rdioPhred64.setEnabled(false);
        jPanel4.add(rdioPhred64, new org.netbeans.lib.awtextra.AbsoluteConstraints(680, 380, -1, -1));

        chk3364.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                chk3364StateChanged(evt);
            }
        });
        jPanel4.add(chk3364, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 380, -1, 30));

        lblEndTrim.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblEndTrim.setForeground(new java.awt.Color(204, 204, 255));
        lblEndTrim.setText("End options");
        lblEndTrim.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblEndTrimMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lblEndTrimMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lblEndTrimMouseExited(evt);
            }
        });
        jPanel4.add(lblEndTrim, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 140, -1, -1));

        lblWarnEachReadMeanQual.setFont(new java.awt.Font("Segoe UI", 3, 12)); // NOI18N
        lblWarnEachReadMeanQual.setForeground(new java.awt.Color(255, 51, 102));
        jPanel4.add(lblWarnEachReadMeanQual, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 260, 300, 30));

        lblWarnMaxLen.setBackground(new java.awt.Color(170, 204, 255));
        lblWarnMaxLen.setFont(new java.awt.Font("Segoe UI", 3, 12)); // NOI18N
        lblWarnMaxLen.setForeground(new java.awt.Color(255, 51, 102));
        lblWarnMaxLen.setFocusable(false);
        jPanel4.add(lblWarnMaxLen, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 170, 379, 40));

        lblWarnMinLen.setBackground(new java.awt.Color(170, 204, 255));
        lblWarnMinLen.setFont(new java.awt.Font("Segoe UI", 3, 12)); // NOI18N
        lblWarnMinLen.setForeground(new java.awt.Color(255, 51, 102));
        lblWarnMinLen.setFocusable(false);
        jPanel4.add(lblWarnMinLen, new org.netbeans.lib.awtextra.AbsoluteConstraints(301, 220, 390, 30));

        lblStrictness.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblStrictness.setForeground(new java.awt.Color(170, 204, 255));
        lblStrictness.setText("Strictness:");
        lblStrictness.setEnabled(false);
        lblStrictness.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblStrictnessMouseClicked(evt);
            }
        });
        jPanel4.add(lblStrictness, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 90, 116, -1));

        chkAdaptive.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                chkAdaptiveStateChanged(evt);
            }
        });
        jPanel4.add(chkAdaptive, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 70, -1, -1));

        spnStrictness.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        spnStrictness.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(0.5f), Float.valueOf(0.0f), Float.valueOf(1.0f), Float.valueOf(0.1f)));
        spnStrictness.setEnabled(false);
        spnStrictness.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spnStrictnessStateChanged(evt);
            }
        });
        jPanel4.add(spnStrictness, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 80, 70, -1));

        lblWarnStrictness.setBackground(new java.awt.Color(170, 204, 255));
        lblWarnStrictness.setFont(new java.awt.Font("Segoe UI", 3, 12)); // NOI18N
        lblWarnStrictness.setForeground(new java.awt.Color(255, 51, 102));
        lblWarnStrictness.setFocusable(false);
        jPanel4.add(lblWarnStrictness, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 80, 450, 40));

        lblWarnTargetLen.setBackground(new java.awt.Color(170, 204, 255));
        lblWarnTargetLen.setFont(new java.awt.Font("Segoe UI", 3, 12)); // NOI18N
        lblWarnTargetLen.setForeground(new java.awt.Color(255, 51, 102));
        lblWarnTargetLen.setFocusable(false);
        jPanel4.add(lblWarnTargetLen, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 40, 440, 36));

        lblTargetLength.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblTargetLength.setForeground(new java.awt.Color(170, 204, 255));
        lblTargetLength.setText("Target length of reads:");
        lblTargetLength.setEnabled(false);
        lblTargetLength.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblTargetLengthMouseClicked(evt);
            }
        });
        jPanel4.add(lblTargetLength, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 50, -1, -1));

        spnTargetLength.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        spnTargetLength.setModel(new javax.swing.SpinnerNumberModel(40, 10, null, 1));
        spnTargetLength.setEnabled(false);
        spnTargetLength.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spnTargetLengthStateChanged(evt);
            }
        });
        jPanel4.add(spnTargetLength, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 40, 72, -1));

        lblAdaptiveErrorCorrector.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblAdaptiveErrorCorrector.setForeground(new java.awt.Color(204, 204, 255));
        lblAdaptiveErrorCorrector.setText("Adaptive error correction");
        lblAdaptiveErrorCorrector.setToolTipText("MaxInfo function of trimmomatic.");
        lblAdaptiveErrorCorrector.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblAdaptiveErrorCorrectorMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lblAdaptiveErrorCorrectorMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lblAdaptiveErrorCorrectorMouseExited(evt);
            }
        });
        jPanel4.add(lblAdaptiveErrorCorrector, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 10, -1, -1));

        lblMaxGCcontent.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblMaxGCcontent.setForeground(new java.awt.Color(170, 204, 255));
        lblMaxGCcontent.setText("Maximum GC content of each read:");
        lblMaxGCcontent.setEnabled(false);
        lblMaxGCcontent.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblMaxGCcontentMouseClicked(evt);
            }
        });
        jPanel4.add(lblMaxGCcontent, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 300, 300, 30));

        lblWarnMaxGC.setFont(new java.awt.Font("Segoe UI", 3, 12)); // NOI18N
        lblWarnMaxGC.setForeground(new java.awt.Color(255, 51, 102));
        jPanel4.add(lblWarnMaxGC, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 300, 330, 30));

        chkMaxGC.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                chkMaxGCStateChanged(evt);
            }
        });
        jPanel4.add(chkMaxGC, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 300, -1, 30));

        spnMaxGC.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        spnMaxGC.setModel(new javax.swing.SpinnerNumberModel(60, 10, 100, 1));
        spnMaxGC.setEnabled(false);
        spnMaxGC.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spnMaxGCStateChanged(evt);
            }
        });
        jPanel4.add(spnMaxGC, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 300, 70, -1));

        lblMinGCcontent.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblMinGCcontent.setForeground(new java.awt.Color(170, 204, 255));
        lblMinGCcontent.setText("Minimum GC content of each read:");
        lblMinGCcontent.setEnabled(false);
        lblMinGCcontent.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblMinGCcontentMouseClicked(evt);
            }
        });
        jPanel4.add(lblMinGCcontent, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 340, 300, 30));

        chkMinGC.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                chkMinGCStateChanged(evt);
            }
        });
        jPanel4.add(chkMinGC, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 340, -1, 30));

        lblWarnMinGC.setFont(new java.awt.Font("Segoe UI", 3, 12)); // NOI18N
        lblWarnMinGC.setForeground(new java.awt.Color(255, 51, 102));
        jPanel4.add(lblWarnMinGC, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 340, 330, 30));

        spnMinGC.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        spnMinGC.setModel(new javax.swing.SpinnerNumberModel(40, 0, 90, 1));
        spnMinGC.setEnabled(false);
        spnMinGC.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spnMinGCStateChanged(evt);
            }
        });
        jPanel4.add(spnMinGC, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 340, 70, -1));

        lblConvertQualities.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblConvertQualities.setForeground(new java.awt.Color(170, 204, 255));
        lblConvertQualities.setText("Convert qualities encoding to:");
        lblConvertQualities.setEnabled(false);
        lblConvertQualities.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblConvertQualitiesMouseClicked(evt);
            }
        });
        jPanel4.add(lblConvertQualities, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 380, 300, 30));

        add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 1030, 810, 420));
    }// </editor-fold>//GEN-END:initComponents


    private void rdioPEModeItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_rdioPEModeItemStateChanged

        if (evt.getStateChange() == java.awt.event.ItemEvent.SELECTED) {

            btnImportFile1 = new javax.swing.JButton();
            lblImportFile1 = new javax.swing.JLabel();
            txtImportFile1 = new javax.swing.JTextField();
            txtImportFile2 = new javax.swing.JTextField();
            lblImportFile2 = new javax.swing.JLabel();
            btnImportFile2 = new javax.swing.JButton();
            comboPhred = new javax.swing.JComboBox<>();
            lblPhred = new javax.swing.JLabel();
            chkPairValidator = new javax.swing.JCheckBox();

            lblImportFile1.setFont(new java.awt.Font("Segoe UI", 1, 14));
            lblImportFile1.setForeground(new java.awt.Color(170, 204, 255));
            lblImportFile1.setText("Import Forward File :");

            lblImportFile2.setFont(new java.awt.Font("Segoe UI", 1, 14));
            lblImportFile2.setForeground(new java.awt.Color(170, 204, 255));
            lblImportFile2.setText("Import Reverse File :");

            btnImportFile1.setFont(new java.awt.Font("Segoe UI", 1, 14));
            btnImportFile1.setText("Browse");

            btnImportFile2.setFont(new java.awt.Font("Segoe UI", 1, 14));
            btnImportFile2.setText("Browse");

            txtImportFile1.setFont(new java.awt.Font("Segoe UI", 1, 14));

            txtImportFile2.setFont(new java.awt.Font("Segoe UI", 1, 14));

            comboPhred.setFont(new java.awt.Font("Segoe UI", 1, 12));
            comboPhred.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{"AutoDetection", "Phred33", "Phred64"}));
            comboPhred.setToolTipText("<html>If we couldn't detect Phred score of your file, choose your own.<br>"
                    + "<b>Warning:</b> If you specify wrong, all inputted reads will throw away.</html>");

            lblPhred.setFont(new java.awt.Font("Segoe UI", 1, 12));
            lblPhred.setForeground(new java.awt.Color(170, 204, 255));
            lblPhred.setText("Select encoding Phred score of your files :");

            chkPairValidator.setFont(new java.awt.Font("Segoe UI", 1, 12));
            chkPairValidator.setForeground(new java.awt.Color(170, 204, 255));
            chkPairValidator.setSelected(false);
            chkPairValidator.setText("Validate pair reads");
            chkPairValidator.setToolTipText("<html><b>Warning:</b> This function is so time-consuming.</html>");

            javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
            jPanel3.setLayout(jPanel3Layout);
            jPanel3Layout.setHorizontalGroup(
                    jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addGroup(jPanel3Layout.createSequentialGroup()
                                                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                            .addComponent(lblImportFile1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 115, Short.MAX_VALUE)
                                                            .addComponent(lblImportFile2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                            .addComponent(txtImportFile1, javax.swing.GroupLayout.PREFERRED_SIZE, 410, Short.MAX_VALUE)
                                                            .addComponent(txtImportFile2, javax.swing.GroupLayout.PREFERRED_SIZE, 410, Short.MAX_VALUE)))
                                            .addGroup(jPanel3Layout.createSequentialGroup()
                                                    .addComponent(chkPairValidator)
                                                    .addGap(45, 45, 45)
                                                    .addComponent(lblPhred)))
                                    .addGap(20, 20, 20)
                                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(comboPhred, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(btnImportFile2, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(btnImportFile1, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addContainerGap(65, Short.MAX_VALUE))
            );

            jPanel3Layout.setVerticalGroup(
                    jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                    .addGap(15, 15, 15)
                                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                            .addComponent(txtImportFile1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(lblImportFile1)
                                            .addComponent(btnImportFile1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                            .addComponent(txtImportFile2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(lblImportFile2)
                                            .addComponent(btnImportFile2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGap(5, 5, 5)
                                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                            .addComponent(lblPhred)
                                            .addComponent(comboPhred, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(chkPairValidator)))
            );
            add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 80, 810, 120));
            btnImportFile1.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    btnImportFile1MouseClicked(evt);
                }
            });
            btnImportFile2.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    btnImportFile2MouseClicked(evt);
                }
            });
            lblSaveTo.setText("Output Path:");
            if (chkAdapterTrim.isSelected()) {
                if (rdioTrimmomaticApproach.isSelected()) {
                    activateTrimmomaticApproach();
                } else if (rdioAdapterSeqApproach.isSelected()) {
                    activateAdapterSeqApproach();
                }
            }
        } else {
            Container PE1 = lblImportFile1.getParent();
            Container PE2 = btnImportFile1.getParent();
            Container PE3 = txtImportFile1.getParent();
            Container PE4 = lblImportFile2.getParent();
            Container PE5 = btnImportFile2.getParent();
            Container PE6 = txtImportFile2.getParent();
            Container PE7 = lblPhred.getParent();
            Container PE8 = comboPhred.getParent();
            Container PE9 = chkPairValidator.getParent();

            PE1.remove(lblImportFile1);
            PE2.remove(txtImportFile1);
            PE3.remove(btnImportFile1);
            PE4.remove(lblImportFile2);
            PE5.remove(txtImportFile2);
            PE6.remove(btnImportFile2);
            PE7.remove(lblPhred);
            PE8.remove(comboPhred);
            PE9.remove(chkPairValidator);
        }
        txtSaveTo.setText("");
    }//GEN-LAST:event_rdioPEModeItemStateChanged


    private void rdioSEModeItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_rdioSEModeItemStateChanged

        if (evt.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
            btnImportFile1 = new javax.swing.JButton();
            lblImportFile1 = new javax.swing.JLabel();
            txtImportFile1 = new javax.swing.JTextField();
            comboPhred = new javax.swing.JComboBox<>();
            lblPhred = new javax.swing.JLabel();
            comboPhred.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{"AutoDetection", "Phred33", "Phred64"}));
            comboPhred.setToolTipText("<html>If we couldn't detect Phred score of your file, choose your own.<br>"
                    + "<b>Warning:</b> If you specify wrong, all inputted reads will throw away.</html>");
            comboPhred.setFont(new java.awt.Font("Segoe UI", 1, 12));
            lblPhred.setText("Select encoding Phred score of your file:");
            lblPhred.setForeground(new java.awt.Color(170, 204, 255));
            lblPhred.setFont(new java.awt.Font("Segoe UI", 1, 12));
            btnImportFile1.setText("Browse");
            btnImportFile1.setFont(new java.awt.Font("Segoe UI", 1, 14));
            lblImportFile1.setFont(new java.awt.Font("Segoe UI", 1, 14));
            lblImportFile1.setForeground(new java.awt.Color(170, 204, 255));
            lblImportFile1.setText("Import FASTQ File:");
            txtImportFile1.setFont(new java.awt.Font("Segoe UI", 1, 14));
            javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
            jPanel3.setLayout(jPanel3Layout);
            jPanel3Layout.setHorizontalGroup(
                    jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                    .addGap(35, 35, 35)
                                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addGroup(jPanel3Layout.createSequentialGroup()
                                                    .addComponent(lblImportFile1)
                                                    .addGap(26, 26, 26)
                                                    .addComponent(txtImportFile1, javax.swing.GroupLayout.PREFERRED_SIZE, 410, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addComponent(lblPhred))
                                    .addGap(29, 29, 29)
                                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                            .addComponent(comboPhred, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(btnImportFile1, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)))
            );

            jPanel3Layout.setVerticalGroup(
                    jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                    .addGap(34, 34, 34)
                                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                            .addComponent(txtImportFile1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(lblImportFile1)
                                            .addComponent(btnImportFile1))
                                    .addGap(15, 15, 15)
                                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(comboPhred, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(lblPhred)))
            );

            add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 80, 810, 120));
            btnImportFile1.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    btnImportFile1MouseClicked(evt);
                }
            });
            if (chkAdapterTrim.isSelected()) {
                if (rdioTrimmomaticApproach.isSelected()) {
                    activateTrimmomaticApproach();
                } else if (rdioAdapterSeqApproach.isSelected()) {
                    activateAdapterSeqApproach();
                }
            }
        } else {
            Container PE1 = lblImportFile1.getParent();
            Container PE2 = btnImportFile1.getParent();
            Container PE3 = txtImportFile1.getParent();
            Container PE4 = lblPhred.getParent();
            Container PE5 = comboPhred.getParent();
            PE1.remove(lblImportFile1);
            PE2.remove(txtImportFile1);
            PE3.remove(btnImportFile1);
            PE4.remove(lblPhred);
            PE5.remove(comboPhred);
        }
        txtSaveTo.setText("");

    }//GEN-LAST:event_rdioSEModeItemStateChanged

    private void btnImportFile1MouseClicked(java.awt.event.MouseEvent evt) {
        JFileChooser ImportFile1;
        if (lastDirectory == null) {
            ImportFile1 = new JFileChooser();
        } else {
            ImportFile1 = new JFileChooser(lastDirectory);
        }
        ImportFile1.setMultiSelectionEnabled(false);
        TrimmingSequenceFileFilter sff = new TrimmingSequenceFileFilter();
        ImportFile1.setFileFilter(sff);
        if (ImportFile1.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            File file = ImportFile1.getSelectedFile();
            txtImportFile1.setText(file.getAbsolutePath());
            lastDirectory = file.getParent();
        }
    }

    private void btnImportFile2MouseClicked(java.awt.event.MouseEvent evt) {
        JFileChooser ImportFile2;
        if (lastDirectory == null) {
            ImportFile2 = new JFileChooser();
        } else {
            ImportFile2 = new JFileChooser(lastDirectory);
        }
        ImportFile2.setMultiSelectionEnabled(false);
        TrimmingSequenceFileFilter sff = new TrimmingSequenceFileFilter();
        ImportFile2.setFileFilter(sff);
        if (ImportFile2.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            File file2 = ImportFile2.getSelectedFile();
            txtImportFile2.setText(file2.getAbsolutePath());
            lastDirectory = file2.getParent();
        }
    }

    private void lblLeadingMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblLeadingMouseClicked
        if (chkLeading.isSelected()) {
            chkLeading.setSelected(false);
        } else {
            chkLeading.setSelected(true);
        }
    }//GEN-LAST:event_lblLeadingMouseClicked

    private void chkLeadingStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_chkLeadingStateChanged
        if (chkLeading.isSelected()) {
            spnLeading.setEnabled(true);
            lblLeading.setEnabled(true);
            spnLeadingStateChanged(evt);
        } else {
            spnLeading.setEnabled(false);
            lblLeading.setEnabled(false);
            lblWarnLeading.setText("");
        }
    }//GEN-LAST:event_chkLeadingStateChanged

    private void chkTrailingStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_chkTrailingStateChanged
        if (chkTrailing.isSelected()) {
            spnTrailing.setEnabled(true);
            lblTrailing.setEnabled(true);
            spnTrailingStateChanged(evt);
        } else {
            spnTrailing.setEnabled(false);
            lblTrailing.setEnabled(false);
            lblWarnTrailing.setText("");
        }
    }//GEN-LAST:event_chkTrailingStateChanged

    private void chkHeadCropStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_chkHeadCropStateChanged
        if (chkHeadCrop.isSelected()) {
            spnHeadCrop.setEnabled(true);
            lblHeadCrop.setEnabled(true);
            spnHeadCropStateChanged(evt);
        } else {
            spnHeadCrop.setEnabled(false);
            lblHeadCrop.setEnabled(false);
            lblWarnHeadCrop.setText("");
        }
    }//GEN-LAST:event_chkHeadCropStateChanged

    private void chkEndCropStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_chkEndCropStateChanged
        if (chkEndCrop.isSelected()) {
            spnEndCrop.setEnabled(true);
            lblEndCrop.setEnabled(true);
            lblIfreadLongerThan.setEnabled(true);
            spnEndCropThreshold.setEnabled(true);
            spnEndCropStateChanged(evt);
        } else {
            spnEndCrop.setEnabled(false);
            lblEndCrop.setEnabled(false);
            lblIfreadLongerThan.setEnabled(false);
            spnEndCropThreshold.setEnabled(false);
            lblWarnEndCrop.setText("");
        }
    }//GEN-LAST:event_chkEndCropStateChanged

    private void chkMaxLenStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_chkMaxLenStateChanged
        if (chkMaxLen.isSelected()) {
            spnMaxLen.setEnabled(true);
            lblMaxLen.setEnabled(true);
            spnMaxLenStateChanged(evt);
        } else {
            lblWarnMaxLen.setText("");
            spnMaxLen.setEnabled(false);
            lblMaxLen.setEnabled(false);
        }
    }//GEN-LAST:event_chkMaxLenStateChanged

    private void chkMinLenStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_chkMinLenStateChanged
        if (chkMinLen.isSelected()) {
            spnMinLen.setEnabled(true);
            lblMinLen.setEnabled(true);
            spnMinLenStateChanged(evt);
        } else {
            lblWarnMinLen.setText("");
            spnMinLen.setEnabled(false);
            lblMinLen.setEnabled(false);
        }
    }//GEN-LAST:event_chkMinLenStateChanged

    private void chkMeanQualStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_chkMeanQualStateChanged
        if (chkMeanQual.isSelected()) {
            spnMeanQual.setEnabled(true);
            lblMeanQual.setEnabled(true);
            spnMeanQualStateChanged(evt);
        } else {
            lblWarnEachReadMeanQual.setText("");
            spnMeanQual.setEnabled(false);
            lblMeanQual.setEnabled(false);
        }
    }//GEN-LAST:event_chkMeanQualStateChanged

    private void lblTrailingMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblTrailingMouseClicked
        if (chkTrailing.isSelected()) {
            chkTrailing.setSelected(false);
        } else {
            chkTrailing.setSelected(true);
        }
    }//GEN-LAST:event_lblTrailingMouseClicked

    private void lblHeadCropMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblHeadCropMouseClicked
        if (chkHeadCrop.isSelected()) {
            chkHeadCrop.setSelected(false);
        } else {
            chkHeadCrop.setSelected(true);
        }
    }//GEN-LAST:event_lblHeadCropMouseClicked

    private void lblEndCropMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblEndCropMouseClicked
        if (chkEndCrop.isSelected()) {
            chkEndCrop.setSelected(false);
        } else {
            chkEndCrop.setSelected(true);
        }
    }//GEN-LAST:event_lblEndCropMouseClicked

    private void lblMaxLenMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblMaxLenMouseClicked
        if (chkMaxLen.isSelected()) {
            chkMaxLen.setSelected(false);
        } else {
            chkMaxLen.setSelected(true);
        }    }//GEN-LAST:event_lblMaxLenMouseClicked

    private void lblMinLenMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblMinLenMouseClicked
        if (chkMinLen.isSelected()) {
            chkMinLen.setSelected(false);
        } else {
            chkMinLen.setSelected(true);
        }
    }//GEN-LAST:event_lblMinLenMouseClicked

    private void lblMeanQualMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblMeanQualMouseClicked
        if (chkMeanQual.isSelected()) {
            chkMeanQual.setSelected(false);
        } else {
            chkMeanQual.setSelected(true);
        }
    }//GEN-LAST:event_lblMeanQualMouseClicked

    private void chkSlidingWindowStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_chkSlidingWindowStateChanged
        if (chkSlidingWindow.isSelected()) {
            lblEachWindow.setEnabled(true);
            lblMeanQualEachWindow.setEnabled(true);
            spnEachWindow.setEnabled(true);
            spnMeanQualityOfEachWindow.setEnabled(true);
            spnMeanQualityOfEachWindowStateChanged(evt);
        } else {
            lblEachWindow.setEnabled(false);
            lblMeanQualEachWindow.setEnabled(false);
            spnEachWindow.setEnabled(false);
            spnMeanQualityOfEachWindow.setEnabled(false);
            lblWarnSlidingWindow.setText("");
        }
    }//GEN-LAST:event_chkSlidingWindowStateChanged

    private void btnSaveToActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveToActionPerformed

        if (rdioSEMode.isSelected()) {
            JFileChooser SaveTo;
            if (lastDirectory == null) {
                SaveTo = new JFileChooser();
            } else {
                SaveTo = new JFileChooser(lastDirectory);
            }

            SaveTo.setMultiSelectionEnabled(false);

            TrimmingSequenceFileFilter sff = new TrimmingSequenceFileFilter();
            SaveTo.setFileFilter(sff);

            if (SaveTo.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                File file = SaveTo.getSelectedFile();
                if (!file.getAbsolutePath().toLowerCase().endsWith(".fastq") || !file.getAbsolutePath().toLowerCase().endsWith(".fq")) {
                    txtSaveTo.setText(file.getAbsolutePath() + ".fq");
                } else {
                    txtSaveTo.setText(file.getAbsolutePath());
                }
                lastDirectory = file.getParent();
            }

        } else if (rdioPEMode.isSelected()) {
            JFileChooser SaveTo;
            if (lastDirectory == null) {
                SaveTo = new JFileChooser();
            } else {
                SaveTo = new JFileChooser(lastDirectory);
            }

            SaveTo.setMultiSelectionEnabled(false);
            SaveTo.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            SaveTo.setAcceptAllFileFilterUsed(false);
            TrimmingSequenceFileFilter sff = new TrimmingSequenceFileFilter();
            SaveTo.setFileFilter(sff);

            if (SaveTo.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                File file = SaveTo.getSelectedFile();
                txtSaveTo.setText(file.getAbsolutePath());
                lastDirectory = file.getParent();
            }
        } else {
            JOptionPane.showMessageDialog(null, "Select the desired mode of trimming.", "Warning", JOptionPane.OK_OPTION);
        }
    }//GEN-LAST:event_btnSaveToActionPerformed

    public void trimComponentsStarter() {
        progBar = new javax.swing.JProgressBar();
        txtDetails = new javax.swing.JTextArea();
        scrollTxtDetails = new javax.swing.JScrollPane();

        progBar.setStringPainted(true);
        progBar.setFont(new java.awt.Font("Segoe UI", 1, 14));
        progBar.setValue(0);
        progBar.setString("Waiting to start...");

        txtDetails.setEditable(false);
        txtDetails.setBackground(new java.awt.Color(108, 122, 137));
        txtDetails.setColumns(20);
        txtDetails.setFont(new java.awt.Font("Segoe UI", 0, 14));
        txtDetails.setLineWrap(true);
        txtDetails.setRows(5);
        txtDetails.setText(null);
        DefaultCaret caret = (DefaultCaret) txtDetails.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        scrollTxtDetails.setViewportView(txtDetails);

        btnLetsTrim.setEnabled(false);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);

        jPanel6Layout.setHorizontalGroup(
                jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel6Layout.createSequentialGroup()
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel6Layout.createSequentialGroup()
                                                .addGap(50, 50, 50)
                                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                                        .addComponent(progBar, javax.swing.GroupLayout.PREFERRED_SIZE, 700, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(scrollTxtDetails, javax.swing.GroupLayout.PREFERRED_SIZE, 600, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addGroup(jPanel6Layout.createSequentialGroup()))))));

        jPanel6Layout.setVerticalGroup(
                jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel6Layout.createSequentialGroup()
                                .addGap(205, 205, 205)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(progBar, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(scrollTxtDetails, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())
        );
        add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 1450, 950, 470));
    }

    public void trimComponentReset() {
        jPanel6.remove(progBar);
        jPanel6.remove(scrollTxtDetails);
        jPanel6.revalidate();
        jPanel6.repaint();
    }

        private String calculateTemplatedOutput(String fileName, String filetype) {
        int extSplit = getFileExtensionIndex(fileName);
        String core = fileName.substring(0, extSplit);
        String exts = fileName.substring(extSplit);
        String[] suffixes = {"_2","_1",".2",".1","_R1_","_R2_","_f","_r",".f",".r"};
        for (String suffix : suffixes) {
            int x = core.lastIndexOf(suffix);
            if (x != -1) {
                core = core.substring(0, x) + core.substring(x + suffix.length(), core.length());
                break;
            }
        }
        return core + filetype + exts;
    }

    private int getFileExtensionIndex(String str) {
        String extensions[] = {".fq", ".fastq", ".txt", ".gz", ".bz2", ".zip"};
        String tmp = str;
        boolean done = false;
        while (!done) {
            done = true;
            for (String ext : extensions) {
                if (tmp.endsWith(ext)) {
                    tmp = tmp.substring(0, tmp.length() - ext.length());
                    done = false;
                }
            }
        }
        return tmp.length();
    }

    private void btnLetsTrimActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLetsTrimActionPerformed
        try {
            if (!rdioSEMode.isSelected() && !rdioPEMode.isSelected()) {
                JOptionPane.showMessageDialog(null, "Select trimming mode. Then let's trim.", "Warning", JOptionPane.OK_OPTION);
                return;
            }
            if (rdioSEMode.isSelected()) {
                if (getTxtImportFile1().trim().equals("")) {
                    JOptionPane.showMessageDialog(null, "Browse input Fastq file.", "Warning", JOptionPane.OK_OPTION);
                    return;
                }
                if (getTxtSaveTo().trim().equals("")) {
                    JOptionPane.showMessageDialog(null, "Set output Fastq file.", "Warning", JOptionPane.OK_OPTION);
                    return;
                }

                File input = new File(getTxtImportFile1());
                File output = new File(getTxtSaveTo());

                if (!input.isFile()) {
                    JOptionPane.showMessageDialog(null, "Correct input file!", "Warning", JOptionPane.OK_OPTION);
                    return;
                }

                if (getTxtImportFile1().equals(getTxtSaveTo())) {
                    JOptionPane.showMessageDialog(null, "input and output files have same name.\nSorry, We can't handle it! \nPlease change output file name.", "Warning", JOptionPane.OK_OPTION);
                    return;
                }

                if (output.exists()) {
                    int dialogResult = JOptionPane.showConfirmDialog(null, output.getName() + " already exists." + "\nDo you want to replace it? ", "Warning", JOptionPane.YES_NO_OPTION);
                    if (dialogResult == JOptionPane.NO_OPTION || dialogResult == JOptionPane.CLOSED_OPTION) {
                        return;
                    }
                    if (output.getUsableSpace() <= input.length()) {
                        int dialogResult2 = JOptionPane.showConfirmDialog(null, "Low free space in the selected directory.\n"
                                + "Free space there : " + (output.getUsableSpace() / Statics.mb) + " MB\n" + "Do you want to continue? (not recommended) ", "Warning", JOptionPane.YES_NO_OPTION);
                        if (dialogResult2 == JOptionPane.NO_OPTION || dialogResult2 == JOptionPane.CLOSED_OPTION) {
                            return;
                        }
                    }
                } else {
                    File tempFile = File.createTempFile("tempFile", ".fastq", output.getParentFile());
                    if (tempFile.getUsableSpace() <= input.length()) {
                        int dialogResult = JOptionPane.showConfirmDialog(null, "Low free space in the selected directory.\n"
                                + "Free space there : " + (tempFile.getUsableSpace() / Statics.mb) + " MB\n" + "Do you want to continue? (not recommended) ", "Warning", JOptionPane.YES_NO_OPTION);
                        if (dialogResult == JOptionPane.NO_OPTION || dialogResult == JOptionPane.CLOSED_OPTION) {
                            return;
                        }
                    }
                    tempFile.delete();
                }
            } else if (rdioPEMode.isSelected()) {
                if (getTxtImportFile1().trim().equals("") || getTxtImportFile2().trim().equals("")) {
                    JOptionPane.showMessageDialog(null, "Browse input Fastq files (Forward & Reverse reads).", "Warning", JOptionPane.OK_OPTION);
                    return;
                }
                if (getTxtSaveTo().trim().equals("")) {
                    JOptionPane.showMessageDialog(null, "Set output Fastq files directory.\nOutput files will automatically named as :\n"
                            + "ForwardPairs.fastq\nForwardUnpairs.fastq\nReversePairs.fastq\nReverseUnpairs.fastq ", "Warning", JOptionPane.OK_OPTION);
                    return;
                }

                File input1 = new File(getTxtImportFile1());
                File input2 = new File(getTxtImportFile2());

                                if (!input1.isFile() || !input2.isFile()) {
                    JOptionPane.showMessageDialog(null, "Correct input files!", "Warning", JOptionPane.OK_OPTION);
                    return;
                }

                                //can be coded more complicated to detect user's errors
                File ForwardPair = new File(getTxtSaveTo() + calculateTemplatedOutput(input1.getName(), "_ForwardPairs"));
                File ForwardUnpair = new File(getTxtSaveTo() + calculateTemplatedOutput(input1.getName(), "_ForwardUnpairs"));


                File ReversePair = new File(getTxtSaveTo() + calculateTemplatedOutput(input2.getName(), "_ReversePairs"));
                File ReverseUnpairs = new File(getTxtSaveTo() + calculateTemplatedOutput(input2.getName(), "_ReverseUnpairs"));


                if (!new File(getTxtSaveTo()).isDirectory()) {
                    JOptionPane.showMessageDialog(null, "Correct output directory!", "Warning", JOptionPane.OK_OPTION);
                    return;
                }

                boolean K = true;
                File existed = null;
                if (ForwardPair.exists()) {
                    existed = ForwardPair;
                } else if (ForwardUnpair.exists()) {
                    existed = ForwardUnpair;
                } else if (ReversePair.exists()) {
                    existed = ReversePair;
                } else if (ReverseUnpairs.exists()) {
                    existed = ReverseUnpairs;
                } else {
                    K = false;
                }

                if (K == true) {
                    int dialogResult = JOptionPane.showConfirmDialog(null, "In this path you have some files which have same name with 123Fastq paired-end Trimmer output files.\nDo you want to replace? (not Recomended) ", "Warning", JOptionPane.YES_NO_OPTION);
                    if (dialogResult == JOptionPane.NO_OPTION || dialogResult == JOptionPane.CLOSED_OPTION) {
                        return;
                    }
                    if (existed.getUsableSpace() <= (input1.length() + input2.length())) {
                        int dialogResult2 = JOptionPane.showConfirmDialog(null, "Low free space in the selected directory! \n"
                                + "Free space there : " + (ForwardPair.getUsableSpace() / Statics.mb) + " MB\n" + "\nDo you want to continue? (not recommended) ", "Warning", JOptionPane.YES_NO_OPTION);
                        if (dialogResult2 == JOptionPane.NO_OPTION || dialogResult2 == JOptionPane.CLOSED_OPTION) {
                            return;
                        }
                    }
                } else {
                    File tempFile = File.createTempFile("tempFile", ".fastq", ForwardPair.getParentFile());
                    if (tempFile.getUsableSpace() <= (input1.length() + input2.length())) {
                        int dialogResult2 = JOptionPane.showConfirmDialog(null, "Low free space in the selected directory! \n"
                                + "Free space there : " + (tempFile.getUsableSpace() / Statics.mb) + " MB\n" + "\nDo you want to continue? (not recommended) ", "Warning", JOptionPane.YES_NO_OPTION);
                        if (dialogResult2 == JOptionPane.NO_OPTION) {
                            return;
                        }
                    }
                    tempFile.delete();
                }
            }

            if (btnLetsTrim.getText().equals("Trim Again!")) {
                trimComponentReset();
            }

            trimComponentsStarter();

            String Adapter = null;
            if (chkAdapterTrim.isSelected()) {
                if (rdioTrimmomaticApproach.isSelected()) {
                    if (getComboAdaptersFiles() != null) {
                        progBar.setString("Adapters preparation...");
                        txtDetails.append("Adapter sequences: \n");
                        File AdapterFile;
                        //For compile version
                        String filesDirectory = (new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath()).getParent() + "/").replace("%20", " ");
                        AdapterFile = new File(filesDirectory + "dependencies/trimmerAdapters/" + getComboAdaptersFiles());
                        if (!AdapterFile.exists()) {
                            //For src version
                            System.out.println("Embedded adapter files have been used.");
                            AdapterFile = new File("src/Eidi/_123Fastq/TrimFactoryPackage/adapters/" + getComboAdaptersFiles());
                        }
                        AdapterTrimmer A = new AdapterTrimmer(txtDetails);
                        try {
                            A.loadSequences(AdapterFile.getAbsolutePath(), getSpnMinprefix());
                            prefixPairs = A.getPrefixPairs();
                            forwardSeqs = A.getForwardSeqs();
                            reverseSeqs = A.getReverseSeqs();
                            commonSeqs = A.getCommonSeqs();
                        } catch (IOException ex) {
                            txtDetails.setText("");
                            txtDetails.append("Adapter file isn't in the correct path.\n");
                            java.util.logging.Logger.getLogger(TrimPanel.class.getName()).log(Level.SEVERE, null, ex);
                            progBar.setString("Trimming Failed!");
                            txtDetails.append("\nTrimming Failed!");
                            btnStop.setEnabled(false);
                            btnLetsTrim.setText("Trim Again!");
                            btnLetsTrim.setEnabled(true);
                            return;
                        }
                    }
                } else if (rdioAdapterSeqApproach.isSelected() && txtAdapterSeq.getText().trim().length() != 0) {
                    Adapter = txtAdapterSeq.getText().trim().toUpperCase();
                    if (!Pattern.matches("([ATCG]+)", Adapter)) {
                        txtDetails.append("Adapter sequence should contain only A, T, C or G.\n");
                        progBar.setString("Trimming Failed!");
                        txtDetails.append("\nTrimming Failed!");
                        btnStop.setEnabled(false);
                        btnLetsTrim.setText("Trim Again!");
                        btnLetsTrim.setEnabled(true);
                        return;
                    }
                    txtDetails.append("Adapter sequence: " + Adapter + "\n");
                }
            }

            TrimmerMaker CascadeMaker = new TrimmerMaker(
                    rdioPEMode.isSelected(),
                    txtDetails,
                    Adapter,
                    chkReverseComplement.isSelected(),
                    getPhredSelection(),
                    getSpnHeadCrop(),
                    getSpnEndCrop(),
                    getSpnReadLenThresholdEndCrop(),
                    getSpnGEndRepeat(),
                    getSpnEndRepeat(),
                    getSpnLeading(),
                    getSpnTrailing(),
                    getSpnEachWindow(),
                    getSpnMeanQualityOfEachWindow(),
                    getComboAdaptersFiles(),
                    getSpnSeedMismatchAdapters(),
                    getPalindromeLikelihood(),
                    getSequenceLikelihood(),
                    getSpnMinprefix(),
                    getComboKeepBoth(),
                    prefixPairs, forwardSeqs, reverseSeqs, commonSeqs,
                    getSpnTargetLen(),
                    getSpnStrictness(),
                    getSpnMaxLen(),
                    getSpnMinLen(),
                    getSpnAvgQual(),
                    getSpnMaxGCContent(),
                    getSpnMinGCContent(),
                    getRadio33or64(),
                    getThreadNO(),
                    chkReportSheet.isSelected(),
                    chkDeleteComments.isSelected());

            progBar.setString("Waiting To Start...");

            trimPanelThread = new Thread() {
                @Override
                public void run() {
                    if (!shouldRun) {
                        return;
                    }
                    int TrimResult = 0;
                    int JdialogResult = 1;
                    btnStop.setEnabled(true);
                    progBar.setString("Initialization...");
                    Pathways pathways = null;
                    if (rdioSEMode.isSelected()) {
                        try {
                            pathways = new Pathways(getTxtImportFile1(), getTxtSaveTo());
                            TrimResult = SingleEndTerminal.run(pathways, CascadeMaker, txtDetails, progBar);
                        } catch (Exception ex) {
                            java.util.logging.Logger.getLogger(TrimPanel.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } else {
                        try {
                            pathways = new Pathways(getTxtImportFile1(), getTxtImportFile2(), getTxtSaveTo(), getChkPairValidator());
                            TrimResult = PairedEndTerminal.run(pathways, CascadeMaker, txtDetails, progBar);
                        } catch (Exception ex) {
                            java.util.logging.Logger.getLogger(TrimPanel.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    btnStop.setEnabled(false);
                    btnLetsTrim.setText("Trim Again!");
                    btnLetsTrim.setEnabled(true);
                    if (TrimResult == 1) {
                        if (rdioSEMode.isSelected()) {
                            File file = new File(getTxtSaveTo());
                            if (file.length() == 0) {
                                file.delete();
                                return;
                            }
                            JdialogResult = JOptionPane.showConfirmDialog(null, "Do you want to create quality report for trimmed Fastq file? ", "Quality report?", JOptionPane.YES_NO_OPTION);
                            if (JdialogResult == JOptionPane.YES_OPTION) {
                                try {
                                    Single_ModeQC++;
                                    String FileName = file.getName();
                                    SequenceFile sequenceFile = SequenceFactory.getSequenceFile(file);
                                    boolean shouldRun = true;
                                    ResultsPanel rp = new ResultsPanel(sequenceFile, file, mainQueue, shouldRun, tabPane, starterPanel, bg, FileName, 0, QcRunner.defaultThreadCount());
                                    tabPane.addTab("Single-Mode QC Tab " + Single_ModeQC, rp);
                                    int tabIndex = tabPane.indexOfTab("Single-Mode QC Tab " + Single_ModeQC);
                                    tabPane.setSelectedIndex(tabIndex);
                                    tabPane.setTabComponentAt(tabIndex, new TabComponents(tabPane, starterPanel, rp, mainQueue, bg));
                                    tabPane.setComponentAt(tabIndex, rp);
                                } catch (SequenceFormatException | IOException ex) {
                                    Logger.getLogger(TrimPanel.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        } else if (rdioPEMode.isSelected()) {
                            String[] PEouts = {pathways.getFP(), pathways.getFU(), pathways.getRP(), pathways.getRU()};
                            JdialogResult = JOptionPane.showConfirmDialog(null, "Do you want to create quality report for trimmed Fastq files? ", "Quality report?", JOptionPane.YES_NO_OPTION);
                            if (JdialogResult == JOptionPane.YES_OPTION) {
                                for (String PEout : PEouts) {
                                    File file = new File(PEout);
                                    if (file.length() == 0) {
                                        file.delete();
                                        continue;
                                    }
                                    Single_ModeQC++;
                                    String FileName = file.getName();
                                    SequenceFile sequenceFile;
                                    try {
                                        sequenceFile = SequenceFactory.getSequenceFile(file);
                                        ResultsPanel rp = new ResultsPanel(sequenceFile, file, mainQueue, true, tabPane, starterPanel, bg, FileName, 0, QcRunner.defaultThreadCount());
                                        tabPane.addTab("Single-Mode QC Tab " + Single_ModeQC, rp);
                                        int tabIndex = tabPane.indexOfTab("Single-Mode QC Tab " + Single_ModeQC);
                                        tabPane.setTabComponentAt(tabIndex, new TabComponents(tabPane, starterPanel, rp, mainQueue, bg));
                                        tabPane.setComponentAt(tabIndex, rp);
                                    } catch (SequenceFormatException | IOException ex) {
                                        Logger.getLogger(TrimPanel.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }
                            } else {
                                for (String PEout : PEouts) {
                                    File file = new File(getTxtSaveTo() + PEout);
                                    if (file.length() == 0) {
                                        file.delete();
                                    }
                                }
                            }
                        }
                    }
                }
            };
            mainQueue.AddToQueue(trimPanelThread);
        } catch (IOException | NullPointerException ex) {
            java.util.logging.Logger.getLogger(TrimPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnLetsTrimActionPerformed

    private void btnCoreCalculationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCoreCalculationActionPerformed
        int recommendedThreads = getRecommendedThreadCount();
        spnThreads.setModel(new javax.swing.SpinnerNumberModel(recommendedThreads, MIN_TRIM_THREADS, null, 1));
        spnThreads.setEnabled(true);
        lblSpecify.setEnabled(true);
    }//GEN-LAST:event_btnCoreCalculationActionPerformed

    private void spnMeanQualityOfEachWindowStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spnMeanQualityOfEachWindowStateChanged
        if ((float) spnMeanQualityOfEachWindow.getValue() >= 30) {
            lblWarnSlidingWindow.setText("High values can throw away all reads.");
        } else {
            lblWarnSlidingWindow.setText("");
        }
    }//GEN-LAST:event_spnMeanQualityOfEachWindowStateChanged

    private void chk3364StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_chk3364StateChanged
        if (chk3364.isSelected()) {
            rdioPhred33.setEnabled(true);
            rdioPhred64.setEnabled(true);
            lblConvertQualities.setEnabled(true);
        } else {
            rdioPhred33.setEnabled(false);
            rdioPhred64.setEnabled(false);
            lblConvertQualities.setEnabled(false);
        }
    }//GEN-LAST:event_chk3364StateChanged

    private void spnSeedMismatchAdaptersStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spnSeedMismatchAdaptersStateChanged
        if ((int) spnSeedMismatchAdapters.getValue() <= 1) {
            lblWarnsSeedMismatches.setText("Very Stringent!");
        } else if ((int) spnSeedMismatchAdapters.getValue() >= 6) {
            lblWarnsSeedMismatches.setText("Very Lenient!");
        } else {
            lblWarnsSeedMismatches.setText("");
        }
    }//GEN-LAST:event_spnSeedMismatchAdaptersStateChanged

    private void spnMeanQualStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spnMeanQualStateChanged
        if ((int) spnMeanQual.getValue() >= 30) {
            lblWarnEachReadMeanQual.setText("High values can throw away all reads.");
        } else {
            lblWarnEachReadMeanQual.setText("");
        }    }//GEN-LAST:event_spnMeanQualStateChanged

    private void spnMaxLenStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spnMaxLenStateChanged
        if ((int) spnMaxLen.getValue() <= 40) {
            lblWarnMaxLen.setText("Low values can throw away all reads.");
        } else {
            lblWarnMaxLen.setText("");
        }
     }//GEN-LAST:event_spnMaxLenStateChanged

    private void spnMinLenStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spnMinLenStateChanged
        if ((int) spnMinLen.getValue() >= 25) {
            lblWarnMinLen.setText("High values can throw away all reads.");
        } else if ((int) spnMinLen.getValue() <= 15) {
            lblWarnMinLen.setText("<html>Low values can increase multi-mapped reads rate in downstream analysis.</html>");
        } else {
            lblWarnMinLen.setText("");
        }    }//GEN-LAST:event_spnMinLenStateChanged

    private void spnHeadCropStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spnHeadCropStateChanged
        if ((int) spnHeadCrop.getValue() >= 30) {
            lblWarnHeadCrop.setText("High values can throw away all reads.");
        } else {
            lblWarnHeadCrop.setText("");
        }    }//GEN-LAST:event_spnHeadCropStateChanged

    private void spnEndCropStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spnEndCropStateChanged
        if ((int) spnEndCrop.getValue() >= 30) {
            lblWarnEndCrop.setText("<html>High values can throw away all reads.</html>");
        } else {
            lblWarnEndCrop.setText("");
        }    }//GEN-LAST:event_spnEndCropStateChanged

    private void spnLeadingStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spnLeadingStateChanged
        if ((int) spnLeading.getValue() >= 35) {
            lblWarnLeading.setText("High values can throw away all reads.");
        } else {
            lblWarnLeading.setText("");
        }    }//GEN-LAST:event_spnLeadingStateChanged

    private void spnTrailingStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spnTrailingStateChanged
        if ((int) spnTrailing.getValue() >= 35) {
            lblWarnTrailing.setText("High values can throw away all reads.");
        } else {
            lblWarnTrailing.setText("");
        }    }//GEN-LAST:event_spnTrailingStateChanged

    private void chkAdaptiveStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_chkAdaptiveStateChanged
        if (chkAdaptive.isSelected()) {
            lblTargetLength.setEnabled(true);
            lblStrictness.setEnabled(true);
            spnTargetLength.setEnabled(true);
            spnStrictness.setEnabled(true);
            spnStrictnessStateChanged(evt);
        } else {
            lblWarnStrictness.setText("");
            lblWarnTargetLen.setText("");
            lblTargetLength.setEnabled(false);
            lblStrictness.setEnabled(false);
            spnTargetLength.setEnabled(false);
            spnStrictness.setEnabled(false);
        }
    }//GEN-LAST:event_chkAdaptiveStateChanged

    private void spnStrictnessStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spnStrictnessStateChanged
        if ((float) spnStrictness.getValue() <= 0.3) {
            lblWarnStrictness.setText("Lenient Error Correction! Favour to have long reads.");
        } else if ((float) spnStrictness.getValue() >= 0.7) {
            lblWarnStrictness.setText("Stringent Error Correction! Favour to have correct reads.");
        } else {
            lblWarnStrictness.setText("");
        }    }//GEN-LAST:event_spnStrictnessStateChanged

    private void spnTargetLengthStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spnTargetLengthStateChanged
        lblWarnTargetLen.setText("<html>Attention: Shorter reads than the value you set will<br/>hardly punished for their errors.</html>");
    }//GEN-LAST:event_spnTargetLengthStateChanged

    private void btnStopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStopActionPerformed
        int dialogResult = JOptionPane.showConfirmDialog(null, "Do you want to stop trimming?", "Stop?", JOptionPane.YES_NO_OPTION);
        if (dialogResult == JOptionPane.NO_OPTION || dialogResult == JOptionPane.CLOSED_OPTION) {
            return;
        }
        btnStop.setEnabled(false);
        btnLetsTrim.setEnabled(true);
        try {
            mainQueue.StopRunningTask();
        } catch (InterruptedException | RuntimeException ex) {
            Logger.getLogger(TrimPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnStopActionPerformed

    private void spnEndRepeatStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spnEndRepeatStateChanged
        if ((int) spnEndRepeat.getValue() >= 10) {
            lblWarnBaseRepeats.setText("Repeat Conservative");
        } else if ((int) spnEndRepeat.getValue() <= 4) {
            lblWarnBaseRepeats.setText("Repeat Sensitive");
        } else {
            lblWarnBaseRepeats.setText("");
        }
    }//GEN-LAST:event_spnEndRepeatStateChanged

    private void chkEndRepeatStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_chkEndRepeatStateChanged
        if (chkEndRepeat.isSelected()) {
            lblEndRepeat.setEnabled(true);
            spnEndRepeat.setEnabled(true);
            spnEndRepeatStateChanged(evt);
        } else {
            lblEndRepeat.setEnabled(false);
            spnEndRepeat.setEnabled(false);
            lblWarnBaseRepeats.setText("");
        }
    }//GEN-LAST:event_chkEndRepeatStateChanged

    private void lblEndRepeatMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblEndRepeatMouseClicked
        if (chkEndRepeat.isSelected()) {
            chkEndRepeat.setSelected(false);
        } else {
            chkEndRepeat.setSelected(true);
        }
    }//GEN-LAST:event_lblEndRepeatMouseClicked

    public void deactivateTrimmomaticApproach() {
        lblTrimmomaticApproach.setEnabled(false);
        lblSelectAdapter.setEnabled(false);
        lblSeedMismatch.setEnabled(false);
        lblPalindromeLikelihood.setEnabled(false);
        lblSequenceLikelihood.setEnabled(false);
        chkMinAdapterLen.setEnabled(false);
        lblMinPrefix.setEnabled(false);
        lblKeepBoth.setEnabled(false);
        comboAdaptersFiles.setEnabled(false);
        spnSeedMismatchAdapters.setEnabled(false);
        spnPalindromeLikelihood.setEnabled(false);
        spnSequenceLikelihood.setEnabled(false);
        spnMinprefix.setEnabled(false);
        comboKeepBoth.setEnabled(false);
        lblWarnsSeedMismatches.setText("");
        lblWarnsPalindromeLikelihood.setText("");
        lblWarnsSimpleLikelihood.setText("");
        lblWarnsMinAdapterLen.setText("");
        trimmomaticToolTipDeactivator();
    }

    public void activateTrimmomaticApproach() {
        spnSeedMismatchAdaptersStateChanged(new ChangeEvent(new Object()));
        spnPalindromeLikelihoodStateChanged(new ChangeEvent(new Object()));
        spnSequenceLikelihoodStateChanged(new ChangeEvent(new Object()));

        lblTrimmomaticApproach.setEnabled(true);
        lblSelectAdapter.setEnabled(true);
        lblSeedMismatch.setEnabled(true);
        lblSequenceLikelihood.setEnabled(true);
        spnSeedMismatchAdapters.setEnabled(true);
        spnSequenceLikelihood.setEnabled(true);

        lblPalindromeLikelihood.setEnabled(true);
        chkMinAdapterLen.setEnabled(true);
        comboAdaptersFiles.setEnabled(true);
        spnPalindromeLikelihood.setEnabled(true);
        lblKeepBoth.setEnabled(true);
        comboKeepBoth.setEnabled(true);

        if (chkMinAdapterLen.isSelected()) {
            chkMinAdapterLenStateChanged(new ChangeEvent(new Object()));
        }

        trimmomaticToolTipActivator();
    }

    public void trimmomaticToolTipActivator() {
        chkMinAdapterLen.setToolTipText("Only effective on Paired-End mode.");
        lblPalindromeLikelihood.setToolTipText("Only effective on Paired-End mode.");
        lblMinPrefix.setToolTipText("Only effective on Paired-End mode.");
        lblKeepBoth.setToolTipText("Only effective on Paired-End mode.");
    }

    public void trimmomaticToolTipDeactivator() {
        lblPalindromeLikelihood.setToolTipText(null);
        lblMinPrefix.setToolTipText(null);
        lblKeepBoth.setToolTipText(null);
    }

    private void chkAdapterTrimStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_chkAdapterTrimStateChanged
        if (chkAdapterTrim.isSelected()) {
            rdioAdapterSeqApproach.setEnabled(true);
            rdioTrimmomaticApproach.setEnabled(true);

            if (rdioTrimmomaticApproach.isSelected()) {
                activateTrimmomaticApproach();
            } else {
                rdioAdapterSeqApproach.setSelected(true);
                activateAdapterSeqApproach();
            }
        } else {
            rdioAdapterSeqApproach.setEnabled(false);
            rdioTrimmomaticApproach.setEnabled(false);
            deactivateTrimmomaticApproach();
            deactivateAdapterSeqApproach();
        }
    }//GEN-LAST:event_chkAdapterTrimStateChanged

    private void spnGEndRepeatsStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spnGEndRepeatsStateChanged
        if ((int) spnGEndRepeats.getValue() >= 10) {
            lblWarnGEndRepeats.setText("'G' Repeats Conservative");
        } else if ((int) spnGEndRepeats.getValue() <= 4) {
            lblWarnGEndRepeats.setText("'G' Repeats Sensitive");
        } else {
            lblWarnGEndRepeats.setText("");
        }
    }//GEN-LAST:event_spnGEndRepeatsStateChanged

    private void lblGEndRepeatMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblGEndRepeatMouseClicked
        if (chkGEndRepeat.isSelected()) {
            chkGEndRepeat.setSelected(false);
        } else {
            chkGEndRepeat.setSelected(true);
        }
    }//GEN-LAST:event_lblGEndRepeatMouseClicked

    private void chkGEndRepeatStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_chkGEndRepeatStateChanged
        if (chkGEndRepeat.isSelected()) {
            lblGEndRepeat.setEnabled(true);
            spnGEndRepeats.setEnabled(true);
            spnGEndRepeatsStateChanged(evt);
        } else {
            lblGEndRepeat.setEnabled(false);
            spnGEndRepeats.setEnabled(false);
            lblWarnGEndRepeats.setText("");
        }
    }//GEN-LAST:event_chkGEndRepeatStateChanged

    public void activateAdapterSeqApproach() {
        if (rdioPEMode.isSelected()) {
            chkReverseComplement.setVisible(true);
        } else {
            chkReverseComplement.setVisible(false);
        }
        lblAdapterSeq.setEnabled(true);
        lblAdapterSeq1.setEnabled(true);
        lblWarnAdapterSeq.setEnabled(true);
        txtAdapterSeq.setEnabled(true);
        lblAdapterSeq.setToolTipText("it's NOT recommended to use in Paired-end mode trimming, in the current version.");

    }

    public void deactivateAdapterSeqApproach() {
        chkReverseComplement.setVisible(false);
        lblAdapterSeq.setEnabled(false);
        lblAdapterSeq1.setEnabled(false);
        lblWarnAdapterSeq.setEnabled(false);
        txtAdapterSeq.setText("");
        lblAdapterSeq.setToolTipText(null);
        txtAdapterSeq.setEnabled(false);
    }

    private void rdioAdapterSeqApproachItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_rdioAdapterSeqApproachItemStateChanged
        if (rdioAdapterSeqApproach.isSelected()) {
            activateAdapterSeqApproach();
        } else {
            deactivateAdapterSeqApproach();
        }
    }//GEN-LAST:event_rdioAdapterSeqApproachItemStateChanged

    private void rdioTrimmomaticApproachItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_rdioTrimmomaticApproachItemStateChanged
        if (rdioTrimmomaticApproach.isSelected()) {
            activateTrimmomaticApproach();
        } else {
            deactivateTrimmomaticApproach();
        }
    }//GEN-LAST:event_rdioTrimmomaticApproachItemStateChanged

    private void txtAdapterSeqCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_txtAdapterSeqCaretUpdate
        String Adapter = txtAdapterSeq.getText();
        if (txtAdapterSeq.getText().trim().length() > 0 && !Pattern.matches("([ATCGatcg]+)", Adapter)) {
            lblWarnAdapterSeq.setText("Adapter sequence should contain only A, T, C or G.");
        } else if (txtAdapterSeq.getText().trim().length() > 7) {
            lblWarnAdapterSeq.setText("Long sequences are matched using 7-mers extracted from the adapter.");
        } else {
            lblWarnAdapterSeq.setText("");
        }
    }//GEN-LAST:event_txtAdapterSeqCaretUpdate

    private void lblAdapterParameterMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblAdapterParameterMouseClicked
        if (!chkAdapterTrim.isSelected()) {
            chkAdapterTrim.setSelected(true);
            rdioAdapterSeqApproach.setEnabled(true);
            rdioTrimmomaticApproach.setEnabled(true);
            if (rdioTrimmomaticApproach.isSelected()) {
                activateTrimmomaticApproach();
            } else if (rdioAdapterSeqApproach.isSelected()) {
                activateAdapterSeqApproach();
            }
        } else {
            chkAdapterTrim.setSelected(false);
            rdioAdapterSeqApproach.setEnabled(false);
            rdioTrimmomaticApproach.setEnabled(false);
            deactivateTrimmomaticApproach();
            deactivateAdapterSeqApproach();
        }
    }//GEN-LAST:event_lblAdapterParameterMouseClicked

    private void lblMaxGCcontentMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblMaxGCcontentMouseClicked
        if (chkMaxGC.isSelected()) {
            chkMaxGC.setSelected(false);
        } else {
            chkMaxGC.setSelected(true);
        }
    }//GEN-LAST:event_lblMaxGCcontentMouseClicked

    private void chkMaxGCStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_chkMaxGCStateChanged
        if (chkMaxGC.isSelected()) {
            lblMaxGCcontent.setEnabled(true);
            spnMaxGC.setEnabled(true);
            spnMaxGCStateChanged(evt);
        } else {
            lblWarnMaxGC.setText("");
            lblMaxGCcontent.setEnabled(false);
            spnMaxGC.setEnabled(false);
        }
    }//GEN-LAST:event_chkMaxGCStateChanged

    private void spnMaxGCStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spnMaxGCStateChanged
        if ((int) spnMaxGC.getValue() <= 48) {
            lblWarnMaxGC.setText("Low values can throw away all reads.");
        } else {
            lblWarnMaxGC.setText("");
        }
     }//GEN-LAST:event_spnMaxGCStateChanged

    private void lblMinGCcontentMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblMinGCcontentMouseClicked
        if (chkMinGC.isSelected()) {
            chkMinGC.setSelected(false);
        } else {
            chkMinGC.setSelected(true);
        }
    }//GEN-LAST:event_lblMinGCcontentMouseClicked

    private void chkMinGCStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_chkMinGCStateChanged
        if (chkMinGC.isSelected()) {
            lblMinGCcontent.setEnabled(true);
            spnMinGC.setEnabled(true);
            spnMinGCStateChanged(evt);
        } else {
            lblWarnMinGC.setText("");
            lblMinGCcontent.setEnabled(false);
            spnMinGC.setEnabled(false);
        }
    }//GEN-LAST:event_chkMinGCStateChanged

    private void spnMinGCStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spnMinGCStateChanged
        if ((int) spnMinGC.getValue() >= 52) {
            lblWarnMinGC.setText("High values can throw away all reads.");
        } else {
            lblWarnMinGC.setText("");
        }    }//GEN-LAST:event_spnMinGCStateChanged

    private void lblConvertQualitiesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblConvertQualitiesMouseClicked
        if (chk3364.isSelected()) {
            chk3364.setSelected(false);
        } else {
            chk3364.setSelected(true);
        }
    }//GEN-LAST:event_lblConvertQualitiesMouseClicked

    private void lblStrictnessMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblStrictnessMouseClicked
        if (chkAdaptive.isSelected()) {
            chkAdaptive.setSelected(false);
        } else {
            chkAdaptive.setSelected(true);
        }
    }//GEN-LAST:event_lblStrictnessMouseClicked

    private void lblTargetLengthMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblTargetLengthMouseClicked
        if (chkAdaptive.isSelected()) {
            chkAdaptive.setSelected(false);
        } else {
            chkAdaptive.setSelected(true);
        }
    }//GEN-LAST:event_lblTargetLengthMouseClicked

    private void lblSimpleTrimOptionsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblSimpleTrimOptionsMouseClicked
        if (!endOptionsFlag) {
            chkHeadCrop.setSelected(true);
            chkEndCrop.setSelected(true);
            chkLeading.setSelected(true);
            chkTrailing.setSelected(true);
            chkGEndRepeat.setSelected(true);
            chkEndRepeat.setSelected(true);
            chkSlidingWindow.setSelected(true);
            chkAdaptive.setSelected(true);
            chkMaxLen.setSelected(true);
            chkMinLen.setSelected(true);
            chkMeanQual.setSelected(true);
            chkMaxGC.setSelected(true);
            chkMinGC.setSelected(true);
            chk3364.setSelected(true);
            endOptionsFlag = true;
        } else {
            chkHeadCrop.setSelected(false);
            chkEndCrop.setSelected(false);
            chkLeading.setSelected(false);
            chkTrailing.setSelected(false);
            chkGEndRepeat.setSelected(false);
            chkEndRepeat.setSelected(false);
            chkSlidingWindow.setSelected(false);
            chkAdaptive.setSelected(false);
            chkMaxLen.setSelected(false);
            chkMinLen.setSelected(false);
            chkMeanQual.setSelected(false);
            chkMaxGC.setSelected(false);
            chkMinGC.setSelected(false);
            chk3364.setSelected(false);
            endOptionsFlag = false;
        }
    }//GEN-LAST:event_lblSimpleTrimOptionsMouseClicked

    private void lblAdapterParameterMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblAdapterParameterMouseEntered
        lblAdapterParameter.setBorder(new BevelBorder(BevelBorder.RAISED));
    }//GEN-LAST:event_lblAdapterParameterMouseEntered

    private void lblAdapterParameterMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblAdapterParameterMouseExited
        lblAdapterParameter.setBorder(new EmptyBorder(1, 1, 1, 1));
    }//GEN-LAST:event_lblAdapterParameterMouseExited

    private void lblAdapterSeqMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblAdapterSeqMouseClicked
        if (chkAdapterTrim.isSelected()) {
            if (rdioAdapterSeqApproach.isSelected()) {
                rdioAdapterSeqApproach.setSelected(false);
            } else {
                rdioAdapterSeqApproach.setSelected(true);
            }
        }
    }//GEN-LAST:event_lblAdapterSeqMouseClicked

    private void lblSelectAdapterMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblSelectAdapterMouseClicked
        if (chkAdapterTrim.isSelected()) {
            if (rdioTrimmomaticApproach.isSelected()) {
                rdioTrimmomaticApproach.setSelected(false);
            } else {
                rdioTrimmomaticApproach.setSelected(true);
            }
        }
    }//GEN-LAST:event_lblSelectAdapterMouseClicked

    private void lblSeedMismatchMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblSeedMismatchMouseClicked
        if (chkAdapterTrim.isSelected()) {
            if (rdioTrimmomaticApproach.isSelected()) {
                rdioTrimmomaticApproach.setSelected(false);
            } else {
                rdioTrimmomaticApproach.setSelected(true);
            }
        }
    }//GEN-LAST:event_lblSeedMismatchMouseClicked

    private void lblPalindromeLikelihoodMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblPalindromeLikelihoodMouseClicked
        if (chkAdapterTrim.isSelected()) {
            if (rdioTrimmomaticApproach.isSelected()) {
                rdioTrimmomaticApproach.setSelected(false);
            } else {
                rdioTrimmomaticApproach.setSelected(true);
            }
        }    }//GEN-LAST:event_lblPalindromeLikelihoodMouseClicked

    private void lblSequenceLikelihoodMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblSequenceLikelihoodMouseClicked
        if (chkAdapterTrim.isSelected()) {
            if (rdioTrimmomaticApproach.isSelected()) {
                rdioTrimmomaticApproach.setSelected(false);
            } else {
                rdioTrimmomaticApproach.setSelected(true);
            }
        }
    }//GEN-LAST:event_lblSequenceLikelihoodMouseClicked

    private void lblMinPrefixMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblMinPrefixMouseClicked
        if (chkAdapterTrim.isSelected()) {
            if (rdioTrimmomaticApproach.isSelected()) {
                if (chkMinAdapterLen.isSelected()) {
                    chkMinAdapterLen.setSelected(false);
                } else {
                    chkMinAdapterLen.setSelected(true);
                }
            } else {
                rdioTrimmomaticApproach.setSelected(true);
            }
        }
    }//GEN-LAST:event_lblMinPrefixMouseClicked

    private void lblKeepBothMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblKeepBothMouseClicked

    }//GEN-LAST:event_lblKeepBothMouseClicked

    private void lblSimpleTrimOptionsMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblSimpleTrimOptionsMouseEntered
        lblSimpleTrimOptions.setBorder(new BevelBorder(BevelBorder.RAISED));
    }//GEN-LAST:event_lblSimpleTrimOptionsMouseEntered

    private void lblSimpleTrimOptionsMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblSimpleTrimOptionsMouseExited
        lblSimpleTrimOptions.setBorder(new EmptyBorder(1, 1, 1, 1));
    }//GEN-LAST:event_lblSimpleTrimOptionsMouseExited

    private void rdioSEModeMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_rdioSEModeMouseEntered
        rdioSEMode.setBorder(new BevelBorder(BevelBorder.RAISED));
    }//GEN-LAST:event_rdioSEModeMouseEntered

    private void rdioSEModeMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_rdioSEModeMouseExited
        rdioSEMode.setBorder(new EmptyBorder(1, 1, 1, 1));
    }//GEN-LAST:event_rdioSEModeMouseExited

    private void rdioPEModeMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_rdioPEModeMouseEntered
        rdioPEMode.setBorder(new BevelBorder(BevelBorder.RAISED));
    }//GEN-LAST:event_rdioPEModeMouseEntered

    private void rdioPEModeMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_rdioPEModeMouseExited
        rdioPEMode.setBorder(new EmptyBorder(1, 1, 1, 1));
    }//GEN-LAST:event_rdioPEModeMouseExited

    private void lblMeanQualEachWindowMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblMeanQualEachWindowMouseClicked
        if (chkSlidingWindow.isSelected()) {
            chkSlidingWindow.setSelected(false);
        } else {
            chkSlidingWindow.setSelected(true);
        }
    }//GEN-LAST:event_lblMeanQualEachWindowMouseClicked

    private void lblEachWindowMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblEachWindowMouseClicked
        if (chkSlidingWindow.isSelected()) {
            chkSlidingWindow.setSelected(false);
        } else {
            chkSlidingWindow.setSelected(true);
        }    }//GEN-LAST:event_lblEachWindowMouseClicked

    private void lblQualitySlidingWindowsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblQualitySlidingWindowsMouseClicked
        if (chkSlidingWindow.isSelected()) {
            chkSlidingWindow.setSelected(false);
        } else {
            chkSlidingWindow.setSelected(true);
        }
    }//GEN-LAST:event_lblQualitySlidingWindowsMouseClicked

    private void lblAdaptiveErrorCorrectorMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblAdaptiveErrorCorrectorMouseClicked
        if (chkAdaptive.isSelected()) {
            chkAdaptive.setSelected(false);
        } else {
            chkAdaptive.setSelected(true);
        }
    }//GEN-LAST:event_lblAdaptiveErrorCorrectorMouseClicked

    private void lblQualitySlidingWindowsMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblQualitySlidingWindowsMouseEntered
        lblQualitySlidingWindows.setBorder(new BevelBorder(BevelBorder.RAISED));
    }//GEN-LAST:event_lblQualitySlidingWindowsMouseEntered

    private void lblQualitySlidingWindowsMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblQualitySlidingWindowsMouseExited
        lblQualitySlidingWindows.setBorder(new EmptyBorder(1, 1, 1, 1));
    }//GEN-LAST:event_lblQualitySlidingWindowsMouseExited

    private void lblAdaptiveErrorCorrectorMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblAdaptiveErrorCorrectorMouseEntered
        lblAdaptiveErrorCorrector.setBorder(new BevelBorder(BevelBorder.RAISED));
    }//GEN-LAST:event_lblAdaptiveErrorCorrectorMouseEntered

    private void lblAdaptiveErrorCorrectorMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblAdaptiveErrorCorrectorMouseExited
        lblAdaptiveErrorCorrector.setBorder(new EmptyBorder(1, 1, 1, 1));
    }//GEN-LAST:event_lblAdaptiveErrorCorrectorMouseExited

    private void lblEndTrimMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblEndTrimMouseEntered
        lblEndTrim.setBorder(new BevelBorder(BevelBorder.RAISED));
    }//GEN-LAST:event_lblEndTrimMouseEntered

    private void lblEndTrimMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblEndTrimMouseExited
        lblEndTrim.setBorder(new EmptyBorder(1, 1, 1, 1));
    }//GEN-LAST:event_lblEndTrimMouseExited

    private void lblEndTrimMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblEndTrimMouseClicked
        if (!endOptionsFlag) {
            chkMaxLen.setSelected(true);
            chkMinLen.setSelected(true);
            chkMeanQual.setSelected(true);
            chkMaxGC.setSelected(true);
            chkMinGC.setSelected(true);
            chk3364.setSelected(true);
            endOptionsFlag = true;
        } else {
            chkMaxLen.setSelected(false);
            chkMinLen.setSelected(false);
            chkMeanQual.setSelected(false);
            chkMaxGC.setSelected(false);
            chkMinGC.setSelected(false);
            chk3364.setSelected(false);
            endOptionsFlag = false;
        }
    }//GEN-LAST:event_lblEndTrimMouseClicked

    private void menuCopyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuCopyActionPerformed
        StringSelection stringSelection = new StringSelection(txtAdapterSeq.getSelectedText());
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }//GEN-LAST:event_menuCopyActionPerformed

    private void txtAdapterSeqMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtAdapterSeqMouseReleased
        if (evt.isPopupTrigger() || SwingUtilities.isRightMouseButton(evt)) {
            if (chkAdapterTrim.isSelected() && rdioAdapterSeqApproach.isSelected()) {
                if (txtAdapterSeq.getSelectedText() != null) {
                    menuCopy.setEnabled(true);
                } else {
                    menuCopy.setEnabled(false);
                }
                popupAdapterSeq.show(this.txtAdapterSeq, evt.getX(), evt.getY());
            }
        }
    }//GEN-LAST:event_txtAdapterSeqMouseReleased

    private void menuPasteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuPasteActionPerformed
        String ret = "";
        Clipboard sysClip = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable clipTf = sysClip.getContents(null);
        if (clipTf != null) {
            if (clipTf.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                try {
                    ret = (String) clipTf
                            .getTransferData(DataFlavor.stringFlavor);
                    txtAdapterSeq.setText(ret);
                } catch (UnsupportedFlavorException | IOException e) {
                }
            }
        }
     }//GEN-LAST:event_menuPasteActionPerformed

    private void lblAdapterSeq1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblAdapterSeq1MouseClicked
        if (chkAdapterTrim.isSelected()) {
            if (rdioAdapterSeqApproach.isSelected()) {
                rdioAdapterSeqApproach.setSelected(false);
            } else {
                rdioAdapterSeqApproach.setSelected(true);
            }
        }
    }//GEN-LAST:event_lblAdapterSeq1MouseClicked

    private void lblTrimmomaticApproachMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblTrimmomaticApproachMouseClicked
        if (chkAdapterTrim.isSelected()) {
            if (rdioTrimmomaticApproach.isSelected()) {
                rdioTrimmomaticApproach.setSelected(false);
            } else {
                rdioTrimmomaticApproach.setSelected(true);
            }
        }
    }//GEN-LAST:event_lblTrimmomaticApproachMouseClicked

    private void chkMinAdapterLenStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_chkMinAdapterLenStateChanged
        if (chkMinAdapterLen.isSelected()) {
            lblMinPrefix.setEnabled(true);
            spnMinprefix.setEnabled(true);
            spnMinprefixStateChanged(new ChangeEvent(new Object()));
        } else {
            lblMinPrefix.setEnabled(false);
            spnMinprefix.setEnabled(false);
            lblWarnsMinAdapterLen.setText("");
        }
    }//GEN-LAST:event_chkMinAdapterLenStateChanged

    private void spnPalindromeLikelihoodStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spnPalindromeLikelihoodStateChanged
        if (rdioPEMode.isSelected() && (int) spnPalindromeLikelihood.getValue() <= 15) {
            lblWarnsPalindromeLikelihood.setText("Can cause over-trimming!");
        } else if (rdioPEMode.isSelected() && (int) spnPalindromeLikelihood.getValue() >= 31) {
            lblWarnsPalindromeLikelihood.setText("Can left adapters untrimmed!");
        } else {
            lblWarnsPalindromeLikelihood.setText("");
        }
    }//GEN-LAST:event_spnPalindromeLikelihoodStateChanged

    private void spnSequenceLikelihoodStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spnSequenceLikelihoodStateChanged
        if ((int) spnSequenceLikelihood.getValue() <= 6) {
            lblWarnsSimpleLikelihood.setText("Can cause over-trimming!");
        } else if ((int) spnSequenceLikelihood.getValue() >= 15) {
            lblWarnsSimpleLikelihood.setText("Can left adapters untrimmed!");
        } else {
            lblWarnsSimpleLikelihood.setText("");
        }
    }//GEN-LAST:event_spnSequenceLikelihoodStateChanged

    private void spnMinprefixStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spnMinprefixStateChanged
        if (chkMinAdapterLen.isSelected() && rdioPEMode.isSelected() && (int) spnMinprefix.getValue() <= 3) {
            lblWarnsMinAdapterLen.setText("Sensitive adapter detection!");
        } else if (chkMinAdapterLen.isSelected() && rdioPEMode.isSelected() && (int) spnMinprefix.getValue() >= 9) {
            lblWarnsMinAdapterLen.setText("Stringent adapter detection!");
        } else {
            lblWarnsMinAdapterLen.setText("");
        }
    }//GEN-LAST:event_spnMinprefixStateChanged

    public boolean isPE() {
        return rdioPEMode.isSelected();
    }

    public boolean getChkPairValidator() {
        return chkPairValidator.isSelected();
    }

    public String getTxtImportFile1() {
        return txtImportFile1.getText();
    }

    public String getTxtImportFile2() {
        return txtImportFile2.getText();
    }

    public String getTxtSaveTo() {
        return txtSaveTo.getText();
    }

    public void setCore(java.awt.event.ActionEvent evt) {
        btnCoreCalculationActionPerformed(evt);
        spnThreads.setValue(getRecommendedThreadCount());
    }

    public Integer getRadioSEorPE() {
        if (rdioSEMode.isSelected()) {
            return 1;
        } else if (rdioPhred64.isSelected()) {
            return 2;
        }
        return null;
    }

    public int getPhredSelection() {
        if (comboPhred.getSelectedItem().equals("Phred33")) {
            return 33;
        } else if (comboPhred.getSelectedItem().equals("Phred64")) {
            return 64;
        }
        return 0;
    }

    public String getComboAdaptersFiles() {
        if (chkAdapterTrim.isSelected() && rdioTrimmomaticApproach.isSelected()) {
            return comboAdaptersFiles.getSelectedItem().toString();
        }
        return null;
    }

    public Integer getSpnSeedMismatchAdapters() {
        if (chkAdapterTrim.isSelected() && rdioTrimmomaticApproach.isSelected()) {
            return (Integer) spnSeedMismatchAdapters.getValue();
        }
        return null;
    }

    public Integer getPalindromeLikelihood() {
        if (chkAdapterTrim.isSelected()) {
            return (Integer) spnPalindromeLikelihood.getValue();
        }
        return null;
    }

    public Integer getSpnMinprefix() {
        if (chkAdapterTrim.isSelected() && rdioTrimmomaticApproach.isSelected() && chkMinAdapterLen.isSelected()) {
            return (Integer) spnMinprefix.getValue();
        }
        return null;
    }

    public Integer getSequenceLikelihood() {
        if (chkAdapterTrim.isSelected()) {
            return (Integer) spnSequenceLikelihood.getValue();
        }
        return null;
    }

    public Boolean getComboKeepBoth() {
        if (chkAdapterTrim.isSelected() && rdioTrimmomaticApproach.isSelected() && rdioPEMode.isSelected()) {
            if (comboKeepBoth.getSelectedItem().equals("No")) {
                return false;
            } else if (comboKeepBoth.getSelectedItem().equals("Yes")) {
                return true;
            }
        }
        return null;
    }

    public Integer getSpnEachWindow() {
        if (chkSlidingWindow.isSelected()) {
            return (Integer) spnEachWindow.getValue();
        }
        return null;
    }

    public Float getSpnMeanQualityOfEachWindow() {
        if (chkSlidingWindow.isSelected()) {
            return (Float) spnMeanQualityOfEachWindow.getValue();
        }
        return null;
    }

    public Integer getSpnHeadCrop() {
        if (chkHeadCrop.isSelected()) {
            return (Integer) spnHeadCrop.getValue();
        }
        return null;
    }

    public Integer getSpnEndCrop() {
        if (chkEndCrop.isSelected()) {
            return (Integer) spnEndCrop.getValue();
        }
        return null;
    }

    public Integer getSpnReadLenThresholdEndCrop() {
        if (chkEndCrop.isSelected()) {
            return (Integer) spnEndCropThreshold.getValue();
        }
        return null;
    }

    public Integer getSpnEndRepeat() {
        if (chkEndRepeat.isSelected()) {
            return (Integer) spnEndRepeat.getValue();
        }
        return null;
    }

    public Integer getSpnGEndRepeat() {
        if (chkGEndRepeat.isSelected()) {
            return (Integer) spnGEndRepeats.getValue();
        }
        return null;
    }

    public Integer getSpnLeading() {
        if (chkLeading.isSelected()) {
            return (Integer) spnLeading.getValue();
        }
        return null;
    }

    public Integer getSpnTrailing() {
        if (chkTrailing.isSelected()) {
            return (Integer) spnTrailing.getValue();
        }
        return null;
    }

    public Integer getSpnTargetLen() {
        if (chkAdaptive.isSelected()) {
            return (Integer) spnTargetLength.getValue();
        }
        return null;
    }

    public Float getSpnStrictness() {
        if (chkAdaptive.isSelected()) {
            return (Float) spnStrictness.getValue();
        }
        return null;
    }

    public Integer getSpnMaxLen() {
        if (chkMaxLen.isSelected()) {
            return (Integer) spnMaxLen.getValue();
        }
        return null;
    }

    public Integer getSpnMinLen() {
        if (chkMinLen.isSelected()) {
            return (Integer) spnMinLen.getValue();
        }
        return null;
    }

    public Integer getSpnAvgQual() {
        if (chkMeanQual.isSelected()) {
            return (Integer) spnMeanQual.getValue();
        }
        return null;
    }

    public Integer getSpnMaxGCContent() {
        if (chkMaxGC.isSelected()) {
            return (Integer) spnMaxGC.getValue();
        }
        return null;
    }

    public Integer getSpnMinGCContent() {
        if (chkMinGC.isSelected()) {
            return (Integer) spnMinGC.getValue();
        }
        return null;
    }

    public Integer getRadio33or64() {
        if (chk3364.isSelected()) {
            if (rdioPhred33.isSelected()) {
                return 33;
            } else if (rdioPhred64.isSelected()) {
                return 64;
            } else {
                return null;
            }
        }
        return null;
    }

    public int getThreadNO() {
        Object value = spnThreads.getValue();
        if (value instanceof Number) {
            return Math.max(MIN_TRIM_THREADS, ((Number) value).intValue());
        }
        return MIN_TRIM_THREADS;
    }

    private int getRecommendedThreadCount() {
        return Math.max(MIN_TRIM_THREADS, Runtime.getRuntime().availableProcessors());
    }

    public void setTxtImportFile1(String txtImportFile1) {
        this.txtImportFile1.setText(txtImportFile1);
    }

    public void setTxtImportFile2(String txtImportFile2) {
        this.txtImportFile2.setText(txtImportFile2);
    }

    public void setRdioSEMode() {
        this.rdioSEMode.setSelected(true);
    }

    public void setRdioPEMode() {
        this.rdioPEMode.setSelected(true);
    }

    public void setChkAdapterTrim(boolean chkAdapterTrim) {
        this.chkAdapterTrim.setSelected(chkAdapterTrim);
    }

    public void setRdioAdapterSeqApproach(boolean selected) {
        this.rdioAdapterSeqApproach.setSelected(selected);
        if (selected) {
            activateAdapterSeqApproach();
        }
    }

    public void setTxtAdapterSeq(String txtAdapterSeq) {
        this.txtAdapterSeq.setText(txtAdapterSeq);
    }

    public void setComboPhred(String comboPhred) {
        for (int i = 0; i < this.comboPhred.getItemCount(); i++) {
            if (this.comboPhred.getItemAt(i).contains(comboPhred)) {
                this.comboPhred.setSelectedIndex(i);
            }
        }
    }

    public void setChkHeadCrop(boolean chkHeadCrop) {
        this.chkHeadCrop.setSelected(chkHeadCrop);
    }

    public void setChkEndCrop(boolean chkEndCrop) {
        this.chkEndCrop.setSelected(chkEndCrop);
    }

    public void setSpnLeading(int spnLeading) {
        this.spnLeading.setValue(spnLeading);
    }

    public void setSpnTrailing(int spnTrailing) {
        this.spnTrailing.setValue(spnTrailing);
    }

    public void setSpnEndCrop(int EndCrop) {
        this.spnEndCrop.setValue(EndCrop);
    }

    public void setComboAdaptersFiles(String comboAdaptersFiles) {
        for (int i = 0; i < this.comboAdaptersFiles.getItemCount(); i++) {
            if (this.comboAdaptersFiles.getItemAt(i).contains(comboAdaptersFiles)) {
                this.comboAdaptersFiles.setSelectedIndex(i);
            }
        }
    }

    public void setSpnSeedMismatchAdapters(int spnSeedMismatchAdapters) {
        this.spnSeedMismatchAdapters.setValue(spnSeedMismatchAdapters);
    }

    public void setSpnMatchSE(int spnMatchSE) {
        this.spnPalindromeLikelihood.setValue(spnMatchSE);
    }

    public void setSpnEachWindow(int spnEachWindows) {
        this.spnEachWindow.setValue(spnEachWindows);
    }

    public void setSpnMeanQualityOfEachWindow(float spnMeanQualityOfEachWindow) {
        this.spnMeanQualityOfEachWindow.setValue(spnMeanQualityOfEachWindow);
    }

    public void setChkAdaptive(boolean ChkAdaptive) {
        this.chkAdaptive.setSelected(ChkAdaptive);
    }

    public void setSpnTargetLength(int spnTargetLength) {
        this.spnTargetLength.setValue(spnTargetLength);
    }

    public void setSpnStrictness(float spnStrictness) {
        this.spnStrictness.setValue(spnStrictness);
    }

    public void setChkMeanQual(boolean chkMeanQual) {
        this.chkMeanQual.setSelected(chkMeanQual);
    }

    public void setSpnMeanQual(int spnMeanQual) {
        this.spnMeanQual.setValue(spnMeanQual);
    }

    public void setSpnReadLenThreshold(int spnReadLenThreshold) {
        this.spnEndCropThreshold.setValue(spnReadLenThreshold);
    }

    public void setSpnMinLen(int spnMinLen) {
        this.spnMinLen.setValue(spnMinLen);
    }

    public void setChkPhredConvert(boolean PhredConvert) {
        this.chk3364.setSelected(PhredConvert);
    }

    public void setChkPhredConvertTo33(boolean rdioPhred33) {
        this.rdioPhred33.setSelected(rdioPhred33);
    }

    public void setTxtSaveTo(String txtSaveTo) {
        this.txtSaveTo.setText(txtSaveTo);
    }

    private List<AdapterTrimmer.IlluminaPrefixPair> prefixPairs;
    private Set<AdapterTrimmer.IlluminaClippingSeq> forwardSeqs;
    private Set<AdapterTrimmer.IlluminaClippingSeq> reverseSeqs;
    private Set<AdapterTrimmer.IlluminaClippingSeq> commonSeqs;
    private javax.swing.JCheckBox chkPairValidator;
    private javax.swing.JProgressBar progBar;
    private javax.swing.JTextArea txtDetails;
    private javax.swing.JScrollPane scrollTxtDetails;
    private javax.swing.JComboBox<String> comboPhred;
    private javax.swing.JLabel lblPhred;
    private javax.swing.JTextField txtImportFile1;
    private javax.swing.JLabel lblImportFile1;
    private javax.swing.JButton btnImportFile1;
    private javax.swing.JTextField txtImportFile2;
    private javax.swing.JLabel lblImportFile2;
    private javax.swing.JButton btnImportFile2;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCoreCalculation;
    private javax.swing.ButtonGroup btnGroupAdapters;
    private javax.swing.ButtonGroup btnGroupConvertEncoding;
    private javax.swing.ButtonGroup btnGroupTrimmingModes;
    private javax.swing.JButton btnLetsTrim;
    private javax.swing.JButton btnSaveTo;
    private javax.swing.JButton btnStop;
    private javax.swing.JCheckBox chk3364;
    private javax.swing.JCheckBox chkAdapterTrim;
    private javax.swing.JCheckBox chkAdaptive;
    private javax.swing.JCheckBox chkDeleteComments;
    private javax.swing.JCheckBox chkEndCrop;
    private javax.swing.JCheckBox chkEndRepeat;
    private javax.swing.JCheckBox chkGEndRepeat;
    private javax.swing.JCheckBox chkHeadCrop;
    private javax.swing.JCheckBox chkLeading;
    private javax.swing.JCheckBox chkMaxGC;
    private javax.swing.JCheckBox chkMaxLen;
    private javax.swing.JCheckBox chkMeanQual;
    private javax.swing.JCheckBox chkMinAdapterLen;
    private javax.swing.JCheckBox chkMinGC;
    private javax.swing.JCheckBox chkMinLen;
    private javax.swing.JCheckBox chkReportSheet;
    private javax.swing.JCheckBox chkReverseComplement;
    private javax.swing.JCheckBox chkSlidingWindow;
    private javax.swing.JCheckBox chkTrailing;
    private javax.swing.JComboBox<String> comboAdaptersFiles;
    private javax.swing.JComboBox<String> comboKeepBoth;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JLabel lblAdapterParameter;
    private javax.swing.JLabel lblAdapterSeq;
    private javax.swing.JLabel lblAdapterSeq1;
    private javax.swing.JLabel lblAdaptiveErrorCorrector;
    private javax.swing.JLabel lblConvertQualities;
    private javax.swing.JLabel lblEachWindow;
    private javax.swing.JLabel lblEndCrop;
    private javax.swing.JLabel lblEndRepeat;
    private javax.swing.JLabel lblEndTrim;
    private javax.swing.JLabel lblFinalizeTrim;
    private javax.swing.JLabel lblGEndRepeat;
    private javax.swing.JLabel lblHeadCrop;
    private javax.swing.JLabel lblIfreadLongerThan;
    private javax.swing.JLabel lblKeepBoth;
    private javax.swing.JLabel lblLeading;
    private javax.swing.JLabel lblMaxGCcontent;
    private javax.swing.JLabel lblMaxLen;
    private javax.swing.JLabel lblMeanQual;
    private javax.swing.JLabel lblMeanQualEachWindow;
    private javax.swing.JLabel lblMinGCcontent;
    private javax.swing.JLabel lblMinLen;
    private javax.swing.JLabel lblMinPrefix;
    private javax.swing.JLabel lblOptionalField;
    private javax.swing.JLabel lblPalindromeLikelihood;
    private javax.swing.JLabel lblQualitySlidingWindows;
    private javax.swing.JLabel lblSaveTo;
    private javax.swing.JLabel lblSeedMismatch;
    private javax.swing.JLabel lblSelectAdapter;
    private javax.swing.JLabel lblSequenceLikelihood;
    private javax.swing.JLabel lblSimpleTrimOptions;
    private javax.swing.JLabel lblSpecify;
    private javax.swing.JLabel lblStrictness;
    private javax.swing.JLabel lblTargetLength;
    private javax.swing.JLabel lblThreads;
    private javax.swing.JLabel lblTrailing;
    private javax.swing.JLabel lblTrimmomaticApproach;
    private javax.swing.JLabel lblWarnAdapterSeq;
    private javax.swing.JLabel lblWarnBaseRepeats;
    private javax.swing.JLabel lblWarnEachReadMeanQual;
    private javax.swing.JLabel lblWarnEndCrop;
    private javax.swing.JLabel lblWarnGEndRepeats;
    private javax.swing.JLabel lblWarnHeadCrop;
    private javax.swing.JLabel lblWarnLeading;
    private javax.swing.JLabel lblWarnMaxGC;
    private javax.swing.JLabel lblWarnMaxLen;
    private javax.swing.JLabel lblWarnMinGC;
    private javax.swing.JLabel lblWarnMinLen;
    private javax.swing.JLabel lblWarnSlidingWindow;
    private javax.swing.JLabel lblWarnStrictness;
    private javax.swing.JLabel lblWarnTargetLen;
    private javax.swing.JLabel lblWarnTrailing;
    private javax.swing.JLabel lblWarnsMinAdapterLen;
    private javax.swing.JLabel lblWarnsPalindromeLikelihood;
    private javax.swing.JLabel lblWarnsSeedMismatches;
    private javax.swing.JLabel lblWarnsSimpleLikelihood;
    private javax.swing.JMenuItem menuCopy;
    private javax.swing.JMenuItem menuPaste;
    private javax.swing.JPanel pnlAdapterTrim;
    private javax.swing.JPopupMenu popupAdapterSeq;
    private javax.swing.JRadioButton rdioAdapterSeqApproach;
    private javax.swing.JRadioButton rdioPEMode;
    private javax.swing.JRadioButton rdioPhred33;
    private javax.swing.JRadioButton rdioPhred64;
    private javax.swing.JRadioButton rdioSEMode;
    private javax.swing.JRadioButton rdioTrimmomaticApproach;
    private javax.swing.JSpinner spnEachWindow;
    private javax.swing.JSpinner spnEndCrop;
    private javax.swing.JSpinner spnEndCropThreshold;
    private javax.swing.JSpinner spnEndRepeat;
    private javax.swing.JSpinner spnGEndRepeats;
    private javax.swing.JSpinner spnHeadCrop;
    private javax.swing.JSpinner spnLeading;
    private javax.swing.JSpinner spnMaxGC;
    private javax.swing.JSpinner spnMaxLen;
    private javax.swing.JSpinner spnMeanQual;
    private javax.swing.JSpinner spnMeanQualityOfEachWindow;
    private javax.swing.JSpinner spnMinGC;
    private javax.swing.JSpinner spnMinLen;
    private javax.swing.JSpinner spnMinprefix;
    private javax.swing.JSpinner spnPalindromeLikelihood;
    private javax.swing.JSpinner spnSeedMismatchAdapters;
    private javax.swing.JSpinner spnSequenceLikelihood;
    private javax.swing.JSpinner spnStrictness;
    private javax.swing.JSpinner spnTargetLength;
    private javax.swing.JSpinner spnThreads;
    private javax.swing.JSpinner spnTrailing;
    private javax.swing.JTextField txtAdapterSeq;
    private javax.swing.JTextField txtSaveTo;
    // End of variables declaration//GEN-END:variables
}
