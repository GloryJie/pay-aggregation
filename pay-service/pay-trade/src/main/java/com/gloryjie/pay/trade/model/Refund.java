package com.gloryjie.pay.trade.model;

import com.gloryjie.pay.trade.enums.RefundStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class Refund {
    /**
     * 退款单no
     */
    private String refundNo;

    /**
     * 退款订单对应的支付订单no
     */
    private String chargeNo;

    /**
     * 商户订单号
     */
    private String orderNo;

    /**
     * 应用id
     */
    private Integer appId;

    /**
     * 支付渠道
     */
    private String channel;

    /**
     * 退款金额
     */
    private Long amount;

    /**
     * 退款备注
     */
    private String description;

    private String clientIp;

    /**
     * 特定渠道需要的的额外附加参数
     */
    private String extra;

    private String userHold;

    /**
     * 支付渠道退款订单号
     */
    private String platformTradeNo;

    /**
     * 创建时间
     */
    private Long timeCreated;

    /**
     * 退款成功时间
     */
    private Long timeSucceed;

    /**
     * 退款状态
     */
    private RefundStatus status;

    /**
     * 错误码
     */
    private String failureCode;

    /**
     * 错误消息的描述
     */
    private String failureMsg;

    /**
     * 三位货币ISO代码，目前仅支持cny
     */
    private String currency;

    /**
     * 操作人id
     */
    private String operatorId;

    private Integer version;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;
}