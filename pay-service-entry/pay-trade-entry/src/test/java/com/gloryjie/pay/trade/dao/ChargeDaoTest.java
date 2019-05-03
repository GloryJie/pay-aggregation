/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.newCharge.dao
 *   Date Created: 2018/11/18
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2018/11/18      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.trade.dao;

import com.gloryjie.pay.base.util.idGenerator.IdFactory;
import com.gloryjie.pay.channel.enums.ChannelType;
import com.gloryjie.pay.trade.PayTradeApplication;
import com.gloryjie.pay.trade.enums.ChargeStatus;
import com.gloryjie.pay.trade.model.Charge;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

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

    public static Charge newCharge;


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
        List<Integer> appList = Arrays.asList(10120001, 10120002, 10130001);
        IntStream.range(0, 1000).forEach(i -> {
            Charge charge = new Charge();
            charge.setChargeNo(IdFactory.generateStringId());
            charge.setOrderNo(String.valueOf(System.currentTimeMillis()));
            charge.setAppId(appList.get(i % appList.size()));
            charge.setServiceAppId(10110000);
            charge.setPlatformTradeNo("123456789");
            charge.setAmount((long) (Math.random() * 1000));
            charge.setSubject("测试");
            charge.setBody("测试");
            ChannelType[] typeArray = ChannelType.values();
            charge.setChannel(typeArray[i%typeArray.length]);
            charge.setClientIp("127.0.0.1");
            charge.setDescription("测试");
            charge.setTimeCreated(LocalDateTime.now().minusMinutes((long) (Math.random() * 10)));
            charge.setTimePaid(LocalDateTime.now());
            charge.setTimeExpire(15L);
            charge.setLiveMode(false);
            ChargeStatus[] statusArray = ChargeStatus.values();
            charge.setStatus(statusArray[i % statusArray.length]);
            charge.setCurrency("cny");
            charge.setVersion(0);
            charge.setCredential("测试凭证");

            Map<String, String> extra = new HashMap<>(1);
            extra.put("redirectUrl", "http://www.baidu.com");

            charge.setExtra(extra);

            Assert.assertEquals(1, chargeDao.insert(charge));
        });
//        Charge charge = new Charge();
//        charge.setChargeNo(chargeNo);
//        charge.setOrderNo(orderNo);
//        charge.setAppId(123456);
//        charge.setServiceAppId(123456);
//        charge.setPlatformTradeNo("123456789");
//        charge.setAmount(1L);
//        charge.setSubject("测试");
//        charge.setBody("测试");
//        charge.setChannel(ChannelType.ALIPAY_PAGE);
//        charge.setClientIp("127.0.0.1");
//        charge.setDescription("测试");
//        charge.setTimeCreated(LocalDateTime.now());
//        charge.setTimePaid(LocalDateTime.now());
//        charge.setTimeExpire(15L);
//        charge.setLiveMode(false);
//        charge.setStatus(ChargeStatus.SUCCESS);
//        charge.setCurrency("cny");
//        charge.setVersion(0);
//        charge.setCredential("测试凭证");
//
//        Map<String, String> extra = new HashMap<>(1);
//        extra.put("redirectUrl", "http://www.baidu.com");
//
//        charge.setExtra(extra);
//
//        Assert.assertEquals(1, chargeDao.insert(charge));

    }

    @Test
    public void bLoadTest() {
        newCharge = chargeDao.load(chargeNo);
        Assert.assertNotNull(newCharge);
    }

    @Test
    public void cUpdateTest() {
        newCharge.setSubject("测试更新");

        Assert.assertEquals(1, chargeDao.update(newCharge));
    }
}
