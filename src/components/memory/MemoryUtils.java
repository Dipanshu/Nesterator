package components.memory;

import com.google.common.base.Preconditions;

public class MemoryUtils {
    public static void checkRange(int address, MemoryRange range) {
        Preconditions.checkArgument(address < range.end && address >= range.start);
    }

    public static void checkRange(int address, Memory block) {
        checkRange(address, block.getRange());
    }
}
