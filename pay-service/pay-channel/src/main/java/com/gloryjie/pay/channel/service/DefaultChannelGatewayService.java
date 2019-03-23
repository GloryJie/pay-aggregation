/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.channel.service
 *   Date Created: 2018/12/21
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2018/12/21      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.channel.service;

import com.gloryjie.pay.channel.dto.*;
import com.gloryjie.pay.channel.dto.response.ChannelPayResponse;
import com.gloryjie.pay.channel.dto.response.ChannelResponse;
import com.gloryjie.pay.channel.enums.ChannelType;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 支付渠道方式选择的统一入口,用于分发至实际渠道
 *
 * @author Jie
 * @since
 */
@Service
public class DefaultChannelGatewayService implements ChannelGatewayService, ApplicationContextAware {

    Map<ChannelType, PayChannelService> channelMap = new ConcurrentHashMap<>();

    @Override
    public ChannelPayResponse pay(ChannelPayDto payDto) {
        return channelMap.get(payDto.getChannel()).pay(payDto);
    }

    @Override
    public ChannelPayQueryResponse handleAsyncNotify(Integer appId, ChannelType channelType, Map<String, String> param) {
        return channelMap.get(channelType).handleAsyncNotify(appId, param);
    }

    @Override
    public ChannelResponse refund(ChannelRefundDto refundDto) {
        return channelMap.get(refundDto.getChannel()).refund(refundDto);
    }

    @Override
    public ChannelResponse queryPayment(ChannelPayQueryDto queryDto) {
        return channelMap.get(queryDto.getChannel()).queryPayment(queryDto);
    }

    @Override
    public ChannelResponse queryRefund(ChannelRefundQueryDto queryDto) {
        return channelMap.get(queryDto.getChannel()).queryRefund(queryDto);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, PayChannelService> beanMap = applicationContext.getBeansOfType(PayChannelService.class);
        beanMap.values().forEach(item -> channelMap.put(item.getChannelType(), item));
    }
}
