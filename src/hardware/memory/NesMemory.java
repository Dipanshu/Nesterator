package hardware.memory;

import components.memory.CompositeMemory;
import components.memory.Memory;
import components.memory.MemoryBlock;
import components.memory.MirroredMemory;

public class NesMemory extends CompositeMemory {

    private NesMemory(Memory... blocks) {
        super(blocks);
    }

    public static NesMemory createNesMemory(byte[] prg1, byte[] prg2) {
        MemoryBlock ram = new MemoryBlock(0, 0x800);
        MirroredMemory totalRam = new MirroredMemory(ram, 4);
        MemoryBlock ppuRegisters = new MemoryBlock(0x2000, 8);
        MirroredMemory ppuRegistersMirrored = new MirroredMemory(ppuRegisters, 1024);
        MemoryBlock underConstruction = new MemoryBlock(0x4000, 0x2000);
        MemoryBlock sRam = new MemoryBlock(0x6000, 0x2000);
        MemoryBlock prgRom1 = new MemoryBlock(0x8000, prg1);
        MemoryBlock prgRom2 = new MemoryBlock(0xC000, prg2);

        return new NesMemory(
                totalRam,
                ppuRegistersMirrored,
                underConstruction,
                sRam,
                prgRom1,
                prgRom2);
    }
}
