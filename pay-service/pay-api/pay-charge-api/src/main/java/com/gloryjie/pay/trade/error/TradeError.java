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
package com.gloryjie.pay.trade.error;

import com.gloryjie.pay.base.constant.HttpStatus;
import com.gloryjie.pay.base.enums.base.BaseErrorEnum;
import lombok.Getter;

/**
 * @author Jie
 * @since
 */
@Getter
public enum  TradeError implements BaseErrorEnum {

    /**
     * 交易异常
     */
    ORDER_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "400", "订单已存在"),
    UPDATE_CHARGE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,"501","更新支付单失败"),
    CHARGE_NOT_EXISTS(HttpStatus.BAD_REQUEST,"402","支付单不存在"),
    ORDER_ALREADY_PAY(HttpStatus.BAD_REQUEST,"403","当前订单已支付,请勿重复发起支付")
    ;

    private int code;

    private String status;

    private String message;

    TradeError(int code, String status, String message){
        this.code = code;
        this.status = code + status;
        this.message = message;
    }
}
