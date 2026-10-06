import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/** Dependency-free tests: run with {@code java -ea -cp build HanoiTest}. */
public final class HanoiTest {
    private static int failures;

    private static void check(boolean ok, String what) {
        if (!ok) {
            System.err.println("CHECK failed: " + what);
            failures++;
        }
    }

    /** Applies moves to three pegs; returns false on the first illegal move
     *  or if the tower is not on C, in order, at the end. */
    private static boolean legalAndSolved(int n, List<Hanoi.Move> moves) {
        List<Deque<Integer>> pegs = List.of(new ArrayDeque<>(), new ArrayDeque<>(),
                new ArrayDeque<>());
        for (int d = n; d >= 1; d--) {
            pegs.get(0).push(d);
        }
        for (Hanoi.Move m : moves) {
            int f = m.from() - 'A';
            int t = m.to() - 'A';
            if (f < 0 || f > 2 || t < 0 || t > 2 || f == t) {
                return false;
            }
            Integer top = pegs.get(f).peek();
            Integer under = pegs.get(t).peek();
            if (top == null || top != m.disk() || (under != null && under < m.disk())) {
                return false;
            }
            pegs.get(t).push(pegs.get(f).pop());
        }
        if (!pegs.get(0).isEmpty() || !pegs.get(1).isEmpty() || pegs.get(2).size() != n) {
            return false;
        }
        int expect = 1;
        for (int d : pegs.get(2)) {          // iterates top to bottom
            if (d != expect++) {
                return false;
            }
        }
        return true;
    }

    /** Independent reference for move k (unsigned): descend the recursion. */
    private static Hanoi.Move reference(int n, long k, char s, char t, char v) {
        while (true) {
            long mid = 1L << (n - 1);          // for n = 64 this is 2^63
            if (k == mid) {
                return new Hanoi.Move(n, s, t);
            }
            if (Long.compareUnsigned(k, mid) < 0) {
                char oldT = t;
                t = v;
                v = oldT;
            } else {
                k -= mid;
                char oldS = s;
                s = v;
                v = oldS;
            }
            n--;
        }
    }

    private static long state = 0x9E3779B97F4A7C15L;

    private static long nextRand() {
        state ^= state << 13;
        state ^= state >>> 7;
        state ^= state << 17;
        return state;
    }

    private static boolean throwsIllegalArgument(Runnable r) {
        try {
            r.run();
        } catch (IllegalArgumentException e) {
            return true;
        }
        return false;
    }

    public static void main(String[] args) throws Exception {
        for (int n = 0; n <= 18; n++) {
            List<Hanoi.Move> rec = Hanoi.moves(n, 'A', 'C', 'B');
            List<Hanoi.Move> it = new ArrayList<>();
            Hanoi.solveIterative(n, 'A', 'C', 'B', it::add);
            check(legalAndSolved(n, rec), "recursive legal and solved, n=" + n);
            check(rec.equals(it), "iterative == recursive, n=" + n);
            check(BigInteger.valueOf(rec.size()).equals(Hanoi.moveCount(n)), "count, n=" + n);
            boolean allMatch = true;
            for (int i = 0; i < rec.size(); i++) {
                allMatch &= Hanoi.moveAt(n, i + 1, 'A', 'C', 'B').equals(rec.get(i));
            }
            check(allMatch, "moveAt matches, n=" + n);
        }

        check(Hanoi.moveCount(64).toString().equals("18446744073709551615"), "2^64 - 1");
        check(Hanoi.moveCount(100).equals(BigInteger.TWO.pow(100).subtract(BigInteger.ONE)),
                "2^100 - 1");
        check(throwsIllegalArgument(() -> Hanoi.moveCount(-1)), "moveCount(-1)");
        check(throwsIllegalArgument(() -> Hanoi.solve(-1, 'A', 'C', 'B', m -> { })), "solve(-1)");
        check(throwsIllegalArgument(() -> Hanoi.moveAt(3, 0, 'A', 'C', 'B')), "moveAt k=0");
        check(throwsIllegalArgument(() -> Hanoi.moveAt(3, 8, 'A', 'C', 'B')), "moveAt k=8");
        check(throwsIllegalArgument(() -> Hanoi.moveAt(65, 1, 'A', 'C', 'B')), "moveAt n=65");
        check(throwsIllegalArgument(() -> Hanoi.solveIterative(65, 'A', 'C', 'B', m -> { })),
                "solveIterative(65)");

        // moveAt for 64 disks: the last move, the 64 moves with
        // k | (k - 1) == 2^64 - 1, and random moves, against the reference.
        List<Long> ks = new ArrayList<>(List.of(1L, -1L));
        for (int j = 0; j < 64; j++) {
            ks.add(-1L - ((1L << j) - 1));       // 2^64 - 2^j, unsigned
        }
        while (ks.size() < 2000) {
            long k = nextRand();
            ks.add(k == 0 ? 1 : k);
        }
        boolean allMatch = true;
        for (long k : ks) {
            allMatch &= Hanoi.moveAt(64, k, 'A', 'C', 'B').equals(reference(64, k, 'A', 'C', 'B'));
        }
        check(allMatch, "moveAt(64, k) against the reference");

        // The listing prints the article's output.
        PrintStream old = System.out;
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        System.setOut(new PrintStream(buf, true, StandardCharsets.UTF_8));
        try {
            TowersOfHanoi.main(new String[0]);
        } finally {
            System.setOut(old);
        }
        String expected = Files.readString(Path.of("tests/expected/TowersOfHanoi.txt"));
        check(buf.toString(StandardCharsets.UTF_8).replace("\r", "").equals(expected.replace("\r", "")),
                "TowersOfHanoi output");

        // The animation's model solves the puzzle and renders without a display.
        HanoiAnimation panel = new HanoiAnimation(6);
        while (panel.step()) {
            // run every move; step() throws on an illegal one
        }
        check(panel.solved() && panel.movesDone() == 63, "animation solves 6 disks");
        File png = File.createTempFile("hanoi", ".png");
        try {
            panel.writePng(png);
            check(png.length() > 0, "animation writes a PNG");
        } finally {
            Files.deleteIfExists(png.toPath());
        }

        if (failures != 0) {
            System.err.println(failures + " check(s) failed");
            System.exit(1);
        }
        System.out.println("all tests passed");
    }
}
