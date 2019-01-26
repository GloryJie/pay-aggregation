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
 * 交易异常，1xx
 * @author Jie
 * @since 0.1
 */
@Getter
public enum  TradeError implements BaseErrorEnum {

    /**
     * 支付异常
     */
    ORDER_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "100", "订单已存在"),
    UPDATE_CHARGE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,"101","更新支付单失败"),
    CHARGE_NOT_EXISTS(HttpStatus.BAD_REQUEST,"102","支付单不存在"),
    ORDER_ALREADY_PAY(HttpStatus.BAD_REQUEST,"103","当前订单已支付,请勿重复发起支付"),

    /**
     * 退款异常, 15x
     */
    REFUND_ERROR(HttpStatus.BAD_REQUEST,"150","退款错误"),
    REFUND_AMOUNT_OUT_RANGE(HttpStatus.BAD_REQUEST,"151","退款金额不正确"),
    CHANNEL_REFUND_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,"152","渠道退款异常"),
    REFUND_NOT_EXISTS(HttpStatus.BAD_REQUEST,"153","退款单不存在"),
    REFUND_EXISTS(HttpStatus.BAD_REQUEST,"154","已存在退款")
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
