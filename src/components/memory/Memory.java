package components.memory;

/**
 * Interface to a memory
 */
public interface Memory {
    public byte read(int address);
    public void write(int address, byte value);
    public MemoryRange getRange();
}
