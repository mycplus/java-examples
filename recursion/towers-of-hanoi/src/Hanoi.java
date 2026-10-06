import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/** Towers of Hanoi: recursive and iterative solvers, any single move
 *  computed directly, and the exact move count. */
public final class Hanoi {
    private Hanoi() {
    }

    /** One move: disk 1 is the smallest. */
    public record Move(int disk, char from, char to) {
    }

    /** Largest disk count moveAt and solveIterative accept: move numbers
     *  are held in a long, read as an unsigned 64-bit value. */
    public static final int MAX_DISKS = 64;

    /** 2^n - 1, exactly, for any n >= 0. */
    public static BigInteger moveCount(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("negative disk count: " + n);
        }
        return BigInteger.ONE.shiftLeft(n).subtract(BigInteger.ONE);
    }

    /** Recursive solution. Recursion depth is n + 1. */
    public static void solve(int n, char source, char target, char spare,
                             Consumer<Move> visit) {
        if (n < 0) {
            throw new IllegalArgumentException("negative disk count: " + n);
        }
        solveFrom(n, source, target, spare, visit);
    }

    private static void solveFrom(int n, char source, char target, char spare,
                                  Consumer<Move> visit) {
        if (n == 0) {
            return;
        }
        solveFrom(n - 1, source, spare, target, visit);
        visit.accept(new Move(n, source, target));
        solveFrom(n - 1, spare, target, source, visit);
    }

    /** All moves as a list. Limited to 25 disks (33,554,431 moves). */
    public static List<Move> moves(int n, char source, char target, char spare) {
        if (n > 25) {
            throw new IllegalArgumentException("more than 25 disks: " + n);
        }
        List<Move> out = new ArrayList<>();
        solve(n, source, target, spare, out::add);
        return out;
    }

    /** Move k of the n-disk solution, computed from the bits of k.
     *  k runs from 1 to 2^n - 1 and is read as an unsigned 64-bit value,
     *  so for n = 64 the last move is k = -1L (0xFFFF_FFFF_FFFF_FFFF). */
    public static Move moveAt(int n, long k, char source, char target, char spare) {
        if (n < 1 || n > MAX_DISKS || k == 0
                || (n < 64 && Long.compareUnsigned(k, (1L << n) - 1) > 0)) {
            throw new IllegalArgumentException("n or k out of range: n=" + n
                    + ", k=" + Long.toUnsignedString(k));
        }
        // The bit formula moves the tower from peg index 0 to index 2 when n
        // is odd and to index 1 when n is even.
        char[] peg = {source, n % 2 == 1 ? spare : target, n % 2 == 1 ? target : spare};
        // (k | (k - 1)) + 1 wraps to 0 when n == 64; reduce modulo 3 first.
        int from = (int) Long.remainderUnsigned(k & (k - 1), 3);
        int to = (int) ((Long.remainderUnsigned(k | (k - 1), 3) + 1) % 3);
        return new Move(Long.numberOfTrailingZeros(k) + 1, peg[from], peg[to]);
    }

    /** Iterative solution: the same moves as solve(), with no recursion. */
    public static void solveIterative(int n, char source, char target, char spare,
                                      Consumer<Move> visit) {
        if (n < 0 || n > MAX_DISKS) {
            throw new IllegalArgumentException("disk count out of range: " + n);
        }
        if (n == 0) {
            return;
        }
        long last = n == 64 ? -1L : (1L << n) - 1;    // -1L is 2^64 - 1 unsigned
        for (long k = 1; ; k++) {
            visit.accept(moveAt(n, k, source, target, spare));
            if (k == last) {             // not k <= last: last may be 2^64 - 1
                break;
            }
        }
    }
}
