import java.util.NoSuchElementException;

/** A stack built on a singly linked list. Not thread-safe. */
public final class LinkedStack<T> {
    private static final class Node<T> {
        final T value;
        final Node<T> next;   // the node below this one

        Node(T value, Node<T> next) {
            this.value = value;
            this.next = next;
        }
    }

    private Node<T> top;
    private int size;

    public void push(T value) {
        top = new Node<>(value, top);
        size++;
    }

    public T pop() {
        T value = peek();
        top = top.next;
        size--;
        return value;
    }

    public T peek() {
        if (top == null) {
            throw new NoSuchElementException("pop or peek on an empty stack");
        }
        return top.value;
    }

    public boolean isEmpty() {
        return top == null;
    }

    public int size() {
        return size;
    }
}
