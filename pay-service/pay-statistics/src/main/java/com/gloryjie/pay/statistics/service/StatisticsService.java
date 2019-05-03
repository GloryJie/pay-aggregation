package com.gloryjie.pay.statistics.service;

import com.gloryjie.pay.channel.enums.ChannelType;
import com.gloryjie.pay.statistics.model.PlatformChannelStat;
import com.gloryjie.pay.statistics.model.PlatformTradeStat;
import com.gloryjie.pay.statistics.model.SubAppTradeStat;

import java.util.List;

/**
 * @author jie
 * @since 2019/5/1
 */
public interface StatisticsService {

    /**
     * 统计平台一天内的交易记录
     * @return
     */
    List<PlatformTradeStat> statPlatformTradeInTime();

    /**
     * 统计平台每天的交易记录
     */
    void statPlatformTradeInDay();

    /**
     * 统计子商户的交易总和
     */
    void statAllSubAppTradeEachDay();

    /**
     * 统计具体的支付渠道
     */
    void statChannelTrade();

    /**
     * 获取子应用的交易统计, 若不存在则新建
     * @param appId
     * @return
     */
    SubAppTradeStat listSubAppTradeStat(Integer appId);

    /**
     * 获取平台商户的交易统计, 若不存在则新建
     * @param appId
     * @return
     */
    PlatformTradeStat getTodayPlatformTradeStat(Integer appId);

    /**
     * 统计一天内渠道的交易
     * @param appId
     * @return
     */
    PlatformChannelStat getChannelStat(Integer appId, ChannelType channelType);

    void updateTodayTradeStat(PlatformTradeStat stat);

    void addNewStatBatch(List<PlatformTradeStat> statList);

    /**
     * 获取应用的历史统计记录
     * @param appId
     * @param limit 限制多少条
     * @return
     */
    List<PlatformTradeStat> listPlatformTradeHistoryStat(Integer appId, Integer limit);


    /**
     * 获取渠平台内渠道分布情况
     * @param appId
     * @return
     */
    List<PlatformChannelStat> listPlatformChannelTradeStat(Integer appId);


    /**
     * 获取交易统计中前几的子商户
     * @param appId
     * @param limit 排名前几
     * @return
     */
    List<SubAppTradeStat> listSubAppTradeStat(Integer appId, Integer limit);

}
