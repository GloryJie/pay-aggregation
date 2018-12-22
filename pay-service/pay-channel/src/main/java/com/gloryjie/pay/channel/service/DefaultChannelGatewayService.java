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
import com.gloryjie.pay.channel.service.platform.alipay.AlipayBarCodeChannelServiceImpl;
import com.gloryjie.pay.channel.service.platform.alipay.AlipayPageChannelServiceImpl;
import com.gloryjie.pay.channel.service.platform.alipay.AlipayScanCodeChannelServiceImpl;
import com.gloryjie.pay.channel.service.platform.alipay.AlipayWapChannelServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 支付渠道方式选择的统一入口,用于分发至实际渠道
 *
 * @author Jie
 * @since
 */
@Service
public class DefaultChannelGatewayService implements ChannelGatewayService {

    Map<ChannelType, ChannelService> channelMap = new ConcurrentHashMap<>();

    @Autowired
    private AlipayPageChannelServiceImpl alipayPageChannelService;

    @Autowired
    private AlipayBarCodeChannelServiceImpl alipayBarCodeChannelService;

    @Autowired
    private AlipayWapChannelServiceImpl alipayWapChannelService;

    @Autowired
    private AlipayScanCodeChannelServiceImpl alipayScanCodeChannelService;

    @PostConstruct
    public void initChannel() {
        channelMap.put(ChannelType.ALIPAY_PAGE, alipayPageChannelService);
        channelMap.put(ChannelType.ALIPAY_WAP, alipayWapChannelService);
        channelMap.put(ChannelType.ALIPAY_SCAN_CODE, alipayScanCodeChannelService);
        channelMap.put(ChannelType.ALIPAY_BAR_CODE, alipayBarCodeChannelService);
    }

    @Override
    public ChannelPayResponse pay(ChannelPayDto payDto) {
        return channelMap.get(payDto.getChannel()).pay(payDto);
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
}
