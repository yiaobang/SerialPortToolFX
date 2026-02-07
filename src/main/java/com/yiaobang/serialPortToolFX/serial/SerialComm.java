package com.yiaobang.serialporttoolfx.serial;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.yiaobang.serialporttoolfx.framework.core.FX;
import com.yiaobang.serialporttoolfx.framework.mvvm.ViewModel;
import com.yiaobang.serialporttoolfx.model.ByteBuffer;
import com.yiaobang.serialporttoolfx.model.DataWriteFile;
import com.yiaobang.serialporttoolfx.model.DeviceSimulator;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleLongProperty;
import lombok.Getter;
import lombok.Setter;
import java.io.File;

@Getter
public final class SerialComm implements ViewModel, AutoCloseable {
    private static final int MAX_SHOW_BYTES = 115200;
    private final ByteBuffer buffer = new ByteBuffer(MAX_SHOW_BYTES);

    @Setter private DeviceSimulator deviceSimulator;
    @Setter private volatile long waitTime = 1000;
    private DataWriteFile dataWriteFile;

    @Setter private volatile boolean sendSave;
    @Setter private volatile boolean receiveSave;
    @Setter private volatile boolean receiveShow = true;

    private final SimpleLongProperty SEND_LONG_PROPERTY = new SimpleLongProperty(0);
    private final SimpleLongProperty RECEIVE_LONG_PROPERTY = new SimpleLongProperty(0);
    private final SimpleBooleanProperty serialPortState = new SimpleBooleanProperty(false);

    // 基础常量（不需翻译）
    public static final Integer[] BAUD_RATE = {9600, 19200, 38400, 115200, 128000, 230400, 256000, 460800, 921600, 1382400};
    public static final Integer[] DATA_BITS = {8, 7, 6, 5};
    public static final String[] STOP_BITS = {"1", "1.5", "2"};

    private SerialPort serialPort;
    private String serialPortName = "";
    private int baudRate = 9600;
    private int dataBits = 8;
    private int stopBits = SerialPort.ONE_STOP_BIT;
    private String stopString = "1";
    private int parity = SerialPort.NO_PARITY;
    private int flowControl = SerialPort.FLOW_CONTROL_DISABLED;
    private final SerialPortDataListener listener;

    public SerialComm() {
        this.listener = new SerialDataListener(this);
    }

    public void openSerialPort() {
        close();
        serialPort = findSerialPort();
        if (serialPort == null) {
            FX.run(() -> serialPortState.set(false));
            return;
        }
        serialPort.setComPortParameters(baudRate, dataBits, stopBits, parity);
        serialPort.setFlowControl(flowControl);
        serialPort.addDataListener(this.listener);
        boolean isOpen = serialPort.openPort();
        dataWriteFile = isOpen ? new DataWriteFile(serialPortName) : null;
        FX.run(() -> serialPortState.set(isOpen));
    }

    private SerialPort findSerialPort() {
        for (SerialPort serial : SerialPort.getCommPorts()) {
            if (serial.getSystemPortName().equals(serialPortName)) return serial;
        }
        return null;
    }

    // --- 核心改进：基于索引的设置方法 ---

    public void setParityByIndex(int index) {
        this.parity = switch (index) {
            case 1 -> SerialPort.ODD_PARITY;
            case 2 -> SerialPort.EVEN_PARITY;
            case 3 -> SerialPort.MARK_PARITY;
            case 4 -> SerialPort.SPACE_PARITY;
            default -> SerialPort.NO_PARITY;
        };
        openSerialPort();
    }

    public void setFlowControlByIndex(int index) {
        this.flowControl = switch (index) {
            case 1 -> SerialPort.FLOW_CONTROL_RTS_ENABLED | SerialPort.FLOW_CONTROL_CTS_ENABLED;
            case 2 -> SerialPort.FLOW_CONTROL_DSR_ENABLED | SerialPort.FLOW_CONTROL_DTR_ENABLED;
            case 3 -> SerialPort.FLOW_CONTROL_XONXOFF_IN_ENABLED | SerialPort.FLOW_CONTROL_XONXOFF_OUT_ENABLED;
            default -> SerialPort.FLOW_CONTROL_DISABLED;
        };
        openSerialPort();
    }

    public void setSerialPortName(String name) { this.serialPortName = name; openSerialPort(); }
    public void setBaudRate(int val) { this.baudRate = val; openSerialPort(); }
    public void setDataBits(int val) { this.dataBits = val; openSerialPort(); }
    public void setStopBits(String val) {
        this.stopString = val;
        this.stopBits = switch (val) {
            case "1.5" -> SerialPort.ONE_POINT_FIVE_STOP_BITS;
            case "2" -> SerialPort.TWO_STOP_BITS;
            default -> SerialPort.ONE_STOP_BIT;
        };
        openSerialPort();
    }

    public int write(byte[] bytes) {
        if (bytes != null && serialPort != null && serialPort.isOpen()) {
            int num = serialPort.writeBytes(bytes, bytes.length);
            if (num > 0) {
                FX.run(() -> SEND_LONG_PROPERTY.set(SEND_LONG_PROPERTY.get() + num));
                if (sendSave && dataWriteFile != null) Thread.startVirtualThread(() -> dataWriteFile.serialCommSend(bytes));
            } else { close(); }
            return num;
        }
        return 0;
    }

    public void listen(byte[] bytes) {
        FX.run(() -> RECEIVE_LONG_PROPERTY.set(RECEIVE_LONG_PROPERTY.get() + bytes.length));
        if (receiveSave && dataWriteFile != null) Thread.startVirtualThread(() -> dataWriteFile.serialCommReceive(bytes));
        if (receiveShow) buffer.add(bytes);
        if (deviceSimulator != null) deviceSimulator.checkData(bytes);
    }

    public byte[] getData() { return buffer.getBuffer(); }
    public void clearSend() { FX.run(() -> SEND_LONG_PROPERTY.set(0)); }
    public void clearReceive() { FX.run(() -> RECEIVE_LONG_PROPERTY.set(0)); }

    public boolean createDeviceSimulator(File file) {
        if (this.deviceSimulator != null) this.deviceSimulator.close();
        if (file == null) return false;
        this.deviceSimulator = DeviceSimulator.createDeviceSimulator(file);
        if (this.deviceSimulator != null) deviceSimulator.setSerialComm(this);
        return this.deviceSimulator != null;
    }

    @Override
    public void close() {
        if (serialPort != null) serialPort.closePort();
        FX.run(() -> serialPortState.set(false));
    }
}