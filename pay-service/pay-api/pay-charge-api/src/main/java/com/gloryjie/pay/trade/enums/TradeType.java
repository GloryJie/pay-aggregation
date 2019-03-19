/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.trade.enums
 *   Date Created: 2019/3/19
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2019/3/19      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.trade.enums;

import lombok.Getter;

/**
 * @author Jie
 * @since
 */
@Getter
public enum  TradeType {

    PAY(1,"支付"),
    REFUND(2,"退款");

    int code;

    private String desc;

    TradeType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
