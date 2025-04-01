package com.yiaobang.javafxTool.theme;

public enum Theme {
    CUPERTINO_LIGHT("/atlantafx/base/theme/cupertino-light.css"),
    CUPERTINO_DARK("/atlantafx/base/theme/cupertino-dark.css");

    private final String css;
    private static final int length = values().length;

    Theme(String css) {
        this.css = css;
    }

    public final String getCss() {
        return css;
    }

    public static String rotationTheme(int index) {
        return values()[index % length].css;
    }
}
