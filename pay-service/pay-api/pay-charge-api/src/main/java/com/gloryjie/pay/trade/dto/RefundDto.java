/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.charge.dto
 *   Date Created: 2018/12/9
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2018/12/9      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.trade.dto;

import com.gloryjie.pay.channel.enums.ChannelType;
import com.gloryjie.pay.trade.enums.RefundStatus;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author Jie
 * @since
 */
@Data
public class RefundDto {

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
    private ChannelType channel;

    /**
     * 退款金额
     */
    private Long amount;

    /**
     * 退款备注
     */
    private String description;

    private String clientIp;

    private String userHold;

    /**
     * 发起退款时间
     */
    private LocalDateTime timeCreated;


    /**
     * 退款成功时间
     */
    private LocalDateTime timeSucceed;

    /**
     * 退款状态
     */
    private RefundStatus status;

    /**
     * 三位货币ISO代码，目前仅支持cny
     */
    private String currency;

}
