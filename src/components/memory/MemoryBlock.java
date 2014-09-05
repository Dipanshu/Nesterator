package components.memory;

/**
 * Simple Memory block
 */
public class MemoryBlock implements Memory {

    private final int mStartAddress;

    private final byte[] mData;
    private final MemoryRange mMemoryRange;

    public MemoryBlock(int mStartAddress, int mLength) {
        this.mStartAddress = mStartAddress;
        mData = new byte[mLength];
        mMemoryRange = new MemoryRange(mStartAddress, mStartAddress + mLength);
    }

    @Override
    public byte read(int address) {
        return mData[address - mStartAddress];
    }

    @Override
    public void write(int address, byte value) {
        mData[address - mStartAddress] = value;
    }

    @Override
    public MemoryRange getRange() {
        return mMemoryRange;
    }
}
