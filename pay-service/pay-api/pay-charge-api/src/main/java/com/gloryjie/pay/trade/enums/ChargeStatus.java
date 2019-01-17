/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.charge.enums
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
import org.omg.CORBA.PUBLIC_MEMBER;

/**
 * @author Jie
 * @since
 */
@Getter
public enum ChargeStatus implements BaseEnum {

    /**
     * 支付单状态
     */
    WAIT_PAY(10, "待支付"),
    SUCCESS(20, "支付成功"),
    EXISTS_REFUND(30, "存在退款"),
    REFUND_COMPLETED(40, "全部退款"),
    CLOSED(50, "交易关闭"),
    FAILURE(60, "交易失败");

    private int code;

    private String desc;

    ChargeStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public boolean isPaid() {
        return this == SUCCESS || this == EXISTS_REFUND || this == REFUND_COMPLETED;
    }
}
