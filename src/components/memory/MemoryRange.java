package components.memory;

/**
 * Simple range wrapper
 */
public class MemoryRange {
    public final int start;
    public final int end;

    public MemoryRange(int start, int end) {
        this.start = start;
        this.end = end;
    }

    @Override
    public String toString() {
        return "(" + Integer.toHexString(start) + " - " + Integer.toHexString(end) + ")";
    }
}
