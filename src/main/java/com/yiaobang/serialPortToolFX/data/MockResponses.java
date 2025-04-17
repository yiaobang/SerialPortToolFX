package com.yiaobang.serialPortToolFX.data;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yiaobang.serialPortToolFX.serialComm.SerialComm;
import com.yiaobang.serialPortToolFX.utils.CodeFormat;
import lombok.Setter;

import java.io.File;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 模拟响应
 *
 * @author Y
 * @date 2024/05/15
 */
public abstract class MockResponses implements AutoCloseable {
    public static final Gson JSON = new Gson();
    private static final Type type = new TypeToken<Map<String, String>>() {}.getType();

    protected final Map<String, byte[]> replays;
    protected CircularArray dataBuffer = new CircularArray(4096);
    @Setter
    protected SerialComm serialComm;

    protected MockResponses(Map<String, byte[]> replays) {
        this.replays = replays;
    }


    public void reply(byte[] bytes) {
        serialComm.write(replays.get(CodeFormat.utf8(bytes)));
        dataBuffer.clear();
    }

    /**
     * 创建模拟响应
     *
     * @param file json文件
     * @return {@code MockResponses }
     */
    public static MockResponses createMockResponses(File file) {
        try {
            List<String> strings = Files.readAllLines(file.toPath());
            StringBuilder stringBuilder = new StringBuilder();
            strings.forEach(stringBuilder::append);
            //json  字符串
            String json = stringBuilder.toString();
            Map<String, String> maps = JSON.fromJson(json, type);


            //编码格式
            String encode = maps.get("encode");
            //结束符
            String delimiter = maps.get("delimiter");
            String packSize = maps.get("packSize");
            maps.remove("encode");
            maps.remove("delimiter");
            maps.remove("packSize");

            //如果数据包大小和结束符都是空的 则不必继续
            if (packSize.isEmpty() && delimiter.isEmpty()) {
                return null;
            }
            if (maps.isEmpty()) {
                return null;
            }


            //回复的数据
            Map<String, byte[]> replay = new HashMap<>();
            if ("HEX".equalsIgnoreCase(encode)) {

                //16进制
                try {
                    //包大小
                    int pack = Integer.parseInt(packSize);
                    maps.forEach((k, v) -> replay.put(CodeFormat.hexToUtf8(k), CodeFormat.hex(v)));
                    return new MockResponsesPackSize(replay, pack);
                } catch (NumberFormatException e) {
                    byte[] hex = CodeFormat.hex(delimiter);
                    if (hex == null || hex.length == 0) return null;
                    maps.forEach((k, v) -> replay.put(CodeFormat.hexToUtf8(k), CodeFormat.hex(v)));
                    return new MockResponsesDelimiter(replay, hex);
                }

            } else {
                try {
                    int pack = Integer.parseInt(packSize);
                    maps.forEach((k, v) -> replay.put(CodeFormat.utf8(CodeFormat.utf8(k)), CodeFormat.utf8(v)));
                    return new MockResponsesPackSize(replay, pack);
                } catch (NumberFormatException e) {
                    byte[] deli = CodeFormat.utf8(delimiter);
                    if (deli == null || deli.length == 0) return null;
                    maps.forEach((k, v) -> replay.put(CodeFormat.utf8(CodeFormat.utf8(k)), CodeFormat.utf8(v)));
                    return new MockResponsesDelimiter(replay, deli);
                }
            }
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void close() {
        dataBuffer.clear();
        replays.clear();
        serialComm = null;
    }

    public abstract void checkData(byte[] bytes);

}
