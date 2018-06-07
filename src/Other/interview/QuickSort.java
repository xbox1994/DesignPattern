package Other.interview;

import java.util.Arrays;

public class QuickSort {
    public static void sort(int[] a, int low, int high) {
        int base = a[low];
        int lowTemp = low;
        int highTemp = high;
        while (low < high) {
            while (low < high && base < a[high]) {
                high--;
            }
            if (low < high) {
                int temp = a[low];
                a[low] = a[high];
                a[high] = temp;
                low++;
            }
            while (low < high && base > a[low]) {
                low++;
            }
            if (low < high) {
                int temp = a[low];
                a[low] = a[high];
                a[high] = temp;
                high--;
            }
        }
        if (low > lowTemp) {
            sort(a, lowTemp, low - 1);
        }
        if (high < highTemp) {
            sort(a, high + 1, highTemp);
        }
    }

    public static void main(String[] args) {
        int[] a = {4, 1, 2, 5, 2, 4, 3, 6, 3, 6, 7, 8, 3};
        sort(a, 0, a.length - 1);
        System.out.println(Arrays.toString(a));
    }

}
