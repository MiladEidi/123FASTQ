package Eidi._123Fastq.QualityControlPackage.Graphs;

import Eidi._123Fastq.GUI.Statics;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;

public class PercentLineGraph extends JPanel {

    private String[] xTitles;
    private String xLabel;
    private String[] xCategories;
    private double[][] data;
    private String graphTitle;
    private double minY;
    private double maxY;
    private double yInterval;

    //data[0].length   check it....
    public PercentLineGraph(double[][] data, double minY, double maxY, String xLabel, String[] xTitles, int[] xCategories, String graphTitle) {
        this(data, minY, maxY, xLabel, xTitles, new String[0], graphTitle);
        this.xCategories = new String[xCategories.length];
        for (int i = 0; i < xCategories.length; i++) {
            this.xCategories[i] = "" + xCategories[i];
        }

    }

    public PercentLineGraph(double[][] data, double minY, double maxY, String xLabel, String[] xTitles, String[] xCategories, String graphTitle) {
        this.data = data;
        this.minY = minY;
        this.maxY = maxY;
        this.xTitles = xTitles;
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
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int lastY = 0;
        double yStart;
        if (minY % yInterval == 0) {
            yStart = minY;
        } else {
            yStart = yInterval * (((int) minY / yInterval) + 1);
        }
        int xOffset = 0;
        for (double i = yStart; i <= maxY; i += yInterval) {
            String label = "" + i;
            label = label.replaceAll(".0$", "");
            int width = g2.getFontMetrics().stringWidth(label);
            if (width > xOffset) {
                xOffset = width;
            }
            g2.drawString(label, 37, getY(i) + (g.getFontMetrics().getAscent() / 2));
        }
        xOffset += 40;
        // Draw the graph title
        g2.setFont(Statics.Graph_Max_Font);
        g2.drawString(graphTitle, 50, getY(maxY) - 25 + (g2.getFontMetrics().getAscent() / 2));
        // Now draw the data points
        g2.setFont(Statics.Graph_Min_Font);
        int baseWidth = (getWidth() - (xOffset + 10)) / data[0].length;
        if (baseWidth < 1) {
            baseWidth = 1;
        }
        // First draw faint boxes over alternating bases so you can see which is which
        // Let's find the longest label, and then work out how often we can draw labels
        int lastXLabelEnd = 0;
        for (int i = 0; i < data[0].length; i++) {
            if (i % 2 != 0) {
                g2.setColor(Color.WHITE);
                g2.fillRect(xOffset + (baseWidth * i), 40, baseWidth, getHeight() - 80);
            } else {
                g2.setColor(Color.LIGHT_GRAY);
                g2.fillRect(xOffset + (baseWidth * i), 40, baseWidth, getHeight() - 80);
            }
            g2.setColor(Color.BLACK);
            String baseNumber = "" + xCategories[i];
            int baseNumberWidth = g.getFontMetrics().stringWidth(baseNumber);
            int baseNumberPosition = (baseWidth / 2) + xOffset + (baseWidth * i) - (baseNumberWidth / 2);
            if (baseNumberPosition > lastXLabelEnd) {
                g2.drawString(baseNumber, baseNumberPosition, getHeight() - 25);
                lastXLabelEnd = baseNumberPosition + baseNumberWidth + 5;
            }
        }
        // Now draw horizontal lines across from the y axis
        g2.setColor(Statics.Horizental_Lines);
        for (double i = yStart; i <= maxY; i += yInterval) {
            g2.drawLine(xOffset, getY(i), data[0].length * baseWidth + xOffset, getY(i));
        }
        g2.setColor(Color.BLACK);
        // Now draw the axes
        g2.setStroke(new BasicStroke(2));
        g2.drawLine(xOffset, getHeight() - 40, data[0].length * baseWidth + xOffset, getHeight() - 40);
        g2.drawLine(xOffset, getHeight() - 40, xOffset, 40);
        // Now draw the datasets
        for (int d = 0; d < data.length; d++) {
            g2.setColor(Statics.COLOURS_Line_Graph[d % Statics.COLOURS_Line_Graph.length]);
            lastY = getY(data[d][0]);
            for (int i = 1; i < data[d].length; i++) {
                int thisY = getY(data[d][i]);
                g2.drawLine((baseWidth / 2) + xOffset + (baseWidth * (i - 1)), lastY, (baseWidth / 2) + xOffset + (baseWidth * i), thisY);
                lastY = thisY;
            }
        }
        // Now draw the data legend
        g2.setStroke(new BasicStroke(1));
        // First we need to find the widest label
        int widestLabel = 0;
        for (int t = 0; t < xTitles.length; t++) {
            int width = g2.getFontMetrics().stringWidth(xTitles[t]);
            if (width > widestLabel) {
                widestLabel = width;
            }
        }
        widestLabel += 10;
        // First draw a box to put the legend in
        g2.setColor(Color.WHITE);
        g2.fill3DRect((getWidth() - 15) - widestLabel, 50, widestLabel, 3 + (20 * xTitles.length), true);
        g2.setColor(Color.LIGHT_GRAY);
        g2.drawRect((getWidth() - 15) - widestLabel, 50, widestLabel, 3 + (20 * xTitles.length));
        g2.setFont(Statics.Graph_Mid_Font);
        // Now draw the actual labels
        for (int t = 0; t < xTitles.length; t++) {
            g2.setColor(Statics.COLOURS_Line_Graph[t % Statics.COLOURS_Line_Graph.length]);
            g2.drawString(xTitles[t], ((getWidth() - 15) - widestLabel) + 3, 46 + (20 * (t + 1)));
        }
        g2.setColor(Color.BLACK);
        g2.setFont(Statics.Graph_Mid_Font);
        g2.drawString(xLabel, (getWidth() / 2) - (g.getFontMetrics().stringWidth(xLabel) / 2), getHeight() - 5);
        g2.translate((float) -13, (float) 300);
        g2.rotate(Math.toRadians(-90));
        g2.drawString("Percent", 1, (getY(maxY - yStart)));
        g2.rotate(-Math.toRadians(-90));
        g2.translate(-(float) -13, -(float) 300);
    }

    private int getY(double y) {
        return (getHeight() - 40) - (int) (((getHeight() - 80) / (maxY - minY)) * y);
    }
}
