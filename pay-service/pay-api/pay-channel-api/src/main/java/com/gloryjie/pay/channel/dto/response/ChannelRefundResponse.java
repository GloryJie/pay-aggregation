/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.channel.dto.response
 *   Date Created: 2019/1/22
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2019/1/22      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.channel.dto.response;

import com.alipay.api.AlipayResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author Jie
 * @since
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ChannelRefundResponse extends ChannelResponse {

    /**
     * 交易平台交易号
     */
    private String platformTradeNo;

    /**
     * 当前退款的金额
     */
    private Long refundAmount;


    public ChannelRefundResponse(AlipayResponse response){
        super(response);
    }
}
