import java.util.Arrays;
import java.util.NoSuchElementException;

/** A growable array-backed stack. Not thread-safe. */
public final class ArrayStack<T> {
    private static final int INITIAL_CAPACITY = 8;
    // OpenJDK's conservative "soft maximum" array length: HotSpot can refuse
    // arrays closer to Integer.MAX_VALUE even when heap is available.
    private static final int MAX_CAPACITY = Integer.MAX_VALUE - 8;

    private Object[] items = new Object[INITIAL_CAPACITY];
    private int size;

    public void push(T value) {
        if (size == items.length) {
            if (size == MAX_CAPACITY) {
                throw new IllegalStateException("stack is full");
            }
            // Doubling in long arithmetic cannot wrap to a negative length.
            int newLength = (int) Math.min(2L * items.length, MAX_CAPACITY);
            items = Arrays.copyOf(items, newLength);
        }
        items[size++] = value;
    }

    public T pop() {
        T value = peek();
        items[--size] = null;   // drop the reference so the GC can reclaim it
        return value;
    }

    @SuppressWarnings("unchecked")   // only T values are ever stored
    public T peek() {
        if (size == 0) {
            throw new NoSuchElementException("pop or peek on an empty stack");
        }
        return (T) items[size - 1];
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }
}
