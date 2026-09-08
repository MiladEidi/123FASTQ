package Eidi._123Fastq.TrimFactoryPackage;

import javax.swing.JTextArea;

public class MaximumInformationTrimmer extends AbstractSingleRecordTrimmer {

    public static final int LONGEST_READ = 1000;
    public static final int MAXQUAL = 60;

    private Integer parLength;
    private Float strictness;

    private double[] lengthScoreTmp;
    private double[] qualProbTmp;

    private long[] lengthScore;
    private long[] qualProb;

    public MaximumInformationTrimmer(Integer targetLength, Float strinctness) {

        parLength = targetLength;
        strictness = strinctness;

        lengthScoreTmp = new double[LONGEST_READ];
        for (int i = 0; i < LONGEST_READ; i++) {
            // Unique weighting is logistic function, on difference between length and par length
            double pow1 = Math.exp(parLength - i - 1);    /////if parLength equals or less than i>>under 1...
            //double unique=1.0/(1.0+pow1);
            double unique = Math.log(1.0 / (1.0 + pow1));     ////if parLength equals or less than i>> bozorgo bozorgtar mishe ama bazam be 0 nemiirese..

            // Coverage weighting is length, diluted by strictness
            //double coverage=Math.pow(i+1,1-strictness);
            double coverage = Math.log(i + 1) * (1 - strictness);

            lengthScoreTmp[i] = unique + coverage; //add score to each read..
        }

        qualProbTmp = new double[MAXQUAL + 1];

        for (int i = 0; i < qualProbTmp.length; i++) {
            // Quality weighting is probability of correctness, depending on strictness
            //qualProb[i]=Math.pow(1-Math.pow(0.1, (0.5+i)/10.0),strictness);
            qualProbTmp[i] = Math.log(1 - Math.pow(0.1, (0.5 + i) / 10.0)) * strictness;
        }

        double normRatio = Math.max(
                calcNormalization(lengthScoreTmp, LONGEST_READ * 2),
                calcNormalization(qualProbTmp, LONGEST_READ * 2));

        lengthScore = normalize(lengthScoreTmp, normRatio);
        qualProb = normalize(qualProbTmp, normRatio);

    }

    public long[] getLengthScore() {
        return lengthScore;
    }

    public long[] getQualProb() {
        return qualProb;
    }

    private static double calcNormalization(double array[], int margin) {
        double maxVal = array[0];

        for (int i = 1; i < array.length; i++) {
            double val = Math.abs(array[i]);
            if (val > maxVal) {
                maxVal = val;
            }
        }

        return Long.MAX_VALUE / (maxVal * margin);
    }

    private static long[] normalize(double array[], double ratio) {
        long out[] = new long[array.length];

        for (int i = 0; i < array.length; i++) {
            out[i] = (long) (array[i] * ratio);
        }

        return out;
    }

    @Override
    public FastqRecord processRecord(FastqRecord in, JTextArea messenger) {
        int quals[] = in.getQualityAsInteger(true);
        long accumQuality = 0;
        double maxScore = -Double.MAX_VALUE;
        int maxScorePosition = 0;
        for (int i = 0; i < quals.length; i++) {
            int q = quals[i];
            if (q < 0) {
                q = 0;
            } else if (q > MAXQUAL) {
                q = MAXQUAL;
            }
            accumQuality += qualProb[q];
            long ls = lengthScore[i];
            long score = ls + accumQuality;
            if (score >= maxScore) {
                maxScore = score;
                maxScorePosition = i + 1;
            }
        }
        if (maxScorePosition < 1 || maxScore == 0.0) {
            return null;
        }
        if (maxScorePosition < quals.length) {
            return new FastqRecord(in, 0, maxScorePosition, messenger);
        }
        return in;
    }
}
