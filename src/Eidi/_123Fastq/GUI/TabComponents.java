package Eidi._123Fastq.GUI;

import static Eidi._123Fastq.GUI.App123Fastq.ClickedBarcode;
import static Eidi._123Fastq.GUI.App123Fastq.ClickedTrim;
import static Eidi._123Fastq.GUI.App123Fastq.ClickedFast5ToFastq;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.plaf.basic.BasicButtonUI;
import static Eidi._123Fastq.GUI.App123Fastq.ClickedSamToFastq;
import static Eidi._123Fastq.GUI.App123Fastq.Comprative_ModeQC;
import static Eidi._123Fastq.GUI.App123Fastq.Single_ModeQC;

public class TabComponents extends JPanel {

    private JTabbedPane tabPane;
    private javax.swing.JPanel starterPanel;
    private MyJPanel taskPanel;
    public TaskQueue mainQueue = null;
    private javax.swing.JPanel bg;

    public TabComponents(final JTabbedPane TabPane, JPanel StarterPanel, MyJPanel TaskPanel, TaskQueue MainQueue, JPanel background) {
        super(new FlowLayout(FlowLayout.LEFT, 0, 0));

        this.starterPanel = StarterPanel;
        this.taskPanel = TaskPanel;
        this.mainQueue = MainQueue;
        this.bg = background;
        if (TabPane == null) {
            throw new NullPointerException("TabbedPane is null");
        }
        this.tabPane = TabPane;
        setOpaque(false);
        JLabel label = new JLabel() {
            public String getText() {
                int i = TabPane.indexOfTabComponent(TabComponents.this);
                if (i != -1) {
                    return TabPane.getTitleAt(i);
                }
                return null;
            }
        };
        add(label);
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
        JButton button = new TabButton();
        add(button);
        setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
    }

    public TabComponents(final JTabbedPane TabPane, JPanel StarterPanel, MyJPanel TaskPanel, JPanel background) {
        super(new FlowLayout(FlowLayout.LEFT, 0, 0));

        this.bg = background;
        this.starterPanel = StarterPanel;
        this.taskPanel = TaskPanel;
        if (TabPane == null) {
            throw new NullPointerException("TabbedPane is null");
        }
        this.tabPane = TabPane;
        setOpaque(false);
        JLabel label = new JLabel() {
            public String getText() {
                int i = TabPane.indexOfTabComponent(TabComponents.this);
                if (i != -1) {
                    return TabPane.getTitleAt(i);
                }
                return null;
            }
        };

        add(label);
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
        JButton button = new TabButton();
        add(button);
        setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
    }

    private class TabButton extends JButton implements ActionListener {

        public TabButton() {
            int size = 17;
            setPreferredSize(new Dimension(size, size));
            setToolTipText("Close This Tab");
            setUI(new BasicButtonUI());
            setContentAreaFilled(false);
            setFocusable(false);
            setBorderPainted(false);
            addMouseListener(buttonMouseListener);
            setRolloverEnabled(true);
            addActionListener(this);
        }

        public void actionPerformed(ActionEvent e) {
            int i = tabPane.indexOfTabComponent(TabComponents.this);
            if (i != -1) {
                tabPane.remove(i);
            }

            if (tabPane.getTabCount() == 0) {
                bg.remove(tabPane);
                bg.revalidate();
                bg.repaint();
                starterPanel.setVisible(true);
                ClickedTrim = 0;
                Single_ModeQC = 0;
                Comprative_ModeQC = 0;
                ClickedBarcode = 0;
                ClickedSamToFastq = 0;
                ClickedFast5ToFastq = 0;
            }

            taskPanel.setShouldRun(false);

            if (taskPanel.getThread() == null) {
                return;
            }

            if (taskPanel.getThread().isAlive()) {
                try {
                    mainQueue.StopRunningTask();
                } catch (InterruptedException ex) {
                    Logger.getLogger(TabComponents.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            if (getModel().isPressed()) {
                g2.translate(1, 1);
            }
            g2.setStroke(new BasicStroke(2));
            g2.setColor(Color.BLACK);
            if (getModel().isRollover()) {
                g2.setColor(Color.RED);
            }
            int delta = 6;
            g2.drawLine(delta, delta, getWidth() - delta - 1, getHeight() - delta - 1);
            g2.drawLine(getWidth() - delta - 1, delta, delta, getHeight() - delta - 1);
            g2.dispose();
        }
    }

    private final static MouseListener buttonMouseListener = new MouseAdapter() {
        public void mouseEntered(MouseEvent e) {
            Component component = e.getComponent();
            if (component instanceof AbstractButton) {
                AbstractButton button = (AbstractButton) component;
                button.setBorderPainted(true);
            }
        }

        public void mouseExited(MouseEvent e) {
            Component component = e.getComponent();
            if (component instanceof AbstractButton) {
                AbstractButton button = (AbstractButton) component;
                button.setBorderPainted(false);
            }
        }
    };
}
