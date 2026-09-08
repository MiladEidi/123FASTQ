package Eidi._123Fastq.QualityControlPackage.FileFilters;

import java.io.File;
import javax.swing.filechooser.FileFilter;

public class Fast5FileFilter extends FileFilter {

    public boolean accept(File f) {
        return f.isDirectory() || f.getName().toLowerCase().endsWith(".fast5");
    }

    public String getDescription() {
        return "Fast5 Files";
    }
}
