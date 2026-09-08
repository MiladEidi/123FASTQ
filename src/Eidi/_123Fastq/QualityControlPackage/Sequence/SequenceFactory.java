package Eidi._123Fastq.QualityControlPackage.Sequence;

import Eidi._123Fastq.QualityControlPackage.Config;
import Eidi._123Fastq.QualityControlPackage.Utilities.CasavaBasename;
import Eidi._123Fastq.QualityControlPackage.Utilities.NameFormatException;
import java.io.File;
import java.io.IOException;

public class SequenceFactory {

    public static SequenceFile getSequenceFile(File[] files) throws SequenceFormatException, IOException {

        if (files.length == 1) {
            if (Config.getInstance().casava) {
                try {
                    CasavaBasename.getCasavaBasename(files[0].getName());
                } catch (NameFormatException nfe) {
                    return getSequenceFile(files[0]);
                }
            } else {
                return getSequenceFile(files[0]);
            }
        }
        return new SequenceFileGroup(files);
    }

    public static SequenceFile getSequenceFile(File file) throws SequenceFormatException, IOException {

        Config config = Config.getInstance();
        if (config.sequence_format != null) {
            switch (config.sequence_format) {
                case "bam":
                case "sam":
                    return new BAMFile(file, false);
                case "bam_mapped":
                case "sam_mapped":
                    return new BAMFile(file, true);
                case "fastq":
                    return new FastQFile(config, file);
                default:
                    throw new SequenceFormatException("Didn't understand format name '" + config.sequence_format + "'");
            }

        }
        if (file.getName().toLowerCase().endsWith(".bam") || file.getName().toLowerCase().endsWith(".sam")) {
            return new BAMFile(file, false);
        } else if (file.getName().toLowerCase().endsWith(".fast5")) {
            return new Fast5File(file);
        } else {
            return new FastQFile(config, file);
        }
    }
}
