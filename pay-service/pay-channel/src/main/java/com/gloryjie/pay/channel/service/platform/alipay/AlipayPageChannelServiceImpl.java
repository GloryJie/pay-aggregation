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
import com.gloryjie.pay.base.util.AmountUtil;
import com.gloryjie.pay.channel.constant.ChannelConstant;
import com.gloryjie.pay.channel.dto.ChannelPayDto;
import com.gloryjie.pay.channel.dto.response.ChannelPayResponse;
import com.gloryjie.pay.channel.enums.ChannelType;
import com.gloryjie.pay.channel.error.ChannelError;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * @author Jie
 * @since 0.1
 */
@Service
public class AlipayPageChannelServiceImpl extends BaseAlipayChannelService {



    @Override
    public ChannelPayResponse pay(ChannelPayDto payDto) {

        AlipayClient client = getAlipayClient(payDto.getAppId(), payDto.getChannel());
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        if (payDto.getExtra() != null && StringUtils.isNotBlank(payDto.getExtra().get(ChannelConstant.Alipay.WAP_PAGE_EXTRA))) {
            request.setReturnUrl(payDto.getExtra().get(ChannelConstant.Alipay.WAP_PAGE_EXTRA));
        }
        request.setNotifyUrl(host + notifyUri);

        AlipayTradePagePayModel model = new AlipayTradePagePayModel();
        model.setOutTradeNo(payDto.getChargeNo());
        model.setTotalAmount(AmountUtil.amountToStr(payDto.getAmount()));
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
