package hardware.ppu.registers;

class ByteHelper {

    private static final int BYTE_0 = 0b00000001;
    private static final int BYTE_1 = 0b00000010;
    private static final int BYTE_2 = 0b00000100;
    private static final int BYTE_3 = 0b00001000;
    private static final int BYTE_4 = 0b00010000;
    private static final int BYTE_5 = 0b00100000;
    private static final int BYTE_6 = 0b01000000;
    private static final int BYTE_7 = 0b10000000;

    public static boolean bit0(int value) {
        return (value & BYTE_0) != 0;
    }

    public static boolean bit1(int value) {
        return (value & BYTE_1) != 0;
    }

    public static boolean bit2(int value) {
        return (value & BYTE_2) != 0;
    }

    public static boolean bit3(int value) {
        return (value & BYTE_3) != 0;
    }

    public static boolean bit4(int value) {
        return (value & BYTE_4) != 0;
    }

    public static boolean bit5(int value) {
        return (value & BYTE_5) != 0;
    }

    public static boolean bit6(int value) {
        return (value & BYTE_6) != 0;
    }

    public static boolean bit7(int value) {
        return (value & BYTE_7) != 0;
    }

    public static int setByte0(int value) {
        return value | BYTE_0;
    }

    public static int setByte1(int value) {
        return value | BYTE_1;
    }

    public static int setByte2(int value) {
        return value | BYTE_2;
    }

    public static int setByte3(int value) {
        return value | BYTE_3;
    }

    public static int setByte4(int value) {
        return value | BYTE_4;
    }

    public static int setByte5(int value) {
        return value | BYTE_5;
    }

    public static int setByte6(int value) {
        return value | BYTE_6;
    }

    public static int setByte7(int value) {
        return value | BYTE_7;
    }

    public static int clearByte0(int value) {
        return value & ~BYTE_0;
    }

    public static int clearByte1(int value) {
        return value & ~BYTE_1;
    }

    public static int clearByte2(int value) {
        return value & ~BYTE_2;
    }

    public static int clearByte3(int value) {
        return value & ~BYTE_3;
    }

    public static int clearByte4(int value) {
        return value & ~BYTE_4;
    }

    public static int clearByte5(int value) {
        return value & ~BYTE_5;
    }

    public static int clearByte6(int value) {
        return value & ~BYTE_6;
    }

    public static int clearByte7(int value) {
        return value & ~BYTE_7;
    }

}
