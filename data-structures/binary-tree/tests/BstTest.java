import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

/** Dependency-free tests: run with {@code java -ea BstTest}. */
public final class BstTest {
    private static int failures;

    private BstTest() {
    }

    private static void check(boolean ok, String what) {
        if (!ok) {
            System.err.println("CHECK failed: " + what);
            failures++;
        }
    }

    private static <T> List<T> inorder(BinarySearchTree<T> t) {
        List<T> out = new ArrayList<>();
        t.inorder(out::add);
        return out;
    }

    private static void demoTree() {
        BinarySearchTree<Integer> t = BinarySearchTree.natural();
        for (int k : new int[] {50, 30, 70, 20, 40, 60, 80, 35, 45, 65}) {
            check(t.insert(k), "insert " + k);
        }
        check(!t.insert(40), "duplicate insert");
        List<Integer> pre = new ArrayList<>();
        List<Integer> post = new ArrayList<>();
        List<Integer> lvl = new ArrayList<>();
        t.preorder(pre::add);
        t.postorder(post::add);
        t.levelOrder(lvl::add);
        check(pre.equals(List.of(50, 30, 20, 40, 35, 45, 70, 60, 65, 80)), "preorder");
        check(inorder(t).equals(List.of(20, 30, 35, 40, 45, 50, 60, 65, 70, 80)), "inorder");
        check(post.equals(List.of(20, 35, 45, 40, 30, 65, 60, 80, 70, 50)), "postorder");
        check(lvl.equals(List.of(50, 30, 70, 20, 40, 60, 80, 35, 45, 65)), "level order");
        check(t.height() == 3 && t.size() == 10, "height and size");
        for (int k : new int[] {65, 40, 45, 70, 80, 30, 50}) {   // every removal case
            check(t.remove(k), "remove " + k);
        }
        check(!t.remove(50), "remove missing");
        check(inorder(t).equals(List.of(20, 35, 60)) && t.size() == 3, "after removals");
    }

    private static void emptyAndComparator() {
        BinarySearchTree<Integer> e = BinarySearchTree.natural();
        check(e.isEmpty() && e.height() == -1 && !e.contains(1) && !e.remove(1), "empty tree");
        check(inorder(e).isEmpty(), "empty inorder");
        BinarySearchTree<String> words = new BinarySearchTree<>(Comparator.reverseOrder());
        for (String w : new String[] {"pear", "apple", "fig", "kiwi", "apple"}) {
            words.insert(w);
        }
        check(inorder(words).equals(List.of("pear", "kiwi", "fig", "apple")), "reverse order");
        boolean threw = false;
        try {
            e.insert(null);
        } catch (NullPointerException ex) {
            threw = true;
        }
        check(threw, "null key rejected");
    }

    private static void againstTreeSet() {
        long x = 2463534242L;
        BinarySearchTree<Integer> t = BinarySearchTree.natural();
        TreeSet<Integer> model = new TreeSet<>();
        for (int op = 0; op < 200_000; op++) {
            x ^= x << 13;
            x ^= x >>> 7;
            x ^= x << 17;
            int i = (int) Long.remainderUnsigned(x, 2000);
            int key = i == 0 ? Integer.MIN_VALUE : i == 1999 ? Integer.MAX_VALUE : i * 1000003 - 1000000000;
            switch ((int) Long.remainderUnsigned(x >>> 32, 3)) {
                case 0 -> check(t.insert(key) == model.add(key), "insert");
                case 1 -> check(t.remove(key) == model.remove(key), "remove");
                default -> check(t.contains(key) == model.contains(key), "contains");
            }
            if (op % 1000 == 0) {
                check(t.size() == model.size(), "size");
                check(inorder(t).equals(new ArrayList<>(model)), "inorder vs TreeSet");
            }
        }
    }

    /**
     * 20,000 sorted keys make a chain. Walking it on a thread with a 64 KiB
     * stack shows the traversals do not recurse: a recursive walk of 20,000
     * levels needs far more than that.
     */
    private static void deepChain() throws InterruptedException {
        BinarySearchTree<Integer> t = BinarySearchTree.natural();
        for (int k = 0; k < 20_000; k++) {
            t.insert(k);
        }
        boolean[] done = {false};
        Thread small = new Thread(null, () -> {
            check(t.height() == 19_999, "chain height");
            check(inorder(t).size() == 20_000, "chain inorder");
            List<Integer> post = new ArrayList<>();
            t.postorder(post::add);
            check(post.get(0) == 19_999 && post.size() == 20_000, "chain postorder");
            List<Integer> pre = new ArrayList<>();
            t.preorder(pre::add);
            check(pre.get(0) == 0 && pre.size() == 20_000, "chain preorder");
            done[0] = true;
        }, "small-stack", 64 * 1024);
        small.start();
        small.join();
        check(done[0], "walks on a 64 KiB stack finished");
    }

    public static void main(String[] args) throws InterruptedException {
        demoTree();
        emptyAndComparator();
        againstTreeSet();
        deepChain();
        if (failures > 0) {
            System.err.println(failures + " check(s) failed");
            System.exit(1);
        }
        System.out.println("all binary tree tests passed");
    }
}
