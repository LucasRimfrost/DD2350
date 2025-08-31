/* Labb 2 i DD2350 Algoritmer, datastrukturer och komplexitet    */
/* Se labbinstruktionerna i kursrummet i Canvas                  */
/* Ursprunglig författare: Viggo Kann KTH viggo@nada.kth.se      */
import java.util.LinkedList;
import java.util.List;

public class ClosestWords {
  LinkedList<String> closestWords = null;

  int closestDistance = -1;

  int partDist(String w1, String w2, int w1len, int w2len) {
    int[] prev = new int[w2len + 1];  // Föregående rad (rad i-1)
    int[] curr = new int[w2len + 1];  // Nuvarande rad (rad i)

    // Base case: första raden (tom sträng → w2)
    for (int j = 0; j <= w2len; j++) {
        prev[j] = j;  // prev[j] motsvarar M[0][j]
    }

    // Beräkna rad för rad
    for (int i = 1; i <= w1len; i++) {
        curr[0] = i;  // Base case: M[i][0] = i

        for (int j = 1; j <= w2len; j++) {
            // Samma 3 fall som tidigare:
            int substitution = prev[j-1] + (w1.charAt(i-1) == w2.charAt(j-1) ? 0 : 1);
            int deletion = prev[j] + 1;
            int insertion = curr[j-1] + 1;

            curr[j] = Math.min(substitution, Math.min(deletion, insertion));
        }

        // Swap: curr blir prev för nästa iteration
        int[] temp = prev;
        prev = curr;
        curr = temp;
    }

    return prev[w2len];  // Sista raden ligger nu i prev
  }

  int distance(String w1, String w2) {
    return partDist(w1, w2, w1.length(), w2.length());
  }

  public ClosestWords(String w, List<String> wordList) {
    closestWords = new LinkedList<String>();

    for (String s : wordList) {
      int dist = distance(w, s);

      if (dist == 0) {
        closestDistance = 0;
        closestWords.clear();
        closestWords.add(s);
        return;
      }

      if (dist < closestDistance || closestDistance == -1) {
        closestDistance = dist;
        closestWords.clear();
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
