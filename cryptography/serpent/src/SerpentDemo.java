import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HexFormat;

/**
 * Encrypts and decrypts one block with a 256-bit key, then checks two
 * published test vectors: one in NESSIE byte order, and one from the Serpent
 * AES submission, which writes every value byte-reversed.
 * Run: javac -d build src/*.java && java -cp build SerpentDemo
 */
public final class SerpentDemo {
    private static final HexFormat HEX = HexFormat.of();

    private SerpentDemo() {
    }

    public static void main(String[] args) {
        byte[] key = new byte[32];
        for (int i = 0; i < key.length; i++) {
            key[i] = (byte) i;                                  // 00 01 02 ... 1f
        }
        byte[] plain = "Serpent: 128-bit".getBytes(StandardCharsets.US_ASCII);  // 16 bytes

        Serpent cipher = new Serpent(key);
        byte[] encrypted = cipher.encrypt(plain);
        byte[] back = cipher.decrypt(encrypted);
        cipher.wipe();

        System.out.printf("%-11s%s%n", "key", HEX.formatHex(key));
        System.out.printf("%-11s%s%n", "plaintext", HEX.formatHex(plain));
        System.out.printf("%-11s%s%n", "ciphertext", HEX.formatHex(encrypted));
        System.out.println("decrypts back: " + (Arrays.equals(back, plain) ? "yes" : "no"));

        // NESSIE: 128-bit key 80 00 .. 00, zero block -> 264e5481...
        byte[] k128 = new byte[16];
        k128[0] = (byte) 0x80;
        byte[] nessie = new Serpent(k128).encrypt(new byte[16]);
        boolean nessieOk = Arrays.equals(nessie, HEX.parseHex("264e5481eff42a4606abda06c0bfda3d"));
        System.out.println("NESSIE vector:          " + (nessieOk ? "match" : "MISMATCH"));

        // AES submission: 256-bit zero key, zero block -> 89104945... once the
        // output bytes are reversed (the key and block are all zero either way).
        byte[] out = new Serpent(new byte[32]).encrypt(new byte[16]);
        for (int i = 0; i < 8; i++) {
            byte t = out[i];
            out[i] = out[15 - i];
            out[15 - i] = t;
        }
        boolean submissionOk = Arrays.equals(out, HEX.parseHex("8910494504181950f98dd998a82b6749"));
        System.out.println("AES submission vector:  " + (submissionOk ? "match" : "MISMATCH"));
    }
}
