package hardware.ppu.memory;

import components.memory.CompositeMemory;

public class NameTables extends CompositeMemory {

    public static final int NT_0 = 0x2000;
    public static final int NT_1 = 0x2400;
    public static final int NT_2 = 0x2800;
    public static final int NT_3 = 0x2C00;

    public static NameTables construct() {
        return new NameTables(
                new NameTable(NT_0),
                new NameTable(NT_1),
                new NameTable(NT_2),
                new NameTable(NT_3)
        );
    }

    private NameTables(
            NameTable table1,
            NameTable table2,
            NameTable table3,
            NameTable table4) {
        super(table1, table2, table3, table4);
    }
}
