package Eidi._123Fastq.QualityControlPackage.FileFilters;

import java.io.File;
import javax.swing.filechooser.FileFilter;

public class FastQFileFilter extends FileFilter {

    public boolean accept(File f) {
        return f.isDirectory()
                || f.getName().toLowerCase().endsWith(".txt.gz")
                || f.getName().toLowerCase().endsWith(".fastq.gz")
                || f.getName().toLowerCase().endsWith(".fq.gz")
                || f.getName().toLowerCase().endsWith(".fq")
                || f.getName().toLowerCase().endsWith(".txt.bz2")
                || f.getName().toLowerCase().endsWith(".fastq.bz2")
                || f.getName().toLowerCase().endsWith(".txt")
                || f.getName().toLowerCase().endsWith(".fastq");
    }

    public String getDescription() {
        return "FastQ Files";
    }
}
