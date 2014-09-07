package hardware.cpu;

import components.memory.Memory;
import hardware.cpu.registers.Registers;
import hardware.cpu.registers.StatusRegister;

import static hardware.cpu.AdressingUtils.AddressingMode.*;

public class Cpu {
    public static final int SIGN_BIT_MASK = (1 << StatusRegister.SIGN);

    private boolean mDebugMode = false;
    private StringBuilder mDebugLog;
    private int mPCReadCounter = 0;

    private final Memory mMemory;
    private final Registers mRegisters;
    private final AdressingUtils mAdressingUtils;

    public Cpu(Memory memory) {
        mRegisters = new Registers();
        mMemory = memory;
        mRegisters.PC = 0xC000;
        mRegisters.SP = 0xFD;
        mAdressingUtils = new AdressingUtils(this, mMemory, mRegisters);
    }

    public boolean isDebugMode() {
        return mDebugMode;
    }

    public void setDebugMode(boolean debugMode) {
        mDebugMode = debugMode;
    }

    private void branchRelative() {
        byte operandDelta = (byte) readByte();
        mRegisters.PC += operandDelta;
        mRegisters.PC &= 0xFFFF;
    }

    private void throwAwayBytes(int num) {
        for (int i = 0; i < num; i++) {
            readByte();
        }
    }

    protected int readByte() {
        int readValue = mMemory.read(mRegisters.PC);
        mRegisters.PC++;
        mRegisters.PC &= 0xFFFF;
        debug(String.format("  %02X", readValue));
        mPCReadCounter++;
        return readValue;
    }

    private void setZero(int intValue) {
        mRegisters.flags.setFlag(StatusRegister.ZERO, (intValue & 0xFF) == 0);
    }

    private void setSign(int intValue) {
        mRegisters.flags.setFlag(StatusRegister.SIGN, (intValue & 0b10000000) != 0);
    }

    private void setCarry(int value) {
        mRegisters.flags.setFlag(StatusRegister.CARRY, value > 0xFF);
    }

    private void ADC(int operand) {
        int value = mRegisters.A + operand + mRegisters.flags.getCarry();
        setCarry(value);
        value &= 0xFF;
        mRegisters.flags.setFlag(StatusRegister.OVERFLOW,
                ((mRegisters.A & SIGN_BIT_MASK) == (operand & SIGN_BIT_MASK)) &&
                        ((mRegisters.A & SIGN_BIT_MASK) != (value & SIGN_BIT_MASK))
        );
        setSign(value);
        setZero(value);
        value &= 0xFF;
        mRegisters.A = value;
    }

    private void SBC(int operand) {
        mRegisters.A = getSubtractResultAndSetFlags(mRegisters.A, operand);
    }

    private int compare(int firstOperand, int secondOperand) {
        int value = firstOperand - secondOperand;
        setSign(value);
        setZero(value);
        mRegisters.flags.setFlag(StatusRegister.CARRY, !(firstOperand < secondOperand));
        value &= 0xFF;
        return value;
    }

    private int getSubtractResultAndSetFlags(int firstOperand, int secondOperand) {
        int value = firstOperand - secondOperand - mRegisters.flags.getBorrow();
        mRegisters.flags.setFlag(StatusRegister.OVERFLOW,
                (firstOperand & SIGN_BIT_MASK) != (secondOperand & SIGN_BIT_MASK) &&
                        ((firstOperand & SIGN_BIT_MASK) != (value & SIGN_BIT_MASK))
        );
        setSign(value);
        setZero(value);
        mRegisters.flags.setFlag(StatusRegister.CARRY, !(firstOperand < secondOperand));
        value &= 0xFF;
        return value;
    }

    private int decrement(int operand) {
        int result = (operand - 1) & 0xFF;
        setSign(result);
        setZero(result);
        return result;
    }

    private int increment(int operand) {
        int result = (operand + 1) & 0xFF;
        setSign(result);
        setZero(result);
        return result;
    }

    private void AND(int operand) {
        int value = mRegisters.A & operand;
        setSign(value);
        setZero(value);
        mRegisters.A = value;
    }

    private void AAC(int operand) {
        int value = mRegisters.A & operand;
        setSign(value);
        setZero(value);
        mRegisters.flags.setFlag(StatusRegister.CARRY, mRegisters.flags.getFlag(StatusRegister.SIGN));
    }

    private void AAX(int adddressToStoreTo) {
        int value = mRegisters.A & mRegisters.X;
        mMemory.write(adddressToStoreTo, value);
    }

    private void LAX(int operand) {
        mRegisters.A = operand;
        mRegisters.X = operand;
        setSign(operand);
        setZero(operand);
    }

    private void OR(int operand) {
        int value = mRegisters.A | operand;
        setSign(value);
        setZero(value);
        mRegisters.A = value;
    }

    private int XOR(int operand) {
        int value = mRegisters.A ^ operand;
        setSign(value);
        setZero(value);
        value &= 0xFF;
        mRegisters.A = value;
        return value;
    }

    private int ASL(int operand) {
        int tempValue = operand;
        tempValue = tempValue << 1;
        setSign(tempValue);
        setZero(tempValue);
        setCarry(tempValue);
        tempValue &= 0xFF;
        return tempValue;
    }

    private int LSR(int operand) {
        mRegisters.flags.setFlag(StatusRegister.CARRY, (operand & 0x01) > 0);
        int tempValue = operand >>> 1;
        setZero(tempValue);
        setSign(tempValue);
        tempValue &= 0xFF;
        return tempValue;
    }

    private int ROL(int operand) {
        int previousCarry = mRegisters.flags.getCarry();
        mRegisters.flags.setFlag(StatusRegister.CARRY, (operand & 0b10000000) > 0);
        operand = operand << 1;
        operand += previousCarry;
        setZero(operand);
        setSign(operand);
        operand &= 0xFF;
        return operand;
    }

    private int ROR(int operand) {
        int previousCarry = mRegisters.flags.getCarry();
        mRegisters.flags.setFlag(StatusRegister.CARRY, (operand & 0b00000001) > 0);
        operand = operand >>> 1;
        operand |= previousCarry << 7;
        setZero(operand);
        setSign(operand);
        operand &= 0xFF;
        return operand;
    }

    private void BIT(int operand) {
        int intResult = operand & mRegisters.A;
        setZero(intResult);
        setSign(operand);
        mRegisters.flags.setFlag(StatusRegister.OVERFLOW, (operand & 0b01000000) != 0);
    }

    private void CMP(int operand) {
        compare(mRegisters.A, operand);
    }

    private void CPX(int operand) {
        compare(mRegisters.X, operand);
    }

    private void CPY(int operand) {
        compare(mRegisters.Y, operand);
    }

    private void DCP(AdressingUtils.AddressingMode addressingMode) {
        int address = mAdressingUtils.getAddressForOperand(addressingMode);
        final int decrementedValue = decrement(mMemory.read(address));
        mMemory.write(address, decrementedValue);
        CMP(decrementedValue);
    }

    private void ISC(AdressingUtils.AddressingMode addressingMode) {
        int address = mAdressingUtils.getAddressForOperand(addressingMode);
        final int incrementedValue = increment(mMemory.read(address));
        mMemory.write(address, incrementedValue);
        SBC(incrementedValue);
    }

    private void SLO(AdressingUtils.AddressingMode addressingMode) {
        int address = mAdressingUtils.getAddressForOperand(addressingMode);
        final int aslValue = ASL(mMemory.read(address));
        mMemory.write(address, aslValue);
        OR(aslValue);
    }

    private void RLA(AdressingUtils.AddressingMode addressingMode) {
        int address = mAdressingUtils.getAddressForOperand(addressingMode);
        final int rolValue = ROL(mMemory.read(address));
        mMemory.write(address, rolValue);
        AND(rolValue);
    }

    private void SRE(AdressingUtils.AddressingMode addressingMode) {
        int address = mAdressingUtils.getAddressForOperand(addressingMode);
        final int lsrValue = LSR(mMemory.read(address));
        mMemory.write(address, lsrValue);
        XOR(lsrValue);
    }

    private void RRA(AdressingUtils.AddressingMode addressingMode) {
        int address = mAdressingUtils.getAddressForOperand(addressingMode);
        final int rorValue = ROR(mMemory.read(address));
        mMemory.write(address, rorValue);
        ADC(rorValue);
    }

    private void DEC(AdressingUtils.AddressingMode addressingMode) {
        int address = mAdressingUtils.getAddressForOperand(addressingMode);
        int value = decrement(mMemory.read(address));
        mMemory.write(address, value);
    }

    private void INC(AdressingUtils.AddressingMode addressingMode) {
        int address = mAdressingUtils.getAddressForOperand(addressingMode);
        int value = increment(mMemory.read(address));
        mMemory.write(address, value);
    }

    private void JMP(int addressOfAddress) {
        mRegisters.PC = addressOfAddress;
    }

    private void push(int operand) {
        mMemory.write(0x0100 | mRegisters.SP, operand);
        mRegisters.SP--;
        mRegisters.SP &= 0xFF;
    }

    private int pop() {
        mRegisters.SP++;
        return mMemory.read(0x0100 | mRegisters.SP);
    }

    private void JSR(int addressToJumpTo) {
        mRegisters.PC -= 1;
        mRegisters.PC &= 0xFFFF;
        push(mRegisters.PC >> 8);
        push(mRegisters.PC);
        JMP(addressToJumpTo);
    }

    private void LDA(int operand) {
        mRegisters.A = operand;
        setZero(operand);
        setSign(operand);
    }

    private void LDX(int operand) {
        mRegisters.X = operand;
        setZero(operand);
        setSign(operand);
    }

    private void LDY(int operand) {
        mRegisters.Y = operand;
        setZero(operand);
        setSign(operand);
    }


    private void doASL(AdressingUtils.AddressingMode addressingMode) {
        int address = mAdressingUtils.getAddressForOperand(addressingMode);
        int value = ASL(mMemory.read(address));
        mMemory.write(address, value);
    }

    private void doLSR(AdressingUtils.AddressingMode addressingMode) {
        int address = mAdressingUtils.getAddressForOperand(addressingMode);
        int value = LSR(mMemory.read(address));
        mMemory.write(address, value);
    }

    private void doROR(AdressingUtils.AddressingMode addressingMode) {
        int address = mAdressingUtils.getAddressForOperand(addressingMode);
        int value = ROR(mMemory.read(address));
        mMemory.write(address, value);
    }


    private void doROL(AdressingUtils.AddressingMode addressingMode) {
        int address = mAdressingUtils.getAddressForOperand(addressingMode);
        int value = ROL(mMemory.read(address));
        mMemory.write(address, value);
    }


    private void debug(String message) {
        if (isDebugMode()) {
            if (this.mDebugLog == null) {
                mDebugLog = new StringBuilder();
            }
            mDebugLog.append(message);
        }
    }

    private void breakOn(int on) {
        if ((mRegisters.PC & 0xFFFF) == on) {
            // Breakpoint
            System.out.print("");
        }
    }

    public void process() {
        breakOn(0xE92E);
        mDebugLog = new StringBuilder();
        debug(String.format("%04X", mRegisters.PC));
        mPCReadCounter = 0;
        int instruction = readByte();
        String registerInfo = mRegisters.toString();
        boolean unknownOpCode = false;
        switch (instruction) {
            /**
             * AAC (Undocumented)
             */
            case 0x0B:
            case 0x2B:
                AAC(mAdressingUtils.getOperand(IMMEDIATE, this));
                break;

            /**
             * AAX (Undocumented)
             */
            case 0x87:
                AAX(mAdressingUtils.getAddressForOperand(ZEROPAGE));
                break;
            case 0x97:
                AAX(mAdressingUtils.getAddressForOperand(ZEROPAGE_Y));
                break;
            case 0x83:
                AAX(mAdressingUtils.getAddressForOperand(ZEROPAGE_INDIRECT_X));
                break;
            case 0x8F:
                AAX(mAdressingUtils.getAddressForOperand(ABSOLUTE));
                break;
            /**
             * ADC
             */
            case 0x69:
                ADC(mAdressingUtils.getOperand(IMMEDIATE, this));
                break;
            case 0x65:
                ADC(mAdressingUtils.getOperand(ZEROPAGE, this));
                break;
            case 0x75:
                ADC(mAdressingUtils.getOperand(ZEROPAGE_X, this));
                break;
            case 0x6D:
                ADC(mAdressingUtils.getOperand(ABSOLUTE, this));
                break;
            case 0x7D:
                ADC(mAdressingUtils.getOperand(ABSOLUTE_X, this));
                break;
            case 0x79:
                ADC(mAdressingUtils.getOperand(ABSOLUTE_Y, this));
                break;
            case 0x61:
                ADC(mAdressingUtils.getOperand(ZEROPAGE_INDIRECT_X, this));
                break;
            case 0x71:
                ADC(mAdressingUtils.getOperand(ZEROPAGE_INDIRECT_Y, this));
                break;

            /**
             * AND
             */
            case 0x29:
                AND(mAdressingUtils.getOperand(IMMEDIATE, this));
                break;
            case 0x25:
                AND(mAdressingUtils.getOperand(ZEROPAGE, this));
                break;
            case 0x35:
                AND(mAdressingUtils.getOperand(ZEROPAGE_X, this));
                break;
            case 0x2D:
                AND(mAdressingUtils.getOperand(ABSOLUTE, this));
                break;
            case 0x3D:
                AND(mAdressingUtils.getOperand(ABSOLUTE_X, this));
                break;
            case 0x39:
                AND(mAdressingUtils.getOperand(ABSOLUTE_Y, this));
                break;
            case 0x21:
                AND(mAdressingUtils.getOperand(ZEROPAGE_INDIRECT_X, this));
                break;
            case 0x31:
                AND(mAdressingUtils.getOperand(ZEROPAGE_INDIRECT_Y, this));
                break;

            /**
             * ASL
             */
            case 0x0A:
                mRegisters.A = ASL(mAdressingUtils.getOperand(ACCUMULATOR, this));
                break;
            case 0x06:
                doASL(ZEROPAGE);
                break;
            case 0x16:
                doASL(ZEROPAGE_X);
                break;
            case 0x0E:
                doASL(ABSOLUTE);
                break;
            case 0x1E:
                doASL(ABSOLUTE_X);
                break;

            /**
             * Branching
             */
            case 0x90: // Branch on carry clear
                if (!mRegisters.flags.getFlag(StatusRegister.CARRY)) {
                    branchRelative();
                } else {
                    throwAwayBytes(1);
                }
                break;
            case 0xB0: // Branch on carry set
                if (mRegisters.flags.getFlag(StatusRegister.CARRY)) {
                    branchRelative();
                } else {
                    throwAwayBytes(1);
                }
                break;
            case 0x30: // Branch on result minus
                if (mRegisters.flags.getFlag(StatusRegister.SIGN)) {
                    branchRelative();
                } else {
                    throwAwayBytes(1);
                }
                break;
            case 0xF0: // Branch on result zero
                if (mRegisters.flags.getFlag(StatusRegister.ZERO)) {
                    branchRelative();
                } else {
                    throwAwayBytes(1);
                }
                break;
            case 0xD0: // Branch on result not zero
                if (!mRegisters.flags.getFlag(StatusRegister.ZERO)) {
                    branchRelative();
                } else {
                    throwAwayBytes(1);
                }
                break;
            case 0x10: // Branch on result plus
                if (!mRegisters.flags.getFlag(StatusRegister.SIGN)) {
                    branchRelative();
                } else {
                    throwAwayBytes(1);
                }
                break;
            case 0x50: // Branch on overflow clear
                if (!mRegisters.flags.getFlag(StatusRegister.OVERFLOW)) {
                    branchRelative();
                } else {
                    throwAwayBytes(1);
                }
                break;
            case 0x70: // Branch on overflow set
                if (mRegisters.flags.getFlag(StatusRegister.OVERFLOW)) {
                    branchRelative();
                } else {
                    throwAwayBytes(1);
                }
                break;


            /**
             * Bit tests
             */
            case 0x24:
                BIT(mAdressingUtils.getOperand(ZEROPAGE, this));
                break;
            case 0x2C:
                BIT(mAdressingUtils.getOperand(ABSOLUTE, this));
                break;

            /**
             * BRK
             */
            case 0x00: {
                mRegisters.flags.setFlag(StatusRegister.INTERRUPT, true);
                int flags = mRegisters.flags.getRegister();
                flags |= 1 << StatusRegister.BREAK;
                flags |= 1 << StatusRegister.NOT_USED;
                push(flags);

                mRegisters.PC += 2;
                push(mRegisters.PC >> 8);
                push(mRegisters.PC);

                break;
            }

            /**
             * Clear flags
             */
            case 0x18: // Clear carry
                mRegisters.flags.setFlag(StatusRegister.CARRY, false);
                break;
            case 0xD8: // Clear Decimal
                mRegisters.flags.setFlag(StatusRegister.DECIMAL, false);
                break;
            case 0x58: // Clear interrupt
                mRegisters.flags.setFlag(StatusRegister.INTERRUPT, false);
                break;
            case 0xB8: // Clear overflow
                mRegisters.flags.setFlag(StatusRegister.OVERFLOW, false);
                break;

            /**
             * Compare Accumulator with memory
             */
            case 0xC9:
                CMP(mAdressingUtils.getOperand(IMMEDIATE, this));
                break;
            case 0xC5:
                CMP(mAdressingUtils.getOperand(ZEROPAGE, this));
                break;
            case 0xD5:
                CMP(mAdressingUtils.getOperand(ZEROPAGE_X, this));
                break;
            case 0xCD:
                CMP(mAdressingUtils.getOperand(ABSOLUTE, this));
                break;
            case 0xDD:
                CMP(mAdressingUtils.getOperand(ABSOLUTE_X, this));
                break;
            case 0xD9:
                CMP(mAdressingUtils.getOperand(ABSOLUTE_Y, this));
                break;
            case 0xC1:
                CMP(mAdressingUtils.getOperand(ZEROPAGE_INDIRECT_X, this));
                break;
            case 0xD1:
                CMP(mAdressingUtils.getOperand(ZEROPAGE_INDIRECT_Y, this));
                break;

            /**
             * Compare X with Memeory
             */
            case 0xE0:
                CPX(mAdressingUtils.getOperand(IMMEDIATE, this));
                break;
            case 0xE4:
                CPX(mAdressingUtils.getOperand(ZEROPAGE, this));
                break;
            case 0xEC:
                CPX(mAdressingUtils.getOperand(ABSOLUTE, this));
                break;

            /**
             * Compare Y with Memeory
             */
            case 0xC0:
                CPY(mAdressingUtils.getOperand(IMMEDIATE, this));
                break;
            case 0xC4:
                CPY(mAdressingUtils.getOperand(ZEROPAGE, this));
                break;
            case 0xCC:
                CPY(mAdressingUtils.getOperand(ABSOLUTE, this));
                break;

            /**
             * DCP (Undocumented
             */
            case 0xC7:
                DCP(ZEROPAGE);
                break;
            case 0xD7:
                DCP(ZEROPAGE_X);
                break;
            case 0xCF:
                DCP(ABSOLUTE);
                break;
            case 0xDF:
                DCP(ABSOLUTE_X);
                break;
            case 0xDB:
                DCP(ABSOLUTE_Y);
                break;
            case 0xC3:
                DCP(ZEROPAGE_INDIRECT_X);
                break;
            case 0xD3:
                DCP(ZEROPAGE_INDIRECT_Y);
                break;

            /**
             * Decrement
             */
            case 0xC6:
                DEC(ZEROPAGE);
                break;
            case 0xD6:
                DEC(ZEROPAGE_X);
                break;
            case 0xCE:
                DEC(ABSOLUTE);
                break;
            case 0xDE:
                DEC(ABSOLUTE_X);
                break;
            case 0xCA:
                mRegisters.X = decrement(mRegisters.X);
                break;
            case 0x88:
                mRegisters.Y = decrement(mRegisters.Y);
                break;

            /**
             * XOR
             */
            case 0x49:
                XOR(mAdressingUtils.getOperand(IMMEDIATE, this));
                break;
            case 0x45:
                XOR(mAdressingUtils.getOperand(ZEROPAGE, this));
                break;
            case 0x55:
                XOR(mAdressingUtils.getOperand(ZEROPAGE_X, this));
                break;
            case 0x4D:
                XOR(mAdressingUtils.getOperand(ABSOLUTE, this));
                break;
            case 0x5D:
                XOR(mAdressingUtils.getOperand(ABSOLUTE_X, this));
                break;
            case 0x59:
                XOR(mAdressingUtils.getOperand(ABSOLUTE_Y, this));
                break;
            case 0x41:
                XOR(mAdressingUtils.getOperand(ZEROPAGE_INDIRECT_X, this));
                break;
            case 0x51:
                XOR(mAdressingUtils.getOperand(ZEROPAGE_INDIRECT_Y, this));
                break;

            /**
             * Increment
             */
            case 0xE6:
                INC(ZEROPAGE);
                break;
            case 0xF6:
                INC(ZEROPAGE_X);
                break;
            case 0xEE:
                INC(ABSOLUTE);
                break;
            case 0xFE:
                INC(ABSOLUTE_X);
                break;
            case 0xE8:
                mRegisters.X = increment(mRegisters.X);
                break;
            case 0xC8:
                mRegisters.Y = increment(mRegisters.Y);
                break;

            /**
             * ISC (Undoumented)
             */
            case 0xE7:
                ISC(ZEROPAGE);
                break;
            case 0xF7:
                ISC(ZEROPAGE_X);
                break;
            case 0xEF:
                ISC(ABSOLUTE);
                break;
            case 0xFF:
                ISC(ABSOLUTE_X);
                break;
            case 0xFB:
                ISC(ABSOLUTE_Y);
                break;
            case 0xE3:
                ISC(ZEROPAGE_INDIRECT_X);
                break;
            case 0xF3:
                ISC(ZEROPAGE_INDIRECT_Y);
                break;

            /**
             * Jump
             */
            case 0x4C:
                JMP(mAdressingUtils.getAddressForOperand(ABSOLUTE));
                break;
            case 0x6C:
                JMP(mAdressingUtils.getAddressForOperand(INDIRECT));
                break;
            case 0x20:
                JSR(mAdressingUtils.getAddressForOperand(ABSOLUTE));
                break;

            /**
             * LAX (Undocumented)
             */
            case 0xA7:
                LAX(mAdressingUtils.getOperand(ZEROPAGE, this));
                break;
            case 0xB7:
                LAX(mAdressingUtils.getOperand(ZEROPAGE_Y, this));
                break;
            case 0xAF:
                LAX(mAdressingUtils.getOperand(ABSOLUTE, this));
                break;
            case 0xBF:
                LAX(mAdressingUtils.getOperand(ABSOLUTE_Y, this));
                break;
            case 0xA3:
                LAX(mAdressingUtils.getOperand(ZEROPAGE_INDIRECT_X, this));
                break;
            case 0xB3:
                LAX(mAdressingUtils.getOperand(ZEROPAGE_INDIRECT_Y, this));
                break;

            /**
             * LDA
             */
            case 0xA9:
                LDA(mAdressingUtils.getOperand(IMMEDIATE, this));
                break;
            case 0xA5:
                LDA(mAdressingUtils.getOperand(ZEROPAGE, this));
                break;
            case 0xB5:
                LDA(mAdressingUtils.getOperand(ZEROPAGE_X, this));
                break;
            case 0xAD:
                LDA(mAdressingUtils.getOperand(ABSOLUTE, this));
                break;
            case 0xBD:
                LDA(mAdressingUtils.getOperand(ABSOLUTE_X, this));
                break;
            case 0xB9:
                LDA(mAdressingUtils.getOperand(ABSOLUTE_Y, this));
                break;
            case 0xA1:
                LDA(mAdressingUtils.getOperand(ZEROPAGE_INDIRECT_X, this));
                break;
            case 0xB1:
                LDA(mAdressingUtils.getOperand(ZEROPAGE_INDIRECT_Y, this));
                break;

            /**
             * LDX
             */
            case 0xA2:
                LDX(mAdressingUtils.getOperand(IMMEDIATE, this));
                break;
            case 0xA6:
                LDX(mAdressingUtils.getOperand(ZEROPAGE, this));
                break;
            case 0xB6:
                LDX(mAdressingUtils.getOperand(ZEROPAGE_Y, this));
                break;
            case 0xAE:
                LDX(mAdressingUtils.getOperand(ABSOLUTE, this));
                break;
            case 0xBE:
                LDX(mAdressingUtils.getOperand(ABSOLUTE_Y, this));
                break;

            /**
             * LDY
             */
            case 0xA0:
                LDY(mAdressingUtils.getOperand(IMMEDIATE, this));
                break;
            case 0xA4:
                LDY(mAdressingUtils.getOperand(ZEROPAGE, this));
                break;
            case 0xB4:
                LDY(mAdressingUtils.getOperand(ZEROPAGE_X, this));
                break;
            case 0xAC:
                LDY(mAdressingUtils.getOperand(ABSOLUTE, this));
                break;
            case 0xBC:
                LDY(mAdressingUtils.getOperand(ABSOLUTE_X, this));
                break;

            /**
             * LSR
             */
            case 0x4A:
                mRegisters.A = LSR(mAdressingUtils.getOperand(ACCUMULATOR, this));
                break;
            case 0x46:
                doLSR(ZEROPAGE);
                break;
            case 0x56:
                doLSR(ZEROPAGE_X);
                break;
            case 0x4E:
                doLSR(ABSOLUTE);
                break;
            case 0x5E:
                doLSR(ABSOLUTE_X);
                break;

            /**
             * No-op
             */
            case 0x1A:
            case 0x3A:
            case 0x5A:
            case 0x7A:
            case 0xDA:
            case 0xFA:
            case 0xEA:
                // Implied
                break;
            case 0x80:
            case 0x82:
            case 0xC2:
            case 0xE2:
            case 0x89:
                mAdressingUtils.getOperand(IMMEDIATE, this);
                break;
            case 0x0C:
                mAdressingUtils.getOperand(ABSOLUTE, this);
                break;
            case 0x1C:
            case 0x3C:
            case 0x5C:
            case 0x7C:
            case 0xDC:
            case 0xFC:
                mAdressingUtils.getOperand(ABSOLUTE_X, this);
                break;
            case 0x04:
            case 0x44:
            case 0x64:
                mAdressingUtils.getOperand(ZEROPAGE, this);
                break;
            case 0x14:
            case 0x34:
            case 0x54:
            case 0x74:
            case 0xD4:
            case 0xF4:
                mAdressingUtils.getOperand(ZEROPAGE_X, this);
                break;
            /**
             * OR
             */
            case 0x09:
                OR(mAdressingUtils.getOperand(IMMEDIATE, this));
                break;
            case 0x05:
                OR(mAdressingUtils.getOperand(ZEROPAGE, this));
                break;
            case 0x15:
                OR(mAdressingUtils.getOperand(ZEROPAGE_X, this));
                break;
            case 0x0D:
                OR(mAdressingUtils.getOperand(ABSOLUTE, this));
                break;
            case 0x1D:
                OR(mAdressingUtils.getOperand(ABSOLUTE_X, this));
                break;
            case 0x19:
                OR(mAdressingUtils.getOperand(ABSOLUTE_Y, this));
                break;
            case 0x01:
                OR(mAdressingUtils.getOperand(ZEROPAGE_INDIRECT_X, this));
                break;
            case 0x11:
                OR(mAdressingUtils.getOperand(ZEROPAGE_INDIRECT_Y, this));
                break;

            /**
             * Stack
             */
            case 0x48:
                push(mRegisters.A);
                break;
            case 0x08: {
                int flags = mRegisters.flags.getRegister();
                flags |= 1 << StatusRegister.BREAK;
                flags |= 1 << StatusRegister.NOT_USED;
                push(flags);
                break;
            }
            case 0x68:
                mRegisters.A = pop();
                setZero(mRegisters.A);
                setSign(mRegisters.A);
                break;
            case 0x28:
                mRegisters.flags.setRegister(pop());
                break;

            /**
             * ROL
             */
            case 0x2A:
                mRegisters.A = ROL(mAdressingUtils.getOperand(ACCUMULATOR, this));
                break;
            case 0x26:
                doROL(ZEROPAGE);
                break;
            case 0x36:
                doROL(ZEROPAGE_X);
                break;
            case 0x2E:
                doROL(ABSOLUTE);
                break;
            case 0x3E:
                doROL(ABSOLUTE_X);
                break;

            /**
             * ROR
             */
            case 0x6A:
                mRegisters.A = ROR(mAdressingUtils.getOperand(ACCUMULATOR, this));
                break;
            case 0x66:
                doROR(ZEROPAGE);
                break;
            case 0x76:
                doROR(ZEROPAGE_X);
                break;
            case 0x6E:
                doROR(ABSOLUTE);
                break;
            case 0x7E:
                doROR(ABSOLUTE_X);
                break;

            /**
             * RLA (Undocumented)
             */
            case 0x27:
                RLA(ZEROPAGE);
                break;
            case 0x37:
                RLA(ZEROPAGE_X);
                break;
            case 0x2F:
                RLA(ABSOLUTE);
                break;
            case 0x3F:
                RLA(ABSOLUTE_X);
                break;
            case 0x3B:
                RLA(ABSOLUTE_Y);
                break;
            case 0x23:
                RLA(ZEROPAGE_INDIRECT_X);
                break;
            case 0x33:
                RLA(ZEROPAGE_INDIRECT_Y);
                break;

            /**
             * RRA (Undocumented
             */
            case 0x67:
                RRA(ZEROPAGE);
                break;
            case 0x77:
                RRA(ZEROPAGE_X);
                break;
            case 0x6F:
                RRA(ABSOLUTE);
                break;
            case 0x7F:
                RRA(ABSOLUTE_X);
                break;
            case 0x7B:
                RRA(ABSOLUTE_Y);
                break;
            case 0x63:
                RRA(ZEROPAGE_INDIRECT_X);
                break;
            case 0x73:
                RRA(ZEROPAGE_INDIRECT_Y);
                break;

            /**
             * Return
             */
            case 0x40: {
                mRegisters.flags.setRegister(pop());
                int pcLow = pop();
                int pcHigh = pop();
                mRegisters.PC = (((pcHigh << 8) | pcLow) & 0xFFFF);
                break;
            }
            case 0x60: {
                int pcLow = pop();
                int pcHigh = pop();
                mRegisters.PC = ((((pcHigh << 8) | pcLow) + 1) & 0xFFFF);
                break;
            }

            /**
             * SLO (Undocumented)
             */
            case 0x07:
                SLO(ZEROPAGE);
                break;
            case 0x17:
                SLO(ZEROPAGE_X);
                break;
            case 0x0F:
                SLO(ABSOLUTE);
                break;
            case 0x1F:
                SLO(ABSOLUTE_X);
                break;
            case 0x1B:
                SLO(ABSOLUTE_Y);
                break;
            case 0x03:
                SLO(ZEROPAGE_INDIRECT_X);
                break;
            case 0x13:
                SLO(ZEROPAGE_INDIRECT_Y);
                break;

            /**
             * SBC
             */
            case 0xE9:
            case 0xEB: // Undocumented
                SBC(mAdressingUtils.getOperand(IMMEDIATE, this));
                break;
            case 0xE5:
                SBC(mAdressingUtils.getOperand(ZEROPAGE, this));
                break;
            case 0xF5:
                SBC(mAdressingUtils.getOperand(ZEROPAGE_X, this));
                break;
            case 0xED:
                SBC(mAdressingUtils.getOperand(ABSOLUTE, this));
                break;
            case 0xFD:
                SBC(mAdressingUtils.getOperand(ABSOLUTE_X, this));
                break;
            case 0xF9:
                SBC(mAdressingUtils.getOperand(ABSOLUTE_Y, this));
                break;
            case 0xE1:
                SBC(mAdressingUtils.getOperand(ZEROPAGE_INDIRECT_X, this));
                break;
            case 0xF1:
                SBC(mAdressingUtils.getOperand(ZEROPAGE_INDIRECT_Y, this));
                break;

            /**
             * Set flags
             */
            case 0x38: // Clear carry
                mRegisters.flags.setFlag(StatusRegister.CARRY, true);
                break;
            case 0xF8: // Clear Decimal
                mRegisters.flags.setFlag(StatusRegister.DECIMAL, true);
                break;
            case 0x78: // Clear interrupt
                mRegisters.flags.setFlag(StatusRegister.INTERRUPT, true);
                break;

            /**
             * SRE (Undocumented
             */
            case 0x47:
                SRE(ZEROPAGE);
                break;
            case 0x57:
                SRE(ZEROPAGE_X);
                break;
            case 0x4F:
                SRE(ABSOLUTE);
                break;
            case 0x5F:
                SRE(ABSOLUTE_X);
                break;
            case 0x5B:
                SRE(ABSOLUTE_Y);
                break;
            case 0x43:
                SRE(ZEROPAGE_INDIRECT_X);
                break;
            case 0x53:
                SRE(ZEROPAGE_INDIRECT_Y);
                break;

            /**
             * STA
             */
            case 0x85:
                mMemory.write(mAdressingUtils.getAddressForOperand(ZEROPAGE), mRegisters.A);
                break;
            case 0x95:
                mMemory.write(mAdressingUtils.getAddressForOperand(ZEROPAGE_X), mRegisters.A);
                break;
            case 0x8D:
                mMemory.write(mAdressingUtils.getAddressForOperand(ABSOLUTE), mRegisters.A);
                break;
            case 0x9D:
                mMemory.write(mAdressingUtils.getAddressForOperand(ABSOLUTE_X), mRegisters.A);
                break;
            case 0x99:
                mMemory.write(mAdressingUtils.getAddressForOperand(ABSOLUTE_Y), mRegisters.A);
                break;
            case 0x81:
                mMemory.write(mAdressingUtils.getAddressForOperand(ZEROPAGE_INDIRECT_X), mRegisters.A);
                break;
            case 0x91:
                mMemory.write(mAdressingUtils.getAddressForOperand(ZEROPAGE_INDIRECT_Y), mRegisters.A);
                break;

            /**
             * STX
             */
            case 0x86:
                mMemory.write(mAdressingUtils.getAddressForOperand(ZEROPAGE), mRegisters.X);
                break;
            case 0x96:
                mMemory.write(mAdressingUtils.getAddressForOperand(ZEROPAGE_Y), mRegisters.X);
                break;
            case 0x8E:
                mMemory.write(mAdressingUtils.getAddressForOperand(ABSOLUTE), mRegisters.X);
                break;

            /**
             * STY
             */
            case 0x84:
                mMemory.write(mAdressingUtils.getAddressForOperand(ZEROPAGE), mRegisters.Y);
                break;
            case 0x94:
                mMemory.write(mAdressingUtils.getAddressForOperand(ZEROPAGE_X), mRegisters.Y);
                break;
            case 0x8C:
                mMemory.write(mAdressingUtils.getAddressForOperand(ABSOLUTE), mRegisters.Y);
                break;

            /**
             * Transfer
             */
            case 0xAA: // TAX
                mRegisters.X = mRegisters.A;
                setZero(mRegisters.X);
                setSign(mRegisters.X);
                break;
            case 0xA8: // TAY
                mRegisters.Y = mRegisters.A;
                setZero(mRegisters.Y);
                setSign(mRegisters.Y);
                break;
            case 0xBA: // TSX
                mRegisters.X = mRegisters.SP;
                setZero(mRegisters.X);
                setSign(mRegisters.X);
                break;
            case 0x8A: // TXA
                mRegisters.A = mRegisters.X;
                setZero(mRegisters.X);
                setSign(mRegisters.X);
                break;
            case 0x9A: // TXS
                mRegisters.SP = mRegisters.X;
                break;
            case 0x98: // TYA
                mRegisters.A = mRegisters.Y;
                setZero(mRegisters.Y);
                setSign(mRegisters.Y);
                break;
            default:
                unknownOpCode = true;

        }
        if (mPCReadCounter < 3) {
            debug("    ");
        }
        if (mPCReadCounter < 2) {
            debug("    ");
        }

        debug("  " + registerInfo);

        if (unknownOpCode) {
            debug("Unknown Opcode");
        }
        
        debug("\n");
    }

    public String getDebugLog() {
        return mDebugLog.toString();
    }
}
