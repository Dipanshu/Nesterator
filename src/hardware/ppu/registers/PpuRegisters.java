package hardware.ppu.registers;

import components.memory.Memory;
import components.memory.MemoryRange;
import components.memory.MemoryUtils;

public class PpuRegisters implements Memory {
    public int controller;
    public int mask;
    public int status;
    public int oamAddress;
    public int oamData;
    public int scroll;
    public int address;
    public int data;

    private final MemoryRange mMemoryRange;

    public PpuRegisters() {
        mMemoryRange = new MemoryRange(0x2000, 0x20007);
    }

    @Override
    public int read(int address) {
        MemoryUtils.checkRange(address, this);
        switch (address - 0x2000) {
            case 0:
                return controller;

            case 1:
                return mask;

            case 2:
                return status;

            case 3:
                return oamAddress;

            case 4:
                return oamData;

            case 5:
                return scroll;

            case 6:
                return address;

            case 7:
                return data;

        }
        throw new IllegalArgumentException();
    }

    @Override
    public void write(int address, int value) {
        MemoryUtils.checkRange(address, this);
        switch (address - 0x2000) {
            case 0:
                controller = value;

            case 1:
                mask = value;

            case 2:
                status = value;

            case 3:
                oamAddress = value;

            case 4:
                oamData = value;

            case 5:
                scroll = value;

            case 6:
                address = value;

            case 7:
                data = value;
        }
        throw new IllegalArgumentException();
    }

    @Override
    public MemoryRange getRange() {
        return mMemoryRange;
    }
}
