package com.gloryjie.pay.statistics.dao;


import com.gloryjie.pay.statistics.model.SubAppTradeStat;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SubAppTradeStatDao {

    int insert(SubAppTradeStat record);

    int update(SubAppTradeStat record);

    SubAppTradeStat getByAppId(Integer appId);

    List<SubAppTradeStat> listByAppTreeAndTime(@Param("appId") Integer appId,
                                               @Param("maxAppId") Integer maxAppId,
                                               @Param("limit")Integer limit);
}