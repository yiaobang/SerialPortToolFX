package com.yiaobang.serialPortToolFX.data;

import java.util.Map;

public class MockResponsesPackSize extends MockResponses {
    private final int packSize;

    protected MockResponsesPackSize(Map<String, byte[]> replays, int packSize) {
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
