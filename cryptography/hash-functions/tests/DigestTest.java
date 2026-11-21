import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.List;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Checks MessageDigest and Mac against the published and independently generated answers,
 * one-shot, in chunks and split at every offset.
 * Usage: java -cp build DigestTest tests/vectors
 */
public final class DigestTest {
    private static int failures;
    private static final HexFormat HEX = HexFormat.of();

    private DigestTest() { }

    private static void check(boolean ok, String what, String detail) {
        if (!ok) {
            failures++;
            System.out.print("FAIL " + what + ": " + detail + "\n");
        }
    }

    /** The JCA name for the short names used in the vector files. */
    private static String jca(String name) {
        return switch (name) {
            case "md5" -> "MD5";
            case "sha1" -> "SHA-1";
            case "sha256" -> "SHA-256";
            default -> throw new IllegalArgumentException(name);
        };
    }

    private static String hmacName(String name) {
        return switch (name) {
            case "md5" -> "HmacMD5";
            case "sha1" -> "HmacSHA1";
            case "sha256" -> "HmacSHA256";
            default -> throw new IllegalArgumentException(name);
        };
    }

    private static List<String> lines(Path dir, String file) throws IOException {
        return Files.readAllLines(dir.resolve(file), StandardCharsets.UTF_8).stream()
                .filter(l -> !l.isEmpty() && l.charAt(0) != '#').toList();
    }

    private static int knownAnswers(Path dir) throws GeneralSecurityException, IOException {
        int count = 0;
        for (String line : lines(dir, "known_answers.txt")) {
            String[] f = line.split("\t", -1);
            if (f.length != 4) {
                continue;
            }
            byte[] message = f[2].repeat(Integer.parseInt(f[1])).getBytes(StandardCharsets.UTF_8);
            MessageDigest md = MessageDigest.getInstance(jca(f[0]));
            check(HEX.formatHex(md.digest(message)).equals(f[3]), f[0], f[2]);
            md.reset();
            for (int i = 0; i < message.length; i += 7) {          // 7-byte chunks
                md.update(message, i, Math.min(7, message.length - i));
            }
            check(HEX.formatHex(md.digest()).equals(f[3]), f[0], "7-byte chunks");
            count++;
        }
        return count;
    }

    private static byte[] pattern(int n) {
        byte[] m = new byte[n];
        for (int i = 0; i < n; i++) {
            m[i] = (byte) ((i * 31 + 7) & 0xFF);
        }
        return m;
    }

    private static int lengths(Path dir) throws GeneralSecurityException, IOException {
        String[] names = {"md5", "sha1", "sha256"};
        int count = 0;
        for (String line : lines(dir, "lengths.txt")) {
            String[] f = line.split("\t", -1);
            int n = Integer.parseInt(f[0]);
            byte[] msg = pattern(n);
            for (int i = 0; i < 3; i++) {
                MessageDigest md = MessageDigest.getInstance(jca(names[i]));
                check(HEX.formatHex(md.digest(msg)).equals(f[1 + i]), names[i], f[0]);
                if (n <= 130) {                                   // split at every offset
                    for (int cut = 0; cut <= n; cut++) {
                        md.update(msg, 0, cut);
                        md.update(msg, cut, n - cut);
                        check(HEX.formatHex(md.digest()).equals(f[1 + i]), names[i], "split");
                    }
                }
            }
            count++;
        }
        return count;
    }

    private static int hmacVectors(Path dir) throws GeneralSecurityException, IOException {
        int count = 0;
        for (String line : lines(dir, "hmac.txt")) {
            String[] f = line.split("\t", -1);
            Mac mac = Mac.getInstance(hmacName(f[0]));
            mac.init(new SecretKeySpec(HEX.parseHex(f[1]), hmacName(f[0])));
            byte[] got = mac.doFinal(HEX.parseHex(f[2]));
            check(HEX.formatHex(got).equals(f[3]), "hmac-" + f[0], f[1]);
            // Compare in constant time, as a real verifier must.
            check(MessageDigest.isEqual(got, HEX.parseHex(f[3])), "hmac-" + f[0], "isEqual");
            count++;
        }
        return count;
    }

    public static void main(String[] args) throws GeneralSecurityException, IOException {
        if (args.length != 1) {
            System.err.println("usage: DigestTest <vectors-directory>");
            System.exit(2);
        }
        Path dir = Path.of(args[0]);
        int ka = knownAnswers(dir);
        int ln = lengths(dir);
        int hm = hmacVectors(dir);
        check(MessageDigest.isEqual(new byte[] {1, 2}, new byte[] {1, 2})
                && !MessageDigest.isEqual(new byte[] {1, 2}, new byte[] {1, 3}), "isEqual", "");
        System.out.print("known answers: " + ka + "\n");
        System.out.print("message lengths: " + ln + " (each also split at every offset up to 130 bytes)\n");
        System.out.print("HMAC vectors: " + hm + "\n");
        System.out.print("failures: " + failures + "\n");
        System.exit(failures == 0 ? 0 : 1);
    }
}
