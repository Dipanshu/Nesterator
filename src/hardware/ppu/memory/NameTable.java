package hardware.ppu.memory;

import components.memory.Memory;
import components.memory.MemoryRange;
import components.memory.MemoryUtils;

public class NameTable implements Memory {

    private final int[] mNameTable;
    private final int[] mAttributeTable;
    private final MemoryRange mMemoryRange;

    public NameTable(int offset) {
        mNameTable = new int[0x3C0];
        mAttributeTable = new int[0x40];
        mMemoryRange = new MemoryRange(offset, 0x3C0 + 0x40 + offset);
    }

    @Override
    public int read(int address) {
        MemoryUtils.checkRange(address, this);
        if (address - mMemoryRange.start < 0x3C0) {
            return mNameTable[address - mMemoryRange.start];
        } else {
            return mAttributeTable[address - mMemoryRange.start  - 0x3C0];
        }
    }

    @Override
    public void write(int address, int value) {
        MemoryUtils.checkRange(address, this);
        if (address - mMemoryRange.start < 0x3C0) {
            mNameTable[address - mMemoryRange.start] = value;
        } else {
            mAttributeTable[address - mMemoryRange.start  - 0x3C0] = value;
        }
    }

    @Override
    public MemoryRange getRange() {
        return mMemoryRange;
    }
}
