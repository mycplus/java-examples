import java.util.ArrayDeque;
import java.util.Deque;
import java.util.NoSuchElementException;
import java.util.function.Supplier;

/** Dependency-free tests: run with {@code java -ea StackTest}. */
public final class StackTest {
    private static int failures;

    private static void check(boolean ok, String what) {
        if (!ok) {
            System.err.println("CHECK failed: " + what);
            failures++;
        }
    }

    /** Adapts both stacks to one interface for the shared tests. */
    private interface StringStack {
        void push(String v);
        String pop();
        String peek();
        boolean isEmpty();
        int size();
    }

    private static StringStack wrap(ArrayStack<String> s) {
        return new StringStack() {
            public void push(String v) { s.push(v); }
            public String pop() { return s.pop(); }
            public String peek() { return s.peek(); }
            public boolean isEmpty() { return s.isEmpty(); }
            public int size() { return s.size(); }
        };
    }

    private static StringStack wrap(LinkedStack<String> s) {
        return new StringStack() {
            public void push(String v) { s.push(v); }
            public String pop() { return s.pop(); }
            public String peek() { return s.peek(); }
            public boolean isEmpty() { return s.isEmpty(); }
            public int size() { return s.size(); }
        };
    }

    private static long state = 88172645463325252L;

    private static long nextRand() {
        state ^= state << 13;
        state ^= state >>> 7;
        state ^= state << 17;
        return state;
    }

    /** Random operations checked against java.util.ArrayDeque. */
    private static void differential(String name, Supplier<StringStack> make) {
        StringStack s = make.get();
        Deque<String> model = new ArrayDeque<>();
        for (int i = 0; i < 100_000; i++) {
            long r = nextRand();
            if (Long.remainderUnsigned(r, 3) != 0) {
                String v = "value-" + Long.remainderUnsigned(r, 100_000);
                s.push(v);
                model.push(v);
            } else if (model.isEmpty()) {
                check(s.isEmpty(), name + ": empty when model is empty");
            } else {
                check(s.pop().equals(model.pop()), name + ": pop order");
            }
            check(s.size() == model.size(), name + ": size");
            if (!model.isEmpty()) {
                check(s.peek().equals(model.peek()), name + ": peek");
            }
        }
    }

    private static void emptyErrors(String name, StringStack s) {
        check(s.isEmpty() && s.size() == 0, name + ": new stack is empty");
        for (int op = 0; op < 2; op++) {
            try {
                if (op == 0) {
                    s.pop();
                } else {
                    s.peek();
                }
                check(false, name + ": expected NoSuchElementException");
            } catch (NoSuchElementException e) {
                check("pop or peek on an empty stack".equals(e.getMessage()),
                        name + ": exception message");
            }
        }
    }

    private static void nullsAndGrowth() {
        ArrayStack<String> s = new ArrayStack<>();
        s.push(null);                          // null is a storable value
        check(s.size() == 1 && s.peek() == null, "null element");
        s.pop();
        for (int i = 0; i < 1_000_000; i++) {
            s.push(Integer.toString(i));
        }
        check(s.size() == 1_000_000 && "999999".equals(s.peek()), "growth");
    }

    public static void main(String[] args) {
        differential("ArrayStack", () -> wrap(new ArrayStack<>()));
        differential("LinkedStack", () -> wrap(new LinkedStack<>()));
        emptyErrors("ArrayStack", wrap(new ArrayStack<>()));
        emptyErrors("LinkedStack", wrap(new LinkedStack<>()));
        nullsAndGrowth();
        check(StackDemo.balanced("") && StackDemo.balanced("{[()()]}"), "balanced");
        check(!StackDemo.balanced("([)]") && !StackDemo.balanced(")"), "unbalanced");
        check(StackDemo.balanced("(".repeat(100_000) + ")".repeat(100_000)), "deep");
        if (failures != 0) {
            System.err.println(failures + " check(s) failed");
            System.exit(1);
        }
        System.out.println("stacks: all tests passed");
    }
}
