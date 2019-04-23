package com.gloryjie.pay.channel.enums;

import lombok.Getter;

/**
 * @author jie
 * @since 2019/4/23
 */
@Getter
public enum  AsyncNotifyType {

    /**
     * 异步通知类型: 支付结果, 退款结果
     */
    TRADE_RESULT(0,"支付结果"),
    REFUND_RESULT(1,"退款结果");

    int code;

    String desc;

    AsyncNotifyType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }


}
