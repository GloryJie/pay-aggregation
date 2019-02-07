/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.notification.service
 *   Date Created: 2019/2/7
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2019/2/7      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.notification.service;

import com.gloryjie.pay.base.util.BeanConverter;
import com.gloryjie.pay.notification.dao.EventSubscriptionDao;
import com.gloryjie.pay.notification.dto.EventSubscriptionDto;
import com.gloryjie.pay.notification.enums.EventType;
import com.gloryjie.pay.notification.model.EventSubscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jie
 * @since
 */
@Service
public class EventSubscriptionServiceImpl implements EventSubscriptionService {

    @Autowired
    private EventSubscriptionDao subscriptionDao;

    @Override
    public EventSubscriptionDto subscribeEvent(EventSubscriptionDto dto) {
        EventSubscription eventSubscription = subscriptionDao.getByAppIdAndType(dto.getAppId(), dto.getEventType());
        if (eventSubscription != null) {
            eventSubscription.setNotifyUrl(dto.getNotifyUrl());
            // update
            subscriptionDao.update(eventSubscription);
        } else {
            // add
            eventSubscription = BeanConverter.covert(dto, EventSubscription.class);
            subscriptionDao.insert(eventSubscription);
        }
        return dto;
    }

    @Override
    public Boolean cancelSubscribeEvent(Integer appId, EventType eventType) {
        return subscriptionDao.delete(appId, eventType) > 0;
    }

    @Override
    public List<EventSubscriptionDto> queryAllSubscribeEvent(Integer appId) {
        List<EventSubscription> list = subscriptionDao.getByAppId(appId);
        if (list == null || list.isEmpty()) {
            return new ArrayList<>();
        }
        return BeanConverter.batchCovert(list, EventSubscriptionDto.class);
    }

    @Override
    public EventSubscriptionDto querySingleScribeEvent(Integer appId, EventType type) {
        EventSubscription eventSubscription = subscriptionDao.getByAppIdAndType(appId, type);
        if (eventSubscription == null) {
            return new EventSubscriptionDto();
        }
        return BeanConverter.covert(eventSubscription, EventSubscriptionDto.class);
    }
}
