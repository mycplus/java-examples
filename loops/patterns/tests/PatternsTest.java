import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Checks the Java patterns against the same expected files as the C version,
 * then checks the shape rules for every size, and Pascal's entries against
 * the addition rule. Run: java -ea -cp build PatternsTest tests/expected
 */
public final class PatternsTest {
    private static int checks;
    private static int failures;

    private PatternsTest() {
    }

    private static void check(boolean ok, String what) {
        checks++;
        if (!ok) {
            failures++;
            if (failures <= 20) {
                System.err.println("FAILED: " + what);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        Path expected = Path.of(args.length > 0 ? args[0] : "tests/expected");

        // 1. Byte-for-byte agreement with the C programs' output.
        for (Patterns.Pattern p : Patterns.ALL) {
            String want = Files.readString(expected.resolve(p.name() + ".txt"), StandardCharsets.UTF_8).replace("\r", "");
            check(p.draw().apply(4).equals(want), p.name() + " 4 matches the C output");
        }
        check(Patterns.pascal(7).equals(Files.readString(expected.resolve("pascal-7.txt")).replace("\r", "")), "pascal 7");
        check(Patterns.floyd(5).equals(Files.readString(expected.resolve("floyd-5.txt")).replace("\r", "")), "floyd 5");

        // 2. Shape rules at every size: no trailing spaces; pyramid and diamond geometry.
        for (Patterns.Pattern p : Patterns.ALL) {
            for (int n = 1; n <= p.maxN(); n++) {
                String[] lines = p.draw().apply(n).split("\n");
                for (String line : lines) {
                    check(!line.isEmpty() && !line.endsWith(" "), p.name() + " " + n + ": no trailing spaces");
                }
                if (p.name().equals("pyramid")) {
                    check(lines.length == n, "pyramid rows");
                    for (int i = 1; i <= n; i++) {
                        check(lines[i - 1].equals(" ".repeat(n - i) + "*".repeat(2 * i - 1)), "pyramid row " + i);
                    }
                }
                if (p.name().equals("diamond")) {
                    check(lines.length == 2 * n - 1, "diamond rows");
                    for (int r = 0; r < lines.length; r++) {
                        check(lines[r].equals(lines[lines.length - 1 - r]), "diamond symmetric");
                    }
                }
            }
        }

        // 3. Pascal's entries against the addition rule, up to the last row a long can hold.
        long[] prev = new long[70];
        long[] row = new long[70];
        for (int r = 0; r <= 66; r++) {
            for (int k = 0; k <= r; k++) {
                row[k] = k == 0 || k == r ? 1 : prev[k - 1] + prev[k];
                check(Patterns.pascalEntry(r, k) == row[k], "pascal(" + r + ", " + k + ")");
            }
            System.arraycopy(row, 0, prev, 0, 70);
        }
        check(Patterns.pascalEntry(67, 33) == -1, "row 67 does not fit in a long: reported, not wrapped");

        if (failures > 0) {
            System.out.println(failures + " of " + checks + " checks failed");
            System.exit(1);
        }
        System.out.println("all " + checks + " checks passed");
    }
}
