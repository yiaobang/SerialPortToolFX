package com.yiaobang.serialporttoolfx.utils;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * 国际化工具类
 * Internationalization utility class
 *
 * @author Y
 * @date 2024/05/14
 */
public class I18nUtils {
    private static final String BUNDLE_NAME = "com.yiaobang.serialportfx.i18n.messages";
    private static ResourceBundle bundle;
    private static Locale currentLocale;
    private static boolean initialized = false;

    private static void ensureInitialized() {
        if (!initialized) {
            setLocale(getSystemLocale());
            initialized = true;
        }
    }

    /**
     * 获取系统语言环境
     * Get system locale
     */
    private static Locale getSystemLocale() {
        Locale systemLocale = Locale.getDefault();
        String language = systemLocale.getLanguage();
        
        // 支持的语言：中文、英文、日文
        // Supported languages: Chinese, English, Japanese
        return switch (language) {
            case "zh" -> Locale.SIMPLIFIED_CHINESE;
            case "ja" -> Locale.JAPANESE;
            default -> Locale.ENGLISH;
        };
    }

    /**
     * 设置语言环境
     * Set locale
     */
    public static void setLocale(Locale locale) {
        currentLocale = locale;
        String suffix = switch (locale.getLanguage()) {
            case "zh" -> "_ZH";
            case "ja" -> "_JA";
            default -> "_EN";
        };
        
        try {
            bundle = ResourceBundle.getBundle(BUNDLE_NAME + suffix);
        } catch (Exception e) {
            // 如果加载失败，使用英语作为后备
            // If loading fails, use English as fallback
            try {
                bundle = ResourceBundle.getBundle(BUNDLE_NAME + "_EN");
            } catch (Exception fallbackException) {
                // 如果连英语都加载失败，创建一个空的bundle
                bundle = null;
            }
        }
    }

    /**
     * 获取国际化文本
     * Get internationalized text
     */
    public static String getString(String key) {
        ensureInitialized();
        if (bundle == null) {
            return key;
        }
        try {
            return bundle.getString(key);
        } catch (Exception e) {
            return key; // 如果找不到key，返回key本身
        }
    }

    /**
     * 获取当前语言环境
     * Get current locale
     */
    public static Locale getCurrentLocale() {
        ensureInitialized();
        return currentLocale;
    }

    /**
     * 获取支持的语言环境列表
     * Get supported locales
     */
    public static Locale[] getSupportedLocales() {
        return new Locale[]{
            Locale.SIMPLIFIED_CHINESE,
            Locale.ENGLISH,
            Locale.JAPANESE
        };
    }
}