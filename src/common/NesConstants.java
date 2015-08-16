package common;

import components.TickCalculator;

public class NesConstants {

    public static final int WIDTH = 256;
    public static final int HEIGHT = 240;
    private static final double FPS = 60;
    public static final double FRAME_INTERVAL_SECONDS = 1.0 / FPS;

    public static final double CPU_CLOCK_HZ = 1789773;

    public static final long PPU_DECAY_CYCLES = new TickCalculator().getNumPpuCycles(0.6);
}
