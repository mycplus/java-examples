import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.List;
import java.util.TreeMap;

/**
 * Every NESSIE single-block vector in both directions, plus key and block
 * length validation. Dependency-free:
 * {@code java -ea -cp build SerpentTest tests/vectors/serpent_nessie.txt}.
 */
public final class SerpentTest {
    private SerpentTest() {
    }

    private static boolean rejected(Runnable r) {
        try {
            r.run();
            return false;
        } catch (IllegalArgumentException expected) {
            return true;
        }
    }

    public static void main(String[] args) throws IOException {
        Path path = Path.of(args.length > 0 ? args[0] : "tests/vectors/serpent_nessie.txt");
        HexFormat hex = HexFormat.of();
        List<String> lines = Files.readAllLines(path, StandardCharsets.US_ASCII);

        int total = 0;
        int failures = 0;
        TreeMap<Integer, Integer> passedByBits = new TreeMap<>();
        for (String line : lines) {
            String[] f = line.trim().split("\\s+");
            if (line.startsWith("#") || f.length != 3) {
                continue;
            }
            total++;
            byte[] key = hex.parseHex(f[0]);
            byte[] plain = hex.parseHex(f[1]);
            byte[] expected = hex.parseHex(f[2]);
            Serpent cipher = new Serpent(key);
            if (Arrays.equals(cipher.encrypt(plain), expected)
                    && Arrays.equals(cipher.decrypt(expected), plain)) {
                passedByBits.merge(key.length * 8, 1, Integer::sum);
            } else {
                failures++;
            }
        }
        System.out.printf("%d vectors: %d with 128-bit keys, %d with 192-bit, %d with 256-bit; %d failed%n",
                total, passedByBits.getOrDefault(128, 0), passedByBits.getOrDefault(192, 0),
                passedByBits.getOrDefault(256, 0), failures);

        boolean badLengths = rejected(() -> new Serpent(new byte[0]))
                && rejected(() -> new Serpent(new byte[33]));
        boolean oneByte = !rejected(() -> new Serpent(new byte[1]));
        boolean badBlock = rejected(() -> new Serpent(new byte[16]).encrypt(new byte[15]));
        System.out.printf("rejects 0- and 33-byte keys: %s; accepts a 1-byte key: %s%n",
                badLengths ? "yes" : "no", oneByte ? "yes" : "no");
        if (failures != 0 || total == 0 || !badLengths || !oneByte || !badBlock) {
            System.exit(1);
        }
    }
}
