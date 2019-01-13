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

import com.gloryjie.pay.base.util.JsonUtil;
import com.gloryjie.pay.base.util.idGenerator.IdFactory;
import com.gloryjie.pay.channel.dto.*;
import com.gloryjie.pay.channel.dto.response.ChannelPayResponse;
import com.gloryjie.pay.channel.dto.response.ChannelResponse;
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
        payDto.setAmount(1L);
        payDto.setChannel(ChannelType.ALIPAY_PAGE);
        payDto.setSubject("测试");
        payDto.setChargeNo(IdFactory.generateStringId());

        ChannelPayResponse payResponse = pageChannelService.pay(payDto);

        System.out.println(payResponse.getCredential());
    }


}
