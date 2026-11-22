/**
 * Prints any pattern by name, with the same arguments, output and exit codes
 * as the C program:  java PatternsDemo NAME ROWS   |   java PatternsDemo list
 */
public final class PatternsDemo {
    private PatternsDemo() {
    }

    public static void main(String[] args) {
        if (args.length == 1 && args[0].equals("list")) {
            for (Patterns.Pattern p : Patterns.ALL) {
                System.out.printf("%-25s 1-%-3d %s\n", p.name(), p.maxN(), p.about());
            }
            System.out.flush();
            return;
        }
        if (args.length != 2) {
            System.err.println("usage: PatternsDemo NAME ROWS | PatternsDemo list");
            System.exit(2);
        }
        Patterns.Pattern p = Patterns.find(args[0]);
        if (p == null) {
            System.err.println("PatternsDemo: no pattern called \"" + args[0] + "\"");
            System.exit(2);
        }
        int n;
        try {
            n = Integer.parseInt(args[1]);
        } catch (NumberFormatException notANumber) {
            n = 0;
        }
        if (n < 1 || n > p.maxN()) {
            System.err.println("PatternsDemo: ROWS for " + p.name() + " must be a whole number from 1 to " + p.maxN());
            System.exit(2);
        }
        System.out.print(p.draw().apply(n));
        System.out.flush();
    }
}
