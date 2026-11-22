import java.util.Scanner;

/**
 * Asks for a number of rows and prints a centered pyramid of stars.
 * Row i (counting from 1) has n - i spaces and then 2i - 1 stars.
 */
public final class Pyramid {
    private Pyramid() {
    }

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.print("Number of rows (1-50): ");
        if (!in.hasNextInt()) {
            System.out.print("\nPlease enter a whole number from 1 to 50.\n");
            System.exit(1);
        }
        int n = in.nextInt();
        if (n < 1 || n > 50) {
            System.out.print("\nPlease enter a whole number from 1 to 50.\n");
            System.exit(1);
        }
        StringBuilder out = new StringBuilder("\n");
        for (int i = 1; i <= n; i++) {
            out.append(" ".repeat(n - i))          // leading spaces
               .append("*".repeat(2 * i - 1))      // stars: 1, 3, 5, ...
               .append('\n');
        }
        System.out.print(out);
    }
}
