package Eidi._123Fastq.ConvertorsPackage.SamToFastq;

import static Eidi._123Fastq.GUI.App123Fastq.lastDirectory;
import Eidi._123Fastq.GUI.MyJPanel;
import Eidi._123Fastq.GUI.Statics;
import Eidi._123Fastq.GUI.TaskQueue;
import Eidi._123Fastq.QualityControlPackage.Sequence.SequenceFormatException;
import Eidi._123Fastq.TrimFactoryPackage.Pathways;
import Eidi._123Fastq.TrimFactoryPackage.TrimmingSequenceFileFilter;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class SAMToFastqPanel extends MyJPanel {

    private TaskQueue mainQueue;
    private boolean shouldRun;
    private Thread samToFastqPanelThread = new Thread();

    public SAMToFastqPanel(TaskQueue MainQueue, boolean ShouldRun) {
        this.mainQueue = MainQueue;
        this.shouldRun = ShouldRun;
        initComponents();
    }

    public void setShouldRun(boolean shouldRun) {
        this.shouldRun = shouldRun;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlBG = new javax.swing.JPanel();
        lblConvertMain = new javax.swing.JLabel();
        lblBrowseSAM = new javax.swing.JLabel();
        txtBrowseSam = new javax.swing.JTextField();
        btnBrowseSam = new javax.swing.JButton();
        lblSaveFastq = new javax.swing.JLabel();
        txtSaveFastq = new javax.swing.JTextField();
        btnBrowseSaveFastq = new javax.swing.JButton();
        btnLetsConvert = new javax.swing.JButton();
        lblExplanation = new javax.swing.JLabel();
        btnStop = new javax.swing.JButton();
        lblExplanation1 = new javax.swing.JLabel();

        setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        pnlBG.setBackground(new java.awt.Color(5, 25, 25));
        pnlBG.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblConvertMain.setFont(new java.awt.Font("Segoe UI", 1, 26)); // NOI18N
        lblConvertMain.setForeground(new java.awt.Color(255, 255, 255));
        lblConvertMain.setText("SAM/BAM to FASTQ converter");
        pnlBG.add(lblConvertMain, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 20, -1, -1));

        lblBrowseSAM.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblBrowseSAM.setForeground(new java.awt.Color(170, 204, 255));
        lblBrowseSAM.setText("Import SAM/BAM file:");
        pnlBG.add(lblBrowseSAM, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 260, -1, -1));

        txtBrowseSam.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        pnlBG.add(txtBrowseSam, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 290, 477, -1));

        btnBrowseSam.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnBrowseSam.setText("Browse");
        btnBrowseSam.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBrowseSamActionPerformed(evt);
            }
        });
        pnlBG.add(btnBrowseSam, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 290, 95, -1));

        lblSaveFastq.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblSaveFastq.setForeground(new java.awt.Color(170, 204, 255));
        lblSaveFastq.setText("Export FASTQ file:");
        pnlBG.add(lblSaveFastq, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 340, -1, -1));

        txtSaveFastq.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        pnlBG.add(txtSaveFastq, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 370, 477, -1));

        btnBrowseSaveFastq.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnBrowseSaveFastq.setText("Browse");
        btnBrowseSaveFastq.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBrowseSaveFastqActionPerformed(evt);
            }
        });
        pnlBG.add(btnBrowseSaveFastq, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 370, 95, -1));

        btnLetsConvert.setBackground(new java.awt.Color(246, 36, 89));
        btnLetsConvert.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnLetsConvert.setForeground(new java.awt.Color(255, 255, 255));
        btnLetsConvert.setText("Let's Convert");
        btnLetsConvert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLetsConvertActionPerformed(evt);
            }
        });
        pnlBG.add(btnLetsConvert, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 490, 230, 40));

        lblExplanation.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblExplanation.setForeground(new java.awt.Color(204, 204, 204));
        lblExplanation.setText("This feature can't handle paired-end SAM/BAM files in the current version. ");
        pnlBG.add(lblExplanation, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 140, -1, -1));

        btnStop.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnStop.setText("Stop!");
        btnStop.setEnabled(false);
        btnStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStopActionPerformed(evt);
            }
        });
        pnlBG.add(btnStop, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 510, 130, -1));

        lblExplanation1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblExplanation1.setForeground(new java.awt.Color(204, 204, 204));
        lblExplanation1.setText("Select SAM/BAM file to convert it to a FASTQ file.");
        pnlBG.add(lblExplanation1, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 100, -1, -1));

        add(pnlBG, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 810, 650));
    }// </editor-fold>//GEN-END:initComponents

    public String getTxtBrowseSAM() {
        return txtBrowseSam.getText();
    }

    public String getTxtSaveFASTQ() {
        return txtSaveFastq.getText();
    }

    public Thread getThread() {
        return samToFastqPanelThread;
    }

    private void btnBrowseSamActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBrowseSamActionPerformed
        JFileChooser chooser;

        if (lastDirectory == null) {
            chooser = new JFileChooser();
        } else {
            chooser = new JFileChooser(lastDirectory);
        }

        chooser.setMultiSelectionEnabled(false);
        SAMBAMFileFilter sff = new SAMBAMFileFilter();
        chooser.addChoosableFileFilter(sff);
        chooser.setFileFilter(sff);

        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            txtBrowseSam.setText(file.getAbsolutePath());
            lastDirectory = file.getParent();
        }

    }//GEN-LAST:event_btnBrowseSamActionPerformed

    private void btnBrowseSaveFastqActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBrowseSaveFastqActionPerformed
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
            if (!file.getAbsolutePath().toLowerCase().endsWith(".fq") && !file.getAbsolutePath().toLowerCase().endsWith(".fastq")) {
                txtSaveFastq.setText(file.getAbsolutePath() + ".fq");
            } else {
                txtSaveFastq.setText(file.getAbsolutePath());
            }
            lastDirectory = file.getParent();
        }
    }//GEN-LAST:event_btnBrowseSaveFastqActionPerformed

    public void convertComponentReset() {
        pnlBG.remove(progbar);
        pnlBG.revalidate();
        pnlBG.repaint();
    }

    private void btnLetsConvertActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLetsConvertActionPerformed
        try {

            if (getTxtBrowseSAM().trim().equals("")) {
                JOptionPane.showMessageDialog(null, "Browse input SAM/BAM file.", "Warning", JOptionPane.OK_OPTION);
                return;
            }
            if (getTxtSaveFASTQ().trim().equals("")) {
                JOptionPane.showMessageDialog(null, "Set output Fastq file.", "Warning", JOptionPane.OK_OPTION);
                return;
            }

            File input = new File(getTxtBrowseSAM());
            File output = new File(getTxtSaveFASTQ());

            if (!input.isFile()) {
                JOptionPane.showMessageDialog(null, "Correct input SAM/BAM file!", "Warning", JOptionPane.OK_OPTION);
                return;
            }

            if (getTxtBrowseSAM().equals(getTxtSaveFASTQ())) {
                JOptionPane.showMessageDialog(null, "Your input and output files are same!", "Warning", JOptionPane.OK_OPTION);
                return;
            }
            if (output.exists() && !getTxtBrowseSAM().equals(getTxtSaveFASTQ())) {
                int dialogResult = JOptionPane.showConfirmDialog(null, output.getName() + " already exists." + "\nDo you want to replace it? ", "Warning", JOptionPane.YES_NO_OPTION);
                if (dialogResult == JOptionPane.NO_OPTION) {
                    return;
                }
                if (output.getUsableSpace() <= input.length()) {
                    int dialogResult2 = JOptionPane.showConfirmDialog(null, "Low free space in the selected directory.\n"
                            + "Free space there : " + (output.getUsableSpace() / Statics.mb) + " MB\n" + "Do you want to continue? (not recommended) ", "Warning", JOptionPane.YES_NO_OPTION);
                    if (dialogResult2 == JOptionPane.NO_OPTION) {
                        return;
                    }
                }
            }
            if (!output.exists()) {
                File tempFile = File.createTempFile("tempFile", ".sam", output.getParentFile());
                if (tempFile.getUsableSpace() <= input.length()) {
                    int dialogResult = JOptionPane.showConfirmDialog(null, "Low free space in the selected directory.\n"
                            + "Free space there : " + (tempFile.getUsableSpace() / Statics.mb) + " MB\n" + "Do you want to continue? (not recommended) ", "Warning", JOptionPane.YES_NO_OPTION);
                    if (dialogResult == JOptionPane.NO_OPTION) {
                        return;
                    }
                }
                tempFile.delete();
            }

            if (btnLetsConvert.getText().equals("Convert Again!")) {
                convertComponentReset();
            }

            progbar = new javax.swing.JProgressBar();
            progbar.setValue(0);
            progbar.setStringPainted(true);
            progbar.setFont(new java.awt.Font("Segoe UI", 1, 14));

            progbar.setString("Waiting To Start...");
            pnlBG.add(progbar, new org.netbeans.lib.awtextra.AbsoluteConstraints(33, 546, 733, 38));
            btnLetsConvert.setEnabled(false);

            samToFastqPanelThread = new Thread() {
                @Override
                public void run() {
                    if (!shouldRun) {
                        return;
                    }
                    btnStop.setEnabled(true);
                    Pathways pathConvert = new Pathways(getTxtBrowseSAM(), getTxtSaveFASTQ());
                    try {
                        SAMToFASTQTerminal.run(pathConvert, progbar);
                    } catch (InterruptedException | IOException | SequenceFormatException ex) {
                        Logger.getLogger(SAMToFastqPanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    btnLetsConvert.setText("Convert Again!");
                    btnLetsConvert.setEnabled(true);
                    btnStop.setEnabled(false);
                }
            };

            mainQueue.AddToQueue(samToFastqPanelThread);

        } catch (IOException | NullPointerException ex) {
            java.util.logging.Logger.getLogger(SAMToFastqPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnLetsConvertActionPerformed

    private void btnStopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStopActionPerformed
        int dialogResult = JOptionPane.showConfirmDialog(null, "Do you want to stop conversion?", "Stop?", JOptionPane.YES_NO_OPTION);
        if (dialogResult == JOptionPane.NO_OPTION || dialogResult == JOptionPane.CLOSED_OPTION) {
            return;
        }
        btnStop.setEnabled(false);
        btnLetsConvert.setEnabled(true);
        progbar.setString("Conversion Stopped!");
        try {
            mainQueue.StopRunningTask();
        } catch (InterruptedException | RuntimeException ex) {
        }
    }//GEN-LAST:event_btnStopActionPerformed

    private javax.swing.JProgressBar progbar;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBrowseSam;
    private javax.swing.JButton btnBrowseSaveFastq;
    private javax.swing.JButton btnLetsConvert;
    private javax.swing.JButton btnStop;
    private javax.swing.JLabel lblBrowseSAM;
    private javax.swing.JLabel lblConvertMain;
    private javax.swing.JLabel lblExplanation;
    private javax.swing.JLabel lblExplanation1;
    private javax.swing.JLabel lblSaveFastq;
    private javax.swing.JPanel pnlBG;
    private javax.swing.JTextField txtBrowseSam;
    private javax.swing.JTextField txtSaveFastq;
    // End of variables declaration//GEN-END:variables
}
