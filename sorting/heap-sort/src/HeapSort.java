// HeapSort.java - heap sort in Java 21
import java.util.Arrays;

public class HeapSort {

    // Max-heap in the array: children of i at 2i + 1 and 2i + 2. Not stable.
    static void heapSort(int[] a) {
        int n = a.length;
        for (int i = n / 2 - 1; i >= 0; i--)     // int index: i >= 0 is a real test
            siftDown(a, i, n);
        for (int end = n - 1; end > 0; end--) {
            int t = a[0]; a[0] = a[end]; a[end] = t;
            siftDown(a, 0, end);
        }
    }

    private static void siftDown(int[] a, int root, int n) {
        while (true) {
            int child = 2 * root + 1;
            if (child >= n)
                return;
            if (child + 1 < n && a[child] < a[child + 1])
                child++;
            if (a[root] >= a[child])
                return;
            int t = a[root]; a[root] = a[child]; a[child] = t;
            root = child;
        }
    }

    public static void main(String[] args) {
        int[] data = {29, 10, 14, 37, 13, 5, 41, 22};
        heapSort(data);
        System.out.println("Sorted: " + Arrays.toString(data));
    }
}
