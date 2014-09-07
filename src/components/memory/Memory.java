package components.memory;

/**
 * Interface to a memory
 */
public interface Memory {
    public int read(int address);
    public void write(int address, int value);
    public MemoryRange getRange();
}
