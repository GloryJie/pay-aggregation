/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.notification.dao
 *   Date Created: 2019/1/31
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2019/1/31      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.notification.dao;


import com.gloryjie.pay.notification.model.EventSubscription;

import java.util.List;

/**
 * @author Jie
 * @since
 */
public interface EventSubscriptionDao {

    int insert(EventSubscription record);

    List<EventSubscription> getByAppId(Integer appId);

    int update(EventSubscription record);
}
