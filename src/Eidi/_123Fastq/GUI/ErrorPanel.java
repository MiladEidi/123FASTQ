package Eidi._123Fastq.GUI;


public class ErrorPanel extends MyJPanel {

    private javax.swing.JLabel lblErrorIcon;
    private javax.swing.JLabel lblErrorMessage;

    public ErrorPanel(String ErrorMessage) {
        ErrorPanelComponents();
        lblErrorMessage.setText(ErrorMessage);
    }

    private void ErrorPanelComponents() {

        lblErrorMessage = new javax.swing.JLabel();
        lblErrorIcon = new javax.swing.JLabel();

        lblErrorMessage.setFont(new java.awt.Font("Segoe UI", 1, 24));

        lblErrorIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Eidi/_123Fastq/GUI/Image/icons8_Event_Log_250px.png")));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(215, 215, 215)
                                                .addComponent(lblErrorIcon))
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(50, 85, 85)
                                                .addComponent(lblErrorMessage)))
                                .addContainerGap(85, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGap(103, 103, 103)
                                .addComponent(lblErrorMessage)
                                .addGap(74, 74, 74)
                                .addComponent(lblErrorIcon)
                                .addContainerGap(87, Short.MAX_VALUE))
        );
    }
}
