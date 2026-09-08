package Eidi._123Fastq.BarcodeSplitterPackage;

import static Eidi._123Fastq.GUI.App123Fastq.lastDirectory;
import Eidi._123Fastq.GUI.MyJPanel;
import Eidi._123Fastq.GUI.Statics;
import Eidi._123Fastq.GUI.TaskQueue;
import Eidi._123Fastq.TrimFactoryPackage.TrimmingSequenceFileFilter;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class BarcodeSplitter extends MyJPanel {

    private boolean shouldRun;
    private TaskQueue mainQueue;
    private Thread barcodeThread = new Thread();

    public BarcodeSplitter(boolean ShouldRun, TaskQueue MainQueue) {
        this.shouldRun = ShouldRun;
        this.mainQueue = MainQueue;
        initComponents();

    }

    public void setShouldRun(boolean shouldRun) {
        this.shouldRun = shouldRun;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        rdioGrpMode = new javax.swing.ButtonGroup();
        pnlMain = new javax.swing.JPanel();
        lblBrowseMainFile = new javax.swing.JLabel();
        txtBrowseMainFile = new javax.swing.JTextField();
        btnBrowseMainFile = new javax.swing.JButton();
        lblBrowseBarcodeFile = new javax.swing.JLabel();
        txtBrowseBarcodeFile = new javax.swing.JTextField();
        btnBrowseBarcodeFile = new javax.swing.JButton();
        lblSavePath = new javax.swing.JLabel();
        txtBoxSavePath = new javax.swing.JTextField();
        btnBrowseSavePath = new javax.swing.JButton();
        lblBarcodeSplitter = new javax.swing.JLabel();
        lblTipBarcode1 = new javax.swing.JLabel();
        lblTipOutPut = new javax.swing.JLabel();
        chkClipBarcodes = new javax.swing.JCheckBox();
        lblTipBarcode2 = new javax.swing.JLabel();
        btnLetsSplit = new javax.swing.JButton();
        lblMaxPenalty = new javax.swing.JLabel();
        spnMaxPenalty = new javax.swing.JSpinner();
        rdio5Barcoder = new javax.swing.JRadioButton();
        rdio3Barcoder = new javax.swing.JRadioButton();
        rdio3Barcoder.setVisible(false);
        lblWarnPenalty = new javax.swing.JLabel();
        btnStop = new javax.swing.JButton();
        chkReport = new javax.swing.JCheckBox();

        pnlMain.setBackground(new java.awt.Color(5, 25, 25));
        pnlMain.setPreferredSize(new java.awt.Dimension(690, 570));
        pnlMain.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblBrowseMainFile.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblBrowseMainFile.setForeground(new java.awt.Color(170, 204, 255));
        lblBrowseMainFile.setText("Import main Fastq file:");
        pnlMain.add(lblBrowseMainFile, new org.netbeans.lib.awtextra.AbsoluteConstraints(39, 58, -1, -1));

        txtBrowseMainFile.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        pnlMain.add(txtBrowseMainFile, new org.netbeans.lib.awtextra.AbsoluteConstraints(147, 84, 477, -1));

        btnBrowseMainFile.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnBrowseMainFile.setText("Browse");
        btnBrowseMainFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBrowseMainFileActionPerformed(evt);
            }
        });
        pnlMain.add(btnBrowseMainFile, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 84, 95, -1));

        lblBrowseBarcodeFile.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblBrowseBarcodeFile.setForeground(new java.awt.Color(170, 204, 255));
        lblBrowseBarcodeFile.setText("Import barcode file:");
        pnlMain.add(lblBrowseBarcodeFile, new org.netbeans.lib.awtextra.AbsoluteConstraints(39, 118, -1, -1));

        txtBrowseBarcodeFile.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        pnlMain.add(txtBrowseBarcodeFile, new org.netbeans.lib.awtextra.AbsoluteConstraints(147, 144, 477, -1));

        btnBrowseBarcodeFile.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnBrowseBarcodeFile.setText("Browse");
        btnBrowseBarcodeFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBrowseBarcodeFileActionPerformed(evt);
            }
        });
        pnlMain.add(btnBrowseBarcodeFile, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 140, 95, -1));

        lblSavePath.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblSavePath.setForeground(new java.awt.Color(170, 204, 255));
        lblSavePath.setText("Choose path to save output files:");
        pnlMain.add(lblSavePath, new org.netbeans.lib.awtextra.AbsoluteConstraints(39, 391, -1, -1));

        txtBoxSavePath.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        pnlMain.add(txtBoxSavePath, new org.netbeans.lib.awtextra.AbsoluteConstraints(147, 417, 478, -1));

        btnBrowseSavePath.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnBrowseSavePath.setText("Browse");
        btnBrowseSavePath.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBrowseSavePathActionPerformed(evt);
            }
        });
        pnlMain.add(btnBrowseSavePath, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 413, 95, -1));

        lblBarcodeSplitter.setFont(new java.awt.Font("Segoe UI", 1, 22)); // NOI18N
        lblBarcodeSplitter.setForeground(new java.awt.Color(255, 255, 255));
        lblBarcodeSplitter.setText("Barcode Splitter");
        pnlMain.add(lblBarcodeSplitter, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 16, -1, -1));

        lblTipBarcode1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblTipBarcode1.setForeground(new java.awt.Color(153, 153, 153));
        lblTipBarcode1.setText("Tip: Barcode file is a text file that in each line, sample name and ");
        pnlMain.add(lblTipBarcode1, new org.netbeans.lib.awtextra.AbsoluteConstraints(147, 178, 480, -1));

        lblTipOutPut.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblTipOutPut.setForeground(new java.awt.Color(153, 153, 153));
        lblTipOutPut.setText("Tip: Output files will name based on samples name in the barcode file.");
        pnlMain.add(lblTipOutPut, new org.netbeans.lib.awtextra.AbsoluteConstraints(147, 448, -1, -1));

        chkClipBarcodes.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        chkClipBarcodes.setForeground(new java.awt.Color(170, 204, 255));
        chkClipBarcodes.setText("Clip barcodes");
        pnlMain.add(chkClipBarcodes, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 340, -1, -1));

        lblTipBarcode2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblTipBarcode2.setForeground(new java.awt.Color(153, 153, 153));
        lblTipBarcode2.setText("it's barcode sequence separated by a tab character (Tab-delimited).");
        pnlMain.add(lblTipBarcode2, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 200, 500, -1));

        btnLetsSplit.setBackground(new java.awt.Color(246, 36, 89));
        btnLetsSplit.setFont(new java.awt.Font("Segoe UI", 1, 17)); // NOI18N
        btnLetsSplit.setForeground(new java.awt.Color(255, 255, 255));
        btnLetsSplit.setText("Let's Split");
        btnLetsSplit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLetsSplitActionPerformed(evt);
            }
        });
        pnlMain.add(btnLetsSplit, new org.netbeans.lib.awtextra.AbsoluteConstraints(322, 482, 173, -1));

        lblMaxPenalty.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblMaxPenalty.setForeground(new java.awt.Color(170, 204, 255));
        lblMaxPenalty.setText("Maximum penalty to barcode matching:");
        lblMaxPenalty.setToolTipText("123Fastq barcode detector algorithm uses Levenstein distance in determining penalties between barcodes and reads.");
        pnlMain.add(lblMaxPenalty, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 287, -1, 20));

        spnMaxPenalty.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        spnMaxPenalty.setModel(new javax.swing.SpinnerNumberModel(2, 0, 10, 1));
        spnMaxPenalty.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spnMaxPenaltyStateChanged(evt);
            }
        });
        pnlMain.add(spnMaxPenalty, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 280, 46, -1));

        rdioGrpMode.add(rdio5Barcoder);
        rdio5Barcoder.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        rdio5Barcoder.setForeground(new java.awt.Color(170, 204, 255));
        rdio5Barcoder.setSelected(true);
        rdio5Barcoder.setText("5' Barcode splitter approach");
        rdio5Barcoder.setVisible(false);
        pnlMain.add(rdio5Barcoder, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 220, -1, -1));

        rdioGrpMode.add(rdio3Barcoder);
        rdio3Barcoder.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        rdio3Barcoder.setForeground(new java.awt.Color(170, 204, 255));
        rdio3Barcoder.setText("3' Barcode splitter approach");
        pnlMain.add(rdio3Barcoder, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 220, -1, -1));

        lblWarnPenalty.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblWarnPenalty.setForeground(new java.awt.Color(255, 0, 51));
        pnlMain.add(lblWarnPenalty, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 280, 221, 30));

        btnStop.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnStop.setText("Stop!");
        btnStop.setEnabled(false);
        btnStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStopActionPerformed(evt);
            }
        });
        pnlMain.add(btnStop, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 490, 100, -1));

        chkReport.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        chkReport.setForeground(new java.awt.Color(170, 204, 255));
        chkReport.setText("Make report sheet");
        pnlMain.add(chkReport, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 340, -1, -1));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlMain, javax.swing.GroupLayout.DEFAULT_SIZE, 810, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlMain, javax.swing.GroupLayout.DEFAULT_SIZE, 658, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    public void splitComponentReset() {
        pnlMain.remove(progBar);
        pnlMain.revalidate();
        pnlMain.repaint();
    }

    private void btnLetsSplitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLetsSplitActionPerformed

        if (txtBrowseMainFile.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(null, "Browse input Fastq file.", "Warning", JOptionPane.OK_OPTION);
            return;
        }
        if (txtBrowseBarcodeFile.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(null, "Set barcode text file.\nEach line should present a sample barcode.\nExample:\nsampleName1 (tab character) barcode sequence1\nsampleName2 (tab character) barcode sequence2", "Warning", JOptionPane.OK_OPTION);
            return;
        }

        if (txtBoxSavePath.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(null, "Set output directory for output splitted fastq files! (an empty folder recommended)", "Warning", JOptionPane.OK_OPTION);
            return;
        }

        File inputFastq = new File(txtBrowseMainFile.getText());
        if (!inputFastq.isFile()) {
            JOptionPane.showMessageDialog(null, "Choose an existed file as main Fastq file!", "Warning", JOptionPane.OK_OPTION);
            return;
        }
        File inputBarcodes = new File(txtBrowseBarcodeFile.getText());
        if (!inputBarcodes.isFile()) {
            JOptionPane.showMessageDialog(null, "Choose an existed file as barcodes!", "Warning", JOptionPane.OK_OPTION);
            return;
        }

        File tempFile;
        try {
            tempFile = File.createTempFile("tempFile", ".fastq", new File(txtBoxSavePath.getText()).getParentFile());
            if (tempFile.getUsableSpace() <= inputFastq.length()) {
                int dialogResult = JOptionPane.showConfirmDialog(null, "Low free space in the selected directory.\n"
                        + "Free space there : " + (tempFile.getUsableSpace() / Statics.mb) + " MB\n" + "Do you want to continue? (not recommended) ", "Warning", JOptionPane.YES_NO_OPTION);
                if (dialogResult == JOptionPane.NO_OPTION) {
                    return;
                }
            }
            tempFile.delete();
        } catch (IOException ex) {
            Logger.getLogger(BarcodeSplitter.class.getName()).log(Level.SEVERE, null, ex);
        }

        //there isn't any way to check existence of same name files in the output path. add in the following releases.
        //add at the FileMaker function

        if (btnLetsSplit.getText().equals("Split Again!")) {
            splitComponentReset();
        }

        progBar = new javax.swing.JProgressBar();
        progBar.setStringPainted(true);
        progBar.setValue(0);
        progBar.setFont(new java.awt.Font("Segoe UI", 1, 14));
        progBar.setString("Waiting to start...");
        pnlMain.add(progBar, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 540, 730, 40));
        pnlMain.repaint();
        pnlMain.revalidate();

        int mode = 5;
        if (rdio3Barcoder.isSelected()) {
            mode = 3;
        }

        BarcodeParameterCollector params = new BarcodeParameterCollector(txtBrowseMainFile.getText(),
                txtBrowseBarcodeFile.getText(), chkClipBarcodes.isSelected(), (int) spnMaxPenalty.getValue(), txtBoxSavePath.getText(), mode, chkReport.isSelected());

        barcodeThread = new Thread() {
            @Override
            public void run() {
                if (!shouldRun) {
                    return;
                }
                try {
                    btnStop.setEnabled(true);
                    btnLetsSplit.setEnabled(false);
                    BarcodeTerminal.run(params, progBar);
                    btnLetsSplit.setText("Split Again!");
                    btnLetsSplit.setEnabled(true);
                    btnStop.setEnabled(false);
                } catch (IOException | InterruptedException ex) {
                    Logger.getLogger(BarcodeSplitter.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        mainQueue.AddToQueue(barcodeThread);
    }//GEN-LAST:event_btnLetsSplitActionPerformed

    private void btnBrowseSavePathActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBrowseSavePathActionPerformed
        JFileChooser SaveTo;
        if (lastDirectory == null) {
            SaveTo = new JFileChooser();
        } else {
            SaveTo = new JFileChooser(lastDirectory);
        }
        SaveTo.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        SaveTo.setAcceptAllFileFilterUsed(false);
        SaveTo.setMultiSelectionEnabled(false);
        if (SaveTo.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            File file = SaveTo.getSelectedFile();
            txtBoxSavePath.setText(file.getAbsolutePath());
            lastDirectory = file.getParent();
        }    }//GEN-LAST:event_btnBrowseSavePathActionPerformed

    private void btnBrowseMainFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBrowseMainFileActionPerformed
        JFileChooser ImportFileFastq;
        if (lastDirectory == null) {
            ImportFileFastq = new JFileChooser();
        } else {
            ImportFileFastq = new JFileChooser(lastDirectory);
        }
        ImportFileFastq.setMultiSelectionEnabled(false);
        TrimmingSequenceFileFilter sff = new TrimmingSequenceFileFilter();
        ImportFileFastq.setFileFilter(sff);

        if (ImportFileFastq.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            File file = ImportFileFastq.getSelectedFile();
            txtBrowseMainFile.setText(file.getAbsolutePath());
            lastDirectory = file.getParent();
        }
    }//GEN-LAST:event_btnBrowseMainFileActionPerformed

    private void btnBrowseBarcodeFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBrowseBarcodeFileActionPerformed
        JFileChooser ImportFileBarcodes;
        if (lastDirectory == null) {
            ImportFileBarcodes = new JFileChooser();
        } else {
            ImportFileBarcodes = new JFileChooser(lastDirectory);
        }

        ImportFileBarcodes.setMultiSelectionEnabled(false);

        txtFilter txtF = new txtFilter();
        ImportFileBarcodes.setFileFilter(txtF);

        if (ImportFileBarcodes.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            File file = ImportFileBarcodes.getSelectedFile();
            txtBrowseBarcodeFile.setText(file.getAbsolutePath());
            lastDirectory = file.getParent();
        }
    }//GEN-LAST:event_btnBrowseBarcodeFileActionPerformed

    private void spnMaxPenaltyStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spnMaxPenaltyStateChanged
        if ((int) spnMaxPenalty.getValue() >= 4) {
            lblWarnPenalty.setText("Lenient barcode detection!");
        } else if ((int) spnMaxPenalty.getValue() <= 1) {
            lblWarnPenalty.setText("Stringent barcode detection!");
        } else {
            lblWarnPenalty.setText("");
        }
    }//GEN-LAST:event_spnMaxPenaltyStateChanged

    private void btnStopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStopActionPerformed
        int dialogResult = JOptionPane.showConfirmDialog(null, "Do you want to stop barcode detection?", "Stop?", JOptionPane.YES_NO_OPTION);
        if (dialogResult == JOptionPane.NO_OPTION || dialogResult == JOptionPane.CLOSED_OPTION) {
            return;
        }
        btnStop.setEnabled(false);
        btnLetsSplit.setText("Split Again!");
        btnLetsSplit.setEnabled(true);
        progBar.setString("Splitting Stopped!");
        try {
            mainQueue.StopRunningTask();
        } catch (InterruptedException | RuntimeException ex) {
        }
    }//GEN-LAST:event_btnStopActionPerformed

    public boolean getRdio3Barcoder() {
        return rdio3Barcoder.isSelected();
    }

    public boolean getRdio5Barcoder() {
        return rdio5Barcoder.isSelected();
    }

    public Thread getThread() {
        return barcodeThread;
    }

    private javax.swing.JProgressBar progBar;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBrowseBarcodeFile;
    private javax.swing.JButton btnBrowseMainFile;
    private javax.swing.JButton btnBrowseSavePath;
    private javax.swing.JButton btnLetsSplit;
    private javax.swing.JButton btnStop;
    private javax.swing.JCheckBox chkClipBarcodes;
    private javax.swing.JCheckBox chkReport;
    private javax.swing.JLabel lblBarcodeSplitter;
    private javax.swing.JLabel lblBrowseBarcodeFile;
    private javax.swing.JLabel lblBrowseMainFile;
    private javax.swing.JLabel lblMaxPenalty;
    private javax.swing.JLabel lblSavePath;
    private javax.swing.JLabel lblTipBarcode1;
    private javax.swing.JLabel lblTipBarcode2;
    private javax.swing.JLabel lblTipOutPut;
    private javax.swing.JLabel lblWarnPenalty;
    private javax.swing.JPanel pnlMain;
    private javax.swing.JRadioButton rdio3Barcoder;
    private javax.swing.JRadioButton rdio5Barcoder;
    private javax.swing.ButtonGroup rdioGrpMode;
    private javax.swing.JSpinner spnMaxPenalty;
    private javax.swing.JTextField txtBoxSavePath;
    private javax.swing.JTextField txtBrowseBarcodeFile;
    private javax.swing.JTextField txtBrowseMainFile;
    // End of variables declaration//GEN-END:variables
}
