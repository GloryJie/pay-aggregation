/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.trade.dto
 *   Date Created: 2019/1/13
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2019/1/13      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.trade.dto;

import com.gloryjie.pay.channel.enums.ChannelType;
import com.gloryjie.pay.trade.enums.ChargeStatus;
import lombok.Data;

/**
 * 刷新支付单的参数
 * @author Jie
 * @since 0.1
 */
@Data
public class RefreshChargeDto {

    private String chargeNo;

    private String platformTradeNo;

    private Integer appId;

    private ChannelType channel;

    private ChargeStatus status;


    private Long amount;

    private Long actualAmount;

    private Long timePaid;
}
