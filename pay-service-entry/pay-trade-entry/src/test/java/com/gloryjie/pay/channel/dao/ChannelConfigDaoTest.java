/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.channel.dao
 *   Date Created: 2018/11/18
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2018/11/18      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.channel.dao;

import com.gloryjie.pay.channel.enums.ChannelType;
import com.gloryjie.pay.channel.model.ChannelConfig;
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

import java.time.LocalDateTime;
import java.util.Random;

/**
 * @author Jie
 * @since
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = PayTradeApplication.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ChannelConfigDaoTest {


    @Autowired
    private ChannelConfigDao channelConfigDao;

    public static Integer id;

    public static Integer appId;

    @BeforeClass
    public static void init(){
        appId = new Random().nextInt(10000);
    }


    @Test
    public void aInsertTest(){
        ChannelConfig config = new ChannelConfig();
        config.setAppId(appId);
        config.setChannel(ChannelType.ALIPAY_PAGE);
        config.setChannelConfig("{}");
        config.setStartDate(LocalDateTime.now());
        config.setStopDate(LocalDateTime.now());
        config.setStatus("0");
        config.setLogicalDel(Boolean.FALSE);

        Assert.assertEquals(1,channelConfigDao.insert(config));
        id = config.getId();
    }

    @Test
    public void bLoadTest(){
        Assert.assertNotNull(channelConfigDao.load(id));
    }

    @Test
    public void cUpdateTest(){
        ChannelConfig config = new ChannelConfig();
        config.setId(id);
        config.setAppId(appId);
        config.setChannel(ChannelType.ALIPAY_BAR_CODE);

        Assert.assertEquals(1, channelConfigDao.update(config));

    }

}
