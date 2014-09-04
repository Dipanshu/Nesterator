package testutil;

/**
 * Utility class to calculate standard deviation of a series of numbers
 */
public class StandardDeviation {
    public static double compute(long[] data) {
        final int length = data.length;

        if (length < 2) {
            return Double.NaN;
        }
        double mean = Mean.compute(data);
        double sum = 0;
        for (int i = 0; i < length; i++) {
            final double variance = data[i] - mean;
            sum += variance * variance;
        }
        return Math.sqrt(sum / length);
    }
} 