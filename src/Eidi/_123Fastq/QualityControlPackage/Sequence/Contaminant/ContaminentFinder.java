package Eidi._123Fastq.QualityControlPackage.Sequence.Contaminant;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Vector;

public class ContaminentFinder {

    private static Contaminant[] contaminants;

    public static ContaminantHit findContaminantHit(String sequence) {
        if (contaminants == null) {
            contaminants = makeContaminantList();
        }

        ContaminantHit bestHit = null;

        for (int c = 0; c < contaminants.length; c++) {
            ContaminantHit thisHit = contaminants[c].findMatch(sequence);
            if (thisHit == null) {
                continue; // No hit
            }
            if (bestHit == null || thisHit.length() > bestHit.length()) {
                bestHit = thisHit;
            }

        }

        return bestHit;

    }

    private static Contaminant[] makeContaminantList() {

        Vector<Contaminant> c = new Vector<>();

        InputStream rsrc;
        try {
            BufferedReader br = null;

////For Compiled Version
            try {
                String filesDirectory = (new File(ContaminentFinder.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParent() + "/").replace("%20", " ");
                File contaminations = new File(filesDirectory + "dependencies/contaminant_list.txt");
                rsrc = new FileInputStream(contaminations.getAbsolutePath());
            } catch (Exception e) {
                rsrc = ContaminentFinder.class.getResourceAsStream("/Eidi/_123Fastq/QualityControlPackage/QC_Configs/contaminant_list.txt");
            }
            if (rsrc == null) {
                //ADD HERE PROGBAR TEXT
                throw new FileNotFoundException("cannot find \"contaminant_list.txt\" file.");
            }
            br = new BufferedReader(new InputStreamReader(rsrc));

            String line;
            while ((line = br.readLine()) != null) {

                if (line.startsWith("#")) {
                    continue; // Skip comments
                }
                if (line.trim().length() == 0) {
                    continue; // Skip blank lines
                }
                String[] sections = line.split("\\t+");
                if (sections.length != 2) {
                    System.err.println("Expected 2 sections for contaminant line but got " + sections.length + " from " + line);
                    continue;
                }
                Contaminant con = new Contaminant(sections[0], sections[1]);
                c.add(con);
            }

            br.close();
        } catch (IOException e) {
            // contaminant file unreadable — list stays empty
        }

        return c.toArray(new Contaminant[0]);
    }
}
