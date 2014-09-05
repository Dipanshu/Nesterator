package components.memory;

import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

public class MirroredMemoryTest {

    private static final int BASE_LENGTH = 10;
    private static final int BASE_OFFSET = 30;
    private static final int REPEAT_TIME = 4;

    private Memory mBaseMemory;
    private Memory mMirrorMemory;

    @Before
    public void setup() {
        mBaseMemory = new MemoryBlock(BASE_OFFSET, BASE_LENGTH);
        mMirrorMemory = new MirroredMemory(mBaseMemory, REPEAT_TIME);
    }

    @Test
    public void testRange() {
        assertThat(mMirrorMemory.getRange().start).isEqualTo(BASE_OFFSET);
        assertThat(mMirrorMemory.getRange().end).isEqualTo(BASE_OFFSET + (BASE_LENGTH * REPEAT_TIME));
    }

    @Test
    public void testWriteOnBase() {
        byte value = 12;
        for (int address = BASE_OFFSET; address < BASE_LENGTH + BASE_OFFSET; address++) {
            mBaseMemory.write(address, value);
            for (int mirrorIndex = 0; mirrorIndex < REPEAT_TIME; mirrorIndex++) {
                assertThat(mMirrorMemory.read(address + BASE_LENGTH * mirrorIndex)).isEqualTo(value);
            }
            value++;
        }
    }

    @Test
    public void testWriteOnMirrors() {
        byte value = 13;
        for (int address = BASE_OFFSET; address < BASE_LENGTH + BASE_OFFSET; address++) {
            for (int mirrorIndex = 0; mirrorIndex < REPEAT_TIME; mirrorIndex++) {
                mMirrorMemory.write(address + BASE_LENGTH * mirrorIndex, value);
                assertThat(mBaseMemory.read(address)).isEqualTo(value);
                for (int mirrorIndexInner = 0; mirrorIndexInner < REPEAT_TIME; mirrorIndexInner++) {
                    assertThat(mMirrorMemory.read(address + BASE_LENGTH * mirrorIndex)).isEqualTo(value);
                }
            }
        }
    }
}
