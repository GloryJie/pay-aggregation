package com.gloryjie.pay.channel.dao;


import com.gloryjie.pay.channel.enums.ChannelType;
import com.gloryjie.pay.channel.model.ChannelConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface ChannelConfigDao {

    int insert(ChannelConfig channelConfig);

    ChannelConfig load(Integer id);

    int update(ChannelConfig channelConfig);

    ChannelConfig loadByAppIdAndChannel(@Param("appId") Integer appId, @Param("channel") ChannelType channel);

    List<ChannelConfig> getByAppId(Integer appId);

    int delete(@Param("appId") Integer appId, @Param("channel") ChannelType channel);

}