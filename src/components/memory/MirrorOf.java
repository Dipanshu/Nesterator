package components.memory;

public class MirrorOf implements Memory {

    private final Memory mBaseMemory;
    private final MemoryRange mRange;
    private final MemoryRange mBaseRange;

    public MirrorOf(Memory memory, int startAddress, int length) {
        mBaseMemory = memory;
        mBaseRange = mBaseMemory.getRange();
        mRange = new MemoryRange(startAddress, startAddress + length);
    }

    @Override
    public int read(int address) {
        MemoryUtils.checkRange(address, this);
        return mBaseMemory.read(getMappedAddress(address));
    }

    @Override
    public void write(int address, int value) {
        mBaseMemory.write(getMappedAddress(address), value);
    }

    private int getMappedAddress(int address) {
        return mBaseRange.start + (address - mRange.start);
    }

    @Override
    public MemoryRange getRange() {
        return mRange;
    }
}
