package hardware.ppu.registers;

import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;

public class ByteHelperTest {

    private int mByte;

    @Before
    public void setUp() throws Exception {
        mByte = 0b11111111;
    }

    @Test
    public void testClearing() {
        assertThat(ByteHelper.bit0(mByte)).isTrue();
        mByte = ByteHelper.clearByte0(mByte);
        assertThat(ByteHelper.bit0(mByte)).isFalse();
        mByte =ByteHelper.setByte0(mByte);
        assertThat(ByteHelper.bit0(mByte)).isTrue();

        assertThat(ByteHelper.bit1(mByte)).isTrue();
        mByte = ByteHelper.clearByte1(mByte);
        assertThat(ByteHelper.bit1(mByte)).isFalse();
        mByte =ByteHelper.setByte1(mByte);
        assertThat(ByteHelper.bit1(mByte)).isTrue();

        assertThat(ByteHelper.bit2(mByte)).isTrue();
        mByte = ByteHelper.clearByte2(mByte);
        assertThat(ByteHelper.bit2(mByte)).isFalse();
        mByte =ByteHelper.setByte2(mByte);
        assertThat(ByteHelper.bit2(mByte)).isTrue();

        assertThat(ByteHelper.bit3(mByte)).isTrue();
        mByte = ByteHelper.clearByte3(mByte);
        assertThat(ByteHelper.bit3(mByte)).isFalse();
        mByte =ByteHelper.setByte3(mByte);
        assertThat(ByteHelper.bit3(mByte)).isTrue();

        assertThat(ByteHelper.bit4(mByte)).isTrue();
        mByte = ByteHelper.clearByte4(mByte);
        assertThat(ByteHelper.bit4(mByte)).isFalse();
        mByte =ByteHelper.setByte4(mByte);
        assertThat(ByteHelper.bit4(mByte)).isTrue();

        assertThat(ByteHelper.bit5(mByte)).isTrue();
        mByte = ByteHelper.clearByte5(mByte);
        assertThat(ByteHelper.bit5(mByte)).isFalse();
        mByte =ByteHelper.setByte5(mByte);
        assertThat(ByteHelper.bit5(mByte)).isTrue();

        assertThat(ByteHelper.bit6(mByte)).isTrue();
        mByte = ByteHelper.clearByte6(mByte);
        assertThat(ByteHelper.bit6(mByte)).isFalse();
        mByte =ByteHelper.setByte6(mByte);
        assertThat(ByteHelper.bit6(mByte)).isTrue();

        assertThat(ByteHelper.bit7(mByte)).isTrue();
        mByte = ByteHelper.clearByte7(mByte);
        assertThat(ByteHelper.bit7(mByte)).isFalse();
        mByte =ByteHelper.setByte7(mByte);
        assertThat(ByteHelper.bit7(mByte)).isTrue();
    }
}