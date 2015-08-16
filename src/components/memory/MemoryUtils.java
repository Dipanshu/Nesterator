package components.memory;

import com.google.common.base.Preconditions;

public class MemoryUtils {
    private static void checkRange(int address, MemoryRange range) {
        Preconditions.checkArgument(address < range.end && address >= range.start, range.toString() + " does not contain " + address);
    }

    public static void checkRange(int address, Memory block) {
        checkRange(address, block.getRange());
    }
}
