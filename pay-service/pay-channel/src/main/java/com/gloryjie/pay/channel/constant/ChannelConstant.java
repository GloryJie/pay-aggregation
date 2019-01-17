/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.channel.constant
 *   Date Created: 2018/12/22
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2018/12/22      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.channel.constant;

import com.alipay.api.domain.PubChannelDTO;

/**
 * @author Jie
 * @since
 */
public class ChannelConstant {


    public interface Alipay {
        String TRADE_NOT_EXISTS_STATUS = "ACQ.TRADE_NOT_EXIST";

        String PAGE_EXTRA = "redirectUrl";
    }
}
