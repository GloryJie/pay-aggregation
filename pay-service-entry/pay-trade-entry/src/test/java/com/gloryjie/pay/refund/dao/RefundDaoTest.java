/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.refund.dao
 *   Date Created: 2018/11/18
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2018/11/18      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.refund.dao;

import com.gloryjie.pay.base.util.DateTimeUtil;
import com.gloryjie.pay.base.util.idGenerator.IdFactory;
import com.gloryjie.pay.channel.enums.ChannelType;
import com.gloryjie.pay.trade.enums.RefundStatus;
import com.gloryjie.pay.trade.model.Refund;
import com.gloryjie.pay.trade.PayTradeApplication;
import com.gloryjie.pay.trade.dao.RefundDao;
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
public class RefundDaoTest {

    @Autowired
    private RefundDao refundDao;

    public static String chargeNo;

    public static String orderNo;

    public static String refundNo;



    @BeforeClass
    public static void init() {
        IdFactory factory = new IdFactory();
        factory.setDataCenterId(0);
        factory.setWorkerId(0);
        factory.init();
        chargeNo = IdFactory.generateStringId();
        orderNo = IdFactory.generateStringId();
        refundNo = IdFactory.generateStringId();
    }

    @Test
    public void aInsertTest() {
        Refund refund = new Refund();

        refund.setRefundNo(refundNo);
        refund.setOrderNo(orderNo);
        refund.setChargeNo(chargeNo);
        refund.setAppId(123456);
        refund.setChannel(ChannelType.ALIPAY_PAGE);
        refund.setAmount(1L);
        refund.setDescription("测试");
        refund.setClientIp("127.0.0.1");
        refund.setExtra("额外数据");
        refund.setUserHold("用户保留信息");
        refund.setPlatformTradeNo("212387498327");
        refund.setTimeSucceed(DateTimeUtil.currentTimeMillis());
        refund.setStatus(RefundStatus.PROCESSING);
        refund.setCurrency("cny");
        refund.setVersion(0);

        Assert.assertEquals(1, refundDao.insert(refund));
    }

    @Test
    public void bLoadTest() {
        Assert.assertNotNull(refundDao.load(refundNo));
    }

    @Test
    public void cUpdateTest() {
        Refund refund = new Refund();
        refund.setRefundNo(refundNo);
        refund.setDescription("更新描述");

        Assert.assertEquals(1, refundDao.update(refund));
    }
}
