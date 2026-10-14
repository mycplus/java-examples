import java.util.Arrays;
import java.util.HexFormat;

/**
 * Encryption speed in ECB mode over a buffer of N MiB (default 16), median of
 * 5 runs after one warm-up run for the JIT. Every language's benchmark fills
 * the buffer the same way and prints the XOR of all ciphertext blocks.
 * Usage: java -cp build SerpentSpeed [MiB]
 */
public final class SerpentSpeed {
    private SerpentSpeed() {
    }

    public static void main(String[] args) {
        int mib = args.length > 0 ? Integer.parseInt(args[0]) : 16;
        int bytes = mib << 20;
        byte[][] in = new byte[bytes / 16][16];
        for (int i = 0; i < bytes; i++) {
            in[i / 16][i % 16] = (byte) ((i * 0x9E3779B1) >>> 24);
        }
        byte[] key = new byte[32];
        for (int i = 0; i < 32; i++) {
            key[i] = (byte) i;
        }
        Serpent cipher = new Serpent(key);
        byte[][] out = new byte[in.length][];

        double[] mbps = new double[5];
        for (int run = -1; run < 5; run++) {                    // run -1 warms up the JIT
            long start = System.nanoTime();
            for (int b = 0; b < in.length; b++) {
                out[b] = cipher.encrypt(in[b]);
            }
            double seconds = (System.nanoTime() - start) / 1e9;
            if (run >= 0) {
                mbps[run] = bytes / 1e6 / seconds;
            }
        }
        Arrays.sort(mbps);
        byte[] fold = new byte[16];
        for (byte[] block : out) {
            for (int i = 0; i < 16; i++) {
                fold[i] ^= block[i];
            }
        }
        System.out.printf("Java %-7s %8.2f MB/s (median of 5, %d MiB)  xor of blocks %s%n",
                Runtime.version().feature(), mbps[2], mib, HexFormat.of().formatHex(fold));
    }
}
