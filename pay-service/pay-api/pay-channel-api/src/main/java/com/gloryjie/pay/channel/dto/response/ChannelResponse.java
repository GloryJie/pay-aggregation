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
package com.gloryjie.pay.channel.dto.response;

import com.alipay.api.AlipayResponse;
import lombok.Data;

/**
 * 通用的渠道响应对象
 * @author Jie
 * @since
 */
@Data
public class ChannelResponse {

    protected boolean success;

    protected String code;

    protected String subCode;

    protected String msg;

    protected String subMsg;

    public ChannelResponse(){
    }

    public ChannelResponse(AlipayResponse response){
        this.code = response.getCode();
        this.msg = response.getMsg();
        this.subCode = response.getSubCode();
        this.subCode = response.getSubCode();
        this.success = response.isSuccess();
    }

}
