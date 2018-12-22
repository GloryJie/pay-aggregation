/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.channel.service.platform.alipay
 *   Date Created: 2018/11/25
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2018/11/25      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.channel.service.platform.alipay;

import com.gloryjie.pay.base.util.idGenerator.IdFactory;
import com.gloryjie.pay.channel.dto.ChannelPayDto;
import com.gloryjie.pay.channel.dto.ChannelPayQueryDto;
import com.gloryjie.pay.channel.dto.ChannelRefundDto;
import com.gloryjie.pay.channel.dto.ChannelRefundQueryDto;
import com.gloryjie.pay.channel.enums.ChannelType;
import com.gloryjie.pay.trade.PayTradeApplication;
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
public class AlipayPageChannelServiceTest {

    @Autowired
    private AlipayPageChannelServiceImpl pageChannelService;

    @BeforeClass
    public static void init() {
        IdFactory factory = new IdFactory();
        factory.setDataCenterId(0);
        factory.setWorkerId(0);
        factory.init();
    }

    @Test
    public void payTest(){
        ChannelPayDto payDto = new ChannelPayDto();
        payDto.setAppId(123456);
        payDto.setAmount(new BigDecimal("1"));
        payDto.setChannel(ChannelType.ALIPAY_PAGE);
        payDto.setSubject("测试");
        payDto.setChargeNo(IdFactory.generateStringId());

        pageChannelService.pay(payDto);
    }

    @Test
    public void queryPaymentTest(){
        ChannelPayQueryDto queryDto = new ChannelPayQueryDto();
        queryDto.setAppId(123456);
        queryDto.setChannel(ChannelType.ALIPAY_PAGE);
        queryDto.setChargeNo("517070783986532352");

        pageChannelService.queryPayment(queryDto);
    }

    @Test
    public void refundTest(){
        ChannelRefundDto refundDto = new ChannelRefundDto();
        String refundNo = IdFactory.generateStringId();
        refundDto.setAppId(123456);
        refundDto.setChannel(ChannelType.ALIPAY_PAGE);
        refundDto.setChargeNo("516268444723707904");
        refundDto.setRefundNo(refundNo);
        refundDto.setAmount(new BigDecimal("0.5"));

        pageChannelService.refund(refundDto);
        System.out.println(refundNo);
    }

    @Test
    public void queryRefundTest(){
        ChannelRefundQueryDto queryDto = new ChannelRefundQueryDto();
        queryDto.setAppId(123456);
        queryDto.setChannel(ChannelType.ALIPAY_PAGE);
        queryDto.setChargeNo("516268444723707904");
        queryDto.setRefundNo("516269375045500928");

        pageChannelService.queryRefund(queryDto);
    }
}
