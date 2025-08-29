package com.rimfrost;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DynamicArrayTest {

    @Test
    void newArrayReturnsZeroForUnset() {
        DynamicArray a = DynamicArray.newarray();
        assertEquals(0, a.get(5));
    }

    @Test
    void setThenGetWorksAndIsPersistent() {
        DynamicArray a1 = DynamicArray.newarray();
        DynamicArray a2 = a1.set(5, 42);
        assertEquals(42, a2.get(5));
        // old version unchanged
        assertEquals(0, a1.get(5));
    }

    @Test
    void multipleIndicesPersistTogether() {
        DynamicArray a1 = DynamicArray.newarray();
        DynamicArray a2 = a1.set(3, 17);
        DynamicArray a3 = a2.set(10, 99);

        assertEquals(17, a3.get(3));
        assertEquals(99, a3.get(10));
        // earlier versions remain unchanged
        assertEquals(0, a1.get(3));
        assertEquals(0, a2.get(10));
    }

    @Test
    void overwriteChainKeepsOldVersions() {
        DynamicArray a1 = DynamicArray.newarray();
        DynamicArray a2 = a1.set(5, 1);
        DynamicArray a3 = a2.set(5, 2);
        DynamicArray a4 = a3.set(5, 3);

        assertEquals(3, a4.get(5));
        assertEquals(2, a3.get(5));
        assertEquals(1, a2.get(5));
        assertEquals(0, a1.get(5));
    }

    @Test
    void indexZeroHandled() {
        DynamicArray a1 = DynamicArray.newarray();
        DynamicArray a2 = a1.set(0, 123);
        assertEquals(123, a2.get(0));
        assertEquals(0, a1.get(0));
    }

    @Test
    void largeIndexHandled() {
        DynamicArray a1 = DynamicArray.newarray();
        int idx = 1_000_000;
        DynamicArray a2 = a1.set(idx, 7);
        assertEquals(7, a2.get(idx));
        assertEquals(0, a2.get(idx + 1));
    }

    @Test
    void negativeIndexIsNoOp() {
        DynamicArray a1 = DynamicArray.newarray();
        DynamicArray a2 = a1.set(-1, 99);
        // by your implementation, set(i<0) returns same instance
        assertSame(a1, a2);
        assertEquals(0, a2.get(-1));
    }

    @Test
    void valueRangeIncludesNegativesAndMaxInt() {
        DynamicArray a1 = DynamicArray.newarray();
        DynamicArray a2 = a1.set(123, Integer.MAX_VALUE);
        DynamicArray a3 = a2.set(123, -1);
        assertEquals(Integer.MAX_VALUE, a2.get(123));
        assertEquals(-1, a3.get(123));
    }
}
