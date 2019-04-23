package com.gloryjie.pay.channel.service.platform.union;

import com.egzosn.pay.common.api.PayService;
import com.egzosn.pay.common.bean.MethodType;
import com.egzosn.pay.common.bean.PayOrder;
import com.egzosn.pay.union.bean.UnionTransactionType;
import com.gloryjie.pay.channel.constant.ChannelConstant;
import com.gloryjie.pay.channel.dto.ChannelPayDto;
import com.gloryjie.pay.channel.dto.response.ChannelPayResponse;
import com.gloryjie.pay.channel.enums.ChannelType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author jie
 * @since 2019/4/23
 */
@Service
public class UnionpayWapChannelServiceImpl extends BaseUnionpayChannelService {
    @Override
    public ChannelType getChannelType() {
        return ChannelType.UNIONPAY_WAP;
    }

    @Override
    public ChannelPayResponse pay(ChannelPayDto payDto) {
        String returnUrl = "";
        if (payDto.getExtra() != null && StringUtils.isNotBlank(payDto.getExtra().get(ChannelConstant.Alipay.WAP_PAGE_EXTRA))) {
            returnUrl = payDto.getExtra().get(ChannelConstant.Unionpay.WAP_PAGE_EXTRA);
        }
        PayService payService = getUnionpayService(payDto.getAppId(), getChannelType(), returnUrl, payDto.getLiveMode());
        PayOrder payOrder = initPayOrder(payDto);
        payOrder.setTransactionType(UnionTransactionType.WAP);
        // 执行请求
        Map<String,Object> directOrderInfo = payService.orderInfo(payOrder);

        String directHtml = payService.buildRequest(directOrderInfo, MethodType.POST);
        ChannelPayResponse payResponse = new ChannelPayResponse();
        payResponse.setSuccess(true);
        payResponse.setCredential(directHtml);
        return payResponse;
    }
}
