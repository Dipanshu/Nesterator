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

    public MemoryBlock(int startAddress, byte[] data) {
        mData = data;
        mStartAddress = startAddress;
        mMemoryRange = new MemoryRange(mStartAddress, mStartAddress + mData.length);
    }

    @Override
    public int read(int address) {
        return mData[address - mStartAddress] & 0xFF;
    }

    @Override
    public void write(int address, int value) {
        mData[address - mStartAddress] = (byte) (value & 0xFF);
    }

    @Override
    public MemoryRange getRange() {
        return mMemoryRange;
    }
}
