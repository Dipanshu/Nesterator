package hardware.cpu.registers;

import com.google.common.base.Preconditions;

public class StatusRegister {

    public static final int CARRY = 0;
    public static final int ZERO = 1;
    public static final int INTERRUPT = 2;
    public static final int DECIMAL = 3;
    public static final int BREAK = 4;
    public static final int NOT_USED = 5;
    public static final int OVERFLOW = 6;
    public static final int SIGN = 7;

    private int mStatusRegister;

    public StatusRegister() {
        mStatusRegister = 0x24;
    }

    public void setFlag(int flag, boolean value) {
        Preconditions.checkArgument(flag <= SIGN && flag >= CARRY);
        if (value) {
            mStatusRegister |= 1 << flag;
        }  else {
            mStatusRegister &= ~(1 << flag);
        }
        mStatusRegister &= 0xFF;
    }

    public boolean getFlag(int flag) {
       return ((mStatusRegister & 0b00000001 << flag) != 0);
    }

    public int getCarry() {
        return getFlag(CARRY) ? 1 : 0;
    }

    public int getBorrow() {
        return getFlag(CARRY) ? 0 : 1;
    }

    public int getRegister() {
        return mStatusRegister;
    }

    public void setRegister(int register) {
        this.mStatusRegister = (register & ~(1 << BREAK) | (1 << NOT_USED)) & 0xFF;
    }

    public boolean getInterruptDisabled() {
        return getFlag(INTERRUPT);
    }
}
