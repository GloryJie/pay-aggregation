package com.gloryjie.pay.channel.enums;

import lombok.Getter;

/**
 * @author jie
 * @since 2019/4/21
 */
@Getter
public enum UnionpayStatus {

    /**
     * 根据应答码来划分状态
     * https://open.unionpay.com/tjweb/acproduct/list?apiservId=448&version=V2.2
     */
    TRADE_ACCEPT_SUCCESS(0, "交易成功受理"),
    TRADE_SUCCESS(1, "交易成功"),
    TIMEOUT_OR_UNKNOWN_STATUS(2, "通讯超时或状态未明"),
    TRADE_REPEAT(3, "交易重复"),
    TRADE_NOT_EXISTS(4, "交易不存在"),
    TRADE_FAIL(5, "交易失败");

    int code;

    String desc;

    UnionpayStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
