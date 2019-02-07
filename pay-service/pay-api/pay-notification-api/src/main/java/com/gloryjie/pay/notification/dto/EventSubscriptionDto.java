/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.notification.dto
 *   Date Created: 2019/2/7
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2019/2/7      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.notification.dto;

import com.gloryjie.pay.notification.enums.EventType;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author Jie
 * @since
 */
@Data
public class EventSubscriptionDto {

    /**
     * 订阅的应用
     */
    private Integer appId;

    /**
     * 订阅的事件类型
     */
    @NotNull
    private EventType eventType;

    /**
     * 事件通知地址
     */
    @NotNull
    private String notifyUrl;

}
