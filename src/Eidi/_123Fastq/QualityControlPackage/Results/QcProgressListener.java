package Eidi._123Fastq.QualityControlPackage.Results;

public interface QcProgressListener {

    void progressUpdated(int sequenceCount, int percentComplete);
}
