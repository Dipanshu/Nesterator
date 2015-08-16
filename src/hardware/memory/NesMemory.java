package hardware.memory;

import com.carrotsearch.hppc.IntObjectHashMap;
import com.carrotsearch.hppc.IntObjectMap;
import components.memory.CompositeMemory;
import components.memory.Memory;
import components.memory.MemoryBlock;
import components.memory.MirroredMemory;

public class NesMemory extends CompositeMemory implements Memory.MemoryWithPort {

    private final IntObjectMap<WriteObserver> mObserver = new IntObjectHashMap<>(1);

    private NesMemory(Memory... blocks) {
        super(blocks);
    }

    public static NesMemory createNesMemory(
            byte[] prg1,
            byte[] prg2,
            Memory ppuRegisters) {
        MemoryBlock ram = new MemoryBlock(0, 0x800);
        MirroredMemory totalRam = new MirroredMemory(ram, 4);
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

    @Override
    public int read(int address) {
        return super.read(address);
    }

    @Override
    public void write(int address, int value) {
        super.write(address, value);
        if (mObserver.containsKey(address)) {
            mObserver.get(address).write(value);
        }
    }

    @Override
    public void addObserver(int address, WriteObserver writeObserver) {
        mObserver.put(address, writeObserver);
    }
}
