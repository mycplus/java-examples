import java.util.function.Consumer;

/** The article's demo tree; prints the same output as the C version. */
public final class BstDemo {
    private BstDemo() {
    }

    private static void print(String label, Consumer<Consumer<Integer>> walk) {
        StringBuilder sb = new StringBuilder(String.format("%-13s", label));
        boolean[] first = {true};
        walk.accept(k -> {
            sb.append(first[0] ? "" : " ").append(k);
            first[0] = false;
        });
        System.out.println(sb);
    }

    public static void main(String[] args) {
        BinarySearchTree<Integer> tree = BinarySearchTree.natural();
        for (int key : new int[] {50, 30, 70, 20, 40, 60, 80, 35, 45, 65}) {
            tree.insert(key);
        }
        System.out.println("inserted:    50 30 70 20 40 60 80 35 45 65");
        print("preorder:", tree::preorder);
        print("inorder:", tree::inorder);
        print("postorder:", tree::postorder);
        print("level order:", tree::levelOrder);
        System.out.println("size " + tree.size() + ", height " + tree.height());
        System.out.println("contains 45: " + (tree.contains(45) ? "yes" : "no")
                + ", contains 55: " + (tree.contains(55) ? "yes" : "no"));
        System.out.println("insert 40 again: " + (tree.insert(40) ? 1 : 0));

        tree.remove(30);
        System.out.println("\nremove 30 (two children)");
        print("inorder:", tree::inorder);
        print("level order:", tree::levelOrder);

        tree.remove(50);
        tree.remove(20);
        System.out.println("\nremove 50 (root), 20 (leaf)");
        print("inorder:", tree::inorder);
        print("level order:", tree::levelOrder);
        System.out.println("size " + tree.size() + ", height " + tree.height());
        System.out.println("remove 99: " + (tree.remove(99) ? "removed" : "not found"));
    }
}
