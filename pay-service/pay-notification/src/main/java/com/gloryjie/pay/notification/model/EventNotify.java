/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.notification.model
 *   Date Created: 2019/1/31
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2019/1/31      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.notification.model;

import com.gloryjie.pay.notification.enums.EventType;
import com.gloryjie.pay.notification.enums.NotifyStatus;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author Jie
 * @since 0.1
 */
@Data
public class EventNotify {

    /**
     * 事件号
     */
    private String eventNo;

    /**
     * 事件源号，如chargeNo
     */
    private String sourceNo;

    /**
     * 事件所属应用id
     */
    private Integer appId;

    private NotifyStatus notifyStatus;

    /**
     * 事件类型
     */
    private EventType type;

    /**
     * 事件发生时间
     */
    private LocalDateTime timeOccur;

    /**
     * 当前事件通知次数
     */
    private Integer notifyTime;

    /**
     * 通知的时间间隔，为rmq的延迟级别，json数组
     */
    private String notifyInterval;

    /**
     * 事件携带的数据，json格式
     */
    private String eventData;

    /**
     * 上次响应
     */
    private String lastReply;

    /**
     * 上次通知时间
     */
    private LocalDateTime timeLastNotify;

    private Integer version;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    private LocalDateTime updateTime;
}
