package hardware.rom;

import java.io.IOException;
import java.io.InputStream;

public class RomLoader {
    public static Rom loadRom(InputStream inputStream) throws IOException {

        byte[] headerBytes = new byte[16];
        inputStream.read(headerBytes);
        RomHeader header = new RomHeader(headerBytes);
        if (header.hasTrainer()) {
            byte[] trainer = new byte[512];
            inputStream.read(trainer);
        }
        byte[] prgROM = new byte[header.getPRGROMSize() * 16 * 1024];
        inputStream.read(prgROM);
        byte[] chrROM = new byte[header.getCHRROMSize() * 8 * 1024];
        inputStream.read(chrROM);

        return new Rom(header, prgROM, chrROM);
    }
}
