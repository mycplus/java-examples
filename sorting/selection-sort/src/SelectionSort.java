// SelectionSort.java - selection sort in Java 21
import java.util.Arrays;

public class SelectionSort {

    // In place, at most n - 1 swaps. Not stable.
    static void selectionSort(int[] a) {
        for (int i = 0; i + 1 < a.length; i++) {
            int min = i;
            for (int j = i + 1; j < a.length; j++)
                if (a[j] < a[min])
                    min = j;
            if (min != i) {
                int t = a[i]; a[i] = a[min]; a[min] = t;
            }
        }
    }

    public static void main(String[] args) {
        int[] data = {29, 10, 14, 37, 13, 5, 41, 22};
        selectionSort(data);
        System.out.println("Sorted: " + Arrays.toString(data));
    }
}
