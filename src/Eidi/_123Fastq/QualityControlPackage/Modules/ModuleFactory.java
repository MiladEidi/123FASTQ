package Eidi._123Fastq.QualityControlPackage.Modules;

public class ModuleFactory {

    public static QCModule[] getStandardModuleList(String name) {

        OverRepresentedSeqs os = new OverRepresentedSeqs();

        QCModule[] module_list = new QCModule[]{
            new BasicStats(name),
            new PerBaseQualityScores(),
            new DistributionOfMeanQualitiesPerReadLengths(),
            new PerSequenceQualityScores(),
            new PerBaseSequenceContent(),
            new PerSequenceGCContent(),
            new NContent(),
            new SequenceLengthDistribution(),
            os.duplicationLevelModule(),
            os,
            new AdapterContent(),
            new PerTileQualityScores(),
            new KmerContent()};

        return (module_list);
    }
}
