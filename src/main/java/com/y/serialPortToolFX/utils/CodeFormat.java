package com.y.serialPortToolFX.utils;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.text.StringEscapeUtils;

import java.nio.charset.StandardCharsets;

public class CodeFormat {
    private CodeFormat() {
    }

    public static String utf8(byte[] bytes) {
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public static byte[] utf8(String msg) {
        return StringEscapeUtils.unescapeJava(msg).getBytes(StandardCharsets.UTF_8);
    }

    public static String hex(byte[] bytes) {
        return Hex.encodeHexString(bytes).replaceAll("..", "$0 ").trim();
    }

    public static byte[] hex(String msg) {
        try {
            return Hex.decodeHex(msg.replaceAll("\\s+", ""));
        } catch (DecoderException e) {
            return null;
        }
    }

}
