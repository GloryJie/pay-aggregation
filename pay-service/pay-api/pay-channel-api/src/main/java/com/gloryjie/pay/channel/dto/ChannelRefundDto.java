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
    private Integer appId;

    /**
     * 退款渠道
     */
    private ChannelType channel;

    /**
     * 支付单号
     */
    private String chargeNo;

    /**
     * t退款单号
     */
    private String refundNo;

    /**
     * 退款金额
     */
    private BigDecimal amount;


}
