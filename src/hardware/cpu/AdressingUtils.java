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

    protected enum AddressingMode {
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

    public enum OperationType {
        READ,
        READ_WRITE,
        WRITE
    }

    public int getAddressForOperand(AddressingMode mode, OperationType operationType) {
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
                final int baseAddress = (operandRefHigh << 8) | operandRefLow;
                int address = baseAddress + mRegisters.X;
                if ((operationType != OperationType.READ) || ((address & 0xFF00) != (baseAddress & 0xFF00))) {
                    mCpu.onPageBoundaryCrossed();
                }
                return address & 0xFFFF;
            }
            case ABSOLUTE_Y: {
                int operandRefLow = mCpu.readByte();
                int operandRefHigh = mCpu.readByte();
                final int baseAddress = (operandRefHigh << 8) | operandRefLow;
                int address = baseAddress + mRegisters.Y;
                if ((operationType != OperationType.READ) || ((address & 0xFF00) != (baseAddress & 0xFF00))) {
                    mCpu.onPageBoundaryCrossed();
                }
                return address & 0xFFFF;
            }
            case ZEROPAGE_X: {
                final int pointer = mCpu.readByte();
                mCpu.dummyRead(pointer);
                return ((pointer + mRegisters.X) & 0xFF);

            }
            case ZEROPAGE_Y: {
                final int pointer = mCpu.readByte();
                mCpu.dummyRead(pointer);
                return ((pointer + mRegisters.Y) & 0xFF);
            }
            case ZEROPAGE_INDIRECT_X: {
                final int pointer = mCpu.readByte();
                mCpu.dummyRead(pointer);
                int addressForAddress = (pointer + mRegisters.X) & 0xFF;
                int operandRefLow = mMemory.read(addressForAddress);
                int operandRefHigh = mMemory.read((addressForAddress + 1)  & 0xFF);
                return (operandRefHigh << 8) | operandRefLow;
            }
            case ZEROPAGE_INDIRECT_Y: {
                int addressLow = mCpu.readByte() & 0xFF;
                int addressHigh = (addressLow + 1) & 0xFF;
                int operandRefLow = mMemory.read(addressLow);
                int operandRefHigh = mMemory.read(addressHigh);
                final int baseAddress = (operandRefHigh << 8) | operandRefLow;
                int address = baseAddress + mRegisters.Y;
                if ((operationType != OperationType.READ) || ((address & 0xFF00) != (baseAddress & 0xFF00))) {
                    mCpu.onPageBoundaryCrossed();
                }
                return address & 0xFFFF;
            }

        }
        throw new IllegalArgumentException();
    }

    public int getOperand(AddressingMode addressingMode, Cpu cpu, OperationType operationType) {
        switch (addressingMode) {
            case ACCUMULATOR: {
                return mRegisters.A;
            }
            case IMMEDIATE: {
                return cpu.readByte();
            }
            case ZEROPAGE:
            case ABSOLUTE:
            case ABSOLUTE_X:
            case ABSOLUTE_Y:
            case ZEROPAGE_X:
            case ZEROPAGE_Y:
            case ZEROPAGE_INDIRECT_X:
            case ZEROPAGE_INDIRECT_Y: {
                return mMemory.read(getAddressForOperand(addressingMode, operationType));
            }
        }
        throw new IllegalArgumentException();
    }
}
