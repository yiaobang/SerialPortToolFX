package com.yiaobang.serialPortToolFX.data;

public final class ByteBuffer {
    private final CircularArray CircularQueue;

    public ByteBuffer(int maxSize) {
        CircularQueue = new CircularArray(maxSize);
    }

    public void add(byte[] bytes) {
        CircularQueue.add(bytes);
    }

    public byte[] getBuffer() {
        return CircularQueue.getElements();
    }

    public void close() {
        CircularQueue.clear();
    }
}
