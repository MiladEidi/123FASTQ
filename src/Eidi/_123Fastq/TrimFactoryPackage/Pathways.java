package Eidi._123Fastq.TrimFactoryPackage;

import java.io.File;

/**
 * @author MiladAD
 */

public class Pathways {

    private String importPath1;
    private String importPath2;
    private String exportPath;
    private Boolean pairValidator;

    //For SE Mode
    public Pathways(String importPath1, String exportPath) {
        this.importPath1 = importPath1;
        this.exportPath = exportPath;
    }

    //For PE Mode
    public Pathways(String importPath1, String importPath2, String exportPath, boolean pairValidator) {
        this.importPath1 = importPath1;
        this.importPath2 = importPath2;
        this.exportPath = exportPath;
        this.pairValidator = pairValidator;
    }

    public String getExportPath() {
        return exportPath;
    }

    public String getImportPath1() {
        return importPath1;
    }

    public String getImportPath2() {
        return importPath2;
    }

    public String getFP() {
        return exportPath + "/" + calculateTemplatedOutput(new File(importPath1).getName(), "_ForwardPairs");
    }

    public String getFU() {
        return exportPath + "/" + calculateTemplatedOutput(new File(importPath1).getName(), "_ForwardUnpairs");
    }

    public String getRP() {
        return exportPath + "/" + calculateTemplatedOutput(new File(importPath2).getName(), "_ReversePairs");
    }

    public String getRU() {
        return exportPath + "/" + calculateTemplatedOutput(new File(importPath2).getName(), "_ReverseUnpairs");
    }

    public boolean getPairValidator() {
        return pairValidator;
    }

        private String calculateTemplatedOutput(String fileName, String filetype) {
        int extSplit = getFileExtensionIndex(fileName);
        String core = fileName.substring(0, extSplit);
        String exts = fileName.substring(extSplit);
        String[] suffixes = {"_R1_","_R2_","_f","_r",".f",".r","_2","_1",".2",".1"};
        for (int i = 0; i < suffixes.length; i++) {
            String suffix = suffixes[i];
            int x = core.lastIndexOf(suffix);
            if (x != -1) {
                System.out.println(core);
                core = core.substring(0, x) + core.substring(x + suffix.length(), core.length());
                System.out.println(core);
                break;
            }
        }
        return core + filetype + exts;
    }

    private int getFileExtensionIndex(String str) {
        String extensions[] = {".fq", ".fastq", ".txt", ".gz", ".bz2", ".zip"};
        String tmp = str;
        boolean done = false;
        while (!done) {
            done = true;
            for (String ext : extensions) {
                if (tmp.endsWith(ext)) {
                    tmp = tmp.substring(0, tmp.length() - ext.length());
                    done = false;
                }
            }
        }
        return tmp.length();
    }

}
