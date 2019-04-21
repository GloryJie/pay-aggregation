/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.channel.dto
 *   Date Created: 2018/11/25
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2018/11/25      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.channel.dto;

import com.gloryjie.pay.channel.enums.ChannelType;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 渠道退款参数
 * @author Jie
 * @since
 */
@Data
public class ChannelRefundDto {

    /**
     * 应用
     */
    @NotNull
    private Integer appId;

    /**
     * 退款渠道
     */
    @NotNull
    private ChannelType channel;

    /**
     * 支付单号
     */
    @NotNull
    private String chargeNo;

    /**
     * 退款单号
     */
    @NotNull
    private String refundNo;

    /**
     * 退款金额
     */
    @NotNull
    private Long amount;

    /**
     * 原支付单金额
     */
    @NotNull
    private Long chargeAmount;

    /**
     * 支付平台的流水交易号
     */
    @NotNull
    private String platformTradeNo;


}
