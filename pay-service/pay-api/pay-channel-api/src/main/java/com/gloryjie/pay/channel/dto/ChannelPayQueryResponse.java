/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.channel.dto
 *   Date Created: 2018/12/22
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2018/12/22      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.channel.dto;

import com.alipay.api.AlipayResponse;
import com.gloryjie.pay.channel.dto.response.ChannelResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Jie
 * @since
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ChannelPayQueryResponse extends ChannelResponse {

    private String platformTradeNo;

    private String status;

    private BigDecimal amount;

    private BigDecimal actualAmount;

    private Long timePaid;


    public ChannelPayQueryResponse(AlipayResponse response){
        this.code = response.getCode();
        this.msg = response.getMsg();
        this.subCode = response.getSubCode();
        this.subMsg = response.getSubMsg();
        this.success = response.isSuccess();
    }

    public ChannelPayQueryResponse(){}


}
