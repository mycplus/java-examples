import java.util.List;
import java.util.function.IntFunction;

/**
 * The same 16 patterns as the C version, each returning its lines as a
 * String. Lines end with "\n" on every platform and never with spaces, so
 * the output matches the C programs byte for byte.
 */
public final class Patterns {
    private Patterns() {
    }

    /** A named pattern, its largest sensible size and a short description. */
    public record Pattern(String name, IntFunction<String> draw, int maxN, String about) {
    }

    public static final List<Pattern> ALL = List.of(
            new Pattern("square", Patterns::square, 50, "n x n block of stars"),
            new Pattern("hollow-square", Patterns::hollowSquare, 50, "only the border of the square"),
            new Pattern("right-triangle", Patterns::rightTriangle, 50, "half pyramid: 1, 2, 3 ... stars"),
            new Pattern("inverted-right-triangle", Patterns::invertedRightTriangle, 50, "n, n-1 ... 1 stars"),
            new Pattern("mirrored-right-triangle", Patterns::mirroredRightTriangle, 50, "half pyramid aligned to the right"),
            new Pattern("pyramid", Patterns::pyramid, 50, "centered: 1, 3, 5 ... stars"),
            new Pattern("inverted-pyramid", Patterns::invertedPyramid, 50, "pyramid upside down"),
            new Pattern("hollow-pyramid", Patterns::hollowPyramid, 50, "outline of the pyramid"),
            new Pattern("diamond", Patterns::diamond, 50, "pyramid + inverted pyramid, 2n - 1 rows"),
            new Pattern("hollow-diamond", Patterns::hollowDiamond, 50, "outline of the diamond"),
            new Pattern("hourglass", Patterns::hourglass, 50, "inverted pyramid + pyramid"),
            new Pattern("number-triangle", Patterns::numberTriangle, 9, "1 / 1 2 / 1 2 3"),
            new Pattern("repeated-number-triangle", Patterns::repeatedNumberTriangle, 9, "1 / 2 2 / 3 3 3"),
            new Pattern("floyd", Patterns::floyd, 30, "Floyd's triangle: consecutive numbers"),
            new Pattern("pascal", Patterns::pascal, 20, "Pascal's triangle: binomial coefficients"),
            new Pattern("alphabet-triangle", Patterns::alphabetTriangle, 26, "A / AB / ABC"));

    public static Pattern find(String name) {
        for (Pattern p : ALL) {
            if (p.name().equals(name)) {
                return p;
            }
        }
        return null;
    }

    // ---- Squares and triangles ----

    static String square(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            sb.append("*".repeat(n)).append('\n');
        }
        return sb.toString();
    }

    static String hollowSquare(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= n; j++) {
                sb.append(i == 1 || i == n || j == 1 || j == n ? '*' : ' ');
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    static String rightTriangle(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= n; i++) {
            sb.append("*".repeat(i)).append('\n');
        }
        return sb.toString();
    }

    static String invertedRightTriangle(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = n; i >= 1; i--) {
            sb.append("*".repeat(i)).append('\n');
        }
        return sb.toString();
    }

    static String mirroredRightTriangle(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= n; i++) {
            sb.append(" ".repeat(n - i)).append("*".repeat(i)).append('\n');
        }
        return sb.toString();
    }

    // ---- Pyramids and diamonds ----

    /** Row i of a centered pyramid of height n: n - i spaces, then 2i - 1 stars. */
    static String pyramidRow(int n, int i) {
        return " ".repeat(n - i) + "*".repeat(2 * i - 1) + "\n";
    }

    static String pyramid(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= n; i++) {
            sb.append(pyramidRow(n, i));
        }
        return sb.toString();
    }

    static String invertedPyramid(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = n; i >= 1; i--) {
            sb.append(pyramidRow(n, i));
        }
        return sb.toString();
    }

    static String hollowPyramid(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= n; i++) {
            sb.append(" ".repeat(n - i));
            for (int k = 1; k <= 2 * i - 1; k++) {
                sb.append(k == 1 || k == 2 * i - 1 || i == n ? '*' : ' ');
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    /** A pyramid of n rows over an inverted pyramid of n - 1: the widest row once. */
    static String diamond(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= n; i++) {
            sb.append(pyramidRow(n, i));
        }
        for (int i = n - 1; i >= 1; i--) {
            sb.append(pyramidRow(n, i));
        }
        return sb.toString();
    }

    private static String hollowDiamondRow(int n, int i) {
        StringBuilder sb = new StringBuilder(" ".repeat(n - i));
        for (int k = 1; k <= 2 * i - 1; k++) {
            sb.append(k == 1 || k == 2 * i - 1 ? '*' : ' ');
        }
        return sb.append('\n').toString();
    }

    static String hollowDiamond(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= n; i++) {
            sb.append(hollowDiamondRow(n, i));
        }
        for (int i = n - 1; i >= 1; i--) {
            sb.append(hollowDiamondRow(n, i));
        }
        return sb.toString();
    }

    static String hourglass(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = n; i >= 1; i--) {
            sb.append(pyramidRow(n, i));
        }
        for (int i = 2; i <= n; i++) {
            sb.append(pyramidRow(n, i));
        }
        return sb.toString();
    }

    // ---- Number and letter patterns ----

    static String numberTriangle(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= i; j++) {
                sb.append(j == 1 ? "" : " ").append(j);
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    static String repeatedNumberTriangle(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= i; j++) {
                sb.append(j == 1 ? "" : " ").append(i);
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    static String floyd(int n) {
        int width = String.valueOf(n * (n + 1) / 2).length();
        int next = 1;
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= i; j++) {
                sb.append(j == 1 ? "" : " ").append(String.format("%" + width + "d", next++));
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    private static long gcd(long a, long b) {
        while (b != 0) {
            long t = a % b;
            a = b;
            b = t;
        }
        return a;
    }

    /**
     * Entry k of row r of Pascal's triangle. Java's long is signed, so this
     * is exact up to row 66; beyond that it returns -1 instead of a wrong value.
     */
    public static long pascalEntry(int r, int k) {
        if (k < 0 || k > r) {
            return 0;
        }
        k = Math.min(k, r - k);
        long c = 1;
        for (int j = 1; j <= k; j++) {
            long m = r - k + j;
            long g = gcd(m, j);
            try {
                c = Math.multiplyExact(c / (j / g), m / g);   // divide first: exact, no overflow
            } catch (ArithmeticException tooBig) {
                return -1;
            }
        }
        return c;
    }

    static String pascal(int n) {
        int w = String.valueOf(pascalEntry(n - 1, (n - 1) / 2)).length() + 1;
        if (w % 2 != 0) {
            w++;
        }
        StringBuilder sb = new StringBuilder();
        for (int r = 0; r < n; r++) {
            sb.append(" ".repeat((n - 1 - r) * w / 2));
            for (int k = 0; k <= r; k++) {
                sb.append(String.format("%" + (k == 0 ? w / 2 : w) + "d", pascalEntry(r, k)));
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    static String alphabetTriangle(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= n; i++) {
            sb.append("ABCDEFGHIJKLMNOPQRSTUVWXYZ", 0, i).append('\n');
        }
        return sb.toString();
    }
}
