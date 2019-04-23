package com.gloryjie.pay.channel.service.platform.union;

import com.egzosn.pay.common.api.PayService;
import com.egzosn.pay.common.bean.PayOrder;
import com.egzosn.pay.common.bean.RefundOrder;
import com.egzosn.pay.common.bean.result.PayError;
import com.egzosn.pay.common.exception.PayErrorException;
import com.egzosn.pay.common.util.sign.SignUtils;
import com.egzosn.pay.union.api.UnionPayConfigStorage;
import com.egzosn.pay.union.api.UnionPayService;
import com.egzosn.pay.union.bean.SDKConstants;
import com.gloryjie.pay.base.constant.DefaultConstant;
import com.gloryjie.pay.base.exception.error.ExternalException;
import com.gloryjie.pay.base.util.AmountUtil;
import com.gloryjie.pay.base.util.BeanConverter;
import com.gloryjie.pay.base.util.DateTimeUtil;
import com.gloryjie.pay.channel.config.UnionpayChannelConfig;
import com.gloryjie.pay.channel.dto.*;
import com.gloryjie.pay.channel.dto.response.ChannelRefundResponse;
import com.gloryjie.pay.channel.dto.response.ChannelResponse;
import com.gloryjie.pay.channel.enums.AsyncNotifyType;
import com.gloryjie.pay.channel.enums.ChannelType;
import com.gloryjie.pay.channel.enums.UnionpayStatus;
import com.gloryjie.pay.channel.error.ChannelError;
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

    public static final String UNION_TIME_FORMAT = "yyyyMMddHHmmss";

    @Value("${pay.host}")
    protected String host;

    @Value("${pay.channel.unionpay.notifyUri:/pay/trade/platform/notify/unionpay/charge}")
    protected String tradeNotifyUri;

    @Value("${pay.channel.unionpay.notifyUri:/pay/trade/platform/notify/unionpay/refund}")
    protected String refundNotifyUri;

    @Autowired
    ChannelConfigService configService;

    public PayService getUnionpayService(Integer appId, ChannelType channelType, AsyncNotifyType asyncNotifyType, String returnUrl, Boolean liveMode) {
        ChannelConfigDto channelConfigDto = configService.getUsingChannelConfig(appId, channelType);
        Map<String, Object> configMap = new HashMap<>(channelConfigDto.getChannelConfig());
        UnionpayChannelConfig configModel = BeanConverter.mapToBean(configMap, UnionpayChannelConfig.class);

        // SDK配置内容
        UnionPayConfigStorage configStorage = new UnionPayConfigStorage();
        configStorage.setMerId(configModel.getMerchantId());
        configStorage.setKeyPrivateCert(configModel.getPrivateCertPath());
        configStorage.setKeyPrivateCertPwd(configModel.getPrivateCertPwd());
        configStorage.setAcpMiddleCert(configModel.getMiddleCert());
        configStorage.setAcpRootCert(configModel.getRootCert());
        configStorage.setCertSign(true);
        configStorage.setInputCharset(DefaultConstant.CHARSET);
        configStorage.setSignType(SignUtils.RSA2.name());
        if (AsyncNotifyType.TRADE_RESULT == asyncNotifyType) {
            configStorage.setNotifyUrl(host + tradeNotifyUri);
        } else if (AsyncNotifyType.REFUND_RESULT == asyncNotifyType) {
            configStorage.setNotifyUrl(host + refundNotifyUri);
        }
        // returnUrl必填,否则签名无法通过
        configStorage.setReturnUrl(returnUrl);
        configStorage.setTest((liveMode == null) || !liveMode);

        return new UnionPayService(configStorage);
    }


    @Override
    public ChannelPayQueryResponse queryPayment(ChannelPayQueryDto queryDto) {
        PayService payService = getUnionpayService(queryDto.getAppId(), queryDto.getChannel(), AsyncNotifyType.TRADE_RESULT, null, false);
        ChannelPayQueryResponse response = new ChannelPayQueryResponse();
        try {
            // UnionService.query()内部做了响应码的检查, 只有respCode和origRespCode都为00时才返回数据,否则抛出异常
            Map<String, Object> respParam = payService.query(null, queryDto.getChargeNo());
            response.setSuccess(true);
            response.setStatus(UnionpayStatus.TRADE_SUCCESS.name());
            response.setPlatformTradeNo(respParam.get(SDKConstants.param_queryId).toString());
            response.setAmount(Long.valueOf(respParam.get(SDKConstants.param_txnAmt).toString()));
            response.setTimePaid(DateTimeUtil.parse(respParam.get(SDKConstants.param_txnTime).toString(), UNION_TIME_FORMAT));
            return response;
        } catch (PayErrorException e) {
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
        PayService payService = getUnionpayService(refundDto.getAppId(), refundDto.getChannel(), AsyncNotifyType.REFUND_RESULT, null, false);

        RefundOrder refundOrder = new RefundOrder();
        refundOrder.setOutTradeNo(refundDto.getChargeNo());
        refundOrder.setTotalAmount(AmountUtil.longToBigDecimal(refundDto.getChargeAmount()));
        refundOrder.setRefundAmount(AmountUtil.longToBigDecimal(refundDto.getAmount()));
        refundOrder.setRefundNo(refundDto.getRefundNo());
        refundOrder.setTradeNo(refundDto.getPlatformTradeNo());
        ChannelRefundResponse response = new ChannelRefundResponse();
        try {
            // 银联为异步退款, 结果依赖异步通知, 此处直接返回成功
            Map<String, Object> result = payService.refund(refundOrder);
            response.setSuccess(true);
            return response;
        } catch (PayErrorException e) {
            response.setSuccess(false);
            PayError payError = e.getPayError();
            response.setSubCode(payError.getErrorCode());
            response.setSubMsg(payError.getErrorMsg());
            log.info("unionpay={} charge={} execute refund interface fail, detail={}", getChannelType().name(), refundDto.getRefundNo(), e.getMessage());
            return response;
        }
    }

    @Override
    public ChannelResponse queryRefund(ChannelRefundQueryDto queryDto) {
        throw new UnsupportedOperationException("不支持渠道: " + getChannelType().name() + " 退款查询");
    }

    @Override
    public ChannelPayQueryResponse handleTradeAsyncNotify(Integer appId, Map<String, String> param) {
        if (!verifySign(appId, param)) {
            throw ExternalException.create(ChannelError.PLATFORM_NOTIFY_SIGN_NOT_THROUGH);
        }
        ChannelPayQueryResponse response = new ChannelPayQueryResponse();
        response.setSuccess(true);
        response.setPlatformTradeNo(param.get(SDKConstants.param_queryId));
        response.setStatus(UnionpayStatus.TRADE_SUCCESS.name());
        response.setTimePaid(DateTimeUtil.parse(param.get(SDKConstants.param_txnTime), UNION_TIME_FORMAT));
        response.setAmount(Long.valueOf(param.get(SDKConstants.param_txnAmt)));
        response.setActualAmount(Long.valueOf(param.get(SDKConstants.param_settleAmt)));
        return response;
    }

    @Override
    public ChannelRefundResponse handleRefundAsyncNotify(Integer appId, Map<String, String> param) {
        if (!verifySign(appId, param)) {
            throw ExternalException.create(ChannelError.PLATFORM_NOTIFY_SIGN_NOT_THROUGH);
        }
        ChannelRefundResponse response = new ChannelRefundResponse();
        response.setSuccess(true);
        response.setMsg(param.get(SDKConstants.param_respMsg));
        response.setCode(param.get(SDKConstants.param_respCode));
        response.setPlatformTradeNo(param.get(SDKConstants.param_queryId));
        response.setRefundAmount(Long.valueOf(param.get(SDKConstants.param_settleAmt)));
        response.setTimeSucceed(LocalDateTime.now());
        return response;
    }

    @Override
    public boolean verifySign(Integer appId, Map<String, String> param) {
        PayService payService = getUnionpayService(appId, getChannelType(), null, null, null);
        return payService.verify(param);
    }

    /**
     * 初始化PayOrder对象信息
     *
     * @param payDto
     * @return
     */
    PayOrder initPayOrder(ChannelPayDto payDto) {
        PayOrder payOrder = new PayOrder();
        payOrder.setSubject(payDto.getSubject());
        payOrder.setBody(payDto.getBody());
        payOrder.setPrice(AmountUtil.longToBigDecimal(payDto.getAmount()));
        payOrder.setOutTradeNo(payDto.getChargeNo());
        // 设置请求保留域
        payOrder.setAddition(payDto.getAppId().toString());
        return payOrder;
    }
}
