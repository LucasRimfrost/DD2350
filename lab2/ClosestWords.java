/* Labb 2 i DD2350 Algoritmer, datastrukturer och komplexitet    */
/* Se labbinstruktionerna i kursrummet i Canvas                  */
/* Ursprunglig författare: Viggo Kann KTH viggo@nada.kth.se      */
import java.util.LinkedList;
import java.util.List;

public class ClosestWords {
  LinkedList<String> closestWords = null;

  int closestDistance = -1;

  int partDist(String w1, String w2, int w1len, int w2len) {
    final int cols = w2len + 1;
    int[] M = new int[(w1len + 1) * cols];

    // Base case
    for (int i = 0; i <= w1len; ++i) {
      M[i * cols + 0] = i; // M[i][0] = i
    }
    for (int j = 0; j <= w2len; ++j) {
      M[0 * cols + j] = j; // M[j][0] = j
    }

    // Fill matrix
    for (int i = 1; i <= w1len; i++) {
      for (int j = 1; j <= w2len; j++) {
        int currentIdx = i * cols + j;           // M[i][j]
        int diagIdx = (i-1) * cols + (j-1);     // M[i-1][j-1]
        int upIdx = (i-1) * cols + j;           // M[i-1][j]
        int leftIdx = i * cols + (j-1);         // M[i][j-1]

        int substitution = M[diagIdx] + (w1.charAt(i-1) == w2.charAt(j-1) ? 0 : 1);
        int deletion = M[upIdx] + 1;
        int insertion = M[leftIdx] + 1;

        M[currentIdx] = Math.min(substitution, Math.min(deletion, insertion));
      }
    }

    return M[w1len * cols + w2len];  // M[w1len][w2len]
  }

  int distance(String w1, String w2) {
    return partDist(w1, w2, w1.length(), w2.length());
  }

  public ClosestWords(String w, List<String> wordList) {
    for (String s : wordList) {
      int dist = distance(w, s);
      // System.out.println("d(" + w + "," + s + ")=" + dist);
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
