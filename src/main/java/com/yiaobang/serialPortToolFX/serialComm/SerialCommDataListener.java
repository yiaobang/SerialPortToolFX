package com.yiaobang.serialPortToolFX.serialComm;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

/**
 * 串行通信数据侦听器
 *
 * @author Y
 * @date 2024/05/15
 */
public final class SerialCommDataListener implements SerialPortDataListener {
    private final SerialComm serialComm;
    public SerialCommDataListener(SerialComm serialComm) {
        this.serialComm = serialComm;
    }
    @Override
    public int getListeningEvents() {
        return SerialPort.LISTENING_EVENT_DATA_RECEIVED;
    }
    @Override
    public void serialEvent(SerialPortEvent event) {
        serialComm.listen(event.getReceivedData());
    }
}
