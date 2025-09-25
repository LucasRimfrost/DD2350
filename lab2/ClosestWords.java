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
    for (int i = 0, n = dictionary.size(); i < n; ++i) {
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
    for (int i = 0; i < m; ++i, bit <<= 1) {
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

    // Initialize bit vectors for vertical differences
    long Pv = ~0L; // positive vertical changes: all +1 initially (first column: 0→1→2...)
    long Mv = 0L; // negative vertical changes: none initially
    int score = m; // distance from query to empty string (m deletions)
    final int n = text.length();

    // Process each character in text (each column in DP matrix)
    for (int i = 0; i < n; ++i) {
      // Step 1: Get match positions for current text character
      long Eq = patternMask[text.charAt(i) & 0xFF];

      // Step 2: Create vertical candidates (matches + negative vertical changes)
      long Xv = Eq | Mv;

      // Step 3: Create horizontal candidates through carry propagation
      // First identify strong diagonal candidates (match + positive vertical)
      // Then test via addition with carry, isolate carry effects with XOR
      // Finally combine with original matches to preserve all horizontal signals
      long Xh = (((Eq & Pv) + Pv) ^ Pv) | Eq;

      // Step 4: Calculate actual horizontal changes
      long Ph = Mv | ~(Xh | Pv); // positions getting +1 horizontally
      long Mh = Pv & Xh; // positions getting -1 horizontally

      // Step 5: Update score based on bottom cell change (MSB = last position)
      score += (int) ((Ph >>> (m - 1)) & 1L); // +1 if bottom cell increased
      score -= (int) ((Mh >>> (m - 1)) & 1L); // -1 if bottom cell decreased

      // Step 6: Prepare for next column - shift horizontal changes to become vertical
      Ph = (Ph << 1) | 1L; // shift + set first row (always +1 from left)
      Mh <<= 1; // shift negative horizontal changes

      // Step 7: Calculate new vertical changes for next iteration
      Pv = Mh | ~(Xv | Ph); // positive vertical: from negative horizontal or standard +1
      Mv = Ph & Xv; // negative vertical: from positive horizontal + vertical candidates

      // Early termination: if even optimal remaining steps can't beat bestSoFar
      if (bestSoFar != Integer.MAX_VALUE) {
        int remaining = n - i - 1;
        int optimistic = score - remaining; // assume -1 per remaining column (best case)
        if (optimistic < 0) optimistic = 0;
        if (optimistic > bestSoFar) return bestSoFar + 1;
      }
    }
    return score;
  }
}
