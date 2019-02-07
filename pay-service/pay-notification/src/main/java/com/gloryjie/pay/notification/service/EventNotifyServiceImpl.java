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

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.gloryjie.pay.base.enums.MqDelayMsgLevel;
import com.gloryjie.pay.base.util.BeanConverter;
import com.gloryjie.pay.base.util.JsonUtil;
import com.gloryjie.pay.base.util.idGenerator.IdFactory;
import com.gloryjie.pay.notification.dao.EventNotifyDao;
import com.gloryjie.pay.notification.dao.EventSubscriptionDao;
import com.gloryjie.pay.notification.dto.EventNotifyDto;
import com.gloryjie.pay.notification.enums.EventType;
import com.gloryjie.pay.notification.enums.NotifyStatus;
import com.gloryjie.pay.notification.model.EventNotify;
import com.gloryjie.pay.notification.model.EventSubscription;
import com.gloryjie.pay.notification.mq.NotifyMqProducer;
import com.gloryjie.pay.trade.dto.ChargeDto;
import com.gloryjie.pay.trade.dto.RefundDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Jie
 * @since
 */
@Slf4j
@Service
public class EventNotifyServiceImpl implements EventNotifyService {

    @Autowired
    private NotifyMqProducer notifyMqProducer;

    @Autowired
    private EventNotifyDao eventNotifyDao;

    @Autowired
    private EventSubscriptionDao subscriptionDao;

    @Value("${event.notifyInterval}")
    private String[] notifyInterval;

    /**
     * 处理支付成功
     *
     * @param chargeDto
     */
    public void handleChargeSuccessEvent(ChargeDto chargeDto) {

        EventSubscription subscription = checkTriggerEvent(chargeDto.getAppId(), chargeDto.getChargeNo(),
                EventType.REFUND_CHANGE_EVENT, chargeDto);
        if (subscription == null) {
            return;
        }
        EventNotify eventNotify = generateEventNotify(chargeDto.getAppId(), chargeDto.getChargeNo(),
                EventType.CHARGE_CHANGE_EVENT, chargeDto, subscription.getNotifyUrl());
        // 2. 入库
        eventNotifyDao.insert(eventNotify);

        log.info("save new CHARGE_SUCCESS event, eventNo={}, appId={}", eventNotify.getEventNo(), eventNotify.getAppId());

        // 发送内循环通知
        notifyMqProducer.sendEventNotifyMsg(eventNotify.getEventNo(), MqDelayMsgLevel.FIRST);

    }

    @Override
    public void handleRefundSuccessEvent(RefundDto refundDto) {
        EventSubscription subscription = checkTriggerEvent(refundDto.getAppId(), refundDto.getRefundNo(), EventType.REFUND_CHANGE_EVENT, refundDto);
        if (subscription == null) {
            return;
        }
        EventNotify eventNotify = generateEventNotify(refundDto.getAppId(), refundDto.getRefundNo(),
                EventType.REFUND_CHANGE_EVENT, refundDto, subscription.getNotifyUrl());

        // 2. 入库
        eventNotifyDao.insert(eventNotify);
        log.info("save new REFUND_SUCCESS event, eventNo={}, appId={}", eventNotify.getEventNo(), eventNotify.getAppId());

        // 发送内循环通知
        notifyMqProducer.sendEventNotifyMsg(eventNotify.getEventNo(), MqDelayMsgLevel.FIRST);
    }

    @Override
    public PageInfo<EventNotifyDto> getRecord(Integer appId, Integer startPage, Integer pageSize) {
        PageHelper.startPage(startPage, pageSize);
        List<EventNotify> notifyList = eventNotifyDao.getByAppId(appId);
        PageInfo pageInfo = PageInfo.of(notifyList);
        pageInfo.setList(BeanConverter.batchCovert(notifyList, EventNotifyDto.class));
        return pageInfo;
    }

    /**
     * 检查是否需要触发通知事件,
     *
     * @param appId
     * @param sourceNo
     * @param eventType
     * @return 返回事件订阅模型
     */
    private EventSubscription checkTriggerEvent(Integer appId, String sourceNo, EventType eventType, Object data) {
        // TODO: 2019/2/2 需要检查dto必填项是否齐全

        // 查看是否已经订阅
        EventSubscription subscription = subscriptionDao.getByAppIdAndType(appId, eventType);
        if (subscription == null) {
            log.info("appId={} not subscribe eventType={}, sourceNo={}", appId, eventType.name(), sourceNo);
            return null;
        }

        // 检查是否存在，避免重复消费
        EventNotify eventNotify = eventNotifyDao.getByEventNo(sourceNo);
        if (eventNotify != null) {
            log.warn("appId={} eventNo={} sourceNo={} already exists", eventNotify.getAppId(), eventNotify.getEventNo(), eventNotify.getSourceNo());
            return null;
        }
        return subscription;
    }

    private EventNotify generateEventNotify(Integer appId, String sourceNo, EventType eventType, Object data, String notifyUrl) {

        EventNotify eventNotify = new EventNotify();
        eventNotify.setEventNo(IdFactory.generateStringId());
        eventNotify.setSourceNo(sourceNo);
        eventNotify.setAppId(appId);
        eventNotify.setNotifyStatus(NotifyStatus.PROCESSING);
        eventNotify.setType(eventType);
        eventNotify.setTimeOccur(LocalDateTime.now());
        eventNotify.setNotifyTime(0);
        eventNotify.setNotifyInterval(JsonUtil.toJson(notifyInterval));
        eventNotify.setEventData(JsonUtil.toJson(data));
        eventNotify.setTimeLastNotify(LocalDateTime.now());
        eventNotify.setVersion(0);

        // 冗余推送地址
        eventNotify.setNotifyUrl(notifyUrl);

        return eventNotify;
    }
}
