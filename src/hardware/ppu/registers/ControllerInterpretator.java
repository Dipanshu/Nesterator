package hardware.ppu.registers;

import hardware.ppu.memory.NameTables;
import hardware.ppu.memory.PatternTables;

public class ControllerInterpretator {
    public static int getBaseNameTableAddress(int controller) {
        switch (controller & 0x03) {
            case 0:
                return NameTables.NT_0;
            case 1:
                return NameTables.NT_1;
            case 2:
                return NameTables.NT_2;
            case 3:
                return NameTables.NT_3;
        }
        throw new IllegalArgumentException();
    }

    public static int getVRAMIncrement(int controller) {
        return ByteHelper.bit2(controller) ? 0x32 : 0x01;
    }

    public static int getSpritePatternTableAddress(int controller) {
        return ByteHelper.bit3(controller) ? PatternTables.PATTERN_TABLE_1 : PatternTables.PATTERN_TABLE_0;
    }

    public static int getBackgroundPatternTable(int controller) {
        return ByteHelper.bit4(controller) ? PatternTables.PATTERN_TABLE_1 : PatternTables.PATTERN_TABLE_0;
    }

    public static boolean generateNMI(int controller) {
        return ByteHelper.bit7(controller);
    }
}
