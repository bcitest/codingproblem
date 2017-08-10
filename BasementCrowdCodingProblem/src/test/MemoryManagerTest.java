package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import main.DataBlock;
import main.MemoryManager;

// The numbers appearing in the test exploit the fact that according to the
// problem statement, the size of available memory is fixed.
// Naming convention: testMethodName_input_expectedOutput
public class MemoryManagerTest {

    // Tests are affected by the fact that we try to save more memory and use an additional memory
    // block after the basic memory size. More details in comments of the constructor of MemoryManager.
    private static final int BASIC_MEMORY_SIZE = 65536;

    private MemoryManager manager;

    @Before
    public void setUp() {
        manager = new MemoryManager();
    }

    @Test
    public void testAllocate_blockOfSize1_success() {
        DataBlock block = manager.allocate(1);
        assertEquals(new Integer(1), block.getSize());
        assertEquals(new Integer(BASIC_MEMORY_SIZE), block.getStartIndex());
    }

    @Test
    public void testAllocate_blockOfSize32_success() {
        DataBlock block = manager.allocate(32);
        assertEquals(new Integer(32), block.getSize());
        assertEquals(new Integer(BASIC_MEMORY_SIZE), block.getStartIndex());
    }

    @Test
    public void testAllocate_blockOfSize70000_failure() { // too big
        DataBlock block = manager.allocate(70000);
        assertNull(block);
    }

    @Test
    public void testAllocate_multipleBlocksOfSize32_success() {
        for (int i = 0; i < 5; i++) {
            DataBlock block = manager.allocate(32);
            assertEquals(new Integer(32), block.getSize());
            assertEquals(new Integer(32 * i + BASIC_MEMORY_SIZE), block.getStartIndex());
        }
    }

    // we want to test not only powers of 2
    @Test
    public void testAllocate_multipleBlocksOfSize31_success() {
        for (int i = 0; i < 5; i++) {
            DataBlock block = manager.allocate(31);
            assertEquals(new Integer(31), block.getSize());
            assertEquals(new Integer(32 * i + BASIC_MEMORY_SIZE), block.getStartIndex());
        }
    }

    @Test
    public void testAllocate_multipleBlocksOfDifferentSizes_success() {
        DataBlock block = manager.allocate(31);
        assertEquals(new Integer(31), block.getSize());
        assertEquals(new Integer(BASIC_MEMORY_SIZE), block.getStartIndex());

        block = manager.allocate(16);
        assertEquals(new Integer(16), block.getSize());
        assertEquals(new Integer(BASIC_MEMORY_SIZE + 32), block.getStartIndex());

        block = manager.allocate(32768);
        assertEquals(new Integer(32768), block.getSize());
        assertEquals(new Integer(0), block.getStartIndex());

        block = manager.allocate(32000);
        assertEquals(new Integer(32000), block.getSize());
        assertEquals(new Integer(32768), block.getStartIndex());
    }

    @Test
    public void testAllocate_multipleBlocksOfSize32768_success() {
        DataBlock block = manager.allocate(32768);
        assertEquals(new Integer(32768), block.getSize());
        assertEquals(new Integer(BASIC_MEMORY_SIZE), block.getStartIndex());

        block = manager.allocate(32768);
        assertEquals(new Integer(32768), block.getSize());
        assertEquals(new Integer(0), block.getStartIndex());

        block = manager.allocate(32768);
        assertEquals(new Integer(32768), block.getSize());
        assertEquals(new Integer(32768), block.getStartIndex());
    }

    @Test
    public void testAllocate_multipleBlocksOfSize32768_failure() { // too much
        DataBlock block = null;
        for (int i = 0; i < 4; i++) {
            block = manager.allocate(32678);
        }
        assertNull(block);
    }

    @Test
    public void testAllocate_blockOfSize0_IllegalArgumentException() {
        try {
            manager.allocate(0);
            fail("Allocated block of size 0!");
        }
        catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testAllocate_blockOfNegativeSize_IllegalArgumentException() {
        try {
            manager.allocate(-4);
            fail("Allocated block of negative size!");
        }
        catch (IllegalArgumentException e) {
        }
    }

}
