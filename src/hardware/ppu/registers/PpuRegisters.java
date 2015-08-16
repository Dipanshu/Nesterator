package hardware.ppu.registers;

import common.NesConstants;
import components.memory.Memory;
import components.memory.MemoryRange;
import components.memory.MemoryUtils;
import hardware.ppu.memory.OAM;
import hardware.ppu.memory.PPUMemory;

public class PpuRegisters implements Memory {

    private static final int PPUCTRL = 0x2000;
    private static final int PPUMASK = 0x2001;
    private static final int PPUSTATUS = 0x2002;
    private static final int OAMADDR = 0x2003;
    private static final int OAMDATA = 0x2004;
    private static final int PPUSCROLL = 0x2005;
    private static final int PPUADDR = 0x2006;
    private static final int PPUDATA = 0x2007;

    private int controller;
    private int mask;
    private int status;
    private int oamAddress;
    private int oamData;
    private int scroll;
    private int addr;
    private int data;

    private int bus;

    private int totalScroll;
    private int totalAddr;

    private final OAM mOAM;
    private final PPUMemory mPPUMemory;

    private final MemoryRange mMemoryRange;
    private IsRenderingProvider mIsRenderingProvider;

    private boolean evenForSCROLLandADDR = true;

    private final long[] mDecayTimers = new long[] {0, 0, 0, 0, 0, 0, 0, 0};

    private int clockTimer = 0;

    public PpuRegisters(
            OAM oam,
            PPUMemory ppuMemory) {
        mOAM = oam;
        mPPUMemory = ppuMemory;
        mMemoryRange = new MemoryRange(0x2000, 0x2008);
    }


    public void init(IsRenderingProvider isRenderingProvider) {
        mIsRenderingProvider = isRenderingProvider;
    }

    @Override
    public int read(int address) {
        evenForSCROLLandADDR = true;
        bus = readInternal(address);
        return bus;
    }

    private int readInternal(int address) {
        MemoryUtils.checkRange(address, this);
        switch (address) {
            case PPUCTRL:
                return bus;
            case PPUMASK:
                return bus;

            case PPUSTATUS:
                // System.out.println("Reading PPUSTATUS => " + ByteHelper.bit7(status) +  " : " + PPU.sPPUCycles);
                resetDecay(7);
                resetDecay(6);
                resetDecay(5);
                int tempStatus = getStatus();
                StatusWriter.clearVBlank(this);
                return tempStatus;

            case OAMADDR:
                return bus;

            case OAMDATA:
                resetDecayForAllBits();
                int oamValue = mOAM.read(getOamAddress());
                if (oamAddress % 4 == 2) {
                    oamValue &= 0xE3;
                }
                return oamValue;
            case PPUSCROLL:
                return bus;

            case PPUADDR:
                return bus;

            case PPUDATA: {
                if (totalAddr <= 0x3EFF) {
                    resetDecayForAllBits();
                    int data = getData();
                    setData(mPPUMemory.read(totalAddr));
                    totalAddr++;
                    totalAddr &= 0xFFFF;
                    return data;
                } else {
                    resetDecay(0);
                    resetDecay(1);
                    resetDecay(2);
                    resetDecay(3);
                    resetDecay(4);
                    resetDecay(5);
                    setData(mPPUMemory.read(totalAddr));
                    totalAddr++;
                    totalAddr &= 0xFFFF;
                    return (getData() & 0b00111111) | (bus & 0b11000000);
                }
            }

        }
        throw new IllegalArgumentException();
    }

    @Override
    public void write(int address, int value) {
        bus = value;
        resetDecayForAllBits();
        MemoryUtils.checkRange(address, this);
        switch (address) {
            case PPUCTRL:
                controller = value;
                break;
            case PPUMASK:
                mask = value;
                break;
            case PPUSTATUS:
                // PPUSTATUS is read only
                break;
            case OAMADDR:
                oamAddress = value;
                break;
            case OAMDATA:
                if (mIsRenderingProvider.isRendering()) {
                    break;
                }
                oamData = value;
                mOAM.write(oamAddress, value);
                oamAddress = (oamAddress + 1) & 0xFF;
                break;
            case PPUSCROLL:
                if (evenForSCROLLandADDR) {
                    totalScroll = (value & 0xFF) << 8;
                    evenForSCROLLandADDR = false;
                } else {
                    totalScroll |= value & 0xFF;
                    evenForSCROLLandADDR = true;
                }
                scroll = value;
                break;
            case PPUADDR:
                if (evenForSCROLLandADDR) {
                    totalAddr = (value & 0xFF) << 8;
                    evenForSCROLLandADDR = false;
                } else {
                    totalAddr |= value & 0xFF;
                    evenForSCROLLandADDR = true;
                }
                addr = value;
                break;
            case PPUDATA:
                mPPUMemory.write(totalAddr, value);
                totalAddr += ControllerInterpretator.getVRAMIncrement(controller);
                totalAddr &= 0xFFFF;
                data = value;
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public MemoryRange getRange() {
        return mMemoryRange;
    }

    public void ppuClock() {
        clockTimer++;
        for (int i = 0; i < mDecayTimers.length; i++) {
            if (mDecayTimers[i] == 0) {
                continue;
            }
            if (mDecayTimers[i] == 1) {
                mDecayTimers[i] = 0;
                bus = bus & ~(1<<i);
            }
            mDecayTimers[i]--;
        }
    }

    private void resetDecayForAllBits() {
        // StringWriter out = new StringWriter();
        // PrintWriter writer = new PrintWriter(out);
        // new Throwable().printStackTrace(writer);
        // System.out.println("Resetting Decays" + out.toString());
        for (int i = 0; i < mDecayTimers.length; i++) {
            mDecayTimers[i] = NesConstants.PPU_DECAY_CYCLES;
        }
    }

    private void resetDecay(int bit) {
        mDecayTimers[bit] = NesConstants.PPU_DECAY_CYCLES;
    }

    public int getController() {
        return controller;
    }

    public void setController(int controller) {
        this.controller = controller;
    }

    public int getMask() {
        return mask;
    }

    public void setMask(int mask) {
        this.mask = mask;
    }

    public int getStatus() {
        return (status & ~0x1F) | (bus & 0x1F);
    }

    public void setStatus(int status) {
        this.status = status;
    }

    private int getOamAddress() {
        return oamAddress;
    }

    public void setOamAddress(int oamAddress) {
        this.oamAddress = oamAddress;
    }

    public int getOamData() {
        return oamData;
    }

    public void setOamData(int oamData) {
        this.oamData = oamData;
    }

    public int getScroll() {
        return scroll;
    }

    public void setScroll(int scroll) {
        this.scroll = scroll;
    }

    public int getAddr() {
        return addr;
    }

    public void setAddr(int addr) {
        this.addr = addr;
    }

    private int getData() {
        return data;
    }

    public void setData(int data) {
        this.data = data;
    }

    public int getBus() {
        return bus;
    }

    public void setBus(int bus) {
        this.bus = bus;
    }

    public interface IsRenderingProvider {
        boolean isRendering();
    }
}
