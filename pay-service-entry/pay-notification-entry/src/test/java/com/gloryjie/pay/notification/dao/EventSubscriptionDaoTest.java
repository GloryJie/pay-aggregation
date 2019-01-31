/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.notification.dao
 *   Date Created: 2019/2/1
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2019/2/1      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.notification.dao;

import com.gloryjie.pay.notification.PayNotificationApplication;
import com.gloryjie.pay.notification.enums.EventType;
import com.gloryjie.pay.notification.model.EventSubscription;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author Jie
 * @since
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = PayNotificationApplication.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class EventSubscriptionDaoTest {

    @Autowired
    EventSubscriptionDao subscriptionDao;

    public static Integer id;

    @Test
    public void aInsertTest(){
        EventSubscription subscription = new EventSubscription();
        subscription.setAppId(123456);
        subscription.setEventType(EventType.CHARGE_CHANGE_EVENT);

        Assert.assertTrue(subscriptionDao.insert(subscription) > 0);
        Assert.assertNotNull(subscription.getId());
        id = subscription.getId();
    }

    @Test
    public void bGetByEventNoTest(){
       Assert.assertFalse( subscriptionDao.getByAppId(123456).isEmpty());
    }

    @Test
    public void cUpdateTest(){
        EventSubscription subscription = new EventSubscription();
        subscription.setId(id);
        subscription.setEventType(EventType.REFUND_CHANGE_EVENT);
        Assert.assertTrue(subscriptionDao.update(subscription) > 0);
    }
}
