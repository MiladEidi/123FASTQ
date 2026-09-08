package Eidi._123Fastq.QualityControlPackage.Graphs;

import Eidi._123Fastq.GUI.Statics;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;

public class QualityBoxPlot extends JPanel {

    private double[] means;
    private double[] medians;
    private double[] lowest;
    private double[] highest;
    private double[] lowerQuartile;
    private double[] upperQuartile;
    private String[] xLabels;
    private String graphTitle;
    private double minY;
    private double maxY;
    private double yInterval;

    public QualityBoxPlot(double[] means, double[] medians, double[] lowest, double[] highest, double[] lowerQuartile, double[] upperQuartile, double minY, double maxY, double yInterval, String[] xLabels, String graphTitle) {
        this.means = means;
        this.medians = medians;
        this.lowest = lowest;
        this.highest = highest;
        this.lowerQuartile = lowerQuartile;
        this.upperQuartile = upperQuartile;
        this.minY = minY;
        this.maxY = maxY;
        this.yInterval = yInterval;
        this.xLabels = xLabels;
        this.graphTitle = graphTitle;
    }

    public void paint(Graphics g) {
        super.paint(g);
        g.setFont(Statics.Graph_Min_Font);
        g.setColor(Statics.UIcolor);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(Color.BLACK);
        int lastY = 0;
        double yStart;
        if (minY % yInterval == 0) {
            yStart = minY;
        } else {
            yStart = yInterval * (((int) minY / yInterval) + 1);
        }
        int xOffset = 0;
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        for (double i = yStart; i <= maxY; i += yInterval) {
            String label = "" + i;
            label = label.replaceAll(".0$", "");
            int width = g.getFontMetrics().stringWidth(label);
            if (width > xOffset) {
                xOffset = width;
            }
            g2.drawString(label, 37, getY(i) + (g.getFontMetrics().getAscent() / 2));
        }
        xOffset += 40;
        g2.setFont(Statics.Graph_Max_Font);
        g2.drawString(graphTitle, 45, getY(maxY) - 25 + (g.getFontMetrics().getAscent() / 2));
        // Work out the width of the x axis bins
        int baseWidth = (getWidth() - (xOffset + 10)) / means.length;
        if (baseWidth < 1) {
            baseWidth = 1;
        }
        // First draw faint boxes over alternating bases so you can see which is which
        int lastXLabelEnd = 0;
        // Now draw some background colours which show good / bad quality
        Graphics2D grads = (Graphics2D) g;
        g.setFont(Statics.Graph_Min_Font);
        for (int i = 0; i < means.length; i++) {
            if (i % 2 != 0) {
                GradientPaint grad = new GradientPaint( xOffset + ((baseWidth * i)) + baseWidth, getY(yStart), Statics.UGLY, xOffset + (baseWidth * i), getY(20), Statics.BAD);
                grads.setPaint(grad);
            } else {
                GradientPaint grad = new GradientPaint( xOffset + ((baseWidth * i)) + baseWidth, getY(yStart), Statics.UGLY_DARK, xOffset + (baseWidth * i), getY(20), Statics.BAD_DARK);
                grads.setPaint(grad);
            }
            grads.fillRect(xOffset + (baseWidth * i), getY(20), baseWidth, getY(yStart) - getY(20));
            if (i % 2 != 0) {
                GradientPaint grad = new GradientPaint( xOffset + ((baseWidth * i)) + baseWidth, getY(20), Statics.BAD, xOffset + (baseWidth * i), getY(28), Statics.GOOD_START);
                grads.setPaint(grad);
            } else {
                GradientPaint grad = new GradientPaint( xOffset + ((baseWidth * i)) + baseWidth, getY(20), Statics.BAD_DARK, xOffset + (baseWidth * i), getY(28), Statics.GOOD_DARK_START);
                grads.setPaint(grad);
            }
            grads.fillRect(xOffset + (baseWidth * i), getY(28), baseWidth, getY(20) - getY(28));
            if (i % 2 != 0) {
                GradientPaint grad = new GradientPaint( xOffset + ((baseWidth * i)) + baseWidth, getY(28), Statics.GOOD_START, xOffset + (baseWidth * i), getY(maxY), Statics.GOOD_END);
                grads.setPaint(grad);
            } else {
                GradientPaint grad = new GradientPaint( xOffset + ((baseWidth * i)) + baseWidth, getY(28), Statics.GOOD_DARK_START, xOffset + (baseWidth * i), getY(maxY), Statics.GOOD_DARK_END);
                grads.setPaint(grad);
            }
            grads.fillRect(xOffset + (baseWidth * i), getY(maxY), baseWidth, getY(28) - getY(maxY));
            g.setColor(Color.BLACK);
            int baseNumberWidth = g.getFontMetrics().stringWidth(xLabels[i]);
            int labelStart = ((baseWidth / 2) + xOffset + (baseWidth * i)) - (baseNumberWidth / 2);
            if (labelStart > lastXLabelEnd) {
                g.drawString(xLabels[i], labelStart, getHeight() - 25);
                lastXLabelEnd = labelStart + g.getFontMetrics().stringWidth(xLabels[i]) + 5;
            }
        }
       // Now draw the boxplots
        for (int i = 0; i < medians.length; i++) {
            int boxBottomY = getY(lowerQuartile[i]);
            int boxTopY = getY(upperQuartile[i]);
            int lowerWhiskerY = getY(lowest[i]);
            int upperWhiskerY = getY(highest[i]);
            int medianY = getY(medians[i]);
            // Draw the main box
            g.setColor(new Color(255, 245, 1));
            g.fill3DRect(xOffset + (baseWidth * i) + 2, boxTopY, baseWidth - 4, boxBottomY - boxTopY, true);
            g.setColor(Color.BLACK);
            g.draw3DRect(xOffset + (baseWidth * i) + 2, boxTopY, baseWidth - 4, boxBottomY - boxTopY, true);
            // Draw the upper whisker
            g.drawLine(xOffset + (baseWidth * i) + (baseWidth / 2), upperWhiskerY, xOffset + (baseWidth * i) + (baseWidth / 2), boxTopY);
            g.drawLine(xOffset + (baseWidth * i) + 2, upperWhiskerY, xOffset + (baseWidth * (i + 1)) - 2, upperWhiskerY);
            // Draw the lower whisker
            g.drawLine(xOffset + (baseWidth * i) + (baseWidth / 2), lowerWhiskerY, xOffset + (baseWidth * i) + (baseWidth / 2), boxBottomY);
            g.drawLine(xOffset + (baseWidth * i) + 2, lowerWhiskerY, xOffset + (baseWidth * (i + 1)) - 2, lowerWhiskerY);
            // Draw the median line
            g.setColor(new Color(200, 0, 0));
            g.drawLine(xOffset + (baseWidth * i) + 2, medianY, (xOffset + (baseWidth * (i + 1))) - 2, medianY);
        }
        // Now overlay the means
        g.setColor(new Color(0, 0, 200));
        lastY = getY(means[0]);
        for (int i = 1; i < means.length; i++) {
            int thisY = getY(means[i]);
            g.drawLine((baseWidth / 2) + xOffset + (baseWidth * (i - 1)), lastY, (baseWidth / 2) + xOffset + (baseWidth * i), thisY);
            lastY = thisY;
        }
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(2));
        g2.drawLine(xOffset, getHeight() - 40, baseWidth*means.length + xOffset, getHeight() - 40);
        g2.drawLine(xOffset, getHeight() - 40, xOffset, 40);
        g2.setStroke(new BasicStroke(1));
        g.setFont(Statics.Graph_Mid_Font);
        g.drawString("Position in read (bp)", (getWidth() / 2) - (g.getFontMetrics().stringWidth("Position in read (bp)") / 2), getHeight() - 5);
        g2.translate((float) -13, (float) 300);
        g2.rotate(Math.toRadians(-90));
        g2.drawString("Phred Score", 1, (getY(maxY - yStart)));
        g2.rotate(-Math.toRadians(-90));
        g2.translate(-(float) -13, -(float) 300);
    }

    public int getY(double y) {
        return (getHeight() - 40) - (int) (((getHeight() - 80) / (maxY - minY)) * (y - minY));
    }
}
