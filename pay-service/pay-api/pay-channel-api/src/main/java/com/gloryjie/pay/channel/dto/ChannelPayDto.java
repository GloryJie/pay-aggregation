/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.channel.dto
 *   Date Created: 2018/11/24
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2018/11/24      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.channel.dto;

import com.gloryjie.pay.channel.enums.ChannelType;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @author Jie
 * @since
 */
@Data
public class ChannelPayDto {

    /**
     * 支付单号
     */
    private String chargeNo;

    /**
     * 发起支付的应用Id
     */
    private Integer appId;

    /**
     * 支付单金额
     */
    private BigDecimal amount;

    /**
     * 支付单标题
     */
    private String subject;

    /**
     * 支付渠道
     */
    private ChannelType channel;

    /**
     * 支付单描述
     */
    private String body;

    private Map<String,String> extra;


}
