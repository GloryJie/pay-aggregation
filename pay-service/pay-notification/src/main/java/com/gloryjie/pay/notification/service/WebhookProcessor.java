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

import com.alibaba.fastjson.JSONObject;
import com.gloryjie.pay.base.constant.DefaultConstant;
import com.gloryjie.pay.base.enums.MqDelayMsgLevel;
import com.gloryjie.pay.base.util.BeanConverter;
import com.gloryjie.pay.base.util.JsonUtil;
import com.gloryjie.pay.notification.constant.NotifyConstant;
import com.gloryjie.pay.notification.dao.EventNotifyDao;
import com.gloryjie.pay.notification.dto.EventNotifyDto;
import com.gloryjie.pay.notification.enums.EventType;
import com.gloryjie.pay.notification.enums.NotifyStatus;
import com.gloryjie.pay.notification.model.EventNotify;
import com.gloryjie.pay.notification.mq.NotifyMqProducer;
import com.gloryjie.pay.trade.dto.ChargeDto;
import com.gloryjie.pay.trade.dto.RefundDto;
import com.oracle.tools.packager.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

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


    public void handleEventNotify(String eventNo) {
        EventNotify eventNotify = eventNotifyDao.getByEventNo(eventNo);
        if (eventNotify == null || NotifyStatus.PROCESSING != eventNotify.getNotifyStatus()) {
            return;
        }
        log.info("handle notify eventNo={}, appId={},notifyTime={}", eventNo, eventNotify.getAppId(), eventNotify.getNotifyTime());

        // 修改时间
        eventNotify.setTimeLastNotify(LocalDateTime.now());
        EventNotifyDto eventNotifyDto = BeanConverter.covert(eventNotify, EventNotifyDto.class);
        eventNotifyDto.setEventData(JsonUtil.parse(eventNotify.getEventData(), getEventDataType(eventNotify.getType())));
        eventNotifyDto.setCurrentTimeNotify(eventNotify.getTimeLastNotify());

        // TODO: 2019/2/1 需要获取应用的目标地址以及私钥
        String url = "http://localhost:9090/notify";

        String responseResult;
        try {
            log.info("ready to notify appId={}, url={}, eventNo={}", eventNotify.getAppId(), eventNotify.getNotifyTime(), eventNo);
            responseResult = restTemplate.postForObject(url, eventNotifyDto, String.class);
        } catch (Exception e) {
            log.info("notify eventNo={} to appId={}, url={} fail", eventNo, eventNotify.getAppId(), url);
            responseResult = e.getMessage();
        }

        eventNotify.setNotifyTime(eventNotify.getNotifyTime() + 1);
        eventNotify.setLastReply(responseResult);

        // 通知成功
        if (DefaultConstant.NOTIFY_SUCCESS_RESPONSE.equals(responseResult)) {
            log.info("notify appId={}, url={}, eventNo={}, time={} success", eventNotify.getAppId(), eventNotify.getNotifyTime(), eventNo, eventNotify.getNotifyTime());
            eventNotify.setNotifyStatus(NotifyStatus.SUCCESS);
        } else {
            // 判断次数
            if (eventNotify.getNotifyTime() >= NotifyConstant.MAX_NOTIFY_TIME) {
                log.info("notify appId={}, url={}, eventNo={} fail", eventNotify.getAppId(), eventNotify.getNotifyTime(), eventNo);
                eventNotify.setNotifyStatus(NotifyStatus.FAIL);
            }
        }

        // 先更新数据库
        eventNotifyDao.update(eventNotify);

        // 若状态仍是处理中，则延迟发送消息至rmq
        if (NotifyStatus.PROCESSING == eventNotify.getNotifyStatus()) {
            String[] interval = JsonUtil.parse(eventNotify.getNotifyInterval(), String[].class);
            MqDelayMsgLevel level = MqDelayMsgLevel.valueOfTime(interval[eventNotify.getNotifyTime()]);
            notifyMqProducer.sendEventNotifyMsg(eventNotify.getEventNo(), level);
        }

    }


    private Class getEventDataType(EventType eventType) {
        switch (eventType) {
            case CHARGE_CHANGE_EVENT:
                return ChargeDto.class;
            case REFUND_CHANGE_EVENT:
                return RefundDto.class;
            default:
                return Map.class;
        }
    }

}
