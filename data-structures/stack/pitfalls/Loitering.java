import java.lang.ref.WeakReference;
import java.util.Arrays;

/** DO NOT COPY LoiteringStack. Its pop() lowers the index but leaves the
 *  reference in the array, so a popped object stays reachable. */
public final class Loitering {
    static final class LoiteringStack<T> {
        private Object[] items = new Object[8];
        private int size;

        void push(T value) {
            if (size == items.length) {
                items = Arrays.copyOf(items, items.length * 2);
            }
            items[size++] = value;
        }

        @SuppressWarnings("unchecked")
        T pop() {
            return (T) items[--size];      // the slot still holds the reference
        }
    }

    // Each helper pushes a 64 MiB array and pops it. The array is allocated
    // here, so no local variable in the caller refers to it afterwards.
    static WeakReference<byte[]> pushAndPop(ArrayStack<byte[]> s) {
        byte[] big = new byte[64 << 20];
        s.push(big);
        s.pop();
        return new WeakReference<>(big);
    }

    static WeakReference<byte[]> pushAndPop(LoiteringStack<byte[]> s) {
        byte[] big = new byte[64 << 20];
        s.push(big);
        s.pop();
        return new WeakReference<>(big);
    }

    static boolean reclaimed(WeakReference<byte[]> watch) {
        for (int i = 0; i < 10 && watch.get() != null; i++) {
            System.gc();
        }
        return watch.get() == null;
    }

    public static void main(String[] args) {
        ArrayStack<byte[]> good = new ArrayStack<>();
        LoiteringStack<byte[]> bad = new LoiteringStack<>();
        WeakReference<byte[]> a = pushAndPop(good);
        WeakReference<byte[]> b = pushAndPop(bad);
        // Both stacks are still in use here; only the popped arrays differ.
        System.out.println("slot cleared on pop: reclaimed=" + reclaimed(a)
                + ", stack size " + good.size());
        System.out.println("slot left in array:  reclaimed=" + reclaimed(b)
                + ", stack size " + bad.size);
    }
}
