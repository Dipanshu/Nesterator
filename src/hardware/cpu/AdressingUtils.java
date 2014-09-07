package hardware.cpu;

import components.memory.Memory;
import hardware.cpu.registers.Registers;

public class AdressingUtils {

    private final Cpu mCpu;
    private final Memory mMemory;
    private final Registers mRegisters;

    public AdressingUtils(Cpu cpu, Memory memory, Registers registers) {
        mCpu = cpu;
        mMemory = memory;
        mRegisters = registers;
    }

    protected static enum AddressingMode {
        ACCUMULATOR,
        IMPLIED,
        IMMEDIATE,
        ABSOLUTE,
        ZEROPAGE,
        RELATIVE,
        ABSOLUTE_X,
        ABSOLUTE_Y,
        ZEROPAGE_X,
        ZEROPAGE_Y,
        ZEROPAGE_INDIRECT_X,
        ZEROPAGE_INDIRECT_Y,
        INDIRECT,;
    }

    public int getAddressForOperand(AddressingMode mode) {
        switch (mode) {
            case ABSOLUTE: {
                int operandRefLow = mCpu.readByte();
                int operandRefHigh = mCpu.readByte();
                return (operandRefHigh << 8) | operandRefLow;
            }
            case INDIRECT: {
                int operandRefLow = mCpu.readByte();
                int operandRefHigh = mCpu.readByte();
                int addressForLow = (operandRefHigh << 8) | operandRefLow;
                int addressForHigh = (operandRefHigh << 8) | ((operandRefLow + 1) & 0xFF);
                int addressLow = mMemory.read(addressForLow);
                int addressHigh = mMemory.read(addressForHigh);
                return (addressHigh << 8) | addressLow;
            }
            case ZEROPAGE: {
                return mCpu.readByte();
            }
            case ABSOLUTE_X: {
                int operandRefLow = mCpu.readByte();
                int operandRefHigh = mCpu.readByte();
                int address = ((operandRefHigh << 8) | operandRefLow) + mRegisters.X;
                return address & 0xFFFF;
            }
            case ABSOLUTE_Y: {
                int operandRefLow = mCpu.readByte();
                int operandRefHigh = mCpu.readByte();
                int address = ((operandRefHigh << 8) | operandRefLow) + mRegisters.Y;
                return address & 0xFFFF;
            }
            case ZEROPAGE_X: {
                int operandRefLow = mCpu.readByte();
                return ((operandRefLow + mRegisters.X) & 0xFF);

            }
            case ZEROPAGE_Y: {
                int operandRefLow = mCpu.readByte();
                return ((operandRefLow + mRegisters.Y) & 0xFF);
            }
            case ZEROPAGE_INDIRECT_X: {
                int addressForAddress = (mCpu.readByte() + mRegisters.X) & 0xFF;
                int operandRefLow = mMemory.read(addressForAddress);
                int operandRefHigh = mMemory.read((addressForAddress + 1)  & 0xFF);
                return (operandRefHigh << 8) | operandRefLow;
            }
            case ZEROPAGE_INDIRECT_Y: {
                int addressLow = mCpu.readByte() & 0xFF;
                int addressHigh = (addressLow + 1) & 0xFF;
                int operandRefLow = mMemory.read(addressLow);
                int operandRefHigh = mMemory.read(addressHigh);
                int address = ((operandRefHigh << 8) | operandRefLow) + mRegisters.Y;
                return address & 0xFFFF;
            }

        }
        throw new IllegalArgumentException();
    }

    public int getOperand(AddressingMode addressingMode, Cpu cpu) {
        switch (addressingMode) {
            case ACCUMULATOR: {
                return mRegisters.A;
            }
            case IMMEDIATE: {
                int operand = cpu.readByte();
                return operand;
            }
            case ZEROPAGE:
            case ABSOLUTE:
            case ABSOLUTE_X:
            case ABSOLUTE_Y:
            case ZEROPAGE_X:
            case ZEROPAGE_Y:
            case ZEROPAGE_INDIRECT_X:
            case ZEROPAGE_INDIRECT_Y: {
                return mMemory.read(getAddressForOperand(addressingMode));
            }
        }
        throw new IllegalArgumentException();
    }
}
