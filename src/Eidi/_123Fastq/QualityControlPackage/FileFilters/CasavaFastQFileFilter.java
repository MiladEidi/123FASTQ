package Eidi._123Fastq.QualityControlPackage.FileFilters;

import java.io.File;
import javax.swing.filechooser.FileFilter;

public class CasavaFastQFileFilter extends FileFilter {

    public boolean accept(File f) {
        return f.isDirectory() || f.getName().endsWith(".fastq.gz");
    }

    public String getDescription() {
        return "Casava FastQ Files";
    }
}
