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

import com.gloryjie.pay.base.util.idGenerator.IdFactory;
import com.gloryjie.pay.notification.PayNotificationApplication;
import com.gloryjie.pay.notification.enums.EventType;
import com.gloryjie.pay.notification.enums.NotifyStatus;
import com.gloryjie.pay.notification.model.EventNotify;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;

/**
 * @author Jie
 * @since
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = PayNotificationApplication.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class EventNotifyDaoTest {


    @Autowired
    EventNotifyDao eventNotifyDao;

    public static String eventNo;

    public static String sourceNo;


    public static EventNotify eventNotify;

    @BeforeClass
    public static void init() {
        IdFactory factory = new IdFactory();
        factory.setDataCenterId(0);
        factory.setWorkerId(0);
        factory.init();
        eventNo = IdFactory.generateStringId();
        sourceNo = IdFactory.generateStringId();
    }


    @Test
    public void aInsertTest() {
        EventNotify eventNotify = new EventNotify();
        eventNotify.setEventNo(eventNo);
        eventNotify.setSourceNo(sourceNo);
        eventNotify.setAppId(123456);
        eventNotify.setNotifyStatus(NotifyStatus.PROCESSING);
        eventNotify.setType(EventType.CHARGE_CHANGE_EVENT);
        eventNotify.setTimeOccur(LocalDateTime.now());
        eventNotify.setNotifyTime(1);
        eventNotify.setNotifyUrl("http://www.xxxx.co");
        eventNotify.setNotifyInterval("[1,2,3,4,5]");
        eventNotify.setEventData("list");
        eventNotify.setLastReply("success");
        eventNotify.setTimeLastNotify(LocalDateTime.now());
        eventNotify.setVersion(0);

        Assert.assertTrue(eventNotifyDao.insert(eventNotify) > 0);

        EventNotifyDaoTest.eventNotify = eventNotify;
    }

    @Test
    public void bGetByEventNoTest() {
        Assert.assertNotNull(eventNotifyDao.getByEventNo(eventNo));
    }

    @Test
    public void cUpdateTest() {

        EventNotifyDaoTest.eventNotify.setNotifyStatus(NotifyStatus.SUCCESS);

        Assert.assertTrue(eventNotifyDao.update(EventNotifyDaoTest.eventNotify) > 0);
    }

}
