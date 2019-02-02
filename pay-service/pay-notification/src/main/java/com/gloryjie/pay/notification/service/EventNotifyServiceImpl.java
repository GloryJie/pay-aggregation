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

import com.gloryjie.pay.base.enums.MqDelayMsgLevel;
import com.gloryjie.pay.base.util.JsonUtil;
import com.gloryjie.pay.base.util.idGenerator.IdFactory;
import com.gloryjie.pay.notification.dao.EventNotifyDao;
import com.gloryjie.pay.notification.dao.EventSubscriptionDao;
import com.gloryjie.pay.notification.enums.EventType;
import com.gloryjie.pay.notification.enums.NotifyStatus;
import com.gloryjie.pay.notification.model.EventNotify;
import com.gloryjie.pay.notification.model.EventSubscription;
import com.gloryjie.pay.notification.mq.NotifyMqProducer;
import com.gloryjie.pay.trade.dto.ChargeDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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

        // 查看是否已经订阅
        EventSubscription subscription = subscriptionDao.getByAppIdAndType(chargeDto.getAppId(), EventType.CHARGE_CHANGE_EVENT);
        if (subscription == null) {
            log.info("appId={} not subscribe charge change event, chargeNo={}", chargeDto.getAppId(), chargeDto.getChargeNo());
            return;
        }

        // 检查是否存在，避免重复消费
        EventNotify eventNotify = eventNotifyDao.getByEventNo(chargeDto.getChargeNo());
        if (eventNotify != null) {
            log.warn("appId={} charge success event already exists, eventNo={}, sourceNo={}", eventNotify.getAppId(),
                    eventNotify.getEventNo(), eventNotify.getSourceNo());
            return;
        }

        eventNotify = new EventNotify();
        eventNotify.setEventNo(IdFactory.generateStringId());
        eventNotify.setSourceNo(chargeDto.getChargeNo());
        eventNotify.setAppId(chargeDto.getAppId());
        eventNotify.setNotifyStatus(NotifyStatus.PROCESSING);
        eventNotify.setType(EventType.CHARGE_CHANGE_EVENT);
        eventNotify.setTimeOccur(LocalDateTime.now());
        eventNotify.setNotifyTime(0);
        eventNotify.setNotifyInterval(JsonUtil.toJson(notifyInterval));
        eventNotify.setEventData(JsonUtil.toJson(chargeDto));
        eventNotify.setTimeLastNotify(LocalDateTime.now());
        eventNotify.setVersion(0);

        // 2. 入库
        eventNotifyDao.insert(eventNotify);
        log.info("save new CHARGE_SUCCESS event, eventNo={}, appId={}", eventNotify.getEventNo(), eventNotify.getAppId());

        // 发送内循环通知
        notifyMqProducer.sendEventNotifyMsg(eventNotify.getEventNo(), MqDelayMsgLevel.FIRST);

    }

}
