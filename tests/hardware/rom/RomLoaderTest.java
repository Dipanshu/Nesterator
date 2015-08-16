package hardware.rom;

import hardware.util.TestRoms;
import org.junit.Test;

import java.io.IOException;

import static org.fest.assertions.Assertions.assertThat;

/**
 * Tests {@link hardware.rom.RomLoader}
 */
public class RomLoaderTest {
    @Test
    public void testRomInit() throws IOException {
        Rom rom = TestRoms.getInstance().loadCpuNesTestRom();
        assertThat(rom.getRomHeader().getCHRROMSize()).isEqualTo(1);
        assertThat(rom.getRomHeader().getPRGROMSize()).isEqualTo(1);
        assertThat(rom.getRomHeader().hasTrainer()).isFalse();
    }

}
