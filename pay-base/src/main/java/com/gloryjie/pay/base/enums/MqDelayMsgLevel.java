/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.base.enums
 *   Date Created: 2019/1/31
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2019/1/31      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.base.enums;

import lombok.Getter;

/**
 * @author Jie
 * @since 0.1
 */
@Getter
public enum MqDelayMsgLevel {

    /**
     * 延迟消息级别
     * 默认：1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h
     * 修改后：0s 10s 30s 1m 4m 8m 10m 20m 30m 1h 2h 6h 10h 12h 14h 15h 16h 17h 20h 24h
     */
    FIRST(1, 0L, "0s"),
    SECOND(2, 10L, "10s"),
    THIRD(3, 30L, "30s"),
    FOURTH(4, 60L, "1m"),
    FIFTH(5, 240L, "4m"),
    SIXTH(6, 480L, "8m"),
    SEVENTH(7, 600L, "10m"),
    EIGHTH(8, 1200L, "20m"),
    NINTH(9, 1800L, "30m"),
    TENTH(10, 3600L, "1h"),
    ELEVENTH(11, 7200L, "2h"),
    TWELFTH(12, 21600L, "6h"),
    THIRTEENTH(13, 36000L, "10h"),
    FOURTEENTH(14, 43200L, "12h"),
    FIFTEENTH(15, 50400L, "14h"),
    SIXTEENTH(16, 54000L, "15h"),
    SEVENTEENTH(17, 57600L, "16h"),
    EIGHTEENTH(18, 61200L, "17h"),
    NINETEENTH(19, 72000L, "20h"),
    TWENTIETH(20, 86400L, "24h");


    int level;

    long millisecond;

    String time;

    MqDelayMsgLevel(int level, long millisecond, String time) {
        this.level = level;
        this.millisecond = millisecond;
        this.time = time;
    }

    /**
     * 根据时间来获取对应延迟级别
     * @param time 时间，秒为单位
     * @return
     */
    public static MqDelayMsgLevel computeLevel(long time) {
        for (MqDelayMsgLevel value : MqDelayMsgLevel.values()) {
            if (time <= value.getMillisecond()) {
                return value;
            }
        }
        return MqDelayMsgLevel.TWENTIETH;
    }

    public static MqDelayMsgLevel valueOfTime(String time){
        for (MqDelayMsgLevel value : MqDelayMsgLevel.values()) {
            if (value.getTime().equals(time)) {
                return value;
            }
        }
        return MqDelayMsgLevel.TWENTIETH;
    }
}
