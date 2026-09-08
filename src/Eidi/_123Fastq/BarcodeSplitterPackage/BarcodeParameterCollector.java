package Eidi._123Fastq.BarcodeSplitterPackage;

public class BarcodeParameterCollector {

    private String FastqFilePath;
    private String BarcodeFilePath;
    private boolean ClipBarcodes;
    private int penalty;
    private String outputPath;
    private int mode3or5;
    private boolean report;

    public BarcodeParameterCollector(String FastqFilePath, String BarcodeFilePath, boolean ClipBarcodes, int penalty, String outputPath, int mode, boolean makeReport) {
        this.FastqFilePath = FastqFilePath;
        this.BarcodeFilePath = BarcodeFilePath;
        this.ClipBarcodes = ClipBarcodes;
        this.penalty = penalty;
        this.outputPath = outputPath;
        this.mode3or5 = mode;
        this.report = makeReport;
    }

    public String getFastqFilePath() {
        return FastqFilePath;
    }

    public String getBarcodeFilePath() {
        return BarcodeFilePath;
    }

    public boolean isClipBarcodes() {
        return ClipBarcodes;
    }

    public int getPenalty() {
        return penalty;
    }

    public String getOutputPath() {
        return outputPath;
    }

    public int getMode3or5() {
        return mode3or5;
    }

    public boolean makeReport() {
        return report;
    }
}
