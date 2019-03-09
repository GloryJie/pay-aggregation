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

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gloryjie.pay.base.annotation.IgnoreCovertProperty;
import com.gloryjie.pay.base.constant.DefaultConstant;
import com.gloryjie.pay.notification.enums.EventType;
import com.gloryjie.pay.notification.enums.NotifyStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

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
     * 事件源号，如chargeNo
     */
    @IgnoreCovertProperty
    private String sourceNo;

    /**
     * 事件所属应用id
     */
    private Integer appId;

    /**
     * 事件类型
     */
    private EventType type;

    @IgnoreCovertProperty
    private NotifyStatus notifyStatus;

    @IgnoreCovertProperty
    private String notifyUrl;

    /**
     * 事件发生时间
     */
    @JsonFormat(pattern = DefaultConstant.DATE_TIME_FORMAT)
    private LocalDateTime timeOccur;

    /**
     * 当前通知发起的时间
     */
    @JsonFormat(pattern = DefaultConstant.DATE_TIME_FORMAT)
    private LocalDateTime currentTimeNotify;

    /**
     * 当前事件通知次数
     */
    private Integer notifyTime;

    /**
     * 事件携带的数据
     */
    private Map<String,Object> eventData;

    /**
     * 携带的签名
     */
    private String sign;

    /**
     * 上次响应
     */
    @IgnoreCovertProperty
    private String lastReply;

    /**
     * 上次通知时间
     */
    @IgnoreCovertProperty
    @JsonFormat(pattern = DefaultConstant.DATE_TIME_FORMAT)
    private LocalDateTime timeLastNotify;

}
