import java.util.Arrays;

/** Dependency-free tests: run with {@code java -ea MergeSortTest}. */
public final class MergeSortTest {
    private static int failures;

    private static void check(boolean ok, String what) {
        if (!ok) {
            System.err.println("CHECK failed: " + what);
            failures++;
        }
    }

    private static long state = 0x9E3779B97F4A7C15L;

    private static long nextRand() {
        state ^= state << 13;
        state ^= state >>> 7;
        state ^= state << 17;
        return state;
    }

    /** Sorts a copy with MergeSort and compares it with Arrays.sort. */
    private static void against(int[] input, String what) {
        int[] expected = input.clone();
        int[] actual = input.clone();
        Arrays.sort(expected);
        MergeSort.mergeSort(actual);
        check(Arrays.equals(actual, expected), what);
    }

    public static void main(String[] args) {
        // 20,000 random arrays of 0 to 64 elements: every third one draws
        // from only 4 values, and about 1 value in 8 is MIN_VALUE or MAX_VALUE.
        for (int t = 0; t < 20_000; t++) {
            int n = (int) Long.remainderUnsigned(nextRand(), 65);
            int range = t % 3 == 0 ? 4 : 1000;
            int[] a = new int[n];
            for (int i = 0; i < n; i++) {
                long r = Long.remainderUnsigned(nextRand(), 16);
                a[i] = r == 0 ? Integer.MIN_VALUE
                     : r == 1 ? Integer.MAX_VALUE
                     : (int) Long.remainderUnsigned(nextRand(), range) - range / 2;
            }
            against(a, "random case " + t);
        }

        int[] big = new int[20_000];
        for (int i = 0; i < big.length; i++) {
            big[i] = (int) nextRand();
        }
        against(big, "20,000 random ints");

        int[] reversed = new int[2_000];
        for (int i = 0; i < reversed.length; i++) {
            reversed[i] = reversed.length - i;
        }
        against(reversed, "reversed");
        against(new int[0], "empty array");
        against(new int[] {42}, "one element");

        if (failures != 0) {
            System.err.println(failures + " check(s) failed");
            System.exit(1);
        }
        System.out.println("mergeSort: all tests passed");
    }
}
