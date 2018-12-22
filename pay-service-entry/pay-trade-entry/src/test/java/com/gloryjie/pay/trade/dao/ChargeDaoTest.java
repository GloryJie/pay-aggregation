/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.charge.dao
 *   Date Created: 2018/11/18
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2018/11/18      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.trade.dao;

import com.gloryjie.pay.base.util.DateTimeUtil;
import com.gloryjie.pay.base.util.idGenerator.IdFactory;
import com.gloryjie.pay.channel.enums.ChannelType;
import com.gloryjie.pay.trade.enums.ChargeStatus;
import com.gloryjie.pay.trade.model.Charge;
import com.gloryjie.pay.trade.PayTradeApplication;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;

/**
 * @author Jie
 * @since
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = PayTradeApplication.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ChargeDaoTest {

    @Autowired
    private ChargeDao chargeDao;

    public static String chargeNo;

    public static String orderNo;


    @BeforeClass
    public static void init() {
        IdFactory factory = new IdFactory();
        factory.setDataCenterId(0);
        factory.setWorkerId(0);
        factory.init();
        chargeNo = IdFactory.generateStringId();
        orderNo = IdFactory.generateStringId();
    }

    @Test
    public void aInsertTest() {
        Charge charge = new Charge();
        charge.setChargeNo(chargeNo);
        charge.setOrderNo(orderNo);
        charge.setAppId(123456);
        charge.setServiceAppId(123456);
        charge.setAmount(new BigDecimal("1.00"));
        charge.setSubject("测试");
        charge.setBody("测试");
        charge.setChannel(ChannelType.ALIPAY_WAP);
        charge.setClientIp("127.0.0.1");
        charge.setDescription("测试");
        charge.setTimeCreated(DateTimeUtil.currentTimeMillis());
        charge.setTimePaid(DateTimeUtil.currentTimeMillis());
        charge.setExpireTimestamp(DateTimeUtil.currentTimeMillis());
        charge.setTimeExpire(15L);
        charge.setLiveMode(false);
        charge.setStatus(ChargeStatus.WAIT_PAY);
        charge.setCurrency("cny");
        charge.setVersion(0);
        charge.setCredential("测试凭证");

        Assert.assertEquals(1, chargeDao.insert(charge));

    }

    @Test
    public void bLoadTest() {
        Assert.assertNotNull(chargeDao.load(chargeNo));
    }

    @Test
    public void cUpdateTest() {
        Charge charge = new Charge();
        charge.setChargeNo(chargeNo);
        charge.setSubject("测试更新");

        Assert.assertEquals(1, chargeDao.update(charge));
    }
}
