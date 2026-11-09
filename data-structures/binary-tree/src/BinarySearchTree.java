import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * A binary search tree with unique keys. Insert, search, remove and the
 * traversals use loops, an explicit stack or a queue, so a tree as deep as
 * it is large (sorted insertion) cannot overflow the call stack. Not
 * thread-safe.
 */
public final class BinarySearchTree<T> {
    private static final class Node<T> {
        T key;
        Node<T> left;    // keys that compare before key
        Node<T> right;   // keys that compare after key

        Node(T key) {
            this.key = key;
        }
    }

    private final Comparator<? super T> cmp;
    private Node<T> root;
    private int size;

    public BinarySearchTree(Comparator<? super T> cmp) {
        this.cmp = Objects.requireNonNull(cmp);
    }

    /** A tree ordered by the keys' natural ordering. */
    public static <T extends Comparable<? super T>> BinarySearchTree<T> natural() {
        return new BinarySearchTree<>(Comparator.naturalOrder());
    }

    /** Returns false if an equal key was already present. */
    public boolean insert(T key) {
        Objects.requireNonNull(key);
        if (root == null) {
            root = new Node<>(key);
            size++;
            return true;
        }
        Node<T> n = root;
        while (true) {
            int c = cmp.compare(key, n.key);
            if (c == 0) {
                return false;
            }
            Node<T> next = c < 0 ? n.left : n.right;
            if (next == null) {
                if (c < 0) {
                    n.left = new Node<>(key);
                } else {
                    n.right = new Node<>(key);
                }
                size++;
                return true;
            }
            n = next;
        }
    }

    public boolean contains(T key) {
        Node<T> n = root;
        while (n != null) {
            int c = cmp.compare(key, n.key);
            if (c == 0) {
                return true;
            }
            n = c < 0 ? n.left : n.right;
        }
        return false;
    }

    /** Returns false if the key was not present. */
    public boolean remove(T key) {
        Node<T> parent = null;
        Node<T> n = root;
        int c;
        while (n != null && (c = cmp.compare(key, n.key)) != 0) {
            parent = n;
            n = c < 0 ? n.left : n.right;
        }
        if (n == null) {
            return false;
        }
        if (n.left != null && n.right != null) {
            // Two children: copy the successor's key in, then remove the
            // successor, which has no left child.
            Node<T> succParent = n;
            Node<T> succ = n.right;
            while (succ.left != null) {
                succParent = succ;
                succ = succ.left;
            }
            n.key = succ.key;
            parent = succParent;
            n = succ;
        }
        Node<T> child = n.left != null ? n.left : n.right;
        if (parent == null) {
            root = child;
        } else if (parent.left == n) {
            parent.left = child;
        } else {
            parent.right = child;
        }
        size--;
        return true;
    }

    public void preorder(Consumer<? super T> visit) {
        Deque<Node<T>> stack = new ArrayDeque<>();
        if (root != null) {
            stack.push(root);
        }
        while (!stack.isEmpty()) {
            Node<T> n = stack.pop();
            visit.accept(n.key);
            if (n.right != null) {
                stack.push(n.right);    // pushed first, so visited second
            }
            if (n.left != null) {
                stack.push(n.left);
            }
        }
    }

    public void inorder(Consumer<? super T> visit) {
        Deque<Node<T>> stack = new ArrayDeque<>();
        Node<T> n = root;
        while (n != null || !stack.isEmpty()) {
            if (n != null) {
                stack.push(n);
                n = n.left;
            } else {
                n = stack.pop();
                visit.accept(n.key);
                n = n.right;
            }
        }
    }

    /** Postorder is preorder with the children swapped, reversed. */
    public void postorder(Consumer<? super T> visit) {
        Deque<Node<T>> stack = new ArrayDeque<>();
        Deque<T> out = new ArrayDeque<>();
        if (root != null) {
            stack.push(root);
        }
        while (!stack.isEmpty()) {
            Node<T> n = stack.pop();
            out.push(n.key);
            if (n.left != null) {
                stack.push(n.left);
            }
            if (n.right != null) {
                stack.push(n.right);
            }
        }
        out.forEach(visit);
    }

    public void levelOrder(Consumer<? super T> visit) {
        Deque<Node<T>> queue = new ArrayDeque<>();
        if (root != null) {
            queue.add(root);
        }
        while (!queue.isEmpty()) {
            Node<T> n = queue.remove();
            visit.accept(n.key);
            if (n.left != null) {
                queue.add(n.left);
            }
            if (n.right != null) {
                queue.add(n.right);
            }
        }
    }

    /** Height in edges: -1 for an empty tree. Counts levels. */
    public int height() {
        List<Node<T>> level = new ArrayList<>();
        if (root != null) {
            level.add(root);
        }
        int h = -1;
        while (!level.isEmpty()) {
            h++;
            List<Node<T>> next = new ArrayList<>();
            for (Node<T> n : level) {
                if (n.left != null) {
                    next.add(n.left);
                }
                if (n.right != null) {
                    next.add(n.right);
                }
            }
            level = next;
        }
        return h;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }
}
