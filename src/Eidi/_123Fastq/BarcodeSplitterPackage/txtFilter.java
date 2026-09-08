package Eidi._123Fastq.BarcodeSplitterPackage;

import java.io.File;
import javax.swing.filechooser.FileFilter;

public class txtFilter extends FileFilter {

    public boolean accept(File f) {
        return f.isDirectory()
                || f.getName().toLowerCase().endsWith(".txt.gz")
                || f.getName().toLowerCase().endsWith(".txt.bz2")
                || f.getName().toLowerCase().endsWith(".txt");
    }

    public String getDescription() {
        return "Text Files";
    }
}
