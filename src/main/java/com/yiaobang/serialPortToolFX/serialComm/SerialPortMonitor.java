package com.yiaobang.serialPortToolFX.serialComm;

import com.fazecast.jSerialComm.SerialPort;
import com.yiaobang.serialPortToolFX.javafxTool.core.FX;
import javafx.beans.property.SimpleStringProperty;
import java.util.StringJoiner;

/**
 * 串口监视器
 *
 * @author Y
 * @date 2024/05/14
 */
public class SerialPortMonitor {
    public static volatile SerialPort[] commPorts = SerialPort.getCommPorts();
    public static final SimpleStringProperty serialPorts = new SimpleStringProperty(serialPortsToString());
    public static final Thread serialPortMonitorThread;
    static {
        serialPortMonitorThread = new Thread(() -> {
            while (true) {
                scanSerialPort();
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ignored) {

                }
            }
        }, "串口监控线程");
    }

    /**
     * 扫描串口
     */
    public static void scanSerialPort() {
        SerialPort[] newPorts = SerialPort.getCommPorts();
        if (!checkSerialPort(newPorts)) {
            commPorts = newPorts;
            FX.run(() -> {
                String s = serialPortsToString();
                serialPorts.set(s == null ? "" : s);
            });
        }
    }

    private static String serialPortsToString() {
        StringJoiner sj = new StringJoiner("\n");
        for (SerialPort commPort : commPorts) {
            sj.add(commPort.getSystemPortName());
        }
        return sj.toString();
    }

    /**
     * 检查这次扫描到的串口的列表和上次的列表是否一致从而决定是否更新串口的列表
     *
     * @param newPorts 最新扫描到的串口列表
     * @return boolean  false=更新  true = 不更新
     */
    private static boolean checkSerialPort(SerialPort[] newPorts) {
        if (commPorts.length != newPorts.length) {
            return false;
        }
        for (int i = 0; i < newPorts.length; i++) {
            if (!commPorts[i].getSystemPortName().equals(newPorts[i].getSystemPortName())) {
                return false;
            }
        }
        return true;
    }

    public static void init() {
        serialPortMonitorThread.start();
    }
}
