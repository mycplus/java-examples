// InsertionSort.java - insertion sort in Java 21
import java.util.Arrays;

public class InsertionSort {

    // Stable, in place: the strict < never moves an element past an equal one.
    static void insertionSort(int[] a) {
        for (int i = 1; i < a.length; i++) {
            int v = a[i];
            int j = i;
            while (j > 0 && v < a[j - 1]) {
                a[j] = a[j - 1];
                j--;
            }
            a[j] = v;
        }
    }

    public static void main(String[] args) {
        int[] data = {29, 10, 14, 37, 13, 5, 41, 22};
        insertionSort(data);
        System.out.println("Sorted: " + Arrays.toString(data));
    }
}
