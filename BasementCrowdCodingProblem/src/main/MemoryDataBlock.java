package main;

import java.util.List;

public class MemoryDataBlock implements DataBlock {

    private List<Byte[]> memory;

    private final Integer startIndex;
    private final Integer size;

    public MemoryDataBlock(Integer startIndex, List<Byte[]> memory, Integer size) {
        this.memory = memory;
        this.startIndex = startIndex;
        this.size = size;
    }

    @Override
    public boolean write(Byte[] data) {
        return false;
    }

    @Override
    public Byte[] read() {
        return null;
    }

    @Override
    public Integer getStartIndex() {
        return startIndex;
    }

    @Override
    public Integer getSize() {
        return size;
    }

}
