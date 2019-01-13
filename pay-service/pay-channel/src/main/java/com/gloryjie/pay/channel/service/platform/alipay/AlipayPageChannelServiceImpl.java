/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.channel.service.platform.alipay
 *   Date Created: 2018/11/25
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2018/11/25      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.channel.service.platform.alipay;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayResponse;
import com.alipay.api.domain.AlipayTradePagePayModel;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.gloryjie.pay.base.exception.error.ExternalException;
import com.gloryjie.pay.base.util.JsonUtil;
import com.gloryjie.pay.channel.config.AlipayChannelConfig;
import com.gloryjie.pay.channel.dto.ChannelPayDto;
import com.gloryjie.pay.channel.dto.response.ChannelPayResponse;
import com.gloryjie.pay.channel.enums.ChannelType;
import com.gloryjie.pay.channel.error.ChannelError;
import com.gloryjie.pay.channel.model.ChannelConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author Jie
 * @since 0.1
 */
@Service
public class AlipayPageChannelServiceImpl extends AlipayChannelService {

    @Value("${domain}")
    private String domain;


    @Override
    public ChannelPayResponse pay(ChannelPayDto payDto) {
        ChannelConfig config = channelConfigDao.loadByAppIdAndChannel(payDto.getAppId(), payDto.getChannel().name());
        AlipayChannelConfig alipayChannelConfig = JsonUtil.parse(config.getChannelConfig(), AlipayChannelConfig.class);
        AlipayClient client = getAlipayClient(alipayChannelConfig);
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        // TODO: 2018/11/25 跳转地址需要修改为商户上送,并且需要设置异步通知地址
        request.setReturnUrl("http://localhost:10020/alipay/notify");
        request.setNotifyUrl(domain + "/platform/notify/alipay");
//        http://jierong.nat300.top/notification/alipay

        AlipayTradePagePayModel model = new AlipayTradePagePayModel();
        model.setOutTradeNo(payDto.getChargeNo());
        model.setTotalAmount(String.valueOf(payDto.getAmount() / 100));
        model.setSubject(payDto.getSubject());
        model.setProductCode(ChannelType.ALIPAY_PAGE.getProductCode());

        request.setBizModel(model);

        try {
            AlipayResponse alipayResponse = client.pageExecute(request);
            ChannelPayResponse payResponse = new ChannelPayResponse(alipayResponse);
            if (alipayResponse.isSuccess()) {
                payResponse.setCredential(alipayResponse.getBody());
            }
            return payResponse;
        } catch (AlipayApiException e) {
            throw ExternalException.create(ChannelError.PAY_PLATFORM_ERROR, e.getErrMsg());
        }
    }

}
