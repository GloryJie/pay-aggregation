/* ------------------------------------------------------------------
 *   Product:      music-dj
 *   Module Name:  com.gloryjie.common.util
 *   Package Name: com.gloryjie.common.util
 *   Date Created: 2018-09-16
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2018-09-16      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.base.util;


import com.gloryjie.pay.base.constant.DefaultConstant;
import lombok.NonNull;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * 时间转换工具类,包含Java8时间
 *
 * @author Jie
 * @since 0.1
 */
public class DateTimeUtil {


    /**
     * 将时间转换为string
     *
     * @param dateTime 时间对象
     * @param pattern  格式
     */
    public static String parse(LocalDateTime dateTime, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return formatter.format(dateTime);
    }

    /**
     * 转换为默认的格式
     */
    public static String parse(@NonNull LocalDateTime dateTime) {
        return parse(dateTime, DefaultConstant.DATE_TIME_FORMAT);
    }

    /**
     * 获取当前的时间
     */
    public static String now(@NonNull String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return Instant.now().atOffset(ZoneOffset.of(DefaultConstant.ZONE_OFFSET)).format(formatter);
    }

    /**
     * 获取当前字符串描述的时间
     *
     * @return 默认格式
     */
    public static String now() {
        return now(DefaultConstant.DATE_TIME_FORMAT);
    }

    /**
     * 获取当前时间戳
     */
    public static long currentTimeMillis() {
        return Instant.now().toEpochMilli();
    }


}
