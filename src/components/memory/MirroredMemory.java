package components.memory;

import com.google.common.base.Preconditions;

public class MirroredMemory implements Memory {

    private final Memory mBaseMemory;
    private final int mBaseLength;
    private final MemoryRange mMemoryRange;

    public MirroredMemory(Memory startMemory, int times) {
        Preconditions.checkArgument(times > 0);
        mBaseMemory = startMemory;
        mBaseLength = startMemory.getRange().end - startMemory.getRange().start;

        mMemoryRange = new MemoryRange(
                startMemory.getRange().start,
                startMemory.getRange().end + (times - 1) * mBaseLength);
    }

    private int getMappedAddress(int address) {
        MemoryUtils.checkRange(address, this);
        return mMemoryRange.start + ((address - mMemoryRange.start) % mBaseLength);
    }

    @Override
    public int read(int address) {
        return mBaseMemory.read(getMappedAddress(address));
    }

    @Override
    public void write(int address, int value) {
        MemoryUtils.checkRange(address, this);
        mBaseMemory.write(getMappedAddress(address), value);
    }

    @Override
    public MemoryRange getRange() {
        return mMemoryRange;
    }
}
