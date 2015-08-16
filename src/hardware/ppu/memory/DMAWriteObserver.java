package hardware.ppu.memory;

import components.memory.Memory;
import hardware.cpu.Cpu;

public class DMAWriteObserver implements Memory.WriteObserver {

    private final Cpu mCpu;

    public DMAWriteObserver(Cpu cpu) {
        mCpu = cpu;
    }

    @Override
    public void write(int data) {
        mCpu.startDMA(data & 0xFF);
    }
}
