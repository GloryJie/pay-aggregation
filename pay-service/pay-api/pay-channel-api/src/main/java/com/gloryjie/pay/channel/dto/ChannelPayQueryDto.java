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

/**
 * 渠道支付单查询
 * @author Jie
 * @since
 */
@Data
public class ChannelPayQueryDto {

    private String chargeNo;

    private Integer appId;

    private ChannelType channelType;

}
