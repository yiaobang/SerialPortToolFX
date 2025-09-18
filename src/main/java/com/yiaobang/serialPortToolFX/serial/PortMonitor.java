package com.yiaobang.serialporttoolfx.serial;

import com.fazecast.jSerialComm.SerialPort;
import com.yiaobang.serialporttoolfx.framework.core.FX;
import javafx.beans.property.SimpleStringProperty;
import java.util.StringJoiner;

/**
 * 端口监视器
 *
 * @author Y
 * @date 2024/05/14
 */
public class PortMonitor {
    public static volatile SerialPort[] commPorts = SerialPort.getCommPorts();
    public static final SimpleStringProperty serialPorts = new SimpleStringProperty(serialPortsToString());
    public static final Thread serialPortMonitorThread;
    
    static {
        serialPortMonitorThread = new Thread(() -> {
            while (true) {
                scanSerialPort();
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
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
            String portString = serialPortsToString();
            FX.run(() -> serialPorts.set(portString == null ? "" : portString));
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
        // Use Set-based comparison to handle port reordering
        java.util.Set<String> currentPorts = new java.util.HashSet<>();
        java.util.Set<String> newPortNames = new java.util.HashSet<>();
        
        for (SerialPort port : commPorts) {
            currentPorts.add(port.getSystemPortName());
        }
        for (SerialPort port : newPorts) {
            newPortNames.add(port.getSystemPortName());
        }
        
        return currentPorts.equals(newPortNames);
    }

    public static void init() {
        serialPortMonitorThread.start();
    }
}