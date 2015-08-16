package hardware.ppu;

import hardware.cpu.Cpu;
import hardware.ppu.memory.OAM;
import hardware.ppu.memory.PPUMemory;
import hardware.ppu.memory.Palette;
import hardware.ppu.registers.ControllerInterpretator;
import hardware.ppu.registers.MaskReader;
import hardware.ppu.registers.PpuRegisters;
import hardware.ppu.registers.StatusWriter;

import java.nio.ByteBuffer;

public class PPU implements PpuRegisters.IsRenderingProvider {

    public static long sPPUCycles = 0;
    private static long sVBLON = 0;
    private final PpuRegisters mPpuRegisters;
    private final PPUMemory mMemory;
    private final Cpu mCpu;
    private final OAM mOam;
    private ByteBuffer mBitmap;

    public PPU(
            PpuRegisters registers,
            PPUMemory memory,
            OAM oam,
            Cpu cpu) {
        mPpuRegisters = registers;
        mMemory = memory;
        mCpu = cpu;
        mOam = oam;

        mPpuRegisters.setController(0x00);
        mPpuRegisters.setMask(0x00);
        mPpuRegisters.setStatus(0b10100000);
        mPpuRegisters.setOamAddress(0x00);
        mPpuRegisters.setScroll( 0x00);
        mPpuRegisters.setAddr(0x00);
        mPpuRegisters.setData(0x00);
    }


    private int mCurrentScanLine;
    private int mCurrentDot;

    public void clock(int numClocks) {
        for (int i = 0; i < numClocks; i++) {
            sPPUCycles++;
            mPpuRegisters.ppuClock();
            if (mCurrentDot == 0) {
                mCurrentDot = 1;
                // idle cycle
                return;
            }

            if (mCurrentScanLine == 0) {
                preRenderLine();
            }

            if (mCurrentScanLine >= 0 && mCurrentScanLine <= 239) {
                visibleScanLine();
            }

            if (mCurrentScanLine == 240) {
                postRenderScanline();
            }

            if (mCurrentScanLine >= 241 && mCurrentScanLine <= 260) {
                vBlankScanline();
            }

            if (mCurrentDot == 340) {
                mCurrentDot = 0;
                mCurrentScanLine = (mCurrentScanLine + 1) % 260;
            } else {
                mCurrentDot++;
            }
        }
    }

    public ByteBuffer renderFrame() {
        renderNameTable(ControllerInterpretator.getBaseNameTableAddress(mPpuRegisters.getController()));
        return mBitmap;
    }

    private void renderNameTable(int nameTableOffset) {
        int tileX;
        int tileY = 0;
        while (tileY < 30) {
            int yAttributeOffest = tileY / 4;
            tileX = 0;
            while (tileX < 32) {
                int xAttributeOffset = tileX / 4;
                int totalAttributeOffset = (yAttributeOffest * (32 / 4)) + xAttributeOffset;
                int colorInfo = mMemory.read(nameTableOffset + 0x3C0 + totalAttributeOffset);
                int pattern = mMemory.read(nameTableOffset + ((tileY * 32) + tileX));
                addTileData(tileX, tileY, pattern * 2 * 8, colorInfo);
                tileX++;
            }
            tileY++;
        }
    }

    private void addTileData(int tileX, int tileY, int pattern, int colorInfo) {
        int tileOffset = (tileX * 8) + (tileY * 256 * 8);
        for (int row = 0; row < 8; row++) {
            int patternTableOffset =
                    ControllerInterpretator.getBackgroundPatternTable(mPpuRegisters.getController()) + pattern;
            int valueLow = mMemory.read(patternTableOffset);
            int valueHigh = mMemory.read(patternTableOffset + 8);
            for (int col = 7; col >= 0; col--) {
                int color = ((valueLow >> col) & 1) | (((valueHigh >> col) & 1) << 1);
                int index = (tileOffset + (256 * row) + col) * 3;

                if (color == 0) {
                    continue;
                }

                if (tileX % 4 < 2 && tileY % 4 < 2) {
                    color |= (colorInfo & 0b11) << 2;
                } else if (tileX % 4 > 2 && tileY % 4 < 2) {
                    color |= ((colorInfo >> 2) & 0b11) << 2;
                } else if (tileX % 4 < 2 && tileY % 4 > 2) {
                    color |= ((colorInfo >> 4) & 0b11) << 2;
                } else if (tileX % 4 > 2 && tileY % 4 > 2) {
                    color |= ((colorInfo >> 6) & 0b11) << 2;
                }

                int paletteColor = Palette.PALETTE.get(color);
                setColor(paletteColor, index);
            }
        }
    }

    private void setColor(int color, int index) {
        mBitmap.put(index, (byte) ((color & 0xFF0000) >> 16));
        mBitmap.put(index + 1, (byte) ((color & 0x00FF00) >> 8));
        mBitmap.put(index + 2, (byte) (color & 0x0000FF));
    }

    // (-1. 261)
    private void preRenderLine() {
      if (mCurrentDot == 1) {
          // System.out.println("VBL OFF : " + sPPUCycles);
          sPPUCycles -= sVBLON;
          StatusWriter.clearSpriteOverflow(mPpuRegisters);
          StatusWriter.clearSprite0Hit(mPpuRegisters);
          StatusWriter.clearVBlank(mPpuRegisters);
      }
    }

    // (0, 239)
    private void visibleScanLine() {
        if (mCurrentDot >= 257 && mCurrentDot <= 320) {
            mPpuRegisters.setOamAddress(0x00);
        }
    }

    // 240
    private void postRenderScanline() {
        if (mCurrentDot >= 257 && mCurrentDot <= 320) {
            mPpuRegisters.setOamAddress(0x00);
        }
    }

    // VBLANK (241-260)
    private void vBlankScanline() {
        if (mCurrentDot == 1 && mCurrentScanLine == 241) {
            if (ControllerInterpretator.generateNMI(mPpuRegisters.getController())) {
                mCpu.sendNMI();
            }
            sVBLON = sPPUCycles;
            StatusWriter.setVBlank(mPpuRegisters);
        }
    }

    @Override
    public boolean isRendering() {
        return MaskReader.showBackground(mPpuRegisters) || MaskReader.showSprites(mPpuRegisters);
    }
    
    public void initializeBuffer(ByteBuffer byteBuffer) {
        mBitmap = byteBuffer;
    }
}
