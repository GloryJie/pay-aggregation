/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.trade.enums
 *   Date Created: 2018/12/9
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2018/12/9      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.trade.enums;

import com.gloryjie.pay.base.enums.base.BaseEnum;
import lombok.Getter;

/**
 * @author Jie
 * @since
 */
@Getter
public enum  RefundStatus implements BaseEnum {

    /**
     * 退款单状态
     */
    PROCESSING(10, "退款处理中"),
    SUCCESS(20, "退款成功"),
    FAILURE(30, "退款失败");

    private int code;

    private String desc;

    RefundStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
