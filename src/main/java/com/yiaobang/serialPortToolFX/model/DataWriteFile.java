package com.yiaobang.serialporttoolfx.model;

import com.yiaobang.serialporttoolfx.AppLauncher;
import com.yiaobang.serialporttoolfx.utils.CodeFormat;
import com.yiaobang.serialporttoolfx.utils.TimeUtils;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * 数据文件写入
 *
 * @author Y
 * @version 1.0
 * @date 2024/3/23 14:56
 */
public class DataWriteFile {
    private static final File dataFile = new File(AppLauncher.ROOT_FILE_PATH, "serial_port_data");
    static {
        if (!dataFile.exists()) {
            dataFile.mkdirs();
        }
    }
    //文件操作参数为 创建和追加
    private static final OpenOption[] options = {StandardOpenOption.CREATE, StandardOpenOption.APPEND};
    private final Path readFile;
    private final Path writeFile;

    public DataWriteFile(String serialPortName) {
        //文件命中加入时间戳 以文件后缀来区分发送和接受
        File read = new File(dataFile, serialPortName + "-" + TimeUtils.getFileName() + ".read");
        File write = new File(dataFile, serialPortName + "-" + TimeUtils.getFileName() + ".write");
        read.delete();
        write.delete();
        readFile = read.toPath();
        writeFile = write.toPath();
    }
    
    public void serialCommReceive(byte[] bytes) {
        Thread.startVirtualThread(() -> {
            try {
                Files.writeString(readFile, CodeFormat.hex(bytes), options);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
    
    public void serialCommSend(byte[] bytes) {
        Thread.startVirtualThread(() -> {
            try {
                Files.writeString(writeFile, CodeFormat.hex(bytes), options);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}