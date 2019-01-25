/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.trade.service
 *   Date Created: 2018/12/22
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2018/12/22      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.trade.service;

import com.gloryjie.pay.base.util.JsonUtil;
import com.gloryjie.pay.channel.dto.param.ChargeCreateParam;
import com.gloryjie.pay.channel.enums.ChannelType;
import com.gloryjie.pay.trade.PayTradeApplication;
import com.gloryjie.pay.trade.dto.ChargeDto;
import com.gloryjie.pay.trade.dto.RefundDto;
import com.gloryjie.pay.trade.dto.param.RefundParam;
import org.apache.logging.log4j.message.ReusableMessageFactory;
import org.junit.Assert;
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
public class ChargeServiceTest {

    @Autowired
    private ChargeService chargeService;

    @Test
    public void payTest(){
        ChargeCreateParam createParam = new ChargeCreateParam();
        createParam.setAmount(1L);
        createParam.setAppId(123456);
        createParam.setChannel(ChannelType.ALIPAY_WAP);
        createParam.setSubject("测试1222");
        createParam.setBody("描述信息");
        createParam.setClientIp("127.0.0.1");
        createParam.setCurrency("cny");
        createParam.setOrderNo(String.valueOf(System.currentTimeMillis()));
        createParam.setTimeExpire(120L);

        ChargeDto chargeDto = chargeService.pay(createParam);

        Assert.assertNotNull(chargeDto);
    }

    @Test
    public void queryPaymentTest(){
        ChargeDto chargeDto = chargeService.queryPayment(123456,"1545482478");

        System.out.println(JsonUtil.toJson(chargeDto));
    }


    @Test
    public void refundTest(){
        RefundParam refundParam = new RefundParam();
        refundParam.setAppId(123456);
        refundParam.setOrderNo(String.valueOf(System.currentTimeMillis()));
        refundParam.setChargeNo("537386019524182016");
        refundParam.setAmount(1L);
        refundParam.setDescription("测试退款");

        RefundDto refundDto = chargeService.refund(refundParam);

        System.out.println(JsonUtil.toJson(refundDto));
    }


}
