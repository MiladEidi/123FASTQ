package Eidi._123Fastq.BarcodeSplitterPackage;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class aPatternBitapFactory {

    private String PatternName;
    private String PatternSequence5;
    private String PatternSequence3;
    private Map<Character, Long> alphabetMasks5;
    private Map<Character, Long> alphabetMasks3;
    private Set<Character> alphabet;
    private long[] bitArray;
    private int penalty;
    private int hits;

    public aPatternBitapFactory(String PatternName, String Pattern, int penalty, int Mode) {
        this.PatternName = PatternName;
        this.PatternSequence5 = Pattern;
        this.penalty = penalty;
        Character[] n = {'A', 'T', 'C', 'G', 'N'};
        this.alphabet = new HashSet<>(Arrays.asList(n));
        alphabet.add('&');
        this.bitArray = generateBitArray(penalty);
        this.alphabetMasks5 = generateAlphabetMasks();
        if (Mode != 5) {
            this.PatternSequence3 = reverse(Pattern);
            this.alphabetMasks3 = generateAlphabetMasks2();
        }
    }

    public Map.Entry<Integer, Long> bitapFuzzyMatch5Ends(String Read) {
        Read = Read + "&";
        HashMap<Integer, Long> PosBitarray = new HashMap<>();
        for (int i = Read.length() - 1; i >= 0; i--) {
            long[] old = bitArray.clone();
            bitArray[0] = (old[0] << 1) | alphabetMasks5.get(Read.charAt(i));
            if (penalty > 0) {
                for (int k = 1; k <= penalty; k++) {
                    long ins = old[k - 1];
                    long sub = ins << 1;
                    long del = bitArray[k - 1] << 1;
                    long match = (old[k] << 1) | alphabetMasks5.get(Read.charAt(i));
                    bitArray[k] = ins & del & sub & match;
                }
            }
            if (0 == (bitArray[penalty] & (1 << PatternSequence5.length()))) {
                PosBitarray.put(i, bitArray[penalty]);
            }
        }

        if (PosBitarray.size() > 0) { //Best match selection
            Map.Entry<Integer, Long> Best = PosBitarray.entrySet().iterator().next();
            for (Map.Entry<Integer, Long> entry : PosBitarray.entrySet()) {
                //best match and leftmost
                if (entry.getValue() < Best.getValue()) {
                    Best = entry;
                } else if (Objects.equals(entry.getValue(), Best.getValue()) && entry.getKey() < Best.getKey()) {
                    Best = entry;
                }
            }
            return Best;
        } else {
            return null;
        }
    }

    public void Hit() {
        hits += 1;
    }

    public String reverse(String original) {
        return new StringBuilder(original).reverse().toString();
    }

    public Map.Entry<Integer, Long> bitapFuzzyMatch3Ends(String Read) {
        Read = Read + "&";
        HashMap<Integer, Long> PosBitarray = new HashMap<>();
        for (int i = Read.length() - 1; i >= 0; i--) {
            long[] old = bitArray.clone();
            bitArray[0] = (old[0] << 1) | alphabetMasks3.get(Read.charAt(i));
            if (penalty > 0) {
                for (int k = 1; k <= penalty; k++) {
                    long ins = old[k - 1];
                    long sub = ins << 1;
                    long del = bitArray[k - 1] << 1;
                    long match = (old[k] << 1) | alphabetMasks3.get(Read.charAt(i));
                    bitArray[k] = ins & del & sub & match;
                }
            }
            if (0 == (bitArray[penalty] & (1 << PatternSequence3.length()))) {
                PosBitarray.put(i, bitArray[penalty]);
            }
        }

        if (PosBitarray.size() > 0) { //Best match selection
            Map.Entry<Integer, Long> Best = PosBitarray.entrySet().iterator().next();
            for (Map.Entry<Integer, Long> entry : PosBitarray.entrySet()) {
                if (entry.getValue() < Best.getValue()) {
                    Best = entry;
                } else if (Objects.equals(entry.getValue(), Best.getValue()) && entry.getKey() > Best.getKey()) {
                    Best = entry;
                }
            }
            return Best;
        } else {
            return null;
        }
    }

    private long[] generateBitArray(int penalty) {
        long[] bitArrayLocal = new long[penalty + 1];
        for (int k = 0; k <= penalty; k++) {
            bitArrayLocal[k] = ~1;
        }
        return bitArrayLocal;
    }

    private Map<Character, Long> generateAlphabetMasks() {
        Map<Character, Long> masks = new HashMap<>();
        for (Character letter : alphabet) {
            long mask = ~0;
            for (int pos = 0; pos < PatternSequence5.length(); pos++) {
                if (letter.equals(PatternSequence5.charAt(PatternSequence5.length() - 1 - pos))) {
                    mask &= ~(1L << pos);
                }
            }
            masks.put(letter, (mask << 1));
        }
        return masks;
    }

    private Map<Character, Long> generateAlphabetMasks2() {
        Map<Character, Long> masks = new HashMap<>();
        for (Character letter : alphabet) {
            long mask = ~0;
            for (int pos = 0; pos < PatternSequence3.length(); pos++) {
                if (letter.equals(PatternSequence3.charAt(PatternSequence3.length() - 1 - pos))) {
                    mask &= ~(1L << pos);
                }
            }
            masks.put(letter, (mask << 1));
        }
        return masks;
    }

    public String getPatternName() {
        return PatternName;
    }

    public String getPatternSequence5() {
        return PatternSequence5;
    }

    public int getHits() {
        return hits;
    }
}
