package com.gloryjie.pay.charge.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class Charge {
    /**
     * 支付中心凭据号,系统内唯一
     */
    private String chargeNo;

    /**
     * 商户订单号，必须在商户系统内唯一
     */
    private String orderNo;

    /**
     * 平台应用Id
     */
    private Integer appId;

    /**
     * 收款方应用id
     */
    private Integer receiptAppId;

    /**
     * 服务方应用id
     */
    private Integer serviceAppId;

    /**
     * 支付单金额
     */
    private BigDecimal amount;

    /**
     * 购买商品的标题
     */
    private String subject;

    /**
     * 购买商品的描述信息
     */
    private String body;

    /**
     * 支付渠道
     */
    private String channel;

    /**
     * 发起支付的客户端IP
     */
    private String clientIp;

    /**
     * 订单备注，限制300个字符内
     */
    private String description;

    /**
     * 用户保留信息
     */
    private String userHold;

    /**
     * 支付渠道订单号
     */
    private String transactionNo;

    /**
     * 支付单创建时间，13位Unix时间戳
     */
    private Long timeCreated;

    /**
     * 支付单支付完成时间，13位Unix时间戳
     */
    private Long timePaid;

    /**
     * 支付单单失效时间，单位分钟
     */
    private Long timeExpire;

    /**
     * 订单失效时间，13位Unix时间戳
     */
    private Long expireTimestamp;

    /**
     * 是否是生产模式
     */
    private Boolean liveMode;

    /**
     * 订单状态
     */
    private Integer status;

    /**
     * 三位ISO货币代码，只支持人民币cny，默认cny
     */
    private String currency;

    /**
     * 渠道额外参数
     */
    private String extra;

    private Integer version;

    private String failureCode;

    private String failureMsg;

    /**
     * 记录修改时间
     */
    private Date updateTime;

    /**
     * 记录创建时间
     */
    private Date createTime;

    /**
     * 支付凭证
     */
    private String credential;
}