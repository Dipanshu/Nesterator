package hardware;

import components.memory.Memory;
import hardware.memory.NesMemory;

public class DMAController {

    private final NesMemory mNesMemory;
    private final Memory mOAM;

    public DMAController(NesMemory nesMemory, Memory oam) {
        mNesMemory = nesMemory;
        mOAM = oam;
    }

    public enum DMA_STATE {
        IDLE_CYCLE,
        READING_WRITING
    }

    // DMA
    private boolean mIsDMAActive = false;
    private DMA_STATE mDMA_state;
    private int mDMAAddress;
    private boolean mIRQPending;
    private boolean mNMIPending;

    public void init(int addressHigh) {
        mIsDMAActive = true;
        mDMA_state = DMA_STATE.IDLE_CYCLE;
        mDMAAddress = (addressHigh & 0xFF) << 8;
        mIRQPending = false;
        mNMIPending = false;
    }

    public void stop() {
        mIsDMAActive = false;
        mIRQPending = false;
        mNMIPending = false;
    }

    public int process(int cpuCycle) {
        if (mDMA_state == DMA_STATE.IDLE_CYCLE) {
            mDMA_state = DMA_STATE.READING_WRITING;
            return cpuCycle % 2 == 0 ? 1 : 2;
        }
        mOAM.write(mDMAAddress & 0xFF, mNesMemory.read(mDMAAddress));
        if ((mDMAAddress & 0xFF) == 0xFF) {
            mIsDMAActive = false;
        }
        return 2;
    }

    public boolean isActive() {
        return mIsDMAActive;
    }
}
