package testutil;

/**
 * Utility class to calculate mean of a series of numbers
 */
public class Mean {
    public static double compute(long data[]) {
        double mean = 0;
        for (int i = 0; i < data.length; i++) {
            mean += data[i];
        }
        mean /= data.length;
        return mean;
    }
}
