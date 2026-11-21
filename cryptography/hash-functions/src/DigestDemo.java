import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/**
 * MD5, SHA-1 and SHA-256 of three short messages, with the digests that java.security
 * provides. Build: javac -d build src/*.java
 */
public final class DigestDemo {
    private DigestDemo() { }

    /** Hex digest of the UTF-8 bytes of text, for an algorithm name such as "SHA-256". */
    static String digestHex(String algorithm, byte[] data) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance(algorithm);
        return HexFormat.of().formatHex(md.digest(data));
    }

    private static void show(String text) throws NoSuchAlgorithmException {
        byte[] data = text.getBytes(StandardCharsets.UTF_8);
        System.out.print("message: \"" + text + "\" (" + data.length + " bytes)\n");
        System.out.print("  MD5     " + digestHex("MD5", data) + "\n");
        System.out.print("  SHA-1   " + digestHex("SHA-1", data) + "\n");
        System.out.print("  SHA-256 " + digestHex("SHA-256", data) + "\n");
    }

    public static void main(String[] args) throws NoSuchAlgorithmException {
        show("");
        show("abc");
        show("The quick brown fox jumps over the lazy dog");
    }
}
