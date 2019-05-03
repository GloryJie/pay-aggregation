package com.glory.pay.statistics.service;

import com.gloryjie.pay.PayAllApplication;
import com.gloryjie.pay.statistics.service.StatisticsService;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author jie
 * @since 2019/5/2
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = PayAllApplication.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class StatisticsServiceTest {

    @Autowired
    StatisticsService statisticsService;

    @Test
    public void platformTradeStatTest(){

        statisticsService.statPlatformTradeInDay();

    }

    @Test
    public void subAppTradeTest(){
        statisticsService.statAllSubAppTradeEachDay();
    }

    @Test
    public void channelTradeStatTest(){
        statisticsService.statChannelTrade();

    }


}
