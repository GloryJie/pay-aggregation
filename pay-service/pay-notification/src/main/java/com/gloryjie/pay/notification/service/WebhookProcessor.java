/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.notification.service
 *   Date Created: 2019/2/1
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2019/2/1      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.notification.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.gloryjie.pay.app.dto.AppDto;
import com.gloryjie.pay.app.service.api.AppFeignApi;
import com.gloryjie.pay.base.constant.DefaultConstant;
import com.gloryjie.pay.base.enums.MqDelayMsgLevel;
import com.gloryjie.pay.base.exception.error.SystemException;
import com.gloryjie.pay.base.util.BeanConverter;
import com.gloryjie.pay.base.util.JsonUtil;
import com.gloryjie.pay.base.util.SignUtil;
import com.gloryjie.pay.base.util.cipher.Rsa;
import com.gloryjie.pay.notification.constant.NotifyConstant;
import com.gloryjie.pay.notification.dao.EventNotifyDao;
import com.gloryjie.pay.notification.dto.EventNotifyDto;
import com.gloryjie.pay.notification.enums.EventType;
import com.gloryjie.pay.notification.enums.NotifyStatus;
import com.gloryjie.pay.notification.error.NotificationError;
import com.gloryjie.pay.notification.model.EventNotify;
import com.gloryjie.pay.notification.mq.NotifyMqProducer;
import com.gloryjie.pay.trade.dto.ChargeDto;
import com.gloryjie.pay.trade.dto.RefundDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 实际的通知处理器
 *
 * @author Jie
 * @since 0.1
 */
@Slf4j
@Component
public class WebhookProcessor {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private EventNotifyDao eventNotifyDao;

    @Autowired
    private NotifyMqProducer notifyMqProducer;

    @Autowired
    private AppFeignApi appFeignApi;


    public void handleEventNotify(String eventNo) {
        // 1. 检查数据
        EventNotify eventNotify = eventNotifyDao.getByEventNo(eventNo);
        if (eventNotify == null || NotifyStatus.PROCESSING != eventNotify.getNotifyStatus()) {
            log.info("eventNo={} not exists or status is not processing",eventNo);
            return;
        }
        log.info("handle notify eventNo={}, appId={},notifyTime={}", eventNo, eventNotify.getAppId(), eventNotify.getNotifyTime());

        // 2. 获取APP信息
        AppDto appDto = appFeignApi.getAppInfo(eventNotify.getAppId());
        if (StringUtils.isBlank(appDto.getNotifyPrivateKey())) {
            return;
        }

        // 3. 执行通知
        executeNotify(eventNotify, appDto.getNotifyPrivateKey());

        // 4. 无论成功与否先更新数据
        eventNotifyDao.update(eventNotify);

        // 5. 若状态仍是处理中，则延迟发送消息至rmq，等待再次发送通知
        if (NotifyStatus.PROCESSING == eventNotify.getNotifyStatus()) {
            String[] interval = JsonUtil.parse(eventNotify.getNotifyInterval(), String[].class);
            MqDelayMsgLevel level = MqDelayMsgLevel.valueOfTime(interval[eventNotify.getNotifyTime()]);
            notifyMqProducer.sendEventNotifyMsg(eventNotify.getEventNo(), level);
        }

    }


    /**
     * 执行通知
     *
     * @param eventNotify
     * @return
     */
    private void executeNotify(EventNotify eventNotify, String privateKey) {

        EventNotifyDto eventNotifyDto = BeanConverter.covertIgnore(eventNotify, EventNotifyDto.class);
        eventNotifyDto.setEventData(JsonUtil.parse(eventNotify.getEventData(), new TypeReference<Map<String,Object>>() {}));
        eventNotifyDto.setCurrentTimeNotify(LocalDateTime.now());

        // 签名
        String signStr = SignUtil.toSignStr(BeanConverter.beanToMap(eventNotifyDto));
        log.debug("event notification eventNo={}, sourceNo={} signStr={}", eventNotify.getEventNo(), eventNotify.getSourceNo(), signStr);
        String signResult;
        try {
            signResult = Rsa.sign(signStr.getBytes(StandardCharsets.UTF_8), privateKey);
        } catch (SignatureException | NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException e) {
            log.error("event notification eventNo={}, sign fail ", e);
            throw SystemException.create(NotificationError.SIGN_FAIL);
        }

        // post请求通知
        String responseResult = postWithSign(eventNotifyDto, eventNotify.getNotifyUrl(), signResult);

        eventNotify.setNotifyTime(eventNotify.getNotifyTime() + 1);
        responseResult = responseResult.length() > NotifyConstant.LAST_REPLY_MAX_LENGTH ? responseResult.substring(0, NotifyConstant.LAST_REPLY_MAX_LENGTH) : responseResult;
        eventNotify.setLastReply(responseResult);
        eventNotify.setTimeLastNotify(eventNotifyDto.getCurrentTimeNotify());

        // 更新状态
        if (DefaultConstant.NOTIFY_SUCCESS_RESPONSE.equals(responseResult)) {
            log.info("notify appId={}, url={}, eventNo={}, time={} success", eventNotify.getAppId(), eventNotify.getNotifyUrl(), eventNotify.getEventNo(), eventNotify.getNotifyTime());
            eventNotify.setNotifyStatus(NotifyStatus.SUCCESS);
        } else {
            if (eventNotify.getNotifyTime() >= NotifyConstant.MAX_NOTIFY_TIME) {
                log.info("notify eventNo={} to appId={}, target url={} fail", eventNotify.getEventNo(), eventNotify.getAppId(), eventNotify.getNotifyUrl(), eventNotify.getNotifyTime());
                eventNotify.setNotifyStatus(NotifyStatus.FAIL);
            }
        }
    }



    /**
     * 发送请求
     *
     * @param dto        数据
     * @param url        地址
     * @param signResult 签名结果
     * @return
     */
    private String postWithSign(EventNotifyDto dto, String url, String signResult) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, DefaultConstant.CONTENT_TYPE);
        headers.add(DefaultConstant.PAY_SIGN_KEY, signResult);
        String body = JsonUtil.toJson(dto);
        HttpEntity<String> httpEntity = new HttpEntity<>(body, headers);

        String responseResult;
        try {
            log.info("ready to notify appId={}, url={}, eventNo={}", dto.getAppId(), dto.getNotifyTime(), dto.getEventNo());
            responseResult = restTemplate.postForObject(url, httpEntity, String.class);
        } catch (RestClientException e) {
            log.info("notify eventNo={} to appId={}, url={} fail, times={}", dto.getEventNo(), dto.getAppId(), url, dto.getNotifyTime(), e);
            responseResult = e.getMessage();
        }
        return responseResult;
    }

}
