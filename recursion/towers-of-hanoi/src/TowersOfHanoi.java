// TowersOfHanoi.java - the recursive Towers of Hanoi solution in Java 17.
// Compile: javac TowersOfHanoi.java   Run: java TowersOfHanoi
public class TowersOfHanoi {

    /** Moves n disks from source to target, using spare as the third peg,
     *  prints each move, and returns the number of moves made. */
    static long hanoi(int n, char source, char target, char spare) {
        if (n <= 0) {
            return 0;                                   // nothing to move
        }
        long moves = hanoi(n - 1, source, spare, target);
        System.out.println("Move disk " + n + " from " + source + " to " + target);
        moves++;
        moves += hanoi(n - 1, spare, target, source);
        return moves;
    }

    public static void main(String[] args) {
        int n = 3;
        long moves = hanoi(n, 'A', 'C', 'B');
        System.out.println(n + " disks: " + moves + " moves");
    }
}
