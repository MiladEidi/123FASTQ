package Eidi._123Fastq.QualityControlPackage;

import java.io.File;

public class Config {

    private static Config instance = new Config();
    public boolean nogroup = false;
    public boolean expgroup = false;
    public boolean quiet = false;
    public boolean show_version = false;
    public Integer kmer_size = null;
    public Integer threads = null;
    public boolean showUpdates = true;
    public File output_dir = null;
    public boolean casava = false;
    public boolean nano = false;
    public boolean nofilter = false;
    public Boolean do_unzip = false;
    public String sequence_format = null;
    public File contaminant_file = null;
    public File adapter_file = null;
    public File limits_file = null;
    public int minLength = 0;

    private Config() {
        if (System.getProperty("123Fastq.nano") != null && System.getProperty("123Fastq.nano").equals("true")) {
            nano = true;
        }
    }

    public void setSequenceFormat(String sequenceFormat) {
        if (sequenceFormat.equals("fastq")
                || sequenceFormat.equals("sam")
                || sequenceFormat.equals("bam")
                || sequenceFormat.equals("sam_mapped")
                || sequenceFormat.equals("bam_mapped")) {
            sequence_format = sequenceFormat;
        } else {
            throw new IllegalArgumentException("Sequence format '" + sequenceFormat + "' wasn't recognised");
        }
    }

    public void setCasavaMode(boolean casava) {
        this.casava = casava;
    }

    public static Config getInstance() {
        return instance;
    }
}
