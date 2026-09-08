package Eidi._123Fastq.QualityControlPackage.Modules;

public interface MergeableQCModule extends QCModule {

    void mergeFrom(MergeableQCModule other);
}
