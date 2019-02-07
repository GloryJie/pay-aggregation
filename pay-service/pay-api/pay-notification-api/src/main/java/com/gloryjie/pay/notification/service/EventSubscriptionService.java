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

import com.gloryjie.pay.notification.dto.EventSubscriptionDto;
import com.gloryjie.pay.notification.enums.EventType;

import java.util.List;

/**
 * 事件订阅服务
 * @author Jie
 * @since
 */
public interface EventSubscriptionService {

    /**
     * 订阅新的事件, 或修改原有事件
     * @param subscriptionDto
     * @return
     */
    EventSubscriptionDto subscribeEvent(EventSubscriptionDto subscriptionDto);

    /**
     * 取消订阅事件
     * @return
     */
    Boolean cancelSubscribeEvent(Integer appId, EventType eventType);

    /**
     * 查询所有的订阅事件
     * @param appId
     * @return
     */
    List<EventSubscriptionDto> queryAllSubscribeEvent(Integer appId);


    /**
     * 查询指定的事件
     * @param appId
     * @param type
     * @return
     */
    EventSubscriptionDto querySingleScribeEvent(Integer appId, EventType type);



}
