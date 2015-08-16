package hardware.cpu;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import hardware.util.BaseIntegrationTest;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.util.Collection;

@RunWith(Parameterized.class)
public class CpuSubsytemsIntegrationTest extends BaseIntegrationTest {

    private static final ImmutableMap<CpuTestRoms, String> DISABLED_TESTS = ImmutableMap.of(
            CpuTestRoms.DUMMY_READS_APU, "APU not implemented",
            CpuTestRoms.IMMEDIATE, "Needs bug-fix",
            CpuTestRoms.ABS_XY, "Needs bug-fix",
            CpuTestRoms.DUMMY_READS, "Timing out");

    public CpuSubsytemsIntegrationTest(TestRom rom) throws IOException {
        super(rom);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Lists.transform(Lists.newArrayList(CpuTestRoms.values()), new Function<CpuTestRoms, Object[]>() {
            @Override
            public Object[] apply(CpuTestRoms input) {
                return new Object[] {input};
            }
        });
    }

    @Override
    public ImmutableMap<? extends TestRom, String> getDisabledTests() {
        return DISABLED_TESTS;
    }

    enum CpuTestRoms implements BaseIntegrationTest.TestRom {
        BASICS("01-basics.nes", "BASICS"),
        IMPLIED("02-implied.nes", "IMPLIED"),
        IMMEDIATE("03-immediate.nes", "IMMEDIATE"),
        ZERO_PAGE("04-zero_page.nes", "ZERO_PAGE"),
        ZP_XY("05-zp_xy.nes", "ZP_XY"),
        ABSOLUTE("06-absolute.nes", "ABSOLUTE"),
        ABS_XY("07-abs_xy.nes", "ABS_XY"),
        IND_X("08-ind_x.nes", "IND_X"),
        IND_Y("09-ind_y.nes", "IND_Y"),
        BRANCHES("10-branches.nes", "BRANCHES"),
        STACK("11-stack.nes", "STACK"),
        JMP_JSR("12-jmp_jsr.nes", "JMP_JSR"),
        RTS("13-rts.nes", "RTS"),
        RTI("14-rti.nes", "RTI"),
        BRK("15-brk.nes", "BRK"),
        SPECIAL("16-special.nes", "SPECIAL"),
        ABS_X_WRAP("17-abs_x_wrap.nes", "ABS_X_WRAP"),
        DUMMY_READS("18-dummy_reads.nes", "DUMMY_READS"),
        BRANCH_WRAP("19-branch_wrap.nes", "BRANCH_WRAP"),
        DUMMY_READS_APU("20-dummy_reads_apu.nes", "DUMMY_READS_APU"),
        // REGISTERS("21-registers.nes", "REGISTERS"),
        RAM_AFTER_RESET("22-ram_after_reset.nes", "RAM_AFTER_RESET"),
        INSTR_TIMING("42-instr_timing.nes", "INSTR_TIMING"),
        BRANCH_TIMING("43-branch_timing.nes", "BRANCH_TIMING");

        private final String mRomName;
        private final String mTestName;

        CpuTestRoms(String romName, String testName) {
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
