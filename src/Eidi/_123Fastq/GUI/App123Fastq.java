package Eidi._123Fastq.GUI;

import Eidi._123Fastq.BarcodeSplitterPackage.BarcodeSplitter;
import Eidi._123Fastq.ConvertorsPackage.Fast5ToFastq.Fast5ToFastqPanel;
import Eidi._123Fastq.ConvertorsPackage.SamToFastq.SAMToFastqPanel;
import Eidi._123Fastq.QualityControlPackage.Config;
import Eidi._123Fastq.QualityControlPackage.FileFilters.BAMFileFilter;
import Eidi._123Fastq.QualityControlPackage.FileFilters.CasavaFastQFileFilter;
import Eidi._123Fastq.QualityControlPackage.FileFilters.Fast5FileFilter;
import Eidi._123Fastq.QualityControlPackage.FileFilters.FastQFileFilter;
import Eidi._123Fastq.QualityControlPackage.FileFilters.MappedBAMFileFilter;
import Eidi._123Fastq.QualityControlPackage.FileFilters.SequenceFileFilter;
import Eidi._123Fastq.QualityControlPackage.Results.QcRunner;
import Eidi._123Fastq.QualityControlPackage.Results.ResultsPanel;
import Eidi._123Fastq.QualityControlPackage.Sequence.SequenceFactory;
import Eidi._123Fastq.QualityControlPackage.Sequence.SequenceFile;
import Eidi._123Fastq.QualityControlPackage.Sequence.SequenceFormatException;
import Eidi._123Fastq.QualityControlPackage.Utilities.NameFormatException;
import Eidi._123Fastq.TrimFactoryPackage.TrimPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;

public class App123Fastq extends javax.swing.JFrame {

    private int mouseX;
    private int mouseY;
    public static int ClickedTrim;
    public static int ClickedAdapterTrim;
    public static int Single_ModeQC;
    public static int Comprative_ModeQC;
    public static int ClickedBarcode;
    public static int ClickedSamToFastq;
    public static int ClickedFast5ToFastq;
    public static String lastDirectory = null;
    public TaskQueue mainQueue;
    public String OutputNameString1 = "";
    public String OutputNameString2 = "";

    public App123Fastq() {
        initComponents();
        mainQueue = new TaskQueue();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        bg = new javax.swing.JPanel();
        pnlLeftSide = new javax.swing.JPanel();
        pnlTrimHide = new javax.swing.JPanel();
        lblBarcodeSplitter = new javax.swing.JLabel();
        lblNewTabTrim = new javax.swing.JLabel();
        pnlQC = new javax.swing.JPanel();
        lblQCIcon = new javax.swing.JLabel();
        lblQC = new javax.swing.JLabel();
        pnlQCHide = new javax.swing.JPanel();
        lblQCimportComprative = new javax.swing.JLabel();
        lblQCimportSingle = new javax.swing.JLabel();
        pnlTrimFactory = new javax.swing.JPanel();
        lblTrimFactory = new javax.swing.JLabel();
        lblCutIcon = new javax.swing.JLabel();
        pnlConvert = new javax.swing.JPanel();
        lblConvert = new javax.swing.JLabel();
        lblIconConvert = new javax.swing.JLabel();
        pnlConvertorHide = new javax.swing.JPanel();
        lblFast5toFastq = new javax.swing.JLabel();
        lblConvertSAMtoFASTQ = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        upperPanel = new javax.swing.JPanel();
        lblCloseSign = new javax.swing.JLabel();
        lblMinSign = new javax.swing.JLabel();
        lblIUpside = new javax.swing.JLabel();
        starterPanel = new javax.swing.JPanel();
        lblWebSite = new javax.swing.JLabel();

        lblStarterPic = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(46, 49, 49));
        setIconImage(new ImageIcon(ClassLoader.getSystemResource("Eidi/_123Fastq/GUI/Image/Logo.png")).getImage());
        setUndecorated(true);
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        bg.setPreferredSize(new java.awt.Dimension(1080, 780));
        bg.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                bgMouseDragged(evt);
            }
        });
        bg.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                bgMousePressed(evt);
            }
        });
        bg.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        pnlLeftSide.setBackground(new java.awt.Color(64, 13, 42));
        pnlLeftSide.setLayout(null);

        pnlTrimHide.setOpaque(false);
        pnlTrimHide.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                pnlTrimHideMouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                pnlTrimHideMouseEntered(evt);
            }
        });
        pnlTrimHide.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblBarcodeSplitter.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblBarcodeSplitter.setForeground(new java.awt.Color(255, 255, 255));
        lblBarcodeSplitter.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblBarcodeSplitterMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lblBarcodeSplitterMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lblBarcodeSplitterMouseExited(evt);
            }
        });
        pnlTrimHide.add(lblBarcodeSplitter, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, 230, -1));

        lblNewTabTrim.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblNewTabTrim.setForeground(new java.awt.Color(255, 255, 255));
        lblNewTabTrim.setText("Trimmer");
        lblNewTabTrim.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblNewTabTrimMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lblNewTabTrimMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lblNewTabTrimMouseExited(evt);
            }
        });
        pnlTrimHide.add(lblNewTabTrim, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, 120, 30));

        pnlLeftSide.add(pnlTrimHide);
        pnlTrimHide.setBounds(0, 360, 300, 100);

        pnlQC.setBackground(new java.awt.Color(187, 187, 187));

        lblQCIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Eidi/_123Fastq/GUI/Image/icons8_Combo_Chart_28px.png"))); // NOI18N

        lblQC.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblQC.setText("Quality Check");

        javax.swing.GroupLayout pnlQCLayout = new javax.swing.GroupLayout(pnlQC);
        pnlQC.setLayout(pnlQCLayout);
        pnlQCLayout.setHorizontalGroup(
            pnlQCLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlQCLayout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(lblQCIcon)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblQC)
                .addContainerGap(67, Short.MAX_VALUE))
        );
        pnlQCLayout.setVerticalGroup(
            pnlQCLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlQCLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlQCLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblQC)
                    .addComponent(lblQCIcon))
                .addGap(10, 10, 10))
        );

        pnlLeftSide.add(pnlQC);
        pnlQC.setBounds(0, 190, 280, 53);

        pnlQCHide.setOpaque(false);
        pnlQCHide.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                pnlQCHideMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                pnlQCHideMouseExited(evt);
            }
        });
        pnlQCHide.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblQCimportComprative.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblQCimportComprative.setForeground(new java.awt.Color(255, 255, 255));
        lblQCimportComprative.setText("Comparative-Mode QC");
        lblQCimportComprative.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblQCimportComprativeMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lblQCimportComprativeMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lblQCimportComprativeMouseExited(evt);
            }
        });
        pnlQCHide.add(lblQCimportComprative, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, 250, -1));

        lblQCimportSingle.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblQCimportSingle.setForeground(new java.awt.Color(255, 255, 255));
        lblQCimportSingle.setText("Single-Mode QC");
        lblQCimportSingle.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblQCimportSingleMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lblQCimportSingleMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lblQCimportSingleMouseExited(evt);
            }
        });
        pnlQCHide.add(lblQCimportSingle, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, 250, -1));

        pnlLeftSide.add(pnlQCHide);
        pnlQCHide.setBounds(0, 230, 290, 90);

        pnlTrimFactory.setBackground(new java.awt.Color(187, 187, 187));

        lblTrimFactory.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblTrimFactory.setText("Trim Factory");

        lblCutIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Eidi/_123Fastq/GUI/Image/icons8_Cut_28px.png"))); // NOI18N

        javax.swing.GroupLayout pnlTrimFactoryLayout = new javax.swing.GroupLayout(pnlTrimFactory);
        pnlTrimFactory.setLayout(pnlTrimFactoryLayout);
        pnlTrimFactoryLayout.setHorizontalGroup(
            pnlTrimFactoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlTrimFactoryLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(lblCutIcon)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblTrimFactory)
                .addContainerGap(77, Short.MAX_VALUE))
        );
        pnlTrimFactoryLayout.setVerticalGroup(
            pnlTrimFactoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTrimFactoryLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlTrimFactoryLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblTrimFactory)
                    .addComponent(lblCutIcon))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlLeftSide.add(pnlTrimFactory);
        pnlTrimFactory.setBounds(0, 320, 280, 54);

        pnlConvert.setBackground(new java.awt.Color(187, 187, 187));

        lblConvert.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lblConvert.setText("Converters");

        lblIconConvert.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Eidi/_123Fastq/GUI/Image/icons8_Import_28px.png"))); // NOI18N

        javax.swing.GroupLayout pnlConvertLayout = new javax.swing.GroupLayout(pnlConvert);
        pnlConvert.setLayout(pnlConvertLayout);
        pnlConvertLayout.setHorizontalGroup(
            pnlConvertLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlConvertLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(lblIconConvert)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblConvert)
                .addContainerGap(98, Short.MAX_VALUE))
        );
        pnlConvertLayout.setVerticalGroup(
            pnlConvertLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlConvertLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnlConvertLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblConvert)
                    .addComponent(lblIconConvert))
                .addContainerGap())
        );

        pnlLeftSide.add(pnlConvert);
        pnlConvert.setBounds(0, 460, 280, 54);

        pnlConvertorHide.setOpaque(false);
        pnlConvertorHide.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                pnlConvertorHideMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                pnlConvertorHideMouseExited(evt);
            }
        });
        pnlConvertorHide.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblFast5toFastq.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblFast5toFastq.setForeground(new java.awt.Color(255, 255, 255));
        lblFast5toFastq.setText("FAST5 to FASTQ");
        lblFast5toFastq.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblFast5toFastqMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lblFast5toFastqMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lblFast5toFastqMouseExited(evt);
            }
        });
        pnlConvertorHide.add(lblFast5toFastq, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, 210, -1));

        lblConvertSAMtoFASTQ.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblConvertSAMtoFASTQ.setForeground(new java.awt.Color(255, 255, 255));
        lblConvertSAMtoFASTQ.setText("SAM/BAM to FASTQ");
        lblConvertSAMtoFASTQ.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblConvertSAMtoFASTQMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lblConvertSAMtoFASTQMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lblConvertSAMtoFASTQMouseExited(evt);
            }
        });
        pnlConvertorHide.add(lblConvertSAMtoFASTQ, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, 210, -1));

        pnlLeftSide.add(pnlConvertorHide);
        pnlConvertorHide.setBounds(0, 500, 290, 90);

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Eidi/_123Fastq/GUI/Image/leftside.png"))); // NOI18N
        pnlLeftSide.add(jLabel3);
        jLabel3.setBounds(1, -4, 270, 700);

        bg.add(pnlLeftSide, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 270, 690));

        upperPanel.setBackground(new java.awt.Color(105, 105, 105));
        upperPanel.setLayout(null);

        lblCloseSign.setBackground(new java.awt.Color(204, 204, 204));
        lblCloseSign.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        lblCloseSign.setForeground(new java.awt.Color(204, 204, 204));
        lblCloseSign.setText("X");
        lblCloseSign.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblCloseSignMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                lblCloseSignMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                lblCloseSignMouseReleased(evt);
            }
        });
        upperPanel.add(lblCloseSign);
        lblCloseSign.setBounds(773, 6, 20, 24);

        lblMinSign.setBackground(new java.awt.Color(204, 204, 204));
        lblMinSign.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        lblMinSign.setForeground(new java.awt.Color(204, 204, 204));
        lblMinSign.setText("_");
        lblMinSign.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblMinSignMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                lblMinSignMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                lblMinSignMouseReleased(evt);
            }
        });
        upperPanel.add(lblMinSign);
        lblMinSign.setBounds(750, 0, 20, 24);

        lblIUpside.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Eidi/_123Fastq/GUI/Image/Upside.png"))); // NOI18N
        upperPanel.add(lblIUpside);
        lblIUpside.setBounds(0, 0, 1190, 50);

        bg.add(upperPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 0, 810, 40));

        starterPanel.setLayout(null);

        lblWebSite.setFont(new java.awt.Font("Segoe UI", 1, 48)); // NOI18N
        lblWebSite.setForeground(new java.awt.Color(225, 27, 34));
        lblWebSite.setText("123NGS");
        lblWebSite.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lblWebSiteMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lblWebSiteMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lblWebSiteMouseExited(evt);
            }
        });
        starterPanel.add(lblWebSite);
        lblWebSite.setBounds(50, 100, 370, 64);

        lblStarterPic.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Eidi/_123Fastq/GUI/Image/123NGS.png"))); // NOI18N
        starterPanel.add(lblStarterPic);
        lblStarterPic.setBounds(0, 0, 810, 650);

        bg.add(starterPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 40, 810, 650));

        getContentPane().add(bg, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1080, 690));

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void lblCloseSignMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblCloseSignMouseClicked
        int X = JOptionPane.showConfirmDialog(null, "Do you want to close 123Fastq Completely?", "Close Confirmation",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE);
        if (X == 0) {
            System.exit(0);
        }
    }//GEN-LAST:event_lblCloseSignMouseClicked

    private void lblMinSignMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblMinSignMouseClicked
        setExtendedState(JFrame.ICONIFIED);
    }//GEN-LAST:event_lblMinSignMouseClicked

    private void bgMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bgMouseDragged
        int XOnScreen = evt.getXOnScreen();
        int YOnScreen = evt.getYOnScreen();
        this.setLocation(XOnScreen - mouseX, YOnScreen - mouseY);
    }//GEN-LAST:event_bgMouseDragged

    private void bgMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bgMousePressed
        mouseX = evt.getX();
        mouseY = evt.getY();
    }//GEN-LAST:event_bgMousePressed

    private void lblCloseSignMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblCloseSignMousePressed
        lblCloseSign.setForeground(Color.red);
    }//GEN-LAST:event_lblCloseSignMousePressed

    private void lblCloseSignMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblCloseSignMouseReleased
        lblCloseSign.setForeground(new Color(204, 204, 204));
    }//GEN-LAST:event_lblCloseSignMouseReleased

    private void lblMinSignMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblMinSignMousePressed
        lblMinSign.setForeground(Color.WHITE);
    }//GEN-LAST:event_lblMinSignMousePressed

    private void lblMinSignMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblMinSignMouseReleased
        lblMinSign.setForeground(new Color(204, 204, 204));
    }//GEN-LAST:event_lblMinSignMouseReleased

    private void lblBarcodeSplitterMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblBarcodeSplitterMouseExited
        lblBarcodeSplitter.setForeground(Color.WHITE);
    }//GEN-LAST:event_lblBarcodeSplitterMouseExited

    private void lblBarcodeSplitterMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblBarcodeSplitterMouseEntered
        lblBarcodeSplitter.setForeground(Color.GRAY);
        pnlTrimFactory.setBackground(Color.WHITE);
    }//GEN-LAST:event_lblBarcodeSplitterMouseEntered

    private void lblBarcodeSplitterMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblBarcodeSplitterMouseClicked
//        if (TabAbsenceChecker()) {
//            TabsStarter();
//        }
//        ClickedBarcode++;
//        boolean shouldRun = true;
//        BarcodeSplitter BarcodeTab = new BarcodeSplitter(shouldRun, mainQueue);
//        TabPane.addTab("Barcode Tab " + ClickedBarcode, BarcodeTab);
//        int index = TabPane.indexOfTab("Barcode Tab " + ClickedBarcode);
//        TabPane.setSelectedIndex(index);
//        TabPane.setTabComponentAt(index, new TabComponents(TabPane, starterPanel, BarcodeTab, mainQueue, bg));
    }//GEN-LAST:event_lblBarcodeSplitterMouseClicked

    private void lblConvertSAMtoFASTQMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblConvertSAMtoFASTQMouseExited
        lblConvertSAMtoFASTQ.setForeground(Color.WHITE);
    }//GEN-LAST:event_lblConvertSAMtoFASTQMouseExited

    private void lblConvertSAMtoFASTQMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblConvertSAMtoFASTQMouseEntered
        pnlConvert.setBackground(Color.WHITE);
        lblConvertSAMtoFASTQ.setForeground(Color.GRAY);
    }//GEN-LAST:event_lblConvertSAMtoFASTQMouseEntered

    private void lblNewTabTrimMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblNewTabTrimMouseExited
        lblNewTabTrim.setForeground(Color.WHITE);
    }//GEN-LAST:event_lblNewTabTrimMouseExited

    private void lblNewTabTrimMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblNewTabTrimMouseEntered
        lblNewTabTrim.setForeground(Color.GRAY);
        pnlTrimFactory.setBackground(Color.WHITE);
    }//GEN-LAST:event_lblNewTabTrimMouseEntered

    private void lblNewTabTrimMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblNewTabTrimMouseClicked
        if (TabAbsenceChecker()) {
            TabsStarter();
        }
        ClickedTrim++;
        boolean shouldRun = true;
        TrimPanel trimTab = new TrimPanel(mainQueue, shouldRun, TabPane, starterPanel, bg);
        JScrollPane scrollFrame = new JScrollPane(trimTab);
        trimTab.setAutoscrolls(true);
        scrollFrame.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollFrame.setPreferredSize(new Dimension(810, 650));
        TabPane.addTab("Trim Tab " + ClickedTrim, scrollFrame);
        int index = TabPane.indexOfTab("Trim Tab " + ClickedTrim);
        TabPane.setSelectedIndex(index);
        TabPane.setTabComponentAt(index, new TabComponents(TabPane, starterPanel, trimTab, mainQueue, bg));
    }//GEN-LAST:event_lblNewTabTrimMouseClicked

    private void lblQCimportSingleMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblQCimportSingleMouseExited
        lblQCimportSingle.setForeground(Color.WHITE);
    }//GEN-LAST:event_lblQCimportSingleMouseExited

    private void lblQCimportSingleMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblQCimportSingleMouseEntered
        pnlQC.setBackground(Color.WHITE);
        lblQCimportSingle.setForeground(Color.GRAY);
    }//GEN-LAST:event_lblQCimportSingleMouseEntered

    private void lblQCimportSingleMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblQCimportSingleMouseClicked
        try {
            Single_Mode_QC_OpenFile();
        } catch (NameFormatException | SequenceFormatException | IOException ex) {
            Logger.getLogger(App123Fastq.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_lblQCimportSingleMouseClicked

    private void lblConvertSAMtoFASTQMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblConvertSAMtoFASTQMouseClicked
        if (TabAbsenceChecker()) {
            TabsStarter();
        }
        ClickedSamToFastq++;
        boolean shouldRun = true;
        SAMToFastqPanel convertTab = new SAMToFastqPanel(mainQueue, shouldRun);
        TabPane.addTab("SAM/BAM to Fastq Tab " + ClickedSamToFastq, convertTab);
        int index = TabPane.indexOfTab("SAM/BAM to Fastq Tab " + ClickedSamToFastq);
        TabPane.setSelectedIndex(index);
        TabPane.setTabComponentAt(index, new TabComponents(TabPane, starterPanel, convertTab, mainQueue, bg));
    }//GEN-LAST:event_lblConvertSAMtoFASTQMouseClicked

    private void lblQCimportComprativeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblQCimportComprativeMouseClicked
        try {
            Comparative_Mode_QC_OpenFile();
        } catch (NameFormatException | SequenceFormatException | IOException ex) {
            Logger.getLogger(App123Fastq.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_lblQCimportComprativeMouseClicked

    private void lblQCimportComprativeMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblQCimportComprativeMouseEntered
        pnlQC.setBackground(Color.WHITE);
        lblQCimportComprative.setForeground(Color.GRAY);
    }//GEN-LAST:event_lblQCimportComprativeMouseEntered

    private void lblQCimportComprativeMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblQCimportComprativeMouseExited
        lblQCimportComprative.setForeground(Color.WHITE);
    }//GEN-LAST:event_lblQCimportComprativeMouseExited

    private void lblWebSiteMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblWebSiteMouseEntered
        lblWebSite.setForeground(Color.blue);
    }//GEN-LAST:event_lblWebSiteMouseEntered

    private void lblWebSiteMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblWebSiteMouseExited
        lblWebSite.setForeground(new Color(225, 27, 34));
    }//GEN-LAST:event_lblWebSiteMouseExited

    private void lblWebSiteMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblWebSiteMouseClicked
        String url_open = "https://sourceforge.net/projects/project-123ngs/";
        try {
            java.awt.Desktop.getDesktop().browse(java.net.URI.create(url_open));
        } catch (IOException ex) {
            Logger.getLogger(App123Fastq.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_lblWebSiteMouseClicked

    private void lblFast5toFastqMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblFast5toFastqMouseClicked
        if (TabAbsenceChecker()) {
            TabsStarter();
        }
        ClickedFast5ToFastq++;
        boolean shouldRun = true;
        Fast5ToFastqPanel Fast5ToFastq = new Fast5ToFastqPanel(mainQueue, shouldRun);
        TabPane.addTab("Fast5 to Fastq Tab " + ClickedFast5ToFastq, Fast5ToFastq);
        int index = TabPane.indexOfTab("Fast5 to Fastq Tab " + ClickedFast5ToFastq);
        TabPane.setSelectedIndex(index);
        TabPane.setTabComponentAt(index, new TabComponents(TabPane, starterPanel, Fast5ToFastq, mainQueue, bg));
    }//GEN-LAST:event_lblFast5toFastqMouseClicked

    private void lblFast5toFastqMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblFast5toFastqMouseEntered
        pnlConvert.setBackground(Color.WHITE);
        lblFast5toFastq.setForeground(Color.GRAY);
    }//GEN-LAST:event_lblFast5toFastqMouseEntered

    private void lblFast5toFastqMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblFast5toFastqMouseExited
        lblFast5toFastq.setForeground(Color.WHITE);
    }//GEN-LAST:event_lblFast5toFastqMouseExited

    private void pnlQCHideMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnlQCHideMouseEntered
        pnlQC.setBackground(Color.WHITE);
    }//GEN-LAST:event_pnlQCHideMouseEntered

    private void pnlQCHideMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnlQCHideMouseExited
        pnlQC.setBackground(new Color(187, 187, 187));
    }//GEN-LAST:event_pnlQCHideMouseExited

    private void pnlTrimHideMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnlTrimHideMouseEntered
        pnlTrimFactory.setBackground(Color.WHITE);
    }//GEN-LAST:event_pnlTrimHideMouseEntered

    private void pnlTrimHideMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnlTrimHideMouseExited
        pnlTrimFactory.setBackground(new Color(187, 187, 187));
    }//GEN-LAST:event_pnlTrimHideMouseExited

    private void pnlConvertorHideMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnlConvertorHideMouseEntered
        pnlConvert.setBackground(Color.WHITE);
    }//GEN-LAST:event_pnlConvertorHideMouseEntered

    private void pnlConvertorHideMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pnlConvertorHideMouseExited
        pnlConvert.setBackground(new Color(187, 187, 187));
    }//GEN-LAST:event_pnlConvertorHideMouseExited

    private int requestQcThreadCount() {
        int recommendedThreads = QcRunner.defaultThreadCount();
        JSpinner threadSpinner = new JSpinner(new SpinnerNumberModel(recommendedThreads, 1, null, 1));
        JPanel panel = new JPanel(new java.awt.BorderLayout(8, 8));
        panel.add(new JLabel("QC threads:"), java.awt.BorderLayout.WEST);
        panel.add(threadSpinner, java.awt.BorderLayout.CENTER);
        int result = JOptionPane.showConfirmDialog(this, panel, "QC Thread Count", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) {
            return -1;
        }
        Object value = threadSpinner.getValue();
        if (value instanceof Number) {
            return Math.max(1, ((Number) value).intValue());
        }
        return recommendedThreads;
    }

    public String getNanoporeBasename(String originalName) throws NameFormatException {
        // Files from nanopores look like: Computer_Samplename_number_chXXX_fileXXX_strand.fast5
        // We need to reduce this to Computer_Samplename_number
        String[] subNames = originalName.split("_");
        if (subNames.length < 5) {
            throw new NameFormatException();
        }
        String basename = subNames[0] + "_" + subNames[1] + "_" + subNames[2];
        return basename;
    }

    public void Comparative_Mode_QC_OpenFile() throws NameFormatException, SequenceFormatException, IOException {

        JFileChooser chooser1;
        if (lastDirectory == null) {
            chooser1 = new JFileChooser();
        } else {
            chooser1 = new JFileChooser(lastDirectory);
        }
        chooser1.setDialogTitle("Select First File");
        chooser1.setMultiSelectionEnabled(true);
        SequenceFileFilter sff = new SequenceFileFilter();
        chooser1.addChoosableFileFilter(sff);
        chooser1.addChoosableFileFilter(new FastQFileFilter());
        chooser1.addChoosableFileFilter(new CasavaFastQFileFilter());
        chooser1.addChoosableFileFilter(new BAMFileFilter());
        chooser1.addChoosableFileFilter(new MappedBAMFileFilter());
        chooser1.addChoosableFileFilter(new Fast5FileFilter());
        chooser1.setFileFilter(sff);
        int result = chooser1.showOpenDialog(this);
        if (result == JFileChooser.CANCEL_OPTION) {
            return;
        }
        lastDirectory = chooser1.getSelectedFile().getParent();
        JFileChooser chooser2;
        if (lastDirectory == null) {
            chooser2 = new JFileChooser();
        } else {
            chooser2 = new JFileChooser(lastDirectory);
        }
        chooser2.setDialogTitle("Select Second File");
        chooser2.setMultiSelectionEnabled(true);
        SequenceFileFilter sff2 = new SequenceFileFilter();
        chooser2.addChoosableFileFilter(sff2);
        chooser2.addChoosableFileFilter(new FastQFileFilter());
        chooser2.addChoosableFileFilter(new CasavaFastQFileFilter());
        chooser2.addChoosableFileFilter(new BAMFileFilter());
        chooser2.addChoosableFileFilter(new MappedBAMFileFilter());
        chooser2.addChoosableFileFilter(new Fast5FileFilter());
        chooser2.setFileFilter(sff);
        int result2 = chooser2.showOpenDialog(this);
        if (result2 == JFileChooser.CANCEL_OPTION) {
            return;
        }

        File[] files1 = chooser1.getSelectedFiles();
        File[] files2 = chooser2.getSelectedFiles();

        File[] filesToProcess1;
        File[] filesToProcess2;

        int status1 = 0;
        int status2 = 0;

        if (files1.length > 1 || files2.length > 1) {
            if (files1.length > 1) {
                int r = JOptionPane.showConfirmDialog(null, "You have selected more than one file as forward.\nAssemble them and make single report as the forward file?\nIf not, 123Fastq will analyse first selected file as the forward file.", "Assemble forward inputed files?", JOptionPane.YES_NO_OPTION);
                if (r == JOptionPane.YES_OPTION) {
                    status1 = 1;
                }
            }
            if (files2.length > 1) {
                int r = JOptionPane.showConfirmDialog(null, "You have selected more than one file as reverse.\nAssemble them and make single report as the reverse file?\nIf not, 123Fastq will analyse first selected file as the reverse file.", "Assemble reverse inputed files?", JOptionPane.YES_NO_OPTION);
                if (r == JOptionPane.YES_OPTION) {
                    status2 = 1;
                }
            }
        }

        if (status1 == 1) {
            filesToProcess1 = new File[files1.length];
            OutputNameString1 = "<html>";
            for (int i = 0; i < files1.length; i++) {
                filesToProcess1[i] = files1[i];
                if (i == 0) {
                    OutputNameString1 += filesToProcess1[i].getName();
                } else {
                    OutputNameString1 += "<br>" + filesToProcess1[i].getName();
                }
            }
            OutputNameString1 += "</html>";
        } else {
            filesToProcess1 = new File[1];
            filesToProcess1[0] = files1[0];
            OutputNameString1 = filesToProcess1[0].getName();
        }

        if (status2 == 1) {
            filesToProcess2 = new File[files2.length];
            OutputNameString2 = "<html>";
            for (int i = 0; i < files2.length; i++) {
                filesToProcess2[i] = files2[i];
                if (i == 0) {
                    OutputNameString2 += filesToProcess2[i].getName();
                } else {
                    OutputNameString2 += "<br>" + filesToProcess2[i].getName();
                }
            }
            OutputNameString2 += "</html>";
        } else {
            filesToProcess2 = new File[1];
            filesToProcess2[0] = files2[0];
            OutputNameString2 = filesToProcess2[0].getName();
        }
        int qcThreadCount = requestQcThreadCount();
        if (qcThreadCount < 1) {
            return;
        }
        ResultsPanel rp;
        SequenceFile sequenceFile1 = null;
        try {
            sequenceFile1 = SequenceFactory.getSequenceFile(filesToProcess1);
        } catch (SequenceFormatException e) {
            if (TabAbsenceChecker()) {
                TabsStarter();
            }
            Comprative_ModeQC++;
            ErrorPanel errorPanel = new ErrorPanel("Selected file is NOT an output of a sequencing machine.");
            TabPane.addTab("Comparative-Mode QC Tab " + Comprative_ModeQC, errorPanel);
            int tabIndex = TabPane.indexOfTab("Comparative-Mode QC Tab " + Comprative_ModeQC);
            TabPane.setSelectedIndex(tabIndex);
            TabPane.setTabComponentAt(tabIndex, new TabComponents(TabPane, starterPanel, errorPanel, bg));
            TabPane.setComponentAt(tabIndex, errorPanel);
            throw e;
        } catch (IOException e) {
            if (TabAbsenceChecker()) {
                TabsStarter();
            }
            Comprative_ModeQC++;
            ErrorPanel errorPanel = new ErrorPanel("Selected file is broken.");
            TabPane.addTab("Comparative-Mode QC Tab " + Comprative_ModeQC, errorPanel);
            int tabIndex = TabPane.indexOfTab("Comparative-Mode QC Tab " + Comprative_ModeQC);
            TabPane.setSelectedIndex(tabIndex);
            TabPane.setTabComponentAt(tabIndex, new TabComponents(TabPane, starterPanel, errorPanel, bg));
            TabPane.setComponentAt(tabIndex, errorPanel);
            throw e;
        }

        SequenceFile sequenceFile2 = null;
        try {
            sequenceFile2 = SequenceFactory.getSequenceFile(filesToProcess2);
        } catch (SequenceFormatException e) {
            if (TabAbsenceChecker()) {
                TabsStarter();
            }
            Comprative_ModeQC++;
            ErrorPanel errorPanel = new ErrorPanel("<html>The selected file seems NOT an output of a sequencing machine.</html>");
            TabPane.addTab("Comparative-Mode QC Tab " + Comprative_ModeQC, errorPanel);
            int tabIndex = TabPane.indexOfTab("Comparative-Mode QC Tab " + Comprative_ModeQC);
            TabPane.setSelectedIndex(tabIndex);
            TabPane.setTabComponentAt(tabIndex, new TabComponents(TabPane, starterPanel, errorPanel, bg));
            TabPane.setComponentAt(tabIndex, errorPanel);
            throw e;
        } catch (IOException e) {
            if (TabAbsenceChecker()) {
                TabsStarter();
            }
            Comprative_ModeQC++;
            ErrorPanel errorPanel = new ErrorPanel("the selected file is broken.");
            TabPane.addTab("Comparative-Mode QC Tab " + Comprative_ModeQC, errorPanel);
            int tabIndex = TabPane.indexOfTab("Comparative-Mode QC Tab " + Comprative_ModeQC);
            TabPane.setSelectedIndex(tabIndex);
            TabPane.setTabComponentAt(tabIndex, new TabComponents(TabPane, starterPanel, errorPanel, bg));
            TabPane.setComponentAt(tabIndex, errorPanel);
            throw e;
        }
        if (TabAbsenceChecker()) {
            TabsStarter();
        }
        Comprative_ModeQC++;

        if (!sequenceFile1.hasNext() || !sequenceFile2.hasNext()) {
            ErrorPanel errorPanel;
            if (!sequenceFile1.hasNext()) {
                errorPanel = new ErrorPanel("Forward selected file is empty.");
            } else {
                errorPanel = new ErrorPanel("Reverse selected file is empty.");
            }
            TabPane.addTab("Comparative-Mode QC Tab " + Comprative_ModeQC, errorPanel);
            int tabIndex = TabPane.indexOfTab("Comparative-Mode QC Tab " + Comprative_ModeQC);
            TabPane.setSelectedIndex(tabIndex);
            TabPane.setTabComponentAt(tabIndex, new TabComponents(TabPane, starterPanel, errorPanel, bg));
            TabPane.setComponentAt(tabIndex, errorPanel);
            return;
        }

        int automaticStatus = 0;
        if (status1 == 1 || status2 == 1) {
            automaticStatus = 1;
        }
        rp = new ResultsPanel(sequenceFile1, sequenceFile2, filesToProcess1[0], filesToProcess2[0], mainQueue, true, TabPane, starterPanel, bg, OutputNameString1, OutputNameString2, automaticStatus, qcThreadCount);
        TabPane.addTab("Comparative-Mode QC Tab " + Comprative_ModeQC, rp);
        int tabIndex = TabPane.indexOfTab("Comparative-Mode QC Tab " + Comprative_ModeQC);
        TabPane.setSelectedIndex(tabIndex);
        TabPane.setTabComponentAt(tabIndex, new TabComponents(TabPane, starterPanel, rp, mainQueue, bg));
        TabPane.setComponentAt(tabIndex, rp);
    }

    public void Single_Mode_QC_OpenFile() throws NameFormatException, SequenceFormatException, IOException {
        JFileChooser chooser;
        if (lastDirectory == null) {
            chooser = new JFileChooser();
        } else {
            chooser = new JFileChooser(lastDirectory);
        }
        chooser.setMultiSelectionEnabled(true);
        SequenceFileFilter sff = new SequenceFileFilter();
        chooser.addChoosableFileFilter(sff);
        chooser.addChoosableFileFilter(new FastQFileFilter());
        chooser.addChoosableFileFilter(new CasavaFastQFileFilter());
        chooser.addChoosableFileFilter(new BAMFileFilter());
        chooser.addChoosableFileFilter(new MappedBAMFileFilter());
        chooser.addChoosableFileFilter(new Fast5FileFilter());
        chooser.setFileFilter(sff);
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.CANCEL_OPTION) {
            return;
        }
        FileFilter chosenFilter = chooser.getFileFilter();
        if (chosenFilter instanceof FastQFileFilter) {
            Config.getInstance().setSequenceFormat("fastq");
        }
        if (chosenFilter instanceof CasavaFastQFileFilter) {
            Config.getInstance().setSequenceFormat("fastq");
            Config.getInstance().setCasavaMode(true);
        } else if (chosenFilter instanceof BAMFileFilter) {
            Config.getInstance().setSequenceFormat("bam");
        } else if (chosenFilter instanceof MappedBAMFileFilter) {
            Config.getInstance().setSequenceFormat("bam_mapped");
            System.setProperty("123Fastq.sequence_format", "bam_mapped");
        }
        File[] files = chooser.getSelectedFiles();
        File[][] fileGroups;
        int status = 0;
        if (files.length > 1) {
            int r = JOptionPane.showConfirmDialog(null, "You selected more than one file.\nAssemble them and make single report?\n", "Assemble inputed files?", JOptionPane.YES_NO_OPTION);
            if (r == JOptionPane.YES_OPTION) {
                status = 1;
            }
        }
        if (status == 1) {
            fileGroups = new File[1][files.length];
            for (int i = 0; i < files.length; i++) {
                fileGroups[0][i] = files[i];
            }
        } else {
            fileGroups = new File[files.length][1];
            for (int i = 0; i < files.length; i++) {
                fileGroups[i][0] = files[i];
            }
        }
        lastDirectory = fileGroups[0][0].getParent();
        int qcThreadCount = requestQcThreadCount();
        if (qcThreadCount < 1) {
            return;
        }
        for (int i = 0; i < fileGroups.length; i++) {
            File[] filesToProcess = fileGroups[i];

            if (filesToProcess.length > 1) {
                OutputNameString1 = "<html>";
                for (int j = 0; j < filesToProcess.length; j++) {
                    if (j == 0) {
                        OutputNameString1 += filesToProcess[j].getName();

                    } else {
                        OutputNameString1 += "<br>" + filesToProcess[j].getName();
                    }
                }
                OutputNameString1 += "</html>";
            } else {
                OutputNameString1 = filesToProcess[0].getName();
            }
            SequenceFile sequenceFile = null;
            try {
                sequenceFile = SequenceFactory.getSequenceFile(filesToProcess);
            } catch (SequenceFormatException e) {
                if (TabAbsenceChecker()) {
                    TabsStarter();
                }
                Single_ModeQC++;
                ErrorPanel errorPanel = new ErrorPanel("<html>The selected file seems NOT an output of a sequencing machine.</html>");
                TabPane.addTab("Single-Mode QC Tab " + Single_ModeQC, errorPanel);
                int tabIndex = TabPane.indexOfTab("Single-Mode QC Tab " + Single_ModeQC);
                TabPane.setSelectedIndex(tabIndex);
                TabPane.setTabComponentAt(tabIndex, new TabComponents(TabPane, starterPanel, errorPanel, bg));
                TabPane.setComponentAt(tabIndex, errorPanel);
                throw e;
            } catch (IOException e) {
                if (TabAbsenceChecker()) {
                    TabsStarter();
                }
                Single_ModeQC++;
                ErrorPanel errorPanel = new ErrorPanel("The selected file is broken.");
                TabPane.addTab("Single-Mode QC Tab " + Single_ModeQC, errorPanel);
                int tabIndex = TabPane.indexOfTab("Single-Mode QC Tab " + Single_ModeQC);
                TabPane.setSelectedIndex(tabIndex);
                TabPane.setTabComponentAt(tabIndex, new TabComponents(TabPane, starterPanel, errorPanel, bg));
                TabPane.setComponentAt(tabIndex, errorPanel);
                throw e;
            }
            if (TabAbsenceChecker()) {
                TabsStarter();
            }
            try {
                Single_ModeQC++;
                if (!sequenceFile.hasNext()) {
                    ErrorPanel errorPanel;
                    errorPanel = new ErrorPanel("The selected file is empty.");
                    TabPane.addTab("Single-Mode QC Tab " + Single_ModeQC, errorPanel);
                    int tabIndex = TabPane.indexOfTab("Single-Mode QC Tab " + Single_ModeQC);
                    TabPane.setSelectedIndex(tabIndex);
                    TabPane.setTabComponentAt(tabIndex, new TabComponents(TabPane, starterPanel, errorPanel, bg));
                    TabPane.setComponentAt(tabIndex, errorPanel);
                    return;
                }

                ResultsPanel rp = new ResultsPanel(sequenceFile, filesToProcess[0], mainQueue, true, TabPane, starterPanel, bg, OutputNameString1, status, qcThreadCount);
                TabPane.addTab("Single-Mode QC Tab " + Single_ModeQC, rp);
                int tabIndex = TabPane.indexOfTab("Single-Mode QC Tab " + Single_ModeQC);
                TabPane.setSelectedIndex(tabIndex);
                TabPane.setTabComponentAt(tabIndex, new TabComponents(TabPane, starterPanel, rp, mainQueue, bg));
                TabPane.setComponentAt(tabIndex, rp);
            } catch (Exception ex) {
                Logger.getLogger(TrimPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public boolean TabAbsenceChecker() {
        return ClickedFast5ToFastq == 0
                && ClickedSamToFastq == 0
                && ClickedBarcode == 0
                && ClickedTrim == 0
                && Single_ModeQC == 0
                && Comprative_ModeQC == 0;
    }

    public void TabsStarter() {
        starterPanel.setVisible(false);
        TabPane = new MovableTabbedPane();
        TabPane.setForeground(new java.awt.Color(153, 153, 153));
        bg.add(TabPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 40, 810, 650));
    }

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
//                UIManager.setLookAndFeel(
//                        UIManager.getSystemLookAndFeelClassName());

        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(App123Fastq.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        java.awt.EventQueue.invokeLater(() -> {
            try {
                Thread.sleep(2000); //display time of splash screen
            } catch (Exception e) {
            }
            new App123Fastq().setVisible(true);
        });
    }

    private MovableTabbedPane TabPane;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel bg;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel lblBarcodeSplitter;
    private javax.swing.JLabel lblCloseSign;
    private javax.swing.JLabel lblConvert;
    private javax.swing.JLabel lblConvertSAMtoFASTQ;
    private javax.swing.JLabel lblCutIcon;
    private javax.swing.JLabel lblFast5toFastq;
    private javax.swing.JLabel lblIUpside;
    private javax.swing.JLabel lblIconConvert;
    private javax.swing.JLabel lblMinSign;
    private javax.swing.JLabel lblNewTabTrim;
    private javax.swing.JLabel lblQC;
    private javax.swing.JLabel lblQCIcon;
    private javax.swing.JLabel lblQCimportComprative;
    private javax.swing.JLabel lblQCimportSingle;
    private javax.swing.JLabel lblStarterPic;
    private javax.swing.JLabel lblTrimFactory;
    private javax.swing.JLabel lblWebSite;
    private javax.swing.JPanel pnlConvert;
    private javax.swing.JPanel pnlConvertorHide;
    private javax.swing.JPanel pnlLeftSide;
    private javax.swing.JPanel pnlQC;
    private javax.swing.JPanel pnlQCHide;
    private javax.swing.JPanel pnlTrimFactory;
    private javax.swing.JPanel pnlTrimHide;
    private javax.swing.JPanel starterPanel;
    private javax.swing.JPanel upperPanel;
    // End of variables declaration//GEN-END:variables
}
