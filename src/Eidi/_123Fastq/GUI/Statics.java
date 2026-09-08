package Eidi._123Fastq.GUI;

import java.awt.Color;
import java.awt.Font;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import javax.swing.ImageIcon;
import javax.swing.UIManager;

public class Statics {

    public static String VERSION = "1.3";
    public static final ImageIcon ERROR_ICON = new ImageIcon(ClassLoader.getSystemResource("Eidi/_123Fastq/QualityControlPackage/Resources/error.png"));
    public static final ImageIcon WARNING_ICON = new ImageIcon(ClassLoader.getSystemResource("Eidi/_123Fastq/QualityControlPackage/Resources/warning.png"));
    public static final ImageIcon OK_ICON = new ImageIcon(ClassLoader.getSystemResource("Eidi/_123Fastq/QualityControlPackage/Resources/tick.png"));
    public static final Font TrimUI_12 = new Font("Segoe UI", 1, 12);
    public static final Font TrimUI_14 = new Font("Segoe UI", 1, 14);
    public static final Font TrimUI_18 = new Font("Segoe UI", 1, 18);
    public static final Font TrimUI_22 = new Font("Segoe UI", 1, 22);
    public static final Color Light_Blue = new Color(170, 204, 255);
    public static final Color Dark1 = new Color(74,74,88);
    public static final Color UIcolor = UIManager.getColor("Panel.background");
    public static final Color GOOD_START = new Color(226, 255, 155);
    public static final Color GOOD_END = new Color(138, 245, 143);
    public static final Color GOOD_DARK_START = new Color(208, 248, 113);
    public static final Color GOOD_DARK_END = new Color(0, 255, 12);
    public static final Color BAD = new Color(255, 219, 155);
    public static final Color UGLY = new Color(255, 146, 103);
    public static final Color BAD_DARK = new Color(255, 209, 128);
    public static final Color UGLY_DARK = new Color(255, 72, 0);
    public static final Color[] COLOURS_Line_Graph = new Color[]{new Color(220, 0, 0), new Color(0, 0, 220), new Color(0, 220, 0), Color.DARK_GRAY, Color.MAGENTA, Color.ORANGE, Color.YELLOW, Color.CYAN, Color.PINK, Color.LIGHT_GRAY};
    public static final Color Horizental_Lines = Color.BLACK;
    public static final Font Graph_Min_Font = new Font("Sans Serif", 1, 12);
    public static final Font Graph_Mid_Font = new Font("Sans Serif", 1, 14);
    public static final Font Graph_Max_Font = new Font("Sans Serif", 1, 16);
    public static final NumberFormat formatter = new DecimalFormat("###,###,###,###");
    public static final DecimalFormat percentFormatter = new DecimalFormat("0.00");
    public static final DecimalFormat percentFormatter2 = new DecimalFormat("0");
    public static final int mb = 1024 * 1024;
    public static String SysFileSeprator = System.getProperty("file.separator");
}
