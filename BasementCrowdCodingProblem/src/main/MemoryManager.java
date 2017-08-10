package main;

import java.util.ArrayList;
import java.util.TreeSet;
import java.util.concurrent.locks.ReentrantLock;

// Memory manager implementing the buddy algorithm.
public class MemoryManager {

    public static final int BLOCK_SIZE = 1024;
    private static final int BLOCK_NUMBER = 100000;

    private ArrayList<Byte[]> memory;

    private ArrayList<TreeSet<Integer>> freeMemoryBlocks;
    // lock at index i guards free memory block list at index i
    private ArrayList<ReentrantLock> locks;

    public MemoryManager() {
        int memSizeInPowerOfTwo = Utils.binLogFloor(BLOCK_NUMBER);
        memory = new ArrayList<>(BLOCK_NUMBER); // set the initial capacity
        for (int i = 0; i < BLOCK_NUMBER; i++) {
            memory.add(new Byte[BLOCK_SIZE]);
        }
        freeMemoryBlocks = new ArrayList<>();
        locks = new ArrayList<>();
        for (int i = 0; i <= memSizeInPowerOfTwo; i++) {
            freeMemoryBlocks.add(new TreeSet<>());
            locks.add(new ReentrantLock());
        }
        freeMemoryBlocks.get(memSizeInPowerOfTwo).add(0);
        // Since the block number we have to deal with is fixed and equal 100000,
        // if we proceed with the standard buddy algorithm routine, we will waste a lot of memory,
        // because the buddy algorithm is designed to manage memory of size being power of 2.
        // Namely, the highest power of 2 smaller than 100000 is 16, since 2^16 = 65536.
        // If we only use 65536 blocks of memory, we waste more than 30%.
        // In order to prevent this, I not only add a free block of 2^16 starting at index 0,
        // but also a free block of 2^15 starting at index 65536.
        // This allows us to allocate 98304 blocks in total (2^16 + 2^15), so it is available
        // to use more than 98% of memory (of course fragmentation-agnostic).
        // The cost of this solution is that while releasing we'll need to check
        // if the index of released block is bigger than 65536 and then proceed accordingly.
        freeMemoryBlocks.get(memSizeInPowerOfTwo - 1).add(Utils.binPower(memSizeInPowerOfTwo));
    }

    public DataBlock allocate(int numBlocksRequired) {
        if (numBlocksRequired <= 0) {
            throw new IllegalArgumentException("Number of required blocks has to be positive!");
        }

        int desiredBlockSize = Utils.binLogCeil(numBlocksRequired);
        for (int i = desiredBlockSize; i < freeMemoryBlocks.size(); i++) {
            DataBlock ret = tryAllocateBlockOfSize(desiredBlockSize, i, numBlocksRequired);
            if (ret != null) {
                return ret;
            }
        }

        // failed to allocate
        return null;
    }

    private DataBlock tryAllocateBlockOfSize(int desiredBlockSize, int blockSize, int numBlocksRequired) {
        ReentrantLock lock = locks.get(blockSize);
        try {
            lock.lock();
            Integer startIndex = freeMemoryBlocks.get(blockSize).pollFirst();
            if (startIndex != null) {
                lock.unlock(); // we don't need it anymore
                DataBlock ret = new MemoryDataBlock(startIndex,
                        memory.subList(startIndex, startIndex + desiredBlockSize), numBlocksRequired);

                // if we don't take the whole block, split in two
                for (int childBlockSize = blockSize - 1; childBlockSize >= desiredBlockSize;
                        childBlockSize--) {

                    addFreeBlock(childBlockSize, startIndex);
                }

                return ret;
            }

            return null;
        }
        finally {
            if (lock.isHeldByCurrentThread()) { // might have unlocked earlier in case of success
                lock.unlock();
            }
        }
    }

    // adds a free block of given size and index
    private void addFreeBlock(int blockSize, int startIndex) {
        ReentrantLock lock = locks.get(blockSize);
        try {
            lock.lock();
            freeMemoryBlocks.get(blockSize).add(startIndex + Utils.binPower(blockSize));
        }
        finally {
            lock.unlock();
        }
    }

}
