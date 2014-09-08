package hardware.ppu.memory;

import components.memory.Memory;
import components.memory.MemoryRange;
import components.memory.MemoryUtils;

public class PatternTables implements Memory {

    private int[] mPatternTable1;
    private int[] mPatternTable2;
    private MemoryRange mMemoryRange;

    public PatternTables() {
        mPatternTable1 = new int[0x1000];
        mPatternTable2 = new int[0x1000];
        mMemoryRange = new MemoryRange(0, 0x2000);
    }

    @Override
    public int read(int address) {
        MemoryUtils.checkRange(address, this);
        if (address < 0x1000) {
            return mPatternTable1[address];
        } else {
            return mPatternTable2[address - 0x1000];
        }
    }

    @Override
    public void write(int address, int value) {
        MemoryUtils.checkRange(address, this);
        if (address < 0x1000) {
            mPatternTable1[address] = value;
        } else {
            mPatternTable2[address - 0x1000] = value;
        }
    }

    @Override
    public MemoryRange getRange() {
        return mMemoryRange;
    }
}
