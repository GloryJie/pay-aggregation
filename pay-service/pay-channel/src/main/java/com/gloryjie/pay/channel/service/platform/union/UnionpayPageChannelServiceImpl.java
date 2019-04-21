package com.gloryjie.pay.channel.service.platform.union;

import com.egzosn.pay.common.api.PayService;
import com.egzosn.pay.common.bean.MethodType;
import com.egzosn.pay.common.bean.PayOrder;
import com.egzosn.pay.common.bean.TransactionType;
import com.egzosn.pay.union.bean.UnionTransactionType;
import com.gloryjie.pay.base.util.AmountUtil;
import com.gloryjie.pay.channel.dto.ChannelPayDto;
import com.gloryjie.pay.channel.dto.response.ChannelPayResponse;
import com.gloryjie.pay.channel.enums.ChannelType;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 银联PC网页支付
 * @author jie
 * @since 2019/4/21
 */
@Service
public class UnionpayPageChannelServiceImpl extends BaseUnionpayChannelService{

    @Override
    public ChannelType getChannelType() {
        return ChannelType.UNIONPAY_PAGE;
    }

    @Override
    public ChannelPayResponse pay(ChannelPayDto payDto) {
        PayService payService = getUnionpayService(payDto.getAppId(), getChannelType());
        PayOrder payOrder = new PayOrder();
        payOrder.setSubject(payDto.getSubject());
        payOrder.setBody(payDto.getBody());
        payOrder.setPrice(AmountUtil.longToBigDecimal(payDto.getAmount()));
        payOrder.setOutTradeNo(payDto.getChargeNo());
        payOrder.setTransactionType(UnionTransactionType.WEB);
        // 执行请求
        Map<String,Object> directOrderInfo = payService.orderInfo(payOrder);

        String directHtml = payService.buildRequest(directOrderInfo, MethodType.POST);
        ChannelPayResponse payResponse = new ChannelPayResponse();
        payResponse.setSuccess(true);
        payResponse.setCredential(directHtml);
        return payResponse;
    }
}
