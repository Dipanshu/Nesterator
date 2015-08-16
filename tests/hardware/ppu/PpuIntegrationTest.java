package hardware.ppu;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import hardware.util.BaseIntegrationTest;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.util.Collection;

@RunWith(Parameterized.class)
public class PpuIntegrationTest extends BaseIntegrationTest {
    private static final ImmutableMap<PpuTestRoms, String> DISABLED_TESTS = ImmutableMap.of();

    public PpuIntegrationTest(BaseIntegrationTest.TestRom rom) throws IOException {
        super(rom);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Lists.transform(Lists.newArrayList(PpuTestRoms.values()), new Function<PpuTestRoms, Object[]>() {
            @Override
            public Object[] apply(PpuTestRoms input) {
                return new Object[]{input};
            }
        });
    }

    @Override
    public ImmutableMap<? extends BaseIntegrationTest.TestRom, String> getDisabledTests() {
        return DISABLED_TESTS;
    }

    enum PpuTestRoms implements BaseIntegrationTest.TestRom {
        // PPU_OPEN_BUS("23-ppu_open_bus.nes", "PPU_OPEN_BUS"),
        // OAM_READ("25-oam_read.nes", "OAM_READ"),
        // OAM_STRESS("24-oam_stress.nes", "OAM_STRESS");
        // PPU_READ_BUFFFER("26-ppu_read_buffer.nes", "PPU_READ_BUFFER");
        VBL_BASICS("32-vbl_basics.nes", "VBL_BASICS"),
        VBL_SET_TIME("33-vbl_set_time.nes", "VBL_SET_TIME"),
        VBL_CLEAR_TIME("34-vbl_clear_time.nes", "VBL_CLEAR_TIME"),
        NMI_CONTROL("35-nmi_control.nes", "NMI_CONTROL"),
        NMI_TIMING("36-nmi_timing.nes", "NMI_TIMING"),
        SUPPRESSION("37-suppression.nes", "SUPPRESSION"),
        NMI_ON_TIMING("38-nmi_on_timing.nes", "NMI_ON_TIMING"),
        NMI_OFF_TIMING("39-nmi_off_timing.nes", "NMI_OFF_TIMING"),
        EVEN_ODD_FRAMES("40-even_odd_frames.nes", "EVEN_ODD_FRAMES"),
        EVEN_ODD_TIMING("41-even_odd_timing.nes", "EVEN_ODD_TIMING");

        private final String mRomName;
        private final String mTestName;

        PpuTestRoms(String romName, String testName) {
            mRomName = romName;
            mTestName = testName;
        }


        @Override
        public String getRomName() {
            return mRomName;
        }

        @Override
        public String getTestName() {
            return mTestName;
        }
    }

    @Override
    public boolean logEnabled() {
        return false;
    }
}
