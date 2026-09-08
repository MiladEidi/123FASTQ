package Eidi._123Fastq.TrimFactoryPackage;

import javax.swing.JTextArea;

public interface Trimmer {

    public FastqRecord[] processRecords(FastqRecord in[], JTextArea messenger);

}
