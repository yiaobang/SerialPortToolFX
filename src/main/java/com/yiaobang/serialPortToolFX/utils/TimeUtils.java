package com.yiaobang.serialporttoolfx.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * 时间工具类
 *
 * @author Y
 * @version 1.0
 * @date 2023/5/15 12:19
 */
public class TimeUtils {
    public static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss SSS");
    public static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter TF = DateTimeFormatter.ofPattern("HH:mm:ss");
    public static final DateTimeFormatter FILE_NAME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH.mm.ss");

    private TimeUtils() {
    }

    public static String getFileName() {
        return FILE_NAME.format(LocalDateTime.now());
    }

    public static String getTime() {
        return TF.format(LocalDateTime.now());
    }

    public static String getDate() {
        return DF.format(LocalDateTime.now());
    }

    /**
     * 获取当前时间以字符串形式返回
     *
     * @return {@link String}
     */
    public static String getNow() {
        return DTF.format(LocalDateTime.now());
    }

    /**
     * 得到现在时间
     *
     * @return {@link LocalDateTime}
     */
    public static LocalDateTime now() {
        return LocalDateTime.now();
    }

    /**
     * 返回两个时间之间相差多少秒
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return long
     */
    public static long secondsDifference(LocalDateTime startTime, LocalDateTime endTime) {
        return ChronoUnit.SECONDS.between(startTime, endTime);
    }

    /**
     * 返回两个时间之间相差多少分钟
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return long
     */
    public static long minutesDifference(LocalDateTime startTime, LocalDateTime endTime) {
        return ChronoUnit.MINUTES.between(startTime, endTime);
    }

    /**
     * 返回两个时间之间相差多少小时
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return long
     */
    public static long hoursDifference(LocalDateTime startTime, LocalDateTime endTime) {
        return ChronoUnit.HOURS.between(startTime, endTime);
    }

    /**
     * 返回两个时间之间相差多少天
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return long
     */
    public static long daysDifference(LocalDate startTime, LocalDate endTime) {
        return ChronoUnit.DAYS.between(startTime, endTime);
    }

    /**
     * 返回两个时间之间相差多少周
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return long
     */
    public static long weeksDifference(LocalDate startTime, LocalDate endTime) {
        return ChronoUnit.WEEKS.between(startTime, endTime);
    }

    /**
     * 返回两个时间之间相差多少月
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return long
     */
    public static long monthsDifference(LocalDate startTime, LocalDate endTime) {
        return ChronoUnit.MONTHS.between(startTime, endTime);
    }

    /**
     * 返回两个时间之间相差多少年
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return long
     */
    public static long yearsDifference(LocalDate startTime, LocalDate endTime) {
        return ChronoUnit.YEARS.between(startTime, endTime);
    }

    /**
     * 以给定的时间单位为基准返回时间差
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param cu        时间单位
     * @return long
     */
    public static long timeDifference(LocalDate startTime, LocalDate endTime, ChronoUnit cu) {
        return cu.between(startTime, endTime);
    }
}