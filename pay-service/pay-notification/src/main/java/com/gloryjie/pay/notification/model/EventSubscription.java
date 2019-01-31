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
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 事件订阅
 * @author Jie
 * @since 0.1
 */
@Data
public class EventSubscription {

    /**
     * 主键
     */
    private Integer id;

    /**
     * 订阅的应用
     */
    private Integer appId;

    /**
     * 订阅的事件类型
     */
    private EventType eventType;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    private LocalDateTime updateTime;
}
