package vipjokerstudio.com.telegramchart.util;

public class ListUtil {

    public static int findMin(int[] array) {
        int min = array[0];
        for (int n : array)
            if (n < min) min = n;

        return min;
    }

    public static int findMax(int[] array, int start, int end) {

        if (end >= array.length) end = array.length - 1;
        int max = array[start];
        for (int i = start; i <= end; i++) {
            int n = array[i];
            if (n > max) max = n;
        }
        return max;

    }

    public static long findMax(long[] array, int start, int end) {

        if (end >= array.length) end = array.length - 1;
        long max = array[start];
        for (int i = start; i <= end; i++) {
            long n = array[i];
            if (n > max) max = n;
        }
        return max;

    }

    public static int findMax(int[] array) {

        int max = array[0];
        for (int n : array) {
            if (n > max) max = n;
        }
        return max;

    }

    public static long findMin(long[] array) {
        long min = array[0];
        for (long n : array)
            if (n < min) min = n;

        return min;
    }

    public static long findMax(long[] array) {
        long max = array[0];
        for (long n : array)
            if (n > max) max = n;

        return max;

    }

    public static boolean areContentsSame(long[] a, long[] b) {
        if (a.length != b.length) return false;
        for (int i = 0; i < a.length; i++) {
            if (a[i] != b[i]) return false;
        }

        return true;
    }

    public static boolean areContentsSame(float[] a, float[] b) {
        if (a.length != b.length) return false;
        for (int i = 0; i < a.length; i++) {
            if (a[i] != b[i]) return false;
        }

        return true;
    }

    public static void copy(long[] a, long[] b) {
        System.arraycopy(b, 0, a, 0, a.length);
    }

    public static void copy(float[] a, float[] b) {
        System.arraycopy(b, 0, a, 0, a.length);
    }
}
