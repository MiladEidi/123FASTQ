package Eidi._123Fastq.TrimFactoryPackage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.swing.JTextArea;

public class TrimmerMaker {

    private static final int DEFAULT_MIN_ADAPTER_LEN = 8;

    private boolean PEmode;
    private JTextArea messenger;
    private int SelectedPhred;
    private String AdapterSeq;
    private boolean rc;
    private Integer headCrop;
    private Integer endCrop;
    private Integer readLenEndCropThreshold;
    private Integer endGRepeat;
    private Integer endRepeat;
    private Integer leading;
    private Integer trailing;
    private Integer eachWindowLen;
    private Float meanQualEachWindow;
    private String AdapterFileName;
    private Integer seedMaxMiss;
    private Integer minAdapterLen;
    private Integer minSequenceLikelihood;
    private Integer minPalindromeLikelihood;
    private Boolean keepBoth;
    private List<AdapterTrimmer.IlluminaPrefixPair> prefixPairs;
    private Set<AdapterTrimmer.IlluminaClippingSeq> forwardSeqs;
    private Set<AdapterTrimmer.IlluminaClippingSeq> reverseSeqs;
    private Set<AdapterTrimmer.IlluminaClippingSeq> commonSeqs;
    private Integer targetLen;
    private Float strictness;
    private Integer maxLen;
    private Integer minLen;
    private Integer avgQual;
    private Integer MaxGC;
    private Integer MinGC;
    private Integer phredConvertor;
    private int threadNO;
    private boolean ReportSheet;
    private boolean DeleteComments;

    public TrimmerMaker(boolean PE,
            JTextArea messenger,
            String AdapterSeq,
            boolean rc,
            int PhredSelected,
            Integer headCrop,
            Integer endCrop,
            Integer readLenEndCropThreshold,
            Integer EndGRepeat,
            Integer endRepeat,
            Integer leading,
            Integer trailing,
            Integer eachWindowLen,
            Float meanQualEachWindow,
            String adapterFileName,
            Integer seedMaxMis,
            Integer minPalindromeLikelihood,
            Integer minSequenceLikelihood,
            Integer minAdapterLen,
            Boolean keepBoth,
            List<AdapterTrimmer.IlluminaPrefixPair> PrefixPairs,
            Set<AdapterTrimmer.IlluminaClippingSeq> ForwardSeqs,
            Set<AdapterTrimmer.IlluminaClippingSeq> ReverseSeqs,
            Set<AdapterTrimmer.IlluminaClippingSeq> CommonSeqs,
            Integer TargetLen,
            Float Strictness,
            Integer maxLen,
            Integer minLen,
            Integer avgQual,
            Integer MaxGC,
            Integer MinGC,
            Integer phredConvertor,
            int threadNO,
            boolean reportSheet,
            boolean deleteComments)
            throws IOException {

        this.PEmode = PE;
        this.messenger = messenger;
        this.SelectedPhred = PhredSelected;
        this.AdapterSeq = AdapterSeq;
        this.rc = rc;
        this.headCrop = headCrop;
        this.endCrop = endCrop;
        this.readLenEndCropThreshold = readLenEndCropThreshold;
        this.endGRepeat = EndGRepeat;
        this.endRepeat = endRepeat;
        this.leading = leading;
        this.trailing = trailing;
        this.eachWindowLen = eachWindowLen;
        this.meanQualEachWindow = meanQualEachWindow;
        this.seedMaxMiss = seedMaxMis;
        this.minSequenceLikelihood = minSequenceLikelihood;
        this.AdapterFileName = adapterFileName;
        this.minPalindromeLikelihood = minPalindromeLikelihood;
        this.minAdapterLen = minAdapterLen;
        this.keepBoth = keepBoth;
        this.prefixPairs = PrefixPairs;
        this.forwardSeqs = ForwardSeqs;
        this.reverseSeqs = ReverseSeqs;
        this.commonSeqs = CommonSeqs;
        this.targetLen = TargetLen;
        this.strictness = Strictness;
        this.maxLen = maxLen;
        this.minLen = minLen;
        this.avgQual = avgQual;
        this.MaxGC = MaxGC;
        this.MinGC = MinGC;
        this.phredConvertor = phredConvertor;
        this.threadNO = threadNO;
        this.ReportSheet = reportSheet;
        this.DeleteComments = deleteComments;

    }

    public Integer getReadLenEndCropThreshold() {
        return readLenEndCropThreshold;
    }

    public String getAdapterSeq() {
        return AdapterSeq;
    }

    public int getSelectedPhred() {
        return SelectedPhred;
    }

    public int getThreadNO() {
        return threadNO;
    }

    public boolean isReportSheet() {
        return ReportSheet;
    }

    public Integer getHeadCrop() {
        return headCrop;
    }

    public Integer getEndCrop() {
        return endCrop;
    }

    public Integer getEndGRepeat() {
        return endGRepeat;
    }

    public Integer getEndRepeat() {
        return endRepeat;
    }

    public Integer getLeading() {
        return leading;
    }

    public Integer getTrailing() {
        return trailing;
    }

    public Integer getEachWindowLen() {
        return eachWindowLen;
    }

    public Float getMeanQualEachWindow() {
        return meanQualEachWindow;
    }

    public String getAdapterFileName() {
        return AdapterFileName;
    }

    public Integer getSeedMaxMiss() {
        return seedMaxMiss;
    }

    public Integer getMinAdapterLen() {
        if (AdapterFileName != null && minAdapterLen == null) {
            return DEFAULT_MIN_ADAPTER_LEN;
        }
        return minAdapterLen;
    }

    public Integer getMinSequenceLikelihood() {
        return minSequenceLikelihood;
    }

    public Integer getMinPalindromeLikelihood() {
        return minPalindromeLikelihood;
    }

    public Boolean getKeepBoth() {
        return keepBoth;
    }

    public Integer getTargetLen() {
        return targetLen;
    }

    public Float getStrictness() {
        return strictness;
    }

    public Integer getMaxLen() {
        return maxLen;
    }

    public Integer getMinLen() {
        return minLen;
    }

    public Integer getAvgQual() {
        return avgQual;
    }

    public Integer getMaxGC() {
        return MaxGC;
    }

    public Integer getMinGC() {
        return MinGC;
    }

    public Integer getPhredConvertor() {
        return phredConvertor;
    }

    public boolean getDeleteComments() {
        return DeleteComments;
    }


    public Trimmer[] Trimmers() {

        List<Trimmer> steps = new ArrayList<>();
        if (AdapterSeq != null) {
            //reverse complement consideration in two conditions
            if (PEmode && rc) {
                String AdapterSeq2 = FastaRecord.getRCSequence(AdapterSeq);
                steps.add(new AdapterRemover(AdapterSeq, AdapterSeq2));
            } else {
                steps.add(new AdapterRemover(AdapterSeq));
            }
        }
        if (seedMaxMiss != null) {
            int effectiveMinAdapterLen = minAdapterLen == null ? DEFAULT_MIN_ADAPTER_LEN : minAdapterLen;
            boolean effectiveKeepBoth = keepBoth == null ? false : keepBoth;
            steps.add(new AdapterTrimmer(messenger, seedMaxMiss,
                    minPalindromeLikelihood, minSequenceLikelihood,
                    effectiveMinAdapterLen,
                    effectiveKeepBoth,
                    prefixPairs, forwardSeqs, reverseSeqs, commonSeqs));
        }
        if (headCrop != null) {
            steps.add(new HeadCropTrimmer(headCrop));
        }
        if (endCrop != null) {
            steps.add(new EndCropTrimmer(endCrop, readLenEndCropThreshold));
        }
        if (endGRepeat != null) {
            steps.add(new EndGRepeatsTrimmer(endGRepeat));
        }
        if (endRepeat != null) {
            steps.add(new EndComplexityTrimmer(endRepeat));
        }
        if (leading != null) {
            steps.add(new LeadingTrimmer(leading));
        }
        if (trailing != null) {
            steps.add(new TrailingTrimmer(trailing));
        }
        if (eachWindowLen != null) {
            steps.add(new SlidingWindowTrimmer(eachWindowLen, meanQualEachWindow));
        }
        if (targetLen != null) {
            steps.add(new MaximumInformationTrimmer(targetLen, strictness));
        }
        if (maxLen != null) {
            steps.add(new MaxLenTrimmer(maxLen));
        }
        if (minLen != null) {
            steps.add(new MinLenTrimmer(minLen));
        }
        if (avgQual != null) {
            steps.add(new AvgQualTrimmer(avgQual));
        }
        if (MaxGC != null || MinGC != null) {
            steps.add(new GCContentTrimmer(MaxGC, MinGC));
        }
        if (phredConvertor != null) {
            if (phredConvertor == 33) {
                steps.add(new ToPhred33Trimmer());
            } else if (phredConvertor == 64) {
                steps.add(new ToPhred64Trimmer());
            }
        }
        Trimmer trimmers[] = steps.toArray(new Trimmer[0]);
        return trimmers;
    }
}
