package components.memory;

import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

public class MemoryBlockTest {

    public static final int OFFSET = 40;
    public static final int SIZE = 64;

    private MemoryBlock mMemoryBlock;

    @Before
    public void setup() {
        mMemoryBlock = new MemoryBlock(OFFSET, SIZE);
    }

    @Test
    public void testReadWrite() throws Exception {
        mMemoryBlock.write(OFFSET + 3, (byte) 4);
        assertThat(mMemoryBlock.read(OFFSET + 3)).isEqualTo((byte) 4);
    }

    @Test
    public void testGetRange() throws Exception {
        assertThat(mMemoryBlock.getRange().start).isEqualTo(OFFSET);
        assertThat(mMemoryBlock.getRange().end).isEqualTo(OFFSET + SIZE);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testIllegalAccess() {
        mMemoryBlock.read(OFFSET + SIZE + 1);
    }
}
