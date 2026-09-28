import java.util.ArrayDeque;
import java.util.Deque;
import java.util.NoSuchElementException;

public final class StackDemo {
    static boolean balanced(String text) {
        Deque<Character> open = new ArrayDeque<>();
        for (char c : text.toCharArray()) {
            switch (c) {
                case '(', '[', '{' -> open.push(c);
                case ')', ']', '}' -> {
                    char want = c == ')' ? '(' : c == ']' ? '[' : '{';
                    if (open.isEmpty() || open.pop() != want) {
                        return false;
                    }
                }
                default -> { }
            }
        }
        return open.isEmpty();
    }

    public static void main(String[] args) {
        ArrayStack<Integer> s = new ArrayStack<>();
        System.out.println("push 10 20 30");
        s.push(10);
        s.push(20);
        s.push(30);
        System.out.println("size=" + s.size() + " top=" + s.peek());
        while (!s.isEmpty()) {
            System.out.println("pop " + s.pop());
        }
        System.out.println("empty=" + s.isEmpty());
        try {
            s.pop();
            System.out.println("pop on empty: returned a value");
        } catch (NoSuchElementException e) {
            System.out.println("pop on empty: underflow reported");
        }

        for (String t : new String[] {"{[()()]}", "([)]", "((", "())", ""}) {
            System.out.println("balanced(\"" + t + "\") = " + balanced(t));
        }
    }
}
