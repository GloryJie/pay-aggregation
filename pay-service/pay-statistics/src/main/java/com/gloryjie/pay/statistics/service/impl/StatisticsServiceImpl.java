package com.gloryjie.pay.statistics.service.impl;

import com.gloryjie.pay.app.service.AppService;
import com.gloryjie.pay.channel.enums.ChannelType;
import com.gloryjie.pay.statistics.dao.PlatformChannelStatDao;
import com.gloryjie.pay.statistics.dao.PlatformTradeStatDao;
import com.gloryjie.pay.statistics.dao.SubAppTradeStatDao;
import com.gloryjie.pay.statistics.model.PlatformChannelStat;
import com.gloryjie.pay.statistics.model.PlatformTradeStat;
import com.gloryjie.pay.statistics.model.SubAppTradeStat;
import com.gloryjie.pay.statistics.service.StatisticsService;
import com.gloryjie.pay.trade.dto.StatCountDto;
import com.gloryjie.pay.trade.enums.ChargeStatus;
import com.gloryjie.pay.trade.service.ChargeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author jie
 * @since 2019/5/1
 */
@Slf4j
@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private AppService appService;

    @Autowired
    private ChargeService chargeService;

    @Autowired
    private PlatformTradeStatDao platformTradeStatDao;

    @Autowired
    private SubAppTradeStatDao subAppTradeStatDao;

    @Autowired
    private PlatformChannelStatDao channelStatDao;

    @Override
    public List<PlatformTradeStat> statPlatformTradeInTime() {
        List<PlatformTradeStat> allPlatformStat = new ArrayList<>();

        List<Integer> appIdList = appService.listMasterAppId();
        if (appIdList.size() < 1) {
            log.info("no platform app to statistics");
            return allPlatformStat;
        }
        LocalDateTime currentStatTime = LocalDateTime.now();

        // 逐个统计交易
        for (Integer platformAppId : appIdList) {
            // 获取今日的统计记录
            PlatformTradeStat tradeStat = getTodayPlatformTradeStat(platformAppId);
            LocalDateTime lastStatTime = tradeStat.getStatDateTime();
            if (currentStatTime.toLocalDate().isAfter(lastStatTime.toLocalDate())) {
                // 跨越一天, 统计0点
                currentStatTime = currentStatTime.toLocalDate().atStartOfDay();
            }
            LocalDateTime startOfDay = lastStatTime.toLocalDate().atStartOfDay();
            Map<ChargeStatus, StatCountDto> countResult = chargeService.countPlatformTrade(platformAppId, startOfDay, currentStatTime);
            for (Map.Entry<ChargeStatus, StatCountDto> item : countResult.entrySet()) {
                StatCountDto countDto = item.getValue();
                Long count = countDto.getCountNum() == null ? 0L : countDto.getCountNum();
                Long amount = countDto.getTotalAmount() == null ? 0L : countDto.getTotalAmount();
                switch (item.getKey()) {
                    case SUCCESS:
                        tradeStat.setSuccessAmount(amount);
                        tradeStat.setSuccessCount(count);
                        break;
                    case CLOSED:
                        tradeStat.setCloseAmount(amount);
                        tradeStat.setCloseCount(count);
                        break;
                    case WAIT_PAY:
                        tradeStat.setProcessAmount(amount);
                        tradeStat.setProcessCount(count);
                        break;
                    default:
                        break;
                }
            }
            tradeStat.setStatDateTime(currentStatTime);
            updateTodayTradeStat(tradeStat);
            allPlatformStat.add(tradeStat);
        }
        return allPlatformStat;
    }

    @Override
    public void statPlatformTradeInDay() {
        // 统计一天
        List<PlatformTradeStat> platformTradeStatList = this.statPlatformTradeInTime();
        if (platformTradeStatList == null || platformTradeStatList.isEmpty()) {
            return;
        }
        // 添加新的一天记录
        for (PlatformTradeStat tradeStat : platformTradeStatList) {
            tradeStat.setId(null);
            tradeStat.setProcessCount(0L);
            tradeStat.setProcessAmount(0L);
            tradeStat.setSuccessCount(0L);
            tradeStat.setSuccessAmount(0L);
            tradeStat.setCloseCount(0L);
            tradeStat.setCloseAmount(0L);
            tradeStat.setFailCount(0L);
            tradeStat.setFailAmount(0L);
            tradeStat.setStatisticsDay(tradeStat.getStatisticsDay().plusDays(1));
            tradeStat.setStatDateTime(tradeStat.getStatisticsDay().atStartOfDay());
        }
        // 批量添加新的记录
        addNewStatBatch(platformTradeStatList);
    }

    @Override
    public void statAllSubAppTradeEachDay() {
        List<Integer> subAppIdList = appService.listSubAppId();
        if (subAppIdList == null || subAppIdList.isEmpty()) {
            return;
        }
        for (Integer subAppId : subAppIdList) {
            SubAppTradeStat stat = listSubAppTradeStat(subAppId);
            StatCountDto countDto = chargeService.countSubAppTradeInDay(subAppId, ChargeStatus.SUCCESS, stat.getStatDay().plusDays(1));
            stat.setSuccessAmount(stat.getSuccessAmount() + countDto.getTotalAmount());
            stat.setSuccessCount(stat.getSuccessCount() + countDto.getCountNum());
            stat.setStatDay(stat.getStatDay().plusDays(1));

            subAppTradeStatDao.update(stat);
        }
    }

    @Override
    public void statChannelTrade() {
        List<Integer> appIdList = appService.listMasterAppId();
        if (appIdList == null || appIdList.isEmpty()) {
            return;
        }
        // 遍历统计每颗应用树
        for (Integer appId : appIdList) {
            Map<ChannelType, StatCountDto> resultMap = chargeService.countAllChannelTradeInDay(appId, ChargeStatus.SUCCESS, LocalDate.now().minusDays(1));
            for (Map.Entry<ChannelType, StatCountDto> entry : resultMap.entrySet()) {
                PlatformChannelStat stat = getChannelStat(appId, entry.getKey());
                StatCountDto countDto = entry.getValue();
                stat.setSuccessCount(stat.getSuccessAmount() + countDto.getCountNum());
                stat.setSuccessAmount(stat.getSuccessAmount() + countDto.getTotalAmount());
                stat.setStatDay(stat.getStatDay().plusDays(1));

                channelStatDao.update(stat);
            }
        }
    }


    @Override
    public SubAppTradeStat listSubAppTradeStat(Integer appId) {
        SubAppTradeStat stat = subAppTradeStatDao.getByAppId(appId);
        if (stat == null) {
            stat = new SubAppTradeStat();
            stat.setAppId(appId);
            stat.setSuccessAmount(0L);
            stat.setSuccessCount(0L);
            stat.setStatDay(LocalDate.now().minusDays(2));

            subAppTradeStatDao.insert(stat);
        }
        return stat;
    }

    @Override
    public PlatformTradeStat getTodayPlatformTradeStat(Integer appId) {
        PlatformTradeStat tradeStat = platformTradeStatDao.getByAppIdAndStatDay(appId, LocalDate.now());
        if (tradeStat == null) {
            log.info("appId= {} today={} platform trade stat is null, ready to insert new record", appId, LocalDate.now());

            tradeStat = new PlatformTradeStat();
            tradeStat.setAppId(appId);
            tradeStat.setProcessCount(0L);
            tradeStat.setProcessAmount(0L);
            tradeStat.setSuccessCount(0L);
            tradeStat.setSuccessAmount(0L);
            tradeStat.setCloseCount(0L);
            tradeStat.setCloseAmount(0L);
            tradeStat.setFailCount(0L);
            tradeStat.setFailAmount(0L);
            tradeStat.setStatisticsDay(LocalDate.now());
            tradeStat.setStatDateTime(tradeStat.getStatisticsDay().atStartOfDay());

            platformTradeStatDao.insert(tradeStat);
        }
        return tradeStat;
    }

    @Override
    public PlatformChannelStat getChannelStat(Integer appId, ChannelType channelType) {
        PlatformChannelStat stat = channelStatDao.getByAppIdAndChannel(appId, channelType);
        if (stat == null) {
            stat = new PlatformChannelStat();
            stat.setAppId(appId);
            stat.setChannelType(channelType);
            stat.setPlatformType(channelType.getPlatformType());
            stat.setSuccessAmount(0L);
            stat.setSuccessCount(0L);
            stat.setStatDay(LocalDate.now().minusDays(2));

            channelStatDao.insert(stat);
        }
        return stat;
    }

    @Override
    public void updateTodayTradeStat(PlatformTradeStat stat) {
        platformTradeStatDao.update(stat);
    }

    @Override
    public void addNewStatBatch(List<PlatformTradeStat> statList) {
        platformTradeStatDao.insertBatch(statList);
    }

    @Override
    public List<PlatformTradeStat> listPlatformTradeHistoryStat(Integer appId, Integer limit) {
        return platformTradeStatDao.listByAppId(appId, limit);
    }

    @Override
    public List<PlatformChannelStat> listPlatformChannelTradeStat(Integer appId) {
        return channelStatDao.listAllByAppId(appId);
    }

    @Override
    public List<SubAppTradeStat> listSubAppTradeStat(Integer appId, Integer limit) {
        Integer maxAppId = (appId / 100000 * 100000) + 99999;
        return subAppTradeStatDao.listByAppTreeAndTime(appId, maxAppId, limit);
    }


}
