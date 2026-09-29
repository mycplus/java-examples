/** Dependency-free tests: java -ea -cp build PrimesTest */
public final class PrimesTest {
    private static int failures;

    private static void check(boolean ok, String what) {
        if (!ok) {
            System.err.println("CHECK failed: " + what);
            failures++;
        }
    }

    public static void main(String[] args) {
        long[] primes = {2, 3, 5, 7, 11, 13, 97, 7919, 1000003,
                         2147483647L, 1000000007L, 999999999989L};
        long[] composites = {Long.MIN_VALUE, -7, -1, 0, 1, 4, 6, 9, 25, 49, 91, 121, 561, 1105, 7917,
                             1000003L * 1000003L, 1000003L * 1000033L, 2147483647L * 2};
        for (long p : primes) {
            check(Primes.isPrime(p), p + " is prime");
        }
        for (long c : composites) {
            check(!Primes.isPrime(c), c + " is not prime");
        }

        boolean[] flags = Primes.sieve(200000);
        for (int k = 0; k < flags.length; k++) {
            if (Primes.isPrime(k) != flags[k]) {
                check(false, "trial division and sieve disagree at " + k);
                break;
            }
        }

        int[][] counts = {{0, 0}, {1, 0}, {2, 0}, {3, 1}, {10, 4}, {100, 25},
                          {1000, 168}, {1000000, 78498}, {10000000, 664579}};
        for (int[] c : counts) {
            check(Primes.countPrimesBelow(c[0]) == c[1], "pi below " + c[0]);
        }
        if (failures != 0) {
            System.err.println(failures + " check(s) failed");
            System.exit(1);
        }
        System.out.println("primes: all tests passed");
    }
}
