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
import com.gloryjie.pay.channel.dto.response.ChannelPayResponse;
import com.gloryjie.pay.channel.dto.response.ChannelResponse;

import java.util.Map;

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
    ChannelPayResponse pay(ChannelPayDto payDto);

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
    ChannelPayQueryResponse queryPayment(ChannelPayQueryDto queryDto);

    /**
     * 退款结果查询
     * @return
     * @param queryDto
     */
    ChannelResponse queryRefund(ChannelRefundQueryDto queryDto);

    /**
     * 处理渠道的异步通知
     * @return
     * @param param
     */
    boolean handleAsync(Map<String, String> param);

    /**
     * 渠道验签
     * @param param 参数
     * @param publicKey 支付宝公钥
     * @param signType 签名方式
     */
    boolean verifySign(Map<String, String> param, String publicKey, String signType);

}
