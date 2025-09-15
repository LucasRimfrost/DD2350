/* Labb 2 i DD2350 Algoritmer, datastrukturer och komplexitet    */
/* Se labbinstruktionerna i kursrummet i Canvas                  */
/* Ursprunglig författare: Viggo Kann KTH viggo@nada.kth.se      */

import java.util.ArrayList;
import java.util.List;

public class ClosestWords {
  private final int minDistance;
  private final List<String> closestWords = new ArrayList<>();

  public ClosestWords(String query, List<String> dictionary) {
    final int m = query.length();
    int bestDistance = Integer.MAX_VALUE;

    // Edge case: distance from empty string to a word is its length
    if (m == 0) {
      for (String word : dictionary) {
        int distance = word.length();
        if (distance < bestDistance) {
          bestDistance = distance;
          closestWords.clear();
          closestWords.add(word);
        } else if (distance == bestDistance) {
          closestWords.add(word);
        }
      }
      this.minDistance = (bestDistance == Integer.MAX_VALUE) ? 0 : bestDistance;
      return;
    }

    // Build 256-entry mask: patternMask[c] has 1-bits where query has char c
    final long[] patternMask = buildPatternMask256(query);

    // Scan dictionary; use length-diff lower bound + in-loop early abandon
    for (int i = 0, n = dictionary.size(); i < n; i++) {
      final String candidate = dictionary.get(i);

      // Lower bound: at least the absolute length difference
      int lengthDifference = candidate.length() - m;
      if (lengthDifference < 0) lengthDifference = -lengthDifference;
      if (lengthDifference > bestDistance) continue;

      int distance = levenshteinBitParallelPruned(query, candidate, patternMask, m, bestDistance);
      if (distance > bestDistance) continue; // pruned path returns bestDistance+1

      if (distance < bestDistance) {
        bestDistance = distance;
        closestWords.clear();
        closestWords.add(candidate); // dictionary is already lexicographically sorted
      } else { // distance == bestDistance
        closestWords.add(candidate);
      }
    }

    this.minDistance = (bestDistance == Integer.MAX_VALUE) ? 0 : bestDistance;
  }

  public int getMinDistance() {
    return minDistance;
  }

  public List<String> getClosestWords() {
    // Already lexicographically ordered because dictionary input is sorted
    return closestWords;
  }

  // ---------- Helpers ----------

  /** Build 256-entry bitmask for the query: patternMask[c] marks all positions of char c. */
  private static long[] buildPatternMask256(String query) {
    final int m = query.length();
    final long[] mask = new long[256];
    long bit = 1L;
    for (int i = 0; i < m; i++, bit <<= 1) {
      mask[query.charAt(i) & 0xFF] |= bit; // å/ä/ö are in Latin-1 -> safe with & 0xFF
    }
    return mask;
  }

  /**
   * Myers 1-word bit-parallel Levenshtein with pruning. Returns the exact distance, or
   * (bestSoFar+1) if it proves it can't beat bestSoFar. Assumes 1 <= m <= 63 (here: m < 40 by
   * problem spec).
   */
  private static int levenshteinBitParallelPruned(
      String query, String text, long[] patternMask, int m, int bestSoFar) {

    // Fast path: equal strings
    if (m == text.length() && query.equals(text)) return 0;

    long Pv = ~0L; // positive bit-vector (all ones)
    long Mv = 0L; // negative bit-vector (all zeros)
    int score = m; // cost from query to empty (m deletions)
    final int n = text.length();

    for (int i = 0; i < n; i++) {
      long Eq = patternMask[text.charAt(i) & 0xFF];

      long Xv = Eq | Mv;
      long Xh = (((Eq & Pv) + Pv) ^ Pv) | Eq;

      long Ph = Mv | ~(Xh | Pv);
      long Mh = Pv & Xh;

      // Branchless update: MSB tells how the last cell changed
      score += (int) ((Ph >>> (m - 1)) & 1L);
      score -= (int) ((Mh >>> (m - 1)) & 1L);

      // Advance to next column
      Ph = (Ph << 1) | 1L;
      Mh <<= 1;

      Pv = Mh | ~(Xv | Ph);
      Mv = Ph & Xv;

      // Early-abandon: even with best-case improvements, can't beat bestSoFar
      if (bestSoFar != Integer.MAX_VALUE) {
        int remaining = n - i - 1;
        int optimistic = score - remaining; // at most -1 per remaining column
        if (optimistic < 0) optimistic = 0;
        if (optimistic > bestSoFar) return bestSoFar + 1;
      }
    }
    return score;
  }
}
