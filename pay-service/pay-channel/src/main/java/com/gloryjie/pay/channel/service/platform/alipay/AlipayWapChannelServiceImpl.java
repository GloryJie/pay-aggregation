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
import com.alipay.api.domain.AlipayTradeWapPayModel;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.gloryjie.pay.base.exception.error.ExternalException;
import com.gloryjie.pay.base.util.AmountUtil;
import com.gloryjie.pay.base.util.JsonUtil;
import com.gloryjie.pay.channel.config.AlipayChannelConfig;
import com.gloryjie.pay.channel.dto.ChannelPayDto;
import com.gloryjie.pay.channel.dto.response.ChannelPayResponse;
import com.gloryjie.pay.channel.enums.ChannelType;
import com.gloryjie.pay.channel.error.ChannelError;
import com.gloryjie.pay.channel.model.ChannelConfig;
import org.springframework.stereotype.Service;

/**
 * 支付宝手机网页支付
 *
 * @author Jie
 * @since
 */
@Service
public class AlipayWapChannelServiceImpl extends AlipayChannelService {

    @Override
    public ChannelPayResponse pay(ChannelPayDto payDto) {
        ChannelConfig config = channelConfigDao.loadByAppIdAndChannel(payDto.getAppId(), payDto.getChannel().name());
        AlipayChannelConfig alipayChannelConfig = JsonUtil.parse(config.getChannelConfig(), AlipayChannelConfig.class);
        AlipayClient client = getAlipayClient(alipayChannelConfig);

        AlipayTradeWapPayRequest request = new AlipayTradeWapPayRequest();

        AlipayTradeWapPayModel model = new AlipayTradeWapPayModel();
        model.setOutTradeNo(payDto.getChargeNo());
        model.setTotalAmount(AmountUtil.amountToStr(payDto.getAmount()));
        model.setSubject(payDto.getSubject());
        model.setTimeExpire(payDto.getTimeExpire() + "m");
        model.setProductCode(ChannelType.ALIPAY_WAP.getProductCode());

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
