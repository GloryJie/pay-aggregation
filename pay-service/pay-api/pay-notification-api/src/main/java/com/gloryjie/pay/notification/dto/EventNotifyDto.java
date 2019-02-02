/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.notification.dto
 *   Date Created: 2019/1/31
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2019/1/31      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.notification.dto;

import com.gloryjie.pay.notification.enums.EventType;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author Jie
 * @since 0.1
 */
@Data
public class EventNotifyDto {

    /**
     * 事件号
     */
    private String eventNo;

    /**
     * 事件所属应用id
     */
    private Integer appId;

    /**
     * 事件类型
     */
    private EventType type;

    /**
     * 事件发生时间
     */
    private LocalDateTime timeOccur;

    /**
     * 当前通知发起的时间
     */
    private LocalDateTime currentTimeNotify;

    /**
     * 当前事件通知次数
     */
    private Integer notifyTime;

    /**
     * 事件携带的数据
     */
    private Object eventData;

    /**
     * 携带的签名
     */
    private String sign;

}
