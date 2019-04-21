/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.channel.enums
 *   Date Created: 2018/12/22
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2018/12/22      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.channel.enums;

import com.gloryjie.pay.channel.constant.ChannelConstant;
import lombok.Getter;

/**
 * @author Jie
 * @since
 */
@Getter
public enum AlipayStatus {

    /**
     * 支付宝交易状态
     */
    WAIT_BUYER_PAY(0,"交易创建，等待买家付款"),
    TRADE_CLOSED(1,"未付款交易超时关闭，或支付完成后全额退款"),
    TRADE_SUCCESS(2,"交易支付成功"),
    TRADE_FINISHED(3,"交易结束，不可退款"),
    TRADE_FAIL(4,"交易失败,自定义状态,抛出异常才会有"),
    TRADE_NOT_EXISTS(4,"交易不存在");

    int code;

    String desc;

    AlipayStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
