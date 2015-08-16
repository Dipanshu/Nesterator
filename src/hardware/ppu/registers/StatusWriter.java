package hardware.ppu.registers;

public class StatusWriter {

    public static void clearSpriteOverflow(PpuRegisters registers) {
        registers.setStatus(ByteHelper.clearByte5(registers.getStatus()));
    }

    public static void setSpriteOverflow(PpuRegisters registers) {
        registers.setStatus(ByteHelper.setByte5(registers.getStatus()));
    }

    public static void clearSprite0Hit(PpuRegisters registers) {
        registers.setStatus(ByteHelper.clearByte6(registers.getStatus()));
    }

    public static void setSprite0Hit(PpuRegisters registers) {
        registers.setStatus(ByteHelper.setByte6(registers.getStatus()));
    }

    public static void clearVBlank(PpuRegisters registers) {
        registers.setStatus(ByteHelper.clearByte7(registers.getStatus()));
    }

    public static void setVBlank(PpuRegisters registers) {
        registers.setStatus(ByteHelper.setByte7(registers.getStatus()));
    }
}

