package com.gloryjie.pay.statistics.controller;

import com.gloryjie.pay.base.response.Response;
import com.gloryjie.pay.statistics.model.PlatformChannelStat;
import com.gloryjie.pay.statistics.model.PlatformTradeStat;
import com.gloryjie.pay.statistics.model.SubAppTradeStat;
import com.gloryjie.pay.statistics.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author jie
 * @since 2019/5/3
 */
@RestController
@RequestMapping("/web/statistics")
public class StatisticsWebController {


    @Autowired
    private StatisticsService statisticsService;


    /**
     * 获取今日的交易统计
     *
     * @param appId
     * @return
     */
    @GetMapping("/{appId}/today")
    public Response<PlatformTradeStat> getTodayTradeStat(@PathVariable("appId") Integer appId) {
        return Response.success(statisticsService.getTodayPlatformTradeStat(appId));
    }

    /**
     * 获取历史的交易统计记录
     *
     * @param appId
     * @param limit
     * @return
     */
    @GetMapping("/{appId}/history")
    public Response<List<PlatformTradeStat>> getAppHistoryTradeStat(@PathVariable("appId") Integer appId,
                                                                    @RequestParam(value = "limit", defaultValue = "30") Integer limit) {
        return Response.success(statisticsService.listPlatformTradeHistoryStat(appId, limit));
    }

    /**
     * 获取渠道的交易记录
     *
     * @param appId
     * @return
     */
    @GetMapping("/{appId}/channel")
    public Response<List<PlatformChannelStat>> getChannelTradeStat(@PathVariable("appId") Integer appId) {
        return Response.success(statisticsService.listPlatformChannelTradeStat(appId));
    }


    /**
     * 获取子应用的交易统计
     *
     * @param appId
     * @return
     */
    @GetMapping("/{appId}/sub")
    public Response<List<SubAppTradeStat>> getSubAppTradeStat(@PathVariable("appId") Integer appId,
                                                              @RequestParam(value = "limit", defaultValue = "5") Integer limit) {
        return Response.success(statisticsService.listSubAppTradeStat(appId, limit));
    }
}
