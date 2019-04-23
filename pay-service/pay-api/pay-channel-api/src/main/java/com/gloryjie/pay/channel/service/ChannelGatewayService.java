/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.channel.service
 *   Date Created: 2018/12/9
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2018/12/9      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.channel.service;

import com.gloryjie.pay.channel.dto.*;
import com.gloryjie.pay.channel.dto.response.ChannelResponse;
import com.gloryjie.pay.channel.enums.ChannelType;

import java.util.Map;


/**
 * @author Jie
 * @since
 */
public interface ChannelGatewayService {

    /**
     * 发起支付请求
     * @return
     */
    <T extends ChannelResponse> T pay(ChannelPayDto payDto);

    /**
     * 发起退款请求
     * @return
     * @param refundDto
     */
    <T extends ChannelResponse> T refund(ChannelRefundDto refundDto);

    /**
     * 查询支付单结果
     * @return
     * @param queryDto
     */
    <T extends ChannelResponse> T queryPayment(ChannelPayQueryDto queryDto);

    /**
     * 退款结果查询
     * @return
     * @param queryDto
     */
    <T extends ChannelResponse> T queryRefund(ChannelRefundQueryDto queryDto);

    /**
     * 处理交易异步通知
     * @param appId
     * @param channelType
     * @param param
     * @return
     */
    <T extends ChannelResponse> T handleTradeAsyncNotify(Integer appId, ChannelType channelType, Map<String, String> param);


    /**
     * 处理退款异步通知
     * @param appId
     * @param channelType
     * @param param
     * @return
     */
    <T extends ChannelResponse> T handleRefundAsyncNotify(Integer appId, ChannelType channelType, Map<String, String> param);

}
