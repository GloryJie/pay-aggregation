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
import com.gloryjie.pay.channel.dto.response.ChannelRefundResponse;
import com.gloryjie.pay.channel.dto.response.ChannelResponse;
import com.gloryjie.pay.channel.enums.ChannelType;

import java.util.Map;

/**
 * 支付渠道服务
 * @author Jie
 * @since 0.1
 */
public interface PayChannelService {

    /**
     * 获取当前渠道类型
     * @return
     */
    ChannelType getChannelType();

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
     * 处理渠道支付的异步通知，负责验签以及组合需要的参数，不负责业务检查
     * @return
     * @param param
     */
    ChannelPayQueryResponse handleTradeAsyncNotify(Integer appId, Map<String, String> param);

    /**
     * 处理渠道的退款异步通知，负责验签以及组合需要的参数，不负责业务检查
     * @return
     * @param param
     */
    ChannelRefundResponse handleRefundAsyncNotify(Integer appId, Map<String, String> param);

    /**
     * 渠道验签
     * @param appId 应用
     * @param param 参数
     */
    boolean verifySign(Integer appId, Map<String, String> param);

}
