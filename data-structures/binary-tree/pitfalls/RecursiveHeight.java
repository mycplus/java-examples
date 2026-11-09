/**
 * A recursive height on chains of growing depth: the shape sorted insertion
 * gives a binary search tree. Each level costs one stack frame, and the
 * thread's stack runs out long before memory does.
 */
public final class RecursiveHeight {
    private RecursiveHeight() {
    }

    private static final class Node {
        Node left;
        Node right;
    }

    /** The same function as the C version: both subtrees, recursively. */
    static int height(Node n) {
        return n == null ? -1 : 1 + Math.max(height(n.left), height(n.right));
    }

    public static void main(String[] args) {
        for (int n : new int[] {1_000, 10_000, 100_000, 1_000_000}) {
            Node root = null;
            for (int i = 0; i < n; i++) {   // linked directly: no O(n^2) insertion
                Node node = new Node();
                node.right = root;
                root = node;
            }
            try {
                System.out.printf("n = %,d: height %,d%n", n, height(root));
            } catch (StackOverflowError e) {
                System.out.printf("n = %,d: StackOverflowError%n", n);
            }
        }
    }
}
