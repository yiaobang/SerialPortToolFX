package com.yiaobang.serialPortToolFX.data;

import java.util.Map;

public class MockResponsesDelimiter extends MockResponses {
    private final byte[] delimiter;

    protected MockResponsesDelimiter(Map<String, byte[]> replays, byte[] delimiter) {
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
