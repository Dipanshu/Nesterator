package components.memory;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

/**
 * A Block representing adjacent Memory Blocks
 */
public class CompositeMemory implements Memory {

    private final MemoryRange mMemoryRange;
    private final List<Memory> mBlocks;

    /**
     * @param blocks MemoryBlocks comprising of this composite memory. Must be adjacent
     */
    public CompositeMemory(Memory... blocks) {
        this(Lists.newArrayList(blocks));
    }

    /**
     * @param blocks MemoryBlocks comprising of this composite memory. Must be adjacent
     */
    public CompositeMemory(List<Memory> blocks) {
        Map<Memory, MemoryRange> blockToRanges = Maps.newHashMapWithExpectedSize(blocks.size());
        blockToRanges.put(blocks.get(0), blocks.get(0).getRange());
        for (int i = 0; i < blocks.size(); i++) {
            final Memory value = blocks.get(i);
            MemoryRange range = blockToRanges.get(value);
            if (i != 0) {
                MemoryRange previousRange = blockToRanges.get(blocks.get(i - 1));
                Preconditions.checkArgument(previousRange.end == range.start);
            }
            if (i != blocks.size() - 1) {
                final Memory nextBlock = blocks.get(i + 1);
                MemoryRange nextRange = nextBlock.getRange();
                Preconditions.checkArgument(range.end == nextRange.start);
                blockToRanges.put(nextBlock, nextRange);
            }
        }
        mBlocks = ImmutableList.copyOf(blocks);
        mMemoryRange = new MemoryRange(blocks.get(0).getRange().start, blocks.get(blocks.size() - 1).getRange().end);
    }

    @Override
    public int read(int address) {
        MemoryUtils.checkRange(address, this);
        return findBlock(0, mBlocks.size(), address).read(address);
    }

    @Override
    public void write(int address, int value) {
        MemoryUtils.checkRange(address, this);
        findBlock(0, mBlocks.size(), address).write(address, value);
    }

    private Memory findBlock(int startIndex, int endIndex, int address) {
        if (startIndex == endIndex) {
            Memory block = mBlocks.get(startIndex);
            Preconditions.checkArgument(isAddressInRange(address, block.getRange()));
            return block;
        }

        int midPoint = startIndex + (endIndex - startIndex) / 2;
        Memory block = mBlocks.get(midPoint);
        if (isAddressInRange(address, block.getRange())) {
            return block;
        }
        if (address < block.getRange().start) {
            return findBlock(startIndex, midPoint, address);
        } else {
            return findBlock(midPoint + 1, endIndex, address);
        }
    }

    private boolean isAddressInRange(int address, MemoryRange range) {
        return range.start <= address && range.end > address;
    }

    @Override
    public MemoryRange getRange() {
        return mMemoryRange;
    }
}
