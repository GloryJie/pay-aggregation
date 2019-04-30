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
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeFastpayRefundQueryModel;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.domain.AlipayTradeRefundModel;
import com.alipay.api.domain.TradeFundBill;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeFastpayRefundQueryRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.gloryjie.pay.base.constant.DefaultConstant;
import com.gloryjie.pay.base.enums.error.CommonErrorEnum;
import com.gloryjie.pay.base.exception.error.BusinessException;
import com.gloryjie.pay.base.exception.error.ExternalException;
import com.gloryjie.pay.base.exception.error.SystemException;
import com.gloryjie.pay.base.util.AmountUtil;
import com.gloryjie.pay.base.util.BeanConverter;
import com.gloryjie.pay.base.util.DateTimeUtil;
import com.gloryjie.pay.base.util.JsonUtil;
import com.gloryjie.pay.channel.config.AlipayChannelConfig;
import com.gloryjie.pay.channel.constant.ChannelConstant;
import com.gloryjie.pay.channel.dao.ChannelConfigDao;
import com.gloryjie.pay.channel.dto.*;
import com.gloryjie.pay.channel.dto.response.ChannelRefundResponse;
import com.gloryjie.pay.channel.dto.response.ChannelResponse;
import com.gloryjie.pay.channel.enums.AlipayStatus;
import com.gloryjie.pay.channel.enums.ChannelType;
import com.gloryjie.pay.channel.error.ChannelError;
import com.gloryjie.pay.channel.model.ChannelConfig;
import com.gloryjie.pay.channel.service.ChannelConfigService;
import com.gloryjie.pay.channel.service.PayChannelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Jie
 * @since
 */
@Slf4j
public abstract class BaseAlipayChannelService implements PayChannelService {

    // TODO: 2019/3/11 渠道参数需要抽取出来

    protected static final String ALIPAY_SIGN_TYPE = "RSA2";

    protected static final String ALIPAY_FORMAT = "json";

    protected static final String ALIPAY_SANDBOX_URL = "https://openapi.alipaydev.com/gateway.do";

    protected static final String ALIPAY_PRODUCT_URL = "https://openapi.alipay.com/gateway.do";

    private static final List<String> REDUCE_PRICE_TYPE = Arrays.asList("COUPON", "DISCOUNT", "MDISCOUNT", "MCOUPON");

    @Autowired
    protected ChannelConfigDao channelConfigDao;

    @Value("${pay.channel.alipay.sandboxMode:false}")
    protected boolean sandboxMode;

    @Value("${pay.host}")
    protected String host;

    @Value("${pay.channel.alipay.notifyUri:/pay/trade/platform/notify/alipay}")
    protected String notifyUri;

    @Autowired
    private ChannelConfigService channelConfigService;


    // TODO: 2018/11/27  AlipayClient为线程安全,可以缓存起来进行优化
    protected AlipayClient getAlipayClient(Integer appId, ChannelType channelType) {
        ChannelConfigDto configDto = channelConfigService.getUsingChannelConfig(appId, channelType);
        Map<String, Object> payConfig = new HashMap<>(configDto.getChannelConfig());
        AlipayChannelConfig alipayChannelConfig = BeanConverter.mapToBean(payConfig, AlipayChannelConfig.class);
        String url = sandboxMode ? ALIPAY_SANDBOX_URL : ALIPAY_PRODUCT_URL;
        return DefaultAlipayClient.builder(url, alipayChannelConfig.getMerchantId(), alipayChannelConfig.getMerchantPrivateKey())
                .charset(DefaultConstant.CHARSET)
                .format(ALIPAY_FORMAT)
                .signType(ALIPAY_SIGN_TYPE).build();
    }

    @Override
    public ChannelPayQueryResponse queryPayment(ChannelPayQueryDto queryDto) {
        AlipayClient client = getAlipayClient(queryDto.getAppId(), queryDto.getChannel());
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        AlipayTradeQueryModel model = new AlipayTradeQueryModel();
        model.setOutTradeNo(queryDto.getChargeNo());
        request.setBizModel(model);
        AlipayTradeQueryResponse response;
        ChannelPayQueryResponse queryResponse;
        try {
            response = client.execute(request);
            queryResponse = new ChannelPayQueryResponse(response);
            AlipayStatus status = AlipayStatus.TRADE_FAIL;
            if (response.isSuccess()) {
                status = AlipayStatus.valueOf(response.getTradeStatus());
                queryResponse.setPlatformTradeNo(response.getTradeNo());
                queryResponse.setAmount(AmountUtil.strToAmount(response.getTotalAmount()));
                queryResponse.setTimePaid(DateTimeUtil.dateToLocal(response.getSendPayDate()));
                if (response.getFundBillList() != null) {
                    // 需要减去一些平台优惠的金额, 得到实付金额
                    Long reducedPrice = 0L;
                    for (TradeFundBill fundBill : response.getFundBillList()) {
                        if (REDUCE_PRICE_TYPE.contains(fundBill.getFundChannel())) {
                            reducedPrice += AmountUtil.strToAmount(fundBill.getAmount());
                        }
                    }
                    queryResponse.setActualAmount(queryResponse.getAmount() - reducedPrice);
                } else {
                    queryResponse.setActualAmount(queryResponse.getAmount());
                }
            } else {
                // 支付未完成, 返回交易不存在 ACQ.TRADE_NOT_EXIST
                if (ChannelConstant.Alipay.TRADE_NOT_EXISTS_STATUS.equals(response.getSubCode())) {
                    status = AlipayStatus.WAIT_BUYER_PAY;
                }
            }
            queryResponse.setStatus(status.name());
        } catch (AlipayApiException e) {
            throw ExternalException.create(ChannelError.PAY_PLATFORM_ERROR, e.getErrMsg());
        }
        return queryResponse;
    }

    @Override
    public ChannelResponse refund(ChannelRefundDto refundDto) {
        AlipayClient client = getAlipayClient(refundDto.getAppId(), refundDto.getChannel());
        AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
        AlipayTradeRefundModel model = new AlipayTradeRefundModel();
        model.setOutTradeNo(refundDto.getChargeNo());
        model.setRefundAmount(AmountUtil.amountToStr(refundDto.getAmount()));
        // out_request_no 为退款的唯一标识
        model.setOutRequestNo(refundDto.getRefundNo());
        request.setBizModel(model);
        AlipayTradeRefundResponse response;
        ChannelRefundResponse refundResponse;
        try {
            response = client.execute(request);
            refundResponse = new ChannelRefundResponse(response);
            if (response.isSuccess()) {
                refundResponse.setPlatformTradeNo(response.getTradeNo());
                refundResponse.setRefundAmount(AmountUtil.strToAmount(response.getRefundFee()));
                refundResponse.setTimeSucceed(DateTimeUtil.dateToLocal(response.getGmtRefundPay()));
            }
        } catch (AlipayApiException e) {
            throw ExternalException.create(ChannelError.PAY_PLATFORM_ERROR, e.getErrMsg());
        }
        return refundResponse;
    }

    @Override
    public ChannelResponse queryRefund(ChannelRefundQueryDto queryDto) {
        AlipayClient client = getAlipayClient(queryDto.getAppId(), queryDto.getChannel());
        AlipayTradeFastpayRefundQueryRequest request = new AlipayTradeFastpayRefundQueryRequest();
        AlipayTradeFastpayRefundQueryModel model = new AlipayTradeFastpayRefundQueryModel();
        model.setOutTradeNo(queryDto.getChargeNo());
        model.setOutRequestNo(queryDto.getRefundNo());
        request.setBizModel(model);

        try {
            AlipayResponse response = client.execute(request);
            // TODO: 2018/11/25 需要结合结合业务
            System.out.println(JsonUtil.toJson(response));
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ChannelPayQueryResponse handleTradeAsyncNotify(Integer appId, Map<String, String> param) {
        // 签名验证
        boolean result = this.verifySign(appId, param);
        if (!result) {
            throw ExternalException.create(ChannelError.PLATFORM_NOTIFY_SIGN_NOT_THROUGH);
        }
        ChannelPayQueryResponse response = new ChannelPayQueryResponse();
        response.setSuccess(true);
        response.setPlatformTradeNo(param.get("trade_no"));
        response.setStatus(param.get("trade_status"));
        response.setAmount(AmountUtil.strToAmount(param.get("total_amount")));
        response.setActualAmount(AmountUtil.strToAmount(param.get("buyer_pay_amount")));
        response.setTimePaid(DateTimeUtil.parse(param.get("gmt_payment")));
        return response;
    }

    @Override
    public ChannelRefundResponse handleRefundAsyncNotify(Integer appId, Map<String, String> param) {
        // 支付宝退款为同步
        throw  new UnsupportedOperationException();
    }

    @Override
    public boolean verifySign(Integer appId, Map<String, String> param) {
        ChannelConfig config = channelConfigDao.loadByAppIdAndChannel(appId, getChannelType());
        if (config == null) {
            throw SystemException.create(ChannelError.CHANNEL_CONFIG_NOT_EXISTS);
        }
        AlipayChannelConfig alipayConfig = BeanConverter.mapToBean(new HashMap<>(config.getChannelConfig()), AlipayChannelConfig.class);
        if (!alipayConfig.getMerchantId().equals(param.get("app_id"))) {
            throw ExternalException.create(ChannelError.PLATFORM_NOTIFY_SIGN_NOT_THROUGH);
        }

        try {
            return AlipaySignature.rsaCheckV1(param, alipayConfig.getMerchantPublicKey(), DefaultConstant.CHARSET, param.get("sign_type"));
        } catch (AlipayApiException e) {
            log.warn("verify platform=ALIPAY notify sign fail", e);
        }
        return false;
    }

    protected String buildFormHtmlRequest(String form) {
        StringBuffer sf = new StringBuffer();
        sf.append("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=" + DefaultConstant.CHARSET + "\"/></head><body>");
        sf.append(form);
        sf.append("</body>");
        sf.append("<script type=\"text/javascript\">");
        sf.append("document.all.pay_form.submit();");
        sf.append("</script>");
        sf.append("</html>");
        return sf.toString();
    }
}
