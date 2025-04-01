package com.yiaobang.serialPortToolFX.data;

/**
 * 循环数组
 *
 * @author Y
 * @date 2024/05/14
 */
public class CircularArray {
    private final byte[] array;
    private int front; // 头部指针
    private int rear; // 尾部指针
    private int size; // 队列当前大小
    private final int capacity; // 队列容量

    public CircularArray(int capacity) {
        this.capacity = capacity;
        this.array = new byte[capacity];
        this.front = 0;
        this.rear = -1;
        this.size = 0;
    }

    public void add(byte element) {
        if (size == capacity) {
            dequeue();
        }
        rear = (rear + 1) % capacity;
        array[rear] = element;
        size++;
    }

    public void add(byte[] elements) {
        if (elements.length > capacity) {
            throw new IllegalArgumentException("Array size exceeds queue capacity");
        }

        int elementsToDequeue = Math.max(0, size + elements.length - capacity);
        for (int i = 0; i < elementsToDequeue; i++) {
            dequeue();
        }

        for (byte element : elements) {
            rear = (rear + 1) % capacity;
            array[rear] = element;
            size++;
        }
    }

    public byte getElement(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index out of bounds");
        }
        int actualIndex = (front + index) % capacity;
        return array[actualIndex];
    }

    public byte[] getElements() {
        byte[] result = new byte[size];
        if (size == 0) {
            return result;
        }

        if (rear >= front) {
            System.arraycopy(array, front, result, 0, size);
        } else {
            int frontSegmentLength = capacity - front;
            System.arraycopy(array, front, result, 0, frontSegmentLength);
            System.arraycopy(array, 0, result, frontSegmentLength, size - frontSegmentLength);
        }

        return result;
    }

    public void clear() {
        front = 0;
        rear = -1;
        size = 0;
    }

    public int size() {
        return size;
    }

    private void dequeue() {
        if (size == 0) {
            throw new IllegalStateException("Queue is empty");
        }
        front = (front + 1) % capacity;
        size--;
    }
}
