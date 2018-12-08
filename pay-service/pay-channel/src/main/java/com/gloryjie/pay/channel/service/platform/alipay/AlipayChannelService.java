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
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeFastpayRefundQueryRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.gloryjie.pay.base.constant.DefaultConstant;
import com.gloryjie.pay.base.util.JsonUtil;
import com.gloryjie.pay.channel.config.AlipayChannelConfig;
import com.gloryjie.pay.channel.dao.ChannelConfigDao;
import com.gloryjie.pay.channel.dto.ChannelPayQueryDto;
import com.gloryjie.pay.channel.dto.ChannelRefundDto;
import com.gloryjie.pay.channel.dto.ChannelRefundQueryDto;
import com.gloryjie.pay.channel.dto.ChannelResponse;
import com.gloryjie.pay.channel.model.ChannelConfig;
import com.gloryjie.pay.channel.service.ChannelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Map;

/**
 * @author Jie
 * @since
 */
@Slf4j
public abstract class AlipayChannelService implements ChannelService {

    protected static final String ALIPAY_SIGN_TYPE = "RSA2";

    protected static final String ALIPAY_FORMAT = "json";

    protected static final String ALIPAY_SANDBOX_URL = "https://openapi.alipaydev.com/gateway.do";

    protected static final String ALIPAY_PRODUCT_URL = "https://openapi.alipay.com/gateway.do";

    @Autowired
    protected RedisTemplate redisTemplate;

    @Autowired
    protected ChannelConfigDao channelConfigDao;


    @Value("${alipay.sandboxMode:true}")
    protected boolean sandboxMode;


    // TODO: 2018/11/27  AlipayClient为线程安全,可以缓存起来进行优化
    protected AlipayClient getAlipayClient(AlipayChannelConfig config) {
        String url = sandboxMode ? ALIPAY_SANDBOX_URL : ALIPAY_PRODUCT_URL;
        return DefaultAlipayClient.builder(url, config.getMerchantId(), config.getMerchantPrivateKey())
                .charset(DefaultConstant.CHARSET)
                .format(ALIPAY_FORMAT)
                .signType(ALIPAY_SIGN_TYPE).build();
    }

    @Override
    public ChannelResponse queryPayment(ChannelPayQueryDto queryDto) {
        ChannelConfig config = channelConfigDao.loadByAppIdAndChannel(queryDto.getAppId(), queryDto.getChannelType().name());
        AlipayClient client = getAlipayClient(JsonUtil.parse(config.getChannelConfig(), AlipayChannelConfig.class));
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        AlipayTradeQueryModel model = new AlipayTradeQueryModel();
        model.setOutTradeNo(queryDto.getChargeNo());
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
    public ChannelResponse refund(ChannelRefundDto refundDto) {
        ChannelConfig config = channelConfigDao.loadByAppIdAndChannel(refundDto.getAppId(), refundDto.getChannel().name());
        AlipayClient client = getAlipayClient(JsonUtil.parse(config.getChannelConfig(), AlipayChannelConfig.class));
        AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
        AlipayTradeRefundModel model = new AlipayTradeRefundModel();
        model.setOutTradeNo(refundDto.getChargeNo());
        model.setRefundAmount(refundDto.getAmount().toString());
        model.setOutRequestNo(refundDto.getRefundNo());
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
    public ChannelResponse queryRefund(ChannelRefundQueryDto queryDto) {
        ChannelConfig config = channelConfigDao.loadByAppIdAndChannel(queryDto.getAppId(), queryDto.getChannel().name());
        AlipayClient client = getAlipayClient(JsonUtil.parse(config.getChannelConfig(), AlipayChannelConfig.class));
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
    public boolean handleAsync(Map<String, String> param) {
        return false;
    }

    @Override
    public boolean verifySign(Map<String, String> param, String publicKey, String signType) {
        try {
            // 默认使用RSA2的方式
            return AlipaySignature.rsaCheckV1(param, publicKey, DefaultConstant.CHARSET,"RSA2");
        } catch (AlipayApiException e) {
            log.error("verify alipay notify fail", e);
        }
        return false;
    }
}
