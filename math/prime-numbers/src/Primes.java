/** Test one number for primality, and list primes with the Sieve of Eratosthenes. */
public final class Primes {
    private Primes() { }

    /** Trial division by 2, 3 and then 6k - 1 and 6k + 1 up to sqrt(n).
     *  i <= n / i is the overflow-safe form of i * i <= n. */
    public static boolean isPrime(long n) {
        if (n < 2) {
            return false;
        }
        if (n < 4) {
            return true;                       // 2 and 3
        }
        if (n % 2 == 0 || n % 3 == 0) {
            return false;
        }
        for (long i = 5; i <= n / i; i += 6) {
            if (n % i == 0 || n % (i + 2) == 0) {
                return false;
            }
        }
        return true;
    }

    /** flags[k] is true when k is prime, for 0 <= k < limit. */
    public static boolean[] sieve(int limit) {
        boolean[] flags = new boolean[limit];
        for (int k = 2; k < limit; k++) {
            flags[k] = true;
        }
        for (int p = 2; limit > 0 && p <= (limit - 1) / p; p++) {
            if (!flags[p]) {
                continue;
            }
            for (long m = (long) p * p; m < limit; m += p) {   // long: m + p may pass 2^31 - 1
                flags[(int) m] = false;
            }
        }
        return flags;
    }

    public static int countPrimesBelow(int limit) {
        int count = 0;
        for (boolean f : sieve(limit)) {
            if (f) {
                count++;
            }
        }
        return count;
    }

    public static void main(String[] args) {
        StringBuilder out = new StringBuilder("primes below 100:");
        boolean[] flags = sieve(100);
        for (int k = 0; k < flags.length; k++) {
            if (flags[k]) {
                out.append(' ').append(k);
            }
        }
        System.out.println(out);
        System.out.println("primes below 1000: " + countPrimesBelow(1000));
        System.out.println("primes below 1000000: " + countPrimesBelow(1000000));

        long[] samples = {-7, 0, 1, 2, 91, 97};
        StringBuilder line = new StringBuilder("is_prime:");
        for (int k = 0; k < samples.length; k++) {
            line.append(' ').append(samples[k]).append(' ').append(isPrime(samples[k]))
                .append(k + 1 < samples.length ? "," : "");
        }
        System.out.println(line);
        System.out.println("is_prime(2147483647) = " + isPrime(2147483647L));
        System.out.println("is_prime(1000000007) = " + isPrime(1000000007L));
    }
}
