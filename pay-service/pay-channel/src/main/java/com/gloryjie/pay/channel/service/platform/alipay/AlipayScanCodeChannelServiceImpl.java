/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.channel.service.platform.alipay
 *   Date Created: 2018/11/27
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2018/11/27      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.channel.service.platform.alipay;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayResponse;
import com.alipay.api.domain.AlipayTradePrecreateModel;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.gloryjie.pay.base.util.JsonUtil;
import com.gloryjie.pay.channel.config.AlipayChannelConfig;
import com.gloryjie.pay.channel.dto.ChannelPayDto;
import com.gloryjie.pay.channel.dto.ChannelResponse;
import com.gloryjie.pay.channel.model.ChannelConfig;
import org.springframework.stereotype.Service;

/**
 * 支付宝扫码支付
 * @author Jie
 * @since
 */
@Service
public class AlipayScanCodeChannelServiceImpl extends AlipayChannelService {

    @Override
    public ChannelResponse pay(ChannelPayDto payDto) {
        ChannelConfig config = channelConfigDao.loadByAppIdAndChannel(payDto.getAppId(), payDto.getChannel().name());
        AlipayChannelConfig alipayChannelConfig = JsonUtil.parse(config.getChannelConfig(), AlipayChannelConfig.class);
        AlipayClient client = getAlipayClient(alipayChannelConfig);

        AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest();
        AlipayTradePrecreateModel model = new AlipayTradePrecreateModel();
        model.setTotalAmount(payDto.getAmount().toString());
        model.setOutTradeNo(payDto.getChargeNo());
        model.setSubject(payDto.getSubject());
        request.setBizModel(model);

        try {
            System.out.println(payDto.getChargeNo());
            AlipayResponse response = client.execute(request);
            System.out.println(JsonUtil.toJson(response));
            System.out.println(response.getBody());
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }

        return null;
    }
}
