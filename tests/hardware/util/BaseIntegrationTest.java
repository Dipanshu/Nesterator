package hardware.util;

import com.google.common.collect.ImmutableMap;
import components.TickCalculator;
import hardware.NES;
import hardware.cpu.Cpu;
import hardware.memory.NesMemory;
import hardware.ppu.PPU;
import hardware.rom.Rom;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public abstract class BaseIntegrationTest {

    private final TestRom mRom;
    private StringBuilder mStringBuilder;
    private final TickCalculator mTickCalculator;

    public BaseIntegrationTest(TestRom rom) throws IOException {
        mRom = rom;
        mTickCalculator = new TickCalculator();
    }

    @Test
    public void testRom() throws IOException, TimeoutException {
        ImmutableMap<? extends TestRom, String> disabledTests = getDisabledTests();
        if (disabledTests.containsKey(mRom)) {
            System.out.println(mRom.getTestName() + " is DISABLED.\nReason : " + disabledTests.get(mRom));
            return;
        }

        Rom rom = TestRoms.getInstance().loadRom(mRom.getRomName());
        NES nes = new NES(rom);
        Cpu cpu = nes.getCpu();
        NesMemory nesMemory = nes.getNesMemory();
        cpu.setDebugMode(logEnabled());
        boolean testStarted = false;
        PPU ppu = nes.getPpu();
        while (true) {
            int cycles = cpu.process();
            ppu.clock(cycles * 3);
            if (logEnabled()) {
                System.out.println(cpu.getDebugLog());
            }
            int status = nesMemory.read(0x6000);
            if (status == 0x80) {
                if (!testStarted) {
                    System.out.println("Test started : " + mRom.getTestName());
                }
                testStarted = true;

                /*
                String newOutput = getTestOutput(nesMemory);
                if (!lastOutput.equals(newOutput)) {
                    System.out.println(getTestOutput(nesMemory));
                    lastOutput = newOutput;
                }
                */

                continue;
            }
            if (testStarted) {
                if (status == 0x81) {
                    System.out.println("Test wants reset");
                    long numCpuCycles = mTickCalculator.getNumCpuCycles(2);

                    //noinspection StatementWithEmptyBody
                    for (int i = 0; i < numCpuCycles; i += cpu.process()) {
                        int cpuCycles = cpu.process();
                        ppu.clock(cpuCycles * 3);
                        i += cpuCycles;
                    }

                    testStarted = false;
                    cpu.reset();
                    continue;
                }
                if (status != 0x00) {
                    throw new IllegalArgumentException("Status code : " + status + "\n" + getTestOutput(nesMemory));
                }
                break;
            }
        }
    }

    protected abstract ImmutableMap<? extends TestRom, String> getDisabledTests();

    @SuppressWarnings("SameReturnValue")
    protected boolean logEnabled() {
        return false;
    }

    private String getTestOutput(NesMemory nesMemory) {
        if (mStringBuilder == null) {
            mStringBuilder = new StringBuilder();
        }
        mStringBuilder.setLength(0);
        int index = 0x6004;
        while (true) {
            char character = (char) nesMemory.read(index);
            if (character == '\0') {
                break;
            }
            mStringBuilder.append(character);
            index++;
        }
        return mStringBuilder.toString();
    }

    public interface TestRom {
        String getRomName();
        String getTestName();
    }
}
