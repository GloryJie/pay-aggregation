/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.channel.service.platform.alipay
 *   Date Created: 2018/12/22
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2018/12/22      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.channel.service.platform.alipay;

import com.gloryjie.pay.base.util.idGenerator.IdFactory;
import com.gloryjie.pay.channel.dto.ChannelPayQueryDto;
import com.gloryjie.pay.channel.dto.ChannelPayQueryResponse;
import com.gloryjie.pay.channel.dto.ChannelRefundDto;
import com.gloryjie.pay.channel.dto.ChannelRefundQueryDto;
import com.gloryjie.pay.channel.dto.response.ChannelRefundResponse;
import com.gloryjie.pay.channel.enums.ChannelType;
import com.gloryjie.pay.trade.PayTradeApplication;
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
@SpringBootTest(classes = PayTradeApplication.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BaseAlipayChannelServiceTest {

    @Autowired
    private AlipayPageChannelServiceImpl pageChannelService;

    @Test
    public void queryPaymentTest(){
        ChannelPayQueryDto queryDto = new ChannelPayQueryDto();
        queryDto.setAppId(123456);
        queryDto.setChannel(ChannelType.ALIPAY_PAGE);
        queryDto.setChargeNo("526137175524245504");
        ChannelPayQueryResponse queryResponse = pageChannelService.queryPayment(queryDto);
        Assert.assertNotNull(queryResponse.getAmount());
    }

    @Test
    public void refundTest(){
        ChannelRefundDto refundDto = new ChannelRefundDto();
        String refundNo = IdFactory.generateStringId();
        refundDto.setAppId(123456);
        refundDto.setChannel(ChannelType.ALIPAY_PAGE);
        refundDto.setChargeNo("516268444723707904");
        refundDto.setRefundNo(refundNo);
        refundDto.setAmount(5L);

        ChannelRefundResponse refundResponse = (ChannelRefundResponse) pageChannelService.refund(refundDto);

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
