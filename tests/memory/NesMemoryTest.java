package memory;

import hardware.memory.NesMemory;
import hardware.rom.Rom;
import hardware.util.TestRoms;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;

/**
 * Tests {@link hardware.memory.NesMemory}
 */
public class NesMemoryTest {

    @Test
    public void testMemoryInit() throws IOException {
        Rom rom = TestRoms.getInstance().loadCpuTestRom();
        final byte[] prg_rom = rom.getPRG_ROM();
        NesMemory nesMemory = NesMemory.createNesMemory(prg_rom, Arrays.copyOf(prg_rom, prg_rom.length));
    }
}
