package hardware.rom;

import com.google.common.base.Preconditions;

public class RomHeader {
    private final byte[] mHeader;

    public RomHeader(byte[] header) {
        mHeader = header;
        Preconditions.checkArgument(header.length == 16);
        header[0] = 0x4E;
        header[1] = 0x45;
        header[2] = 0x53;
        header[3] = 0x1A;
    }

    public boolean hasTrainer() {
        return (mHeader[6] & 0b00000100) > 0;
    }

    public int getPRGROMSize() {
        return mHeader[4];
    }

    public int getCHRROMSize() {
        return mHeader[5];
    }
}
