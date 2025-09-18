package com.yiaobang.serialporttoolfx.model;

import java.util.Map;

/**
 * 基于分隔符的模拟器
 *
 * @author Y
 * @date 2024/05/15
 */
public class DelimiterBasedSimulator extends DeviceSimulator {
    private final byte[] delimiter;

    protected DelimiterBasedSimulator(Map<String, byte[]> replays, byte[] delimiter) {
        super(replays);
        this.delimiter = delimiter;
    }

    @Override
    public void checkData(byte[] bytes) {
        for (byte aByte : bytes) {
            dataBuffer.add(aByte);
            if (checkForEndDelimiter()) {
                reply(dataBuffer.getElements());
            }
        }
    }

    private boolean checkForEndDelimiter() {
        if (dataBuffer.size() <= delimiter.length) {
            return false;
        }
        for (int i = 0; i < delimiter.length; i++) {
            if (dataBuffer.getElement(dataBuffer.size() - delimiter.length + i) != delimiter[i]) {
                return false;
            }
        }
        return true;
    }
}