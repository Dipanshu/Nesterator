package hardware;

import components.TickCalculator;
import components.memory.Memory;
import hardware.cpu.Cpu;
import hardware.memory.NesMemory;
import hardware.ppu.PPU;
import hardware.ppu.memory.DMAWriteObserver;
import hardware.ppu.memory.OAM;
import hardware.ppu.memory.PPUMemory;
import hardware.ppu.registers.PpuRegisters;
import hardware.rom.Rom;

import java.util.Arrays;

public class NES {

    private final Cpu mCpu;
    private final PPU mPpu;
    private final TickCalculator mTickCalculator;
    private final NesMemory mNesMemory;

    public NES(Rom rom) {
        OAM oam = new OAM();
        PPUMemory ppuMemory = PPUMemory.construct();
        PpuRegisters ppuRegisters = new PpuRegisters(oam, ppuMemory);
        if (rom.getPRG_ROM_bankCount() == 1) {
            byte[] bank = rom.getPRG_ROM(0);
            mNesMemory = NesMemory.createNesMemory(bank, Arrays.copyOf(bank, bank.length), ppuRegisters);
        } else {
            mNesMemory = NesMemory.createNesMemory(rom.getPRG_ROM(0), rom.getPRG_ROM(1), ppuRegisters);
        }
        DMAController dmaController = new DMAController(mNesMemory, oam);
        mCpu = new Cpu(mNesMemory, dmaController);
        mPpu = new PPU(ppuRegisters, ppuMemory, oam, mCpu);
        ppuRegisters.init(mPpu);
        DMAWriteObserver dmaObserver = new DMAWriteObserver(mCpu);
        Memory.WriteObserver irqWriteObserver = new Memory.WriteObserver() {

            @Override
            public void write(int data) {
                if (data == 0x00) {
                    mCpu.interrupt();
                }
            }
        };

        mNesMemory.addObserver(0x4014, dmaObserver);
        mNesMemory.addObserver(0x4017, irqWriteObserver);
        mTickCalculator = new TickCalculator();
    }

    public void advanceTime(double elapsedTimeSec) {
        long cpuCycles = mTickCalculator.getNumCpuCycles(elapsedTimeSec);
        if (cpuCycles > 2) {
            int elapsedCycles = 0;
            while (elapsedCycles < cpuCycles) {
                int numCpuCycles = mCpu.process();
                elapsedCycles += numCpuCycles;
                int ppuCycles = numCpuCycles * 3;
                PPU.sPPUCycles += numCpuCycles;
                mPpu.clock(ppuCycles);
            }
        }
    }

    public Cpu getCpu() {
        return mCpu;
    }

    public PPU getPpu() {
        return mPpu;
    }

    public NesMemory getNesMemory() {
        return mNesMemory;
    }
}
