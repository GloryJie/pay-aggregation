package com.gloryjie.pay.channel.service.platform.union;

import com.egzosn.pay.common.api.PayService;
import com.egzosn.pay.common.bean.RefundOrder;
import com.egzosn.pay.common.bean.result.PayError;
import com.egzosn.pay.common.exception.PayErrorException;
import com.egzosn.pay.common.util.sign.SignUtils;
import com.egzosn.pay.union.api.UnionPayConfigStorage;
import com.egzosn.pay.union.api.UnionPayService;
import com.egzosn.pay.union.bean.SDKConstants;
import com.gloryjie.pay.base.constant.DefaultConstant;
import com.gloryjie.pay.base.util.AmountUtil;
import com.gloryjie.pay.base.util.BeanConverter;
import com.gloryjie.pay.channel.config.UnionpayChannelConfig;
import com.gloryjie.pay.channel.constant.ChannelConstant;
import com.gloryjie.pay.channel.dto.*;
import com.gloryjie.pay.channel.dto.response.ChannelResponse;
import com.gloryjie.pay.channel.enums.ChannelType;
import com.gloryjie.pay.channel.enums.UnionpayStatus;
import com.gloryjie.pay.channel.service.ChannelConfigService;
import com.gloryjie.pay.channel.service.PayChannelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


/**
 * @author jie
 * @since 2019/4/21
 */
@Slf4j
public abstract class BaseUnionpayChannelService implements PayChannelService {

    @Value("${pay.host}")
    protected String host;

    @Value("${pay.channel.unionpay.notifyUri:/pay/trade/platform/notify/union}")
    protected String notifyUri;

    @Autowired
    ChannelConfigService configService;

    public PayService getUnionpayService(Integer appId, ChannelType channelType) {
        ChannelConfigDto channelConfigDto = configService.getUsingChannelConfig(appId, channelType);
        Map<String, Object> configMap = new HashMap<>(channelConfigDto.getChannelConfig());
        UnionpayChannelConfig configModel = BeanConverter.mapToBean(configMap, UnionpayChannelConfig.class);

        // SDK配置内容
        UnionPayConfigStorage configStorage = new UnionPayConfigStorage();
        configStorage.setMerId(configModel.getMerchantId());
        configStorage.setKeyPrivateCert(configModel.getPrivateCertPath());
        configStorage.setKeyPrivateCertPwd(configModel.getPrivateCertPwd());
        configStorage.setAcpRootCert(configModel.getRootCert());
        configStorage.setCertSign(true);
        configStorage.setInputCharset(DefaultConstant.CHARSET);
        configStorage.setSignType(SignUtils.RSA2.name());
        configStorage.setNotifyUrl(host + notifyUri);
        // returnUrl必填,否则签名无法通过
        configStorage.setReturnUrl(host + notifyUri);
        // TODO: 2019/4/21 沙箱环境可配置
        configStorage.setTest(true);
        return new UnionPayService(configStorage);
    }


    @Override
    public ChannelPayQueryResponse queryPayment(ChannelPayQueryDto queryDto) {
        PayService payService = getUnionpayService(queryDto.getAppId(), queryDto.getChannel());
        ChannelPayQueryResponse response = new ChannelPayQueryResponse();

        try {

            // UnionService.query()内部做了响应码的检查, 只有respCode和origRespCode都为00时才返回数据,否则抛出异常
            Map respParam = payService.query(null, queryDto.getChargeNo());
            response.setSuccess(true);
            response.setStatus(UnionpayStatus.TRADE_SUCCESS.name());
            response.setPlatformTradeNo(respParam.get(SDKConstants.param_queryId).toString());
            response.setAmount(Long.valueOf(respParam.get(SDKConstants.param_txnAmt).toString()));
            response.setActualAmount(Long.valueOf(respParam.get(SDKConstants.param_settleAmt).toString()));
            // 无法从返回的数据获取交易成功实践,所以设置为当前查询成功的时间
            response.setTimePaid(LocalDateTime.now());
            return response;
        }catch (PayErrorException e){
            // 业务异常,如交易不存在, 通讯超时,状态未明等
            //应答码链接: https://open.unionpay.com/tjweb/acproduct/list?apiservId=448&version=V2.2
            response.setSuccess(false);
            PayError payError = e.getPayError();
            response.setStatus(UnionpayStatus.TIMEOUT_OR_UNKNOWN_STATUS.name());
            response.setSubCode(payError.getErrorCode());
            response.setSubMsg(payError.getErrorMsg());
            log.info("query unionpay={} charge={} fail, detail={}", queryDto.getChannel().name(), queryDto.getChargeNo(), e.getMessage());
            return response;
        }
    }


    @Override
    public ChannelResponse refund(ChannelRefundDto refundDto) {
        PayService payService = getUnionpayService(refundDto.getAppId(), refundDto.getChannel());
        RefundOrder refundOrder = new RefundOrder();
        refundOrder.setOutTradeNo(refundDto.getPlatformTradeNo());
        refundOrder.setTotalAmount(AmountUtil.longToBigDecimal(refundDto.getChargeAmount()));
        refundOrder.setRefundAmount(AmountUtil.longToBigDecimal(refundDto.getAmount()));
        refundOrder.setRefundNo(refundDto.getRefundNo());

        Map<String,Object> result = payService.refund(refundOrder);
        return null;
    }

    @Override
    public ChannelResponse queryRefund(ChannelRefundQueryDto queryDto) {
        return null;
    }

    @Override
    public ChannelPayQueryResponse handleAsyncNotify(Integer appId, Map<String, String> param) {
        return null;
    }

    @Override
    public boolean verifySign(Map<String, String> param, String publicKey) {
        return false;
    }
}
