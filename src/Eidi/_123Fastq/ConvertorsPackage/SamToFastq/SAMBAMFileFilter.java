package Eidi._123Fastq.ConvertorsPackage.SamToFastq;

import java.io.File;
import javax.swing.filechooser.FileFilter;

public class SAMBAMFileFilter extends FileFilter {

    public boolean accept(File f) {
        return f.isDirectory()
                || f.getName().toLowerCase().endsWith(".bam")
                || f.getName().toLowerCase().endsWith(".sam");
    }

    public String getDescription() {
        return "SAM/BAM Files";
    }
}
