package hardware.ppu.registers;

public class MaskReader {
    public static boolean showBackground(PpuRegisters registers) {
        return ByteHelper.bit3(registers.getMask());
    }

    public static boolean showSprites(PpuRegisters registers) {
        return ByteHelper.bit4(registers.getMask());
    }
}
