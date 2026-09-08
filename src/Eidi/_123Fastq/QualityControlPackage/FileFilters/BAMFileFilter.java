package Eidi._123Fastq.QualityControlPackage.FileFilters;

import java.io.File;
import javax.swing.filechooser.FileFilter;

public class BAMFileFilter extends FileFilter {

    public boolean accept(File f) {
        return f.isDirectory()
                || f.getName().toLowerCase().endsWith(".bam")
                || f.getName().toLowerCase().endsWith(".sam");
    }

    public String getDescription() {
        return "BAM/SAM Files";
    }
}
