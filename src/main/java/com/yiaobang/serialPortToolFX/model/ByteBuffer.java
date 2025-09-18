package com.yiaobang.serialporttoolfx.model;

/**
 * 字节缓冲区
 *
 * @author Y
 * @date 2024/05/14
 */
public final class ByteBuffer {
    private final CircularArray CircularQueue;

    public ByteBuffer(int maxSize) {
        CircularQueue = new CircularArray(maxSize);
    }

    public void add(byte[] bytes) {
        if (bytes != null) {
            CircularQueue.add(bytes);
        }
    }

    public byte[] getBuffer() {
        return CircularQueue.getElements();
    }

    public void close() {
        CircularQueue.clear();
    }
}