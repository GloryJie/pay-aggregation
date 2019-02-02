/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.notification.enums
 *   Date Created: 2019/1/31
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2019/1/31      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.notification.enums;

import com.gloryjie.pay.base.enums.base.BaseEnum;
import lombok.Getter;


/**
 * @author Jie
 * @since 0.1
 */
@Getter
public enum EventType implements BaseEnum {

    /**
     * 事件类型
     */
    CHARGE_CHANGE_EVENT(10,"支付单变化"),
    REFUND_CHANGE_EVENT(20,"退款单变化");

    private int code;
    private String desc;

    EventType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
