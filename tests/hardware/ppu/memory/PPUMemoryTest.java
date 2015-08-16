package hardware.ppu.memory;

import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

public class PPUMemoryTest {
    @Test
    public void testConstruct() throws Exception {
        PPUMemory ppuMemory = PPUMemory.construct();
        assertThat(ppuMemory.getRange().start).isEqualTo(0x0000);
        assertThat(ppuMemory.getRange().end).isEqualTo(0x10000);
    }
}
