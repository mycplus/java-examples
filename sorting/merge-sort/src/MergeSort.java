// MergeSort.java - top-down merge sort in Java 21
import java.util.Arrays;

public class MergeSort {

    // Stable: on a tie the left element goes first.
    static void mergeSort(int[] a) {
        int[] buf = new int[a.length];       // one buffer for every merge
        sort(a, buf, 0, a.length);
    }

    private static void sort(int[] a, int[] buf, int lo, int hi) {   // [lo, hi)
        if (hi - lo < 2)
            return;
        int mid = lo + (hi - lo) / 2;        // or (lo + hi) >>> 1; never (lo + hi) / 2
        sort(a, buf, lo, mid);
        sort(a, buf, mid, hi);
        int i = lo, j = mid, k = lo;
        while (i < mid && j < hi)
            buf[k++] = (a[j] < a[i]) ? a[j++] : a[i++];
        while (i < mid) buf[k++] = a[i++];
        while (j < hi)  buf[k++] = a[j++];
        System.arraycopy(buf, lo, a, lo, hi - lo);
    }

    public static void main(String[] args) {
        int[] data = {29, 10, 14, 37, 13, 5, 41, 22};
        mergeSort(data);
        System.out.println("Sorted: " + Arrays.toString(data));
    }
}
