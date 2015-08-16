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
public class InterruptsIntegrationTest extends BaseIntegrationTest {

    private static final ImmutableMap<InterruptTestRoms, String> DISABLED_TESTS = ImmutableMap.of();

    public InterruptsIntegrationTest(TestRom rom) throws IOException {
        super(rom);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Lists.transform(Lists.newArrayList(InterruptTestRoms.values()), new Function<InterruptTestRoms, Object[]>() {
            @Override
            public Object[] apply(InterruptTestRoms input) {
                return new Object[] {input};
            }
        });
    }

    @Override
    public ImmutableMap<? extends TestRom, String> getDisabledTests() {
        return DISABLED_TESTS;
    }

    enum InterruptTestRoms implements TestRom {
        CLI_LATENCY("27-cli_latency.nes", "CLI_LATENCY");
        // NMI_AND_BRK("28-nmi_and_brk.nes", "NMI_AND_BRK"),
        // NMI_AND_IRQ("29-nmi_and_irq.nes", "NMI_AND_IRQ"),
        // IRQ_AND_DMA("30-irq_and_dma.nes", "IRQ_AND_DMA"),
        // BRANCH_DELAYS_IRQ("31-branch_delays_irq.nes", "BRANCH_DELAYS_IRQ");

        private final String mRomName;
        private final String mTestName;

        InterruptTestRoms(String romName, String testName) {
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
}
