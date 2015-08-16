package hardware.ppu.memory;

import components.memory.*;

public class PPUMemory extends MirroredMemory {

    public static PPUMemory construct() {
        PatternTables patternTables = new PatternTables();
        NameTables nameTables = NameTables.construct();
        Memory mirrorOfNameTables = new MirrorOf(nameTables, 0x3000, 0x3F00 - 0x3000);
        Memory palettes = new MemoryBlock(0x3F00, 0x20);
        Memory mirroredPalettes = new MirroredMemory(palettes, 8);
        CompositeMemory compositeMemory =
                new CompositeMemory(patternTables, nameTables, mirrorOfNameTables, mirroredPalettes);
        return new PPUMemory(compositeMemory);
    }

    private PPUMemory(Memory singleBlock) {
        super(singleBlock, 4);
    }
}
