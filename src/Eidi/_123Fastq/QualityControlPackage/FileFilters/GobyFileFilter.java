package Eidi._123Fastq.QualityControlPackage.FileFilters;

import java.io.File;
import javax.swing.filechooser.FileFilter;

public class GobyFileFilter extends FileFilter {

	public boolean accept(File f) {
		return true;
	}

	public String getDescription() {
		return "Goby Files (all entries)";
	}

}
