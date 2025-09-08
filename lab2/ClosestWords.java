/* Labb 2 i DD2350 Algoritmer, datastrukturer och komplexitet    */
/* Se labbinstruktionerna i kursrummet i Canvas                  */
/* Ursprunglig författare: Viggo Kann KTH viggo@nada.kth.se      */
import java.util.LinkedList;
import java.util.List;

public class ClosestWords {
  LinkedList<String> closestWords = null;
  int closestDistance = -1;

  int distance(String w1, String w2) {
    int w1len = w1.length();
    int w2len = w2.length();

    // Optimization 1: Use iteration instead of recursion and a matrix for calculating each sub-problem
    int[][] M = new int[w1len + 1][w2len + 1];

    // Base cases: transforming empty string
    for (int i = 0; i <= w1len; ++i) {
      M[i][0] = i; // Delete all i chars from w1
    }
    for (int j = 0; j <= w2len; ++j) {
      M[0][j] = j; // Insert all j chars to get w2
    }

    // Fill the matrix bottom-up
    for (int i = 1; i <= w1len; ++i) {
      for (int j = 1; j <= w2len; ++j) {
        // Check if chars match
        int cost = (w1.charAt(i - 1) == w2.charAt(j - 1)) ? 0 : 1;

        // Three operations: substitute, delete, insert
        int substitute = M[i - 1][j - 1] + cost;
        int delete = M[i - 1][j] + 1;
        int insert = M[i][j - 1] + 1;

        // Take minimum of all three operations
        M[i][j] = Math.min(substitute, Math.min(delete, insert));
      }
    }

    return M[w1len][w2len]; // Final answer is in bottom-right corner
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
