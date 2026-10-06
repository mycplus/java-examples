// ShiftOverflow.java - DO NOT COPY. 2^n - 1 computed with a shift. Java masks
// the shift count to its low 5 bits for int (6 bits for long), so 1 << 32
// is 1 and the "move count" for 32 disks is 0. No exception, no warning.
public class ShiftOverflow {
    public static void main(String[] args) {
        for (int n : new int[] {3, 30, 31, 32, 33}) {
            int moves = (1 << n) - 1;
            System.out.println(n + " disks: " + moves + " moves (int)");
        }
        for (int n : new int[] {63, 64}) {
            long moves = (1L << n) - 1;
            System.out.println(n + " disks: " + moves + " moves (long)");
        }
    }
}
