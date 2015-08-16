package hardware.cpu;

import components.memory.MemoryBlock;
import hardware.DMAController;
import hardware.memory.NesMemory;
import hardware.ppu.memory.OAM;
import hardware.rom.Rom;
import hardware.util.TestRoms;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;

import static org.fest.assertions.Assertions.assertThat;

/**
 * Tests {@link hardware.cpu.Cpu}
 */
public class CpuIntegrationTest {

    @Test
    public void runCPU() throws Exception {
        Rom rom = TestRoms.getInstance().loadCpuNesTestRom();
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(getClass().getResourceAsStream("expected_output")));

        final byte[] prg_rom = rom.getPRG_ROM(0);
        MemoryBlock ppuRegisters = new MemoryBlock(0x2000, 8);
        NesMemory nesMemory = NesMemory.createNesMemory(prg_rom, Arrays.copyOf(prg_rom, prg_rom.length), ppuRegisters);
        Cpu cpu = new Cpu(nesMemory, new DMAController(nesMemory, new OAM()));
        cpu.getRegisters().PC = 0xC000;
        cpu.setDebugMode(true);
        String lastDebugLine = null;
        while (true) {
            cpu.process();
            String debugLine = cpu.getDebugLog();
            String expectedLine = bufferedReader.readLine();
            if (expectedLine == null) {
                break;
            }
            debugLine = debugLine.replace("\t", "  ").replace("\n", "").trim();
            expectedLine = expectedLine.replace("\t", "  ").replace("\n", "").trim();
            if (!debugLine.equals(expectedLine)) {
                if (lastDebugLine != null) {
                    System.out.println(lastDebugLine);
                    System.out.println();
                }
                System.out.println("-----ERROR-----");
                System.out.println(debugLine);
                System.out.println(expectedLine + " EXPECTED");
                System.out.println();
                assertThat(debugLine).isEqualTo(expectedLine);
            }
            lastDebugLine = debugLine;
        }
    }
}
