package hardware.ppu.memory;

import components.memory.MemoryBlock;

public class OAM extends MemoryBlock {
    public OAM() {
        super(0x00, 64 * 4);
    }
}
