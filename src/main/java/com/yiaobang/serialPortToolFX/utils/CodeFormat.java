package com.yiaobang.serialporttoolfx.utils;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.text.StringEscapeUtils;

import java.nio.charset.StandardCharsets;

/**
 * 代码格式化工具类
 *
 * @author Y
 * @date 2024/05/14
 */
public class CodeFormat {
    private CodeFormat() {
    }

    public static String utf8(byte[] bytes) {
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public static byte[] utf8(String msg) {
        if (msg == null || msg.isEmpty()) {
            return null;
        }
        return StringEscapeUtils.unescapeJava(msg).getBytes(StandardCharsets.UTF_8);
    }

    public static String hex(byte[] bytes) {
        if (bytes == null) {
            return "";
        }
        return Hex.encodeHexString(bytes, false).replaceAll("..", "$0 ").trim();
    }

    public static byte[] hex(String msg) {
        try {
            return  msg.isEmpty() ? null : Hex.decodeHex(msg.replaceAll("\\s+", ""));
        } catch (DecoderException e) {
            // Log the exception for debugging purposes
            System.err.println("Failed to decode hex string: " + msg + ", error: " + e.getMessage());
            return null;
        }
    }

    public static String hexToUtf8(String hex) {
        return utf8(hex(hex));
    }
}