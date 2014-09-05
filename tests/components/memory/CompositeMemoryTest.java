package components.memory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.fest.assertions.Assertions.assertThat;

public class CompositeMemoryTest {

    private Memory mMemory1;
    private Memory mMemory2;
    private Memory mMemory3;
    private Memory mMemory4;
    private Memory mMemory5;

    private Memory mCompositeMemory1;
    private Memory mCompositeMemory2;

    private Memory mTotalMemory;

    @Before
    public void setup() {
        mMemory1 = new MemoryBlock(0, 30);
        mMemory2 = new MemoryBlock(30, 70);
        mMemory3 = new MemoryBlock(100, 10);
        mMemory4 = new MemoryBlock(110, 80);
        mMemory5 = new MemoryBlock(190, 20);

        mCompositeMemory1 = new CompositeMemory(mMemory1, mMemory2);
        mCompositeMemory2 = new CompositeMemory(mMemory3, mMemory4, mMemory5);

        mTotalMemory = new CompositeMemory(mCompositeMemory1, mCompositeMemory2);
    }

    @Test
    public void testRanges() {
        assertThat(mCompositeMemory1.getRange().start).isEqualTo(0);
        assertThat(mCompositeMemory1.getRange().end).isEqualTo(100);

        assertThat(mCompositeMemory2.getRange().start).isEqualTo(100);
        assertThat(mCompositeMemory2.getRange().end).isEqualTo(210);

        assertThat(mTotalMemory.getRange().start).isEqualTo(0);
        assertThat(mTotalMemory.getRange().end).isEqualTo(210);
    }

    @Test
    public void testReadWrites() {
        Map<Memory, ImmutableList<Memory>> mExpectedMappings = ImmutableMap.of(
                mMemory1, ImmutableList.of(mCompositeMemory1, mTotalMemory),
                mMemory2, ImmutableList.of(mCompositeMemory1, mTotalMemory),
                mMemory3, ImmutableList.of(mCompositeMemory2, mTotalMemory),
                mMemory4, ImmutableList.of(mCompositeMemory2, mTotalMemory),
                mMemory5, ImmutableList.of(mCompositeMemory2, mTotalMemory)
        );
        for (Memory memory : mExpectedMappings.keySet()) {
            final ImmutableList<Memory> mappedBlocks = mExpectedMappings.get(memory);
            assertThat(mappedBlocks.size()).isEqualTo(2);
            for (int i = memory.getRange().start; i < memory.getRange().end; i++) {
                verifyAddressMapsTheSame(i, memory, mappedBlocks.get(0), mappedBlocks.get(1));
            }
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalReads() {
        mTotalMemory.read(300);
    }

    private void verifyAddressMapsTheSame(int address, Memory... memories) {
        // First reset
        for (Memory memory : memories) {
            memory.write(address, (byte) 0);
            assertThat(memory.read(address)).isEqualTo((byte) 0);
        }

        byte value = 10;
        for (Memory memory : memories) {
            memory.write(address, value);
            for (Memory testBlock : memories) {
                assertThat(testBlock.read(address)).isEqualTo(value);
            }
        }
    }
}
