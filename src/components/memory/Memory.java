package components.memory;

/**
 * Interface to a memory
 */
public interface Memory {
    int read(int address);
    void write(int address, int value);
    MemoryRange getRange();

    interface WriteObserver {
        void write(int data);
    }

    interface MemoryWithPort extends Memory {
        void addObserver(int address, WriteObserver writeObserver);
    }
}
