package hardware.cpu.registers;

public class Registers {
    public int PC;
    public int SP;
    public int A;
    public int X;
    public int Y;
    public final StatusRegister flags;


    public Registers() {
        flags = new StatusRegister();
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append(String.format("A:%02X ", (A & 0xFF)))
                .append(String.format("X:%02X ", (X & 0xFF)))
                .append(String.format("Y:%02X ", (Y & 0xFF)))
                .append(String.format("P:%02X ", (flags.getRegister() & 0xFF)))
                .append(String.format("SP:%02X ", (SP & 0xFF)))
                .toString();

    }
}
