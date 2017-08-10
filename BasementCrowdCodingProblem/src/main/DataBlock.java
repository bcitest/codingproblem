package main;

public interface DataBlock {
    boolean write(Byte[] data);
    Byte[] read();
    Integer getStartIndex();
    Integer getSize();
}
