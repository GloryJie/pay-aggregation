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
import com.gloryjie.pay.base.util.JsonUtil;
import com.gloryjie.pay.channel.config.AlipayChannelConfig;
import com.gloryjie.pay.channel.dto.ChannelPayDto;
import com.gloryjie.pay.channel.dto.ChannelResponse;
import com.gloryjie.pay.channel.enums.ChannelType;
import com.gloryjie.pay.channel.model.ChannelConfig;
import org.springframework.stereotype.Service;

/**
 * @author Jie
 * @since
 */
@Service
public class AlipayPageChannelServiceImpl extends AlipayChannelService {


    @Override
    public ChannelResponse pay(ChannelPayDto payDto) {
        ChannelConfig config = channelConfigDao.loadByAppIdAndChannel(payDto.getAppId(), payDto.getChannel().name());
        AlipayChannelConfig alipayChannelConfig = JsonUtil.parse(config.getChannelConfig(), AlipayChannelConfig.class);
        AlipayClient client = getAlipayClient(alipayChannelConfig);
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        // TODO: 2018/11/25 跳转地址需要修改为商户上送,并且需要设置异步通知地址
        request.setReturnUrl("http://localhost:10020/alipay/notify");
        request.setNotifyUrl("");

        AlipayTradePagePayModel model = new AlipayTradePagePayModel();
        model.setOutTradeNo(payDto.getChargeNo());
        model.setTotalAmount(payDto.getAmount().toString());
        model.setSubject(payDto.getSubject());
        model.setProductCode(ChannelType.ALIPAY_PAGE.getProductCode());

        request.setBizModel(model);
        try {
            AlipayResponse response = client.pageExecute(request);
            System.out.println(JsonUtil.toJson(response));
            System.out.println(response.getBody());
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        return null;
    }

}
