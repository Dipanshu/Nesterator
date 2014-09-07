package hardware.rom;

public class Rom {
    private final RomHeader mRomHeader;
    private final byte[] mPRG_ROM;
    private final byte[] mCHR_ROM;

    public Rom(RomHeader mRomHeader, byte[] mPRG_rom, byte[] mCHR_rom) {
        this.mRomHeader = mRomHeader;
        mPRG_ROM = mPRG_rom;
        mCHR_ROM = mCHR_rom;
    }

    public RomHeader getRomHeader() {
        return mRomHeader;
    }

    public byte[] getPRG_ROM() {
        return mPRG_ROM;
    }

    public byte[] getCHR_ROM() {
        return mCHR_ROM;
    }
}
