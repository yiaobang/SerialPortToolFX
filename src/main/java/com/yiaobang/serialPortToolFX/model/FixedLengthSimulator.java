package com.yiaobang.serialporttoolfx.model;

import java.util.Map;

/**
 * 固定长度模拟器
 *
 * @author Y
 * @date 2024/05/15
 */
public class FixedLengthSimulator extends DeviceSimulator {
    private final int packSize;

    protected FixedLengthSimulator(Map<String, byte[]> replays, int packSize) {
        super(replays);
        this.packSize = packSize;
    }

    @Override
    public void checkData(byte[] bytes) {
        if (dataBuffer.size() + bytes.length < packSize) {
            dataBuffer.add(bytes);
        } else {
            for (byte aByte : bytes) {
                dataBuffer.add(aByte);
                if (dataBuffer.size() == packSize) {
                    reply(dataBuffer.getElements());
                }
            }
        }
    }
}