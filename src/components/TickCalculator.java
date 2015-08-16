package components;

import common.NesConstants;

public class TickCalculator {

    public long getNumCpuCycles(double delta) {
        return (long) (delta * NesConstants.CPU_CLOCK_HZ);
    }

    public long getNumPpuCycles(double delta) {
        return getNumCpuCycles(delta) * 3;
    }
}
