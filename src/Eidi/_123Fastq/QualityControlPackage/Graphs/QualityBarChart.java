package Eidi._123Fastq.QualityControlPackage.Graphs;

import Eidi._123Fastq.GUI.Statics;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;

public class QualityBarChart extends JPanel {

    private String xLabel;
    private String[] xCategories;
    private double[][] data;
    private String graphTitle;
    private double minY;
    private double maxY;
    private double yInterval;

    public QualityBarChart(double[][] data, double minY, double maxY, String xLabel, int[] xCategories, String graphTitle) {
        this(data, minY, maxY, xLabel, new String[0], graphTitle);
        this.xCategories = new String[xCategories.length];
        for (int i = 0; i < xCategories.length; i++) {
            this.xCategories[i] = "" + xCategories[i];
        }
    }

    public QualityBarChart(double[][] data, double minY, double maxY, String xLabel, String[] xCategories, String graphTitle) {
        this.data = data;
        this.minY = minY;
        this.maxY = maxY;
        this.xLabel = xLabel;
        this.xCategories = xCategories;
        this.graphTitle = graphTitle;
        this.yInterval = findOptimalYInterval(maxY);
    }

    private double findOptimalYInterval(double max) {

        int base = 1;
        double[] divisions = new double[]{1, 2, 2.5, 5};
        while (true) {
            for (int d = 0; d < divisions.length; d++) {
                double tester = base * divisions[d];
                if (max / tester <= 10) {
                    return tester;
                }
            }
            base *= 10;
        }
    }

    public Dimension getPreferredSize() {
        return new Dimension(800, 600);
    }

    public Dimension getMinimumSize() {
        return new Dimension(100, 200);
    }

    public void paint(Graphics g) {
        super.paint(g);

        g.setColor(Statics.UIcolor);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(Color.BLACK);
        g.setFont(Statics.Graph_Min_Font);
        double yStart;
        if (minY % yInterval == 0) {
            yStart = minY;
        } else {
            yStart = yInterval * (((int) minY / yInterval) + 1);
        }
        int xOffset = 0;
        for (double i = yStart; i <= maxY; i += yInterval) {
            String label = "" + Statics.formatter.format(i);
            int width = g.getFontMetrics().stringWidth(label);
            if (width > xOffset) {
                xOffset = width;
            }
            g.drawString(label, 37, getY(i) + (g.getFontMetrics().getAscent() / 2));
        }
        xOffset += 40;
        // Draw the graph title
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setFont(Statics.Graph_Max_Font);
        g2.drawString(graphTitle, 65, getY(maxY) - 25 + (g.getFontMetrics().getAscent() / 2));
        // Now draw the data points
        g.setFont(Statics.Graph_Min_Font);
        int baseWidth = (getWidth() - (xOffset + 10)) / data[0].length;
        if (baseWidth < 1) {
            baseWidth = 1;
        }
        // First draw faint boxes over alternating bases so you can see which is which
        // Let's find the longest label, and then work out how often we can draw labels
        int lastXLabelEnd = 0;
        for (int i = 0; i < data[0].length; i++) {
            if (i % 2 != 0) {
                g.setColor(Color.WHITE);
                g.fillRect(xOffset + (baseWidth * i), 40, baseWidth, getHeight() - 80);
            }else{
                g.setColor(Color.LIGHT_GRAY);
                g.fillRect(xOffset + (baseWidth * i), 40, baseWidth, getHeight() - 80);
            }
            g.setColor(Color.BLACK);
            String baseNumber = "" + xCategories[i];
            int baseNumberWidth = g.getFontMetrics().stringWidth(baseNumber);
            int baseNumberPosition = (baseWidth / 2) + xOffset + (baseWidth * i) - (baseNumberWidth / 2);
            if (baseNumberPosition > lastXLabelEnd) {
                g.drawString(baseNumber, baseNumberPosition, getHeight() - 25);
                lastXLabelEnd = baseNumberPosition + baseNumberWidth + 5;
            }
        }
        // Now draw horizontal lines across from the y axis
        g.setColor(Statics.Horizental_Lines);
        for (double i = yStart; i <= maxY; i += yInterval) {
            g.drawLine(xOffset, getY(i), data[0].length * baseWidth + xOffset, getY(i));
        }
        // Now draw the axes
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(2));
        g2.drawLine(xOffset, getHeight() - 40, data[0].length * baseWidth + xOffset, getHeight() - 40);
        g2.drawLine(xOffset, getHeight() - 40, xOffset, 40);
        // Now draw the datasets
        for (int i = 0; i < data[0].length; i++) {
            int xLabelg = Integer.parseInt(xCategories[i].split("-")[0]);
            if (xLabelg < 15) {
                g.setColor(new Color(221, 9, 0));
            } else if (xLabelg < 20) {
                g.setColor(new Color(221, 75, 0));
            } else if (xLabelg < 25) {
                g.setColor(new Color(210, 206, 0));
            } else if (xLabelg < 30) {
                g.setColor(new Color(177, 210, 0));
            } else if (xLabelg >= 30) {
                g.setColor(new Color(23, 194, 0));
            }
            g.fill3DRect(xOffset + (baseWidth * i) + 2, getY(data[0][i]), baseWidth - 4, getY(0) - getY(data[0][i]), true);
            g.setColor(Color.BLACK);
            g.draw3DRect(xOffset + (baseWidth * i) + 2, getY(data[0][i]), baseWidth - 4, getY(0) - getY(data[0][i]), true);
        }
        g.setFont(Statics.Graph_Mid_Font);
        g2.drawString(xLabel, (getWidth() / 2) - (g.getFontMetrics().stringWidth(xLabel) / 2), getHeight() - 5);
        g2.translate((float) -13, (float) 300);
        g2.rotate(Math.toRadians(-90));
        g2.drawString("Reads", 1, (getY(maxY - yStart)));
        g2.rotate(-Math.toRadians(-90));
        g2.translate(-(float) -13, -(float) 300);
    }

    private int getY(double y) {
        return (getHeight() - 40) - (int) (((getHeight() - 80) / (maxY - minY)) * y);
    }
}
