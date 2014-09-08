package hardware.ppu.memory;

import components.memory.CompositeMemory;

public class AttributeNameTables extends CompositeMemory {

    public static AttributeNameTables construct() {
        return new AttributeNameTables(
                new AttributeNameTable(0x2000),
                new AttributeNameTable(0x2400),
                new AttributeNameTable(0x2800),
                new AttributeNameTable(0x2C00)
        );
    }

    private AttributeNameTables(
            AttributeNameTable table1,
            AttributeNameTable table2,
            AttributeNameTable table3,
            AttributeNameTable table4) {
        super(table1, table2, table3, table4);
    }
}
