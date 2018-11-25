/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.channel.service
 *   Date Created: 2018/11/24
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2018/11/24      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.channel.service;

import com.gloryjie.pay.channel.dto.*;

/**
 * 渠道服务
 * @author Jie
 * @since
 */
public interface ChannelService {

    /**
     * 发起支付请求
     * @return
     */
    ChannelResponse pay(ChannelPayDto payDto);

    /**
     * 发起退款请求
     * @return
     * @param refundDto
     */
    ChannelResponse refund(ChannelRefundDto refundDto);

    /**
     * 查询支付单结果
     * @return
     * @param queryDto
     */
    ChannelResponse queryPayment(ChannelPayQueryDto queryDto);

    /**
     * 退款结果查询
     * @return
     * @param queryDto
     */
    ChannelResponse queryRefund(ChannelRefundQueryDto queryDto);

    /**
     * 处理渠道的异步通知
     * @return
     */
    boolean handleAsync();

    /**
     * 渠道验签
     * @return
     */
    boolean verifySign();

}
