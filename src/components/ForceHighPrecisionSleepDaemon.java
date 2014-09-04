package components;

/**
 * Daemon thread that forces a higher precision in {@link java.lang.Thread#sleep(long)}
 */
public class ForceHighPrecisionSleepDaemon extends Thread {

    private static ForceHighPrecisionSleepDaemon sSharedInstance;

    private ForceHighPrecisionSleepDaemon() {
        setDaemon(true);
    }

    public static void ensureRunning() {
        if (sSharedInstance == null) {
            sSharedInstance = new ForceHighPrecisionSleepDaemon();
        }
        if (sSharedInstance.isAlive()) {
            return;
        }
        sSharedInstance.start();
    }

    public void run() {
        while(true) {
            try {
                Thread.sleep(Long.MAX_VALUE);
            }
            catch(Exception exc) {}
        }
    }
}
