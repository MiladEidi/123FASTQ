package Eidi._123Fastq.QualityControlPackage.Results;

import Eidi._123Fastq.QualityControlPackage.Modules.QCModule;

public class QcAnalysisResult {

    private final QCModule[] modules;
    private final int sequenceCount;

    QcAnalysisResult(QCModule[] modules, int sequenceCount) {
        this.modules = modules;
        this.sequenceCount = sequenceCount;
    }

    public QCModule[] getModules() {
        return modules;
    }

    public int getSequenceCount() {
        return sequenceCount;
    }
}
