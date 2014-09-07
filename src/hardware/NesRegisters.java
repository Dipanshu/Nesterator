package hardware;

import components.memory.MemoryBlock;

public class NesRegisters extends MemoryBlock {
    public NesRegisters() {
        super(0x2000, 8);
    }
}
