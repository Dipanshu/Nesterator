package hardware.rom;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class RomLoader {
    public static Rom loadRom(InputStream inputStream) throws IOException {

        byte[] headerBytes = new byte[16];
        Preconditions.checkArgument(inputStream.read(headerBytes) == 16);
        RomHeader header = new RomHeader(headerBytes);
        if (header.hasTrainer()) {
            byte[] trainer = new byte[512];
            Preconditions.checkArgument(inputStream.read(trainer) == 512);
        }

        List<byte[]> prg_banks = Lists.newArrayList();
        for (int i = 0; i < header.getPRGROMSize(); i++) {
            byte[] bank = new byte[16 * 1024];
            Preconditions.checkArgument(inputStream.read(bank) == (16 * 1024));
            prg_banks.add(bank);
        }

        List<byte[]> chr_banks = Lists.newArrayList();
        for (int i = 0; i < header.getCHRROMSize(); i++) {
            byte[] bank = new byte[8 * 1024];
            Preconditions.checkArgument(inputStream.read(bank) == (8 * 1024));
            chr_banks.add(bank);
        }
        return new Rom(header, prg_banks, chr_banks);
    }
}
