package components;

import org.junit.Test;
import testutil.StandardDeviation;

import static org.fest.assertions.Assertions.assertThat;

/**
 * Tests {@link components.Clock}
 */
public class ClockTest {
    private static final int TARGET_FREQUENCY = 60;
    private static final int TARGET_TICK_INTERVAL = Clock.NS_PER_SECOND / TARGET_FREQUENCY;

    private static final int NUMBER_OF_TICKS = 180;
    private enum TestCase {
        NO_OP(NO_OP_CALLBACK, TARGET_FREQUENCY, 3, 2),
        PERFECT(PERFECT_FRAME_CALLBACK, TARGET_FREQUENCY, 3, 2),
        CHEAP(CHEAP_FRAME_CALLBACK, TARGET_FREQUENCY, 3, 2),
        OCCASIONALLY_OVERSHOOTING(OCCASIONALLY_OVERSHOOTING_CALLBACK, TARGET_FREQUENCY, 3, 15),
        ALWAYS_OVERSHOOTING(ALWAYS_OVERSHOOTING_CALLBACK, TARGET_FREQUENCY / 3, 3, 2);

        private final Clock.Callback callback;
        private final int targetMeanFrequency;
        private final int allowedFrequencyDeviationHz;
        private final int allowedTickIntervalDeviationMs;

        TestCase(
                Clock.Callback callback,
                int targetMeanFrequency,
                int allowedFrequencyDeviationHz,
                int allowedTickIntervalDeviationMs) {
            this.callback = callback;
            this.targetMeanFrequency = targetMeanFrequency;
            this.allowedFrequencyDeviationHz = allowedFrequencyDeviationHz;
            this.allowedTickIntervalDeviationMs = allowedTickIntervalDeviationMs;
        }
    }

    @Test
    public void testWithNoOpCallbackTick() throws Exception {
        verifyClockAccuracy(TestCase.NO_OP);
    }

    @Test
    public void testWithPerfectFrameCallbackTick() throws Exception {
        verifyClockAccuracy(TestCase.PERFECT);
    }

    @Test
    public void testWithCheapFrame() throws Exception {
        verifyClockAccuracy(TestCase.CHEAP);
    }

    @Test
    public void testWithOccasionalOvershooting() throws Exception {
        verifyClockAccuracy(TestCase.OCCASIONALLY_OVERSHOOTING);
    }

    @Test
    public void testAlwaysOvershooting() throws Exception {
        verifyClockAccuracy(TestCase.ALWAYS_OVERSHOOTING);
    }

    private void verifyClockAccuracy(TestCase testCase) {
        Clock clock = Clock.ClockWithFrequencyHz(testCase.callback, TARGET_FREQUENCY);
        System.out.println(testCase.name());
        long data[] = new long[NUMBER_OF_TICKS];

        for (int i = 0; i < NUMBER_OF_TICKS; i++) {
            long timeStart = System.nanoTime();
            clock.tick();
            long timeEnd = System.nanoTime();
            data[i] = timeEnd - timeStart;
        }

        double mean = 0.0d;
        for (int i = 0; i < NUMBER_OF_TICKS; i++) {
            mean += data[i];
        }
        mean /= NUMBER_OF_TICKS;

        double meanDeviation = StandardDeviation.compute(data);
        final double meanTickInterval = mean / Clock.NS_PER_SECOND;
        final double meanFrequence = 1 / meanTickInterval;

        System.out.println("Mean Tick Interval: " + meanTickInterval);
        System.out.println("Mean Tick Frequence: " + meanFrequence);
        System.out.println("Mean Deviation : " + (meanDeviation / Clock.NS_PER_MS) + "ms");
        System.out.println("------------------------------------------\n");

        assertThat(Math.abs(testCase.targetMeanFrequency - meanFrequence))
                .isLessThan(testCase.allowedFrequencyDeviationHz);
        assertThat(meanDeviation).isLessThan(Clock.NS_PER_MS * testCase.allowedTickIntervalDeviationMs);
    }

    private static final Clock.Callback NO_OP_CALLBACK = new Clock.Callback() {
        @Override
        public void callback() {

        }
    };

    private static final Clock.Callback PERFECT_FRAME_CALLBACK = new Clock.Callback() {
        @SuppressWarnings("StatementWithEmptyBody")
        @Override
        public void callback() {
            long start = System.nanoTime();
            while (System.nanoTime() - start < TARGET_TICK_INTERVAL) {
                // no-op
            }
        }
    };

    private static final Clock.Callback CHEAP_FRAME_CALLBACK = new Clock.Callback() {
        @Override
        public void callback() {

        }
    };

    private static final Clock.Callback OCCASIONALLY_OVERSHOOTING_CALLBACK = new Clock.Callback() {

        private int mTickNum = 0;

        @Override
        public void callback() {
            if (mTickNum % 10 == 0) {
                waitForNanoSeconds(TARGET_TICK_INTERVAL * 3);
            } else {
                waitForNanoSeconds(TARGET_TICK_INTERVAL / 10);
            }
            mTickNum++;
        }

        @SuppressWarnings("StatementWithEmptyBody")
        private void waitForNanoSeconds(int nanoSeconds) {
            long start = System.nanoTime();
            while (System.nanoTime() - start < nanoSeconds) {
                // no-op
            }
        }
    };

    private static final Clock.Callback ALWAYS_OVERSHOOTING_CALLBACK = new Clock.Callback() {
        @Override
        public void callback() {
            waitForNanoSeconds(TARGET_TICK_INTERVAL * 3);
        }

        @SuppressWarnings("StatementWithEmptyBody")
        private void waitForNanoSeconds(int nanoSeconds) {
            long start = System.nanoTime();
            while (System.nanoTime() - start < nanoSeconds) {
                // no-op
            }
        }
    };
}
