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
import java.util.HashMap;
import java.util.Map;

/**
 * @author Jie
 * @since
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = PayTradeApplication.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AlipayBarCodeChannelServiceTest {

    @Autowired
    private AlipayBarCodeChannelServiceImpl barCodeChannelService;

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
        payDto.setChannel(ChannelType.ALIPAY_SCAN_CODE);
        payDto.setSubject("测试");
        payDto.setChargeNo(IdFactory.generateStringId());
        Map<String,String> extra = new HashMap<>();
        extra.put("authCode","283210497765321084");
        payDto.setExtra(extra);
        barCodeChannelService.pay(payDto);
    }




}
