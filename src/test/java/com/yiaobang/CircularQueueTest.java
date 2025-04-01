package com.yiaobang;

import com.yiaobang.serialPortToolFX.data.CircularArray;

import java.util.Arrays;

public class CircularQueueTest {

    public static void main(String[] args) {
        // 创建循环队列，容量为 5
        CircularArray queue = new CircularArray(5);

        // 插入一些元素
        queue.add(new byte[]{1, 2, 3});
        System.out.println("Enqueued bytes: [1, 2, 3]");

        // 获取队列中的元素并打印
        byte[] result = queue.getElements();
        System.out.println("Queue contents after enqueue: " + Arrays.toString(result));

        // 再插入一些元素
        queue.add(new byte[]{4, 5, 6});
        System.out.println("Enqueued bytes: [4, 5, 6]");

        // 获取队列中的元素并打印
        result = queue.getElements();
        System.out.println("Queue contents after enqueue: " + Arrays.toString(result));

        // 再插入一些元素，使队列超出容量
        queue.add(new byte[]{7, 8, 9, 10});
        System.out.println("Enqueued bytes: [7, 8, 9, 10]");

        // 获取队列中的元素并打印
        result = queue.getElements();
        System.out.println("Queue contents after enqueue (with exceeding capacity): " + Arrays.toString(result));

        queue.clear();
        // 获取队列中的元素并打印
        result = queue.getElements();
        System.out.println("clean after: " + Arrays.toString(result));


        // 插入一些元素
        queue.add(new byte[]{1, 2, 3});
        System.out.println("Enqueued bytes: [1, 2, 3]");

        // 获取队列中的元素并打印
        byte[] result2 = queue.getElements();
        System.out.println("Queue contents after enqueue: " + Arrays.toString(result2));

        // 再插入一些元素
        queue.add(new byte[]{4, 5, 6});
        System.out.println("Enqueued bytes: [4, 5, 6]");

        // 获取队列中的元素并打印
        result = queue.getElements();
        System.out.println("Queue contents after enqueue: " + Arrays.toString(result));

        // 再插入一些元素，使队列超出容量
        queue.add(new byte[]{7, 8, 9, 10});
        System.out.println("Enqueued bytes: [7, 8, 9, 10]");

        // 获取队列中的元素并打印
        result = queue.getElements();
        System.out.println("Queue contents after enqueue (with exceeding capacity): " + Arrays.toString(result));

    }
}
