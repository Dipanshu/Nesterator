package hardware.util;

import hardware.rom.Rom;
import hardware.rom.RomLoader;

import java.io.IOException;

public class TestRoms {

    private static TestRoms sInstance;

    private TestRoms() {}

    public static TestRoms getInstance() {
        if (sInstance == null) {
            sInstance = new TestRoms();
        }
        return sInstance;
    }

    public Rom loadCpuNesTestRom() throws IOException {
        return RomLoader.loadRom(getClass().getResourceAsStream("cpuroms/cputest.nes"));
    }

    public Rom loadRom(String name) throws IOException {
        return RomLoader.loadRom(getClass().getResourceAsStream("cpuroms/" + name));
    }
}
