/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.trade.dto
 *   Date Created: 2019/1/26
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2019/1/26      Jie            GloryJie@163.com
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
public class RefreshRefundDto {

    private String refundNo;

    private String platformTradeNo;

    private Integer appId;

    private ChannelType channel;

    private RefundStatus status;

    private Long amount;

    private LocalDateTime timeSucceed;

    /**
     * 错误码
     */
    private String failureCode;

    /**
     * 错误消息的描述
     */
    private String failureMsg;
}
