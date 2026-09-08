package Eidi._123Fastq.TrimFactoryPackage;

import javax.swing.JTextArea;

public class PairingValidator {

    private boolean complainedAlready;
    private long offset;
    private javax.swing.JTextArea messenger;

    public PairingValidator(JTextArea messenger) {
        this.messenger = messenger;
        complainedAlready = false;
        offset = 0;
    }

    private boolean validateNames(String name1, String name2) {
        String canon1 = FastqNamePattern.canonicalize(name1);
        String canon2 = FastqNamePattern.canonicalize(name2);

        if (canon1 != null && canon2 != null) {
            return canon1.equals(canon2);
        }

        String tokens1[] = name1.split(" ");
        String tokens2[] = name2.split(" ");

        String tok1 = tokens1[0];
        String tok2 = tokens2[0];

        if (tok1.length() != tok2.length()) {
            return false;
        }

        int len = tok1.length();

        for (int i = 0; i < len; i++) {
            char ch1 = tok1.charAt(i);
            char ch2 = tok2.charAt(i);
            if ((ch1 != ch2) && (ch1 != '1' || ch2 != '2')) {
                return false;
            }
        }

        return true;
    }

    public boolean validatePair(FastqRecord rec1, FastqRecord rec2) {
        if (rec1 != null) {
            if (rec2 != null) {
                String name1 = rec1.getName();
                String name2 = rec2.getName();

                if (!validateNames(name1, name2)) {
                    if (!complainedAlready) {
                        complainedAlready = true;
                        messenger.append("WARNING: Pair validation failed at record: " + offset + "\n");
                        messenger.append("         Forward read: " + name1 + "\n");
                        messenger.append("         Reverse read: " + name2 + "\n");
                    }
                    return false;
                }
            } else {
                if (!complainedAlready) {
                    complainedAlready = true;
                    String name1 = rec1.getName();
                    messenger.append("WARNING: Pair validation failed at record: " + offset + "\n");
                    messenger.append("         Forward read: " + name1 + "\n");
                    messenger.append("         No more reverse reads\n");
                }
                return false;
            }
        } else if (rec2 != null) {
            if (!complainedAlready) {
                complainedAlready = true;
                String name2 = rec2.getName();
                messenger.append("WARNING: Pair validation failed at record: " + offset + "\n");
                messenger.append("         No more forward reads\n");
                messenger.append("         Reverse read: " + name2 + "\n");
            }
            return false;
        }
        offset++;
        return true;
    }
}
