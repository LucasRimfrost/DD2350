package com.rimfrost;

public class DynamicArray {
    private Node root;

    private static class Node {
        Node leftChild;
        Node rightChild;
        int value;
        boolean hasValue;
        Node() {}
    }

    public DynamicArray() {
        this.root = null;
    }

    private DynamicArray(Node root) {
        this.root = root;
    }

    public static DynamicArray newarray() {
        return new DynamicArray();
    }

    /** Persistent set: returns a NEW array version with a[i] = value. */
    public DynamicArray set(int i, int value) {
        if (i < 0) return this; // no-op for invalid index per spec assumptions

        DynamicArray newArray = new DynamicArray(new Node());
        Node currNew = newArray.root;
        Node currOld = this.root;

        // highest set bit (0..31). For i == 0, depth is 0.
        // int highestBit = (i == 0) ? 0 : 31 - Integer.numberOfLeadingZeros(i);

        // Build the path; share the opposite branch from old
        for (int bitPos = 31; bitPos >= 0; --bitPos) {
            int bit = (i >>> bitPos) & 1;
            if (bit == 0) {
                currNew.leftChild  = new Node();
                currNew.rightChild = (currOld != null) ? currOld.rightChild : null;

                currNew = currNew.leftChild;
                currOld = (currOld != null) ? currOld.leftChild : null;
            } else {
                currNew.rightChild = new Node();
                currNew.leftChild  = (currOld != null) ? currOld.leftChild  : null;

                currNew = currNew.rightChild;
                currOld = (currOld != null) ? currOld.rightChild : null;
            }
        }

        currNew.value = value;
        currNew.hasValue = true;
        return newArray;
    }

    /** Get: returns 0 if unset or out of path. */
    public int get(int i) {
        if (i < 0 || root == null) return 0;

        Node current = root;
        // int highestBit = (i == 0) ? 0 : 31 - Integer.numberOfLeadingZeros(i);

        for (int bitPos = 31; bitPos >= 0; --bitPos) {
            if (current == null) return 0;
            int bit = (i >>> bitPos) & 1;
            current = (bit == 0) ? current.leftChild : current.rightChild;
        }

        return (current != null && current.hasValue) ? current.value : 0;
    }
}
