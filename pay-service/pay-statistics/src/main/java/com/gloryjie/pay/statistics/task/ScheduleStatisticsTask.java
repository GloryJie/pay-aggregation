package com.gloryjie.pay.statistics.task;

import com.gloryjie.pay.statistics.service.StatisticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 定时统计任务
 * @author jie
 * @since 2019/5/2
 */
@Slf4j
@Component
public class ScheduleStatisticsTask {


    @Autowired
    private StatisticsService statisticsService;


    /**
     * 间隔10分钟执行一次统计
     */
    @Scheduled(cron = "0 0/10 * * * ? ")
    public void statPlatformAtTime(){
        log.info("start to statistics platform trade, time={}", LocalDateTime.now());
        statisticsService.statPlatformTradeInTime();
        log.info("statistics platform trade end, time={}", LocalDateTime.now());
    }

    /**
     * 每日凌晨1点执行, 统计平台一天内的交易
     */
    @Scheduled(cron = "0 0 1 * * ? ")
    public void statPlatformOneDayTrade(){
        log.info("start to statistics platform one day trade, time={}", LocalDateTime.now());
        statisticsService.statPlatformTradeInDay();
        log.info("statistics platform one day trade end , time={}", LocalDateTime.now());
    }

    /**
     * 每日凌晨2点执行, 统计子应用一天的成功交易
     */
    @Scheduled(cron = "0 0 2 * * ? ")
    public void statSubAppOneDayTrade(){
        log.info("start to statistics all sub app one day trade, time={}", LocalDateTime.now());
        statisticsService.statAllSubAppTradeEachDay();
        log.info("statistics all sub app one day trade end , time={}", LocalDateTime.now());
    }

    /**
     * 每日凌晨3点执行, 统计平台内各渠道的交易
     */
    @Scheduled(cron = "0 0 3 * * ? ")
    public void statPlatformChannelOneDayTrade(){
        log.info("start to statistics platform channel one day trade, time={}", LocalDateTime.now());
        statisticsService.statChannelTrade();
        log.info("statistics platform channel one day trade end , time={}", LocalDateTime.now());
    }


}
