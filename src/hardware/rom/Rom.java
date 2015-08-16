package hardware.rom;

import java.util.List;

public class Rom {
    private final RomHeader mRomHeader;
    private final List<byte[]> mPRG_ROM;
    private final List<byte[]> mCHR_ROM;

    public Rom(RomHeader mRomHeader, List<byte[]> mPRG_rom, List<byte[]> mCHR_rom) {
        this.mRomHeader = mRomHeader;
        mPRG_ROM = mPRG_rom;
        mCHR_ROM = mCHR_rom;
    }

    public RomHeader getRomHeader() {
        return mRomHeader;
    }

    public byte[] getPRG_ROM(int index) {
        return mPRG_ROM.get(index);
    }

    public byte[] getCHR_ROM(int index) {
        return mCHR_ROM.get(index);
    }

    public int getPRG_ROM_bankCount() {
        return mPRG_ROM.size();
    }

    public int getCHR_ROM_bankCount() {
        return mCHR_ROM.size();
    }
}
