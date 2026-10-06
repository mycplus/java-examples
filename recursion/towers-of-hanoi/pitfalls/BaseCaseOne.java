// BaseCaseOne.java - DO NOT COPY. The base case is n == 1, so n == 0 never
// reaches it and the recursion runs until the stack is exhausted.
public class BaseCaseOne {
    static long moves;

    static void hanoi(int n, char source, char target, char spare) {
        if (n == 1) {
            moves++;
            return;
        }
        hanoi(n - 1, source, spare, target);
        moves++;
        hanoi(n - 1, spare, target, source);
    }

    public static void main(String[] args) {
        int n = args.length > 0 ? Integer.parseInt(args[0]) : 0;
        System.out.println("solving for " + n + " disks");
        hanoi(n, 'A', 'C', 'B');
        System.out.println(n + " disks: " + moves + " moves");
    }
}
