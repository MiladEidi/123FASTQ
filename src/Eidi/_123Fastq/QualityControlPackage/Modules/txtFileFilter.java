package Eidi._123Fastq.QualityControlPackage.Modules;

import java.io.File;
import javax.swing.filechooser.FileFilter;

public class txtFileFilter extends FileFilter {

	public boolean accept(File f) {
		if (f.isDirectory()
				|| f.getName().toLowerCase().endsWith(".txt")
		) {
			return true;
		}
		else {
			return false;
		}
	}

	public String getDescription() {
		return "Text Files";
	}
}
