import java.util.Arrays;

/**
 * The Serpent block cipher (128-bit blocks, keys up to 256 bits). Bytes are
 * read as little-endian 32-bit words, the order the NESSIE test vectors and
 * libgcrypt use.
 *
 * <p>For learning and testing. It has not been reviewed for side channels;
 * use a maintained cryptographic library for anything that protects real data.
 */
public final class Serpent {
    public static final int BLOCK_SIZE = 16;
    public static final int MAX_KEY_SIZE = 32;

    private static final int[][] SBOX = {
        {3, 8, 15, 1, 10, 6, 5, 11, 14, 13, 4, 2, 7, 0, 9, 12},
        {15, 12, 2, 7, 9, 0, 5, 10, 1, 11, 14, 8, 6, 13, 3, 4},
        {8, 6, 7, 9, 3, 12, 10, 15, 13, 1, 14, 4, 0, 11, 5, 2},
        {0, 15, 11, 8, 12, 9, 6, 3, 13, 1, 2, 4, 10, 7, 5, 14},
        {1, 15, 8, 3, 12, 0, 11, 6, 2, 5, 4, 10, 9, 14, 7, 13},
        {15, 5, 2, 11, 4, 10, 9, 12, 0, 3, 14, 8, 13, 6, 7, 1},
        {7, 2, 12, 5, 8, 4, 6, 11, 14, 9, 1, 15, 13, 3, 10, 0},
        {1, 13, 15, 0, 14, 8, 2, 11, 7, 4, 12, 10, 9, 3, 5, 6},
    };
    /** Inverse S-boxes, computed from SBOX so there is no second table to mistype. */
    private static final int[][] SBOX_INV = new int[8][16];
    static {
        for (int s = 0; s < 8; s++) {
            for (int i = 0; i < 16; i++) {
                SBOX_INV[s][SBOX[s][i]] = i;
            }
        }
    }

    private static final int PHI = 0x9e3779b9;   // fractional part of the golden ratio

    private final int[][] subkeys = new int[33][4];   // K0..K32

    /** Expands a key of 1 to 32 bytes; shorter keys are padded as the specification requires. */
    public Serpent(byte[] key) {
        if (key == null || key.length == 0 || key.length > MAX_KEY_SIZE) {
            throw new IllegalArgumentException("Serpent keys are 1 to 32 bytes");
        }
        // Pad to 256 bits: one 1 bit after the key, then zeros.
        byte[] padded = Arrays.copyOf(key, MAX_KEY_SIZE);
        if (key.length < MAX_KEY_SIZE) {
            padded[key.length] = 1;
        }
        // w[0..7] hold the padded key; w[8..139] are the 132 prekey words.
        int[] w = new int[140];
        for (int i = 0; i < 8; i++) {
            w[i] = loadLe32(padded, 4 * i);
        }
        for (int i = 8; i < 140; i++) {
            w[i] = Integer.rotateLeft(w[i - 8] ^ w[i - 5] ^ w[i - 3] ^ w[i - 1] ^ PHI ^ (i - 8), 11);
        }
        // Subkey i passes prekeys 4i..4i+3 through S-box (3 - i) mod 8.
        for (int i = 0; i < 33; i++) {
            System.arraycopy(w, 8 + 4 * i, subkeys[i], 0, 4);
            applySbox(SBOX[Math.floorMod(3 - i, 8)], subkeys[i]);
        }
        Arrays.fill(padded, (byte) 0);
        Arrays.fill(w, 0);
    }

    /** Encrypts one 16-byte block and returns a new array. */
    public byte[] encrypt(byte[] in) {
        int[] x = loadBlock(in);
        for (int r = 0; r < 32; r++) {
            xorKey(x, subkeys[r]);
            applySbox(SBOX[r % 8], x);
            if (r < 31) {
                linearTransform(x);
            } else {
                xorKey(x, subkeys[32]);           // the last round replaces LT with K32
            }
        }
        return storeBlock(x);
    }

    /** Decrypts one 16-byte block and returns a new array. */
    public byte[] decrypt(byte[] in) {
        int[] x = loadBlock(in);
        for (int r = 31; r >= 0; r--) {
            if (r == 31) {
                xorKey(x, subkeys[32]);
            } else {
                inverseLinearTransform(x);
            }
            applySbox(SBOX_INV[r % 8], x);
            xorKey(x, subkeys[r]);
        }
        return storeBlock(x);
    }

    /** Overwrites the expanded key. Java cannot guarantee no other copies exist. */
    public void wipe() {
        for (int[] k : subkeys) {
            Arrays.fill(k, 0);
        }
    }

    /** Bit j of x[0..3] forms a 4-bit input (x[0] is the low bit). */
    private static void applySbox(int[] box, int[] x) {
        int[] y = new int[4];
        for (int j = 0; j < 32; j++) {
            int in = 0;
            for (int b = 0; b < 4; b++) {
                in |= ((x[b] >>> j) & 1) << b;
            }
            int out = box[in];
            for (int b = 0; b < 4; b++) {
                y[b] |= ((out >>> b) & 1) << j;
            }
        }
        System.arraycopy(y, 0, x, 0, 4);
    }

    private static void linearTransform(int[] x) {
        x[0] = Integer.rotateLeft(x[0], 13);
        x[2] = Integer.rotateLeft(x[2], 3);
        x[1] ^= x[0] ^ x[2];
        x[3] ^= x[2] ^ (x[0] << 3);
        x[1] = Integer.rotateLeft(x[1], 1);
        x[3] = Integer.rotateLeft(x[3], 7);
        x[0] ^= x[1] ^ x[3];
        x[2] ^= x[3] ^ (x[1] << 7);
        x[0] = Integer.rotateLeft(x[0], 5);
        x[2] = Integer.rotateLeft(x[2], 22);
    }

    private static void inverseLinearTransform(int[] x) {
        x[2] = Integer.rotateRight(x[2], 22);
        x[0] = Integer.rotateRight(x[0], 5);
        x[2] ^= x[3] ^ (x[1] << 7);
        x[0] ^= x[1] ^ x[3];
        x[3] = Integer.rotateRight(x[3], 7);
        x[1] = Integer.rotateRight(x[1], 1);
        x[3] ^= x[2] ^ (x[0] << 3);
        x[1] ^= x[0] ^ x[2];
        x[2] = Integer.rotateRight(x[2], 3);
        x[0] = Integer.rotateRight(x[0], 13);
    }

    private static void xorKey(int[] x, int[] k) {
        for (int i = 0; i < 4; i++) {
            x[i] ^= k[i];
        }
    }

    private static int loadLe32(byte[] p, int off) {
        return (p[off] & 0xff) | (p[off + 1] & 0xff) << 8 | (p[off + 2] & 0xff) << 16
                | (p[off + 3] & 0xff) << 24;
    }

    private static int[] loadBlock(byte[] in) {
        if (in.length != BLOCK_SIZE) {
            throw new IllegalArgumentException("Serpent blocks are 16 bytes");
        }
        int[] x = new int[4];
        for (int i = 0; i < 4; i++) {
            x[i] = loadLe32(in, 4 * i);
        }
        return x;
    }

    private static byte[] storeBlock(int[] x) {
        byte[] out = new byte[BLOCK_SIZE];
        for (int i = 0; i < 16; i++) {
            out[i] = (byte) (x[i / 4] >>> (8 * (i % 4)));
        }
        return out;
    }
}
