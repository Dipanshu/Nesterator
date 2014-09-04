package components;

import com.google.common.base.Preconditions;

/**
 * Utility class that can tick at specified frequency. Contains logic to auto-correct
 * when the invocation of callback takes less or more time than tick interval.
 * <p/>
 * Usage :
 * <pre>
 * {@code
 * Clock clock = Clock.ClockWithFrequencyHz(callback, frequency);
 * while (running) {
 *     clock.tick();
 * }
 * }
 * </pre>
 */
public class Clock {
    public static final int NS_PER_SECOND = 1000000000;
    public static final int MS_PER_SECOND = 1000;
    public static final int NS_PER_MS = NS_PER_SECOND / MS_PER_SECOND;

    private long mTickInterval = 1000000;
    private long mTickOvershoot;

    private final Callback mCallback;

    /**
     * Create a clock which can tick at the specified frequency.
     *
     * @param callback      Callback to be invoked on each clock tick
     * @param frequencyInHz Frequency in Hertz
     * @return Instance of a clock that can be used to tick at specific intervals
     */
    public static final Clock ClockWithFrequencyHz(Callback callback, double frequencyInHz) {
        Preconditions.checkArgument(frequencyInHz > 0);
        return new Clock(callback, (long) ((NS_PER_SECOND) / (frequencyInHz)));
    }

    private Clock(Callback callback, long tickInterval) {
        mCallback = callback;
        mTickInterval = tickInterval;
        ForceHighPrecisionSleepDaemon.ensureRunning();
    }

    /**
     * Method called to increment tick. Typically used in a run-loop.
     */
    public void tick() {
        long mTickStartTime = System.nanoTime();
        mCallback.callback();
        long timeTaken = System.nanoTime() - mTickStartTime;
        if (mTickInterval > timeTaken) {
            long timeLeft = mTickInterval - timeTaken;
            if (mTickOvershoot > 0) {
                final long compensationDelay = Math.min(mTickOvershoot, timeLeft);
                timeLeft -= compensationDelay;
            } else if (mTickOvershoot < 0) {
                timeLeft += (-mTickOvershoot);
            }
            // System.out.println(timeLeft + " : " + mTickOvershoot);
            if (timeLeft != 0) {
                try {
                    Thread.sleep(timeLeft / NS_PER_MS, (int) (timeLeft % NS_PER_MS));
                    timeTaken = System.nanoTime() - mTickStartTime;
                } catch (InterruptedException e) {
                }
            }
        }
        mTickOvershoot += timeTaken - mTickInterval;
    }

    /**
     * Callback to be invoked by {@link components.Clock on each tick}
     */
    public static interface Callback {
        public void callback();
    }
}
