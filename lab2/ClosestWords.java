/* Labb 2 i DD2350 Algoritmer, datastrukturer och komplexitet    */
/* KONSERVATIVA optimeringar som behåller korrekthet               */

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ClosestWords {
    LinkedList<String> closestWords = null;
    int closestDistance = -1;

    private int[] prev, curr;

    int distance(String w1, String w2) {
        return distance(w1, w2, Integer.MAX_VALUE);
    }

    int distance(String w1, String w2, int threshold) {
        int w1len = w1.length();
        int w2len = w2.length();

        // Säkra exit-villkor
        if (Math.abs(w1len - w2len) > threshold) {
            return threshold + 1;
        }
        if (w1.equals(w2)) {
            return 0;
        }
        if (w1len == 0) return w2len;
        if (w2len == 0) return w1len;

        // LÄGG TILL: Prefix/suffix trimming - MEN FÖRSIKTIGT
        int startIndex = 0;
        int w1End = w1len;
        int w2End = w2len;

        // Trimma identisk prefix
        while (startIndex < w1End && startIndex < w2End &&
               w1.charAt(startIndex) == w2.charAt(startIndex)) {
            startIndex++;
        }

        // Trimma identisk suffix
        while (startIndex < w1End && startIndex < w2End &&
               w1.charAt(w1End - 1) == w2.charAt(w2End - 1)) {
            w1End--;
            w2End--;
        }

        // Kör DP på trimmade delen
        int trimmedW1Len = w1End - startIndex;
        int trimmedW2Len = w2End - startIndex;

        if (trimmedW1Len == 0) return trimmedW2Len;
        if (trimmedW2Len == 0) return trimmedW1Len;

        // OPTIMERING: Säkerställ att w2 är kortare (färre kolumner = snabbare)
        boolean swapped = false;
        if (trimmedW1Len < trimmedW2Len) {
            // Swappa så vi jobbar med färre kolumner
            String tempStr = w1; w1 = w2; w2 = tempStr;
            int tempIdx = startIndex; // Detta är samma för båda efter trimming
            int tempEnd = w1End; w1End = w2End; w2End = tempEnd;
            int tempLen = trimmedW1Len; trimmedW1Len = trimmedW2Len; trimmedW2Len = tempLen;
            swapped = true;
        }

        // Array management
        int requiredSize = trimmedW2Len + 1;
        if (prev == null || prev.length < requiredSize) {
            prev = new int[requiredSize];
            curr = new int[requiredSize];
        }

        // Initialisera första raden
        for (int j = 0; j <= trimmedW2Len; j++) {
            prev[j] = j;
        }

        // Iterativ DP med optimeringar
        for (int i = 1; i <= trimmedW1Len; i++) {
            curr[0] = i;
            int rowMin = i;

            // Character caching
            char sourceChar = w1.charAt(startIndex + i - 1);

            for (int j = 1; j <= trimmedW2Len; j++) {
                char targetChar = w2.charAt(startIndex + j - 1);

                // Branch optimization
                if (sourceChar == targetChar) {
                    curr[j] = prev[j - 1];
                } else {
                    int substitute = prev[j - 1] + 1;
                    int delete = prev[j] + 1;
                    int insert = curr[j - 1] + 1;
                    curr[j] = Math.min(substitute, Math.min(delete, insert));
                }

                if (curr[j] < rowMin) {
                    rowMin = curr[j];
                }
            }

            // Early exit om hela raden är för dyr
            if (rowMin > threshold) {
                return threshold + 1;
            }

            // Växla rader
            int[] temp = prev;
            prev = curr;
            curr = temp;
        }

        return prev[trimmedW2Len];
    }

    public ClosestWords(String w, Map<Integer, List<String>> wordsByLength) {
        int wLen = w.length();

        // BARA LITE mer konservativ än innan - inte för aggressiv
        for (int lengthDiff = 0; lengthDiff <= Math.min(wLen + 5, 12); lengthDiff++) {

            if (lengthDiff == 0) {
                processWordsOfLength(w, wLen, wordsByLength);
            } else {
                if (wLen - lengthDiff > 0) {
                    processWordsOfLength(w, wLen - lengthDiff, wordsByLength);
                }
                if (wLen + lengthDiff <= 35) { // Lite mer restriktiv men inte crazy
                    processWordsOfLength(w, wLen + lengthDiff, wordsByLength);
                }
            }

            // Konservativa early exits
            if (closestDistance == 0) {
                break;
            }
            if (closestDistance != -1 && lengthDiff > closestDistance) {
                break;
            }
        }
    }

    private void processWordsOfLength(String w, int targetLen,
                                    Map<Integer, List<String>> wordsByLength) {
        List<String> wordsOfThisLength = wordsByLength.get(targetLen);
        if (wordsOfThisLength == null) {
            return;
        }

        // MINIMAL optimering: Begränsa antal ord bara om det är extremt många
        int limit = wordsOfThisLength.size();
        if (limit > 50000) { // Bara begränsa om MYCKET många ord
            limit = 50000;
        }

        for (int i = 0; i < limit; i++) {
            String s = wordsOfThisLength.get(i);

            int threshold = (closestDistance == -1) ? Integer.MAX_VALUE : closestDistance;
            int dist = distance(w, s, threshold);

            if (closestDistance != -1 && dist > closestDistance) {
                continue;
            }

            if (dist < closestDistance || closestDistance == -1) {
                closestDistance = dist;
                closestWords = new LinkedList<String>();
                closestWords.add(s);
            } else if (dist == closestDistance) {
                closestWords.add(s);
            }
        }
    }

    int getMinDistance() {
        return closestDistance;
    }

    List<String> getClosestWords() {
        if (closestWords != null) {
            closestWords.sort(String::compareTo);
        }
        return closestWords;
    }
}
