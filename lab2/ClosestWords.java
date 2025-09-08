/* Labb 2 i DD2350 Algoritmer, datastrukturer och komplexitet    */
/* Se labbinstruktionerna i kursrummet i Canvas                  */
/* Ursprunglig författare: Viggo Kann KTH viggo@nada.kth.se      */
import java.util.LinkedList;
import java.util.List;

public class ClosestWords {
  LinkedList<String> closestWords = null;
  int closestDistance = -1;

  // OPTIMIZATION 2: Reusable arrays - avoid creating new arrays for every comparison
  private int[] prev, curr;

  int distance(String w1, String w2) {
    int w1len = w1.length();
    int w2len = w2.length();

    // Only allocate new arrays if current ones are too small
    int requiredSize = w2len + 1;
    if (prev == null || prev.length < requiredSize) {
      prev = new int[requiredSize];
      curr = new int[requiredSize];
    }

    // Base case: first row (empty string to w2 prefixes)
    for (int j = 0; j <= w2len; j++) {
      prev[j] = j; // prev[j] represents M[0][j]
    }

    // Compute each row one at a time
    for (int i = 1; i <= w1len; i++) {
      curr[0] = i; // Base case: M[i][0] = i (delete all i chars from w1)

      for (int j = 1; j <= w2len; j++) {
        // Check if characters match
        int cost = (w1.charAt(i - 1) == w2.charAt(j - 1)) ? 0 : 1;

        // Three operations using previous and current rows:
        int substitute = prev[j - 1] + cost;  // M[i-1][j-1] is now prev[j-1]
        int delete = prev[j] + 1;             // M[i-1][j] is now prev[j]
        int insert = curr[j - 1] + 1;         // M[i][j-1] is now curr[j-1]

        // Take minimum of all three operations
        curr[j] = Math.min(substitute, Math.min(delete, insert));
      }

      // Swap arrays: current row becomes previous row for next iteration
      int[] temp = prev;
      prev = curr;
      curr = temp;
    }

    return prev[w2len]; // Final answer is now in prev array (after the swap)
  }

public ClosestWords(String w, List<String> wordList) {
    for (String s : wordList) {
      int dist = distance(w, s);
      if (dist < closestDistance || closestDistance == -1) {
        closestDistance = dist;
        closestWords = new LinkedList<String>();
        closestWords.add(s);
      }
      else if (dist == closestDistance)
        closestWords.add(s);
    }
  }

  int getMinDistance() {
    return closestDistance;
  }

  List<String> getClosestWords() {
    return closestWords;
  }
}
