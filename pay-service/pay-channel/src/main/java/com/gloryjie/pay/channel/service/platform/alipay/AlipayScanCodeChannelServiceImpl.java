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
import com.alipay.api.domain.AlipayTradePrecreateModel;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.gloryjie.pay.base.exception.error.ExternalException;
import com.gloryjie.pay.base.util.JsonUtil;
import com.gloryjie.pay.channel.config.AlipayChannelConfig;
import com.gloryjie.pay.channel.dto.ChannelPayDto;
import com.gloryjie.pay.channel.dto.response.ChannelPayResponse;
import com.gloryjie.pay.channel.error.ChannelError;
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
    public ChannelPayResponse pay(ChannelPayDto payDto) {
        ChannelConfig config = channelConfigDao.loadByAppIdAndChannel(payDto.getAppId(), payDto.getChannel().name());
        AlipayChannelConfig alipayChannelConfig = JsonUtil.parse(config.getChannelConfig(), AlipayChannelConfig.class);
        AlipayClient client = getAlipayClient(alipayChannelConfig);

        AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest();
        AlipayTradePrecreateModel model = new AlipayTradePrecreateModel();
        model.setTotalAmount(payDto.getAmount().toString());
        model.setOutTradeNo(payDto.getChargeNo());
        model.setSubject(payDto.getSubject());
        model.setTimeoutExpress(payDto.getTimeExpire() + "m");
        model.setQrCodeTimeoutExpress(payDto.getTimeExpire() + "m");
        request.setBizModel(model);

        try {
            AlipayTradePrecreateResponse alipayResponse = client.execute(request);
            ChannelPayResponse payResponse = new ChannelPayResponse(alipayResponse);
            if (alipayResponse.isSuccess()) {
                payResponse.setCredential(alipayResponse.getQrCode());
            }
            return payResponse;
        } catch (AlipayApiException e) {
            throw ExternalException.create(ChannelError.PAY_PLATFORM_ERROR, e.getErrMsg());

        }
    }
}
