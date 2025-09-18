package com.yiaobang.serialporttoolfx.serial;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

/**
 * 串行数据监听器
 *
 * @author Y
 * @date 2024/05/15
 */
public final class SerialDataListener implements SerialPortDataListener {
    private final SerialComm serialComm;
    
    public SerialDataListener(SerialComm serialComm) {
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