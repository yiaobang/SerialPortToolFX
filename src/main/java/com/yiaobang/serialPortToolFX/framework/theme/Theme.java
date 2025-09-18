package com.yiaobang.serialporttoolfx.framework.theme;

/**
 * 主题枚举
 *
 * @author Y
 * @date 2024/05/14
 */
public enum Theme {
    CUPERTINO_LIGHT("/atlantafx/base/theme/cupertino-light.css"),
    CUPERTINO_DARK("/atlantafx/base/theme/cupertino-dark.css");

    private final String css;
    private static final Theme[] VALUES = values();
    private static final int LENGTH = VALUES.length;

    Theme(String css) {
        this.css = css;
    }

    public final String getCss() {
        return css;
    }

    public static String rotationTheme(int index) {
        return VALUES[index % LENGTH].css;
    }
}