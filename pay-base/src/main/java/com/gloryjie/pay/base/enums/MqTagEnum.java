/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.base.enums
 *   Date Created: 2019/1/24
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2019/1/24      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.base.enums;

import lombok.Getter;

/**
 * @author Jie
 * @since 0.1
 */
@Getter
public enum MqTagEnum {

    /**
     * rocketMq tag枚举
     */
    TRADE_CLOSE_CHARGE(0, "定时关单"),
    TRADE_ASYNC_REFUND(1, "异步退款"),
    CHARGE_SUCCESS(2, "支付成功"),
    REFUND_SUCCESS(3, "退款成功"),
    EVENT_NOTIFY(4,"事件通知");

    private int code;
    private String desc;

    MqTagEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
