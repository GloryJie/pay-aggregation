package com.gloryjie.pay.statistics.dao;


import com.gloryjie.pay.statistics.model.PlatformTradeStat;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

public interface PlatformTradeStatDao {

    int insert(PlatformTradeStat record);

    int insertBatch(@Param("statList") List<PlatformTradeStat> statList);

    int update(PlatformTradeStat record);

    PlatformTradeStat getByAppIdAndStatDay(@Param("appId") Integer appId, @Param("statDay")LocalDate statDay);

    List<PlatformTradeStat> listByAppId(@Param("appId")Integer appId, @Param("limit")Integer limit);

}