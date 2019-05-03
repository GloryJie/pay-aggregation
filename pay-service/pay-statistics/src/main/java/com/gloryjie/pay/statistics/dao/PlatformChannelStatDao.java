package com.gloryjie.pay.statistics.dao;


import com.gloryjie.pay.channel.enums.ChannelType;
import com.gloryjie.pay.statistics.model.PlatformChannelStat;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PlatformChannelStatDao {

    int insert(PlatformChannelStat record);


    int update(PlatformChannelStat record);

    PlatformChannelStat getByAppIdAndChannel(@Param("appId") Integer appId, @Param("channelType") ChannelType channelType);

    List<PlatformChannelStat> listAllByAppId(Integer appId);
}