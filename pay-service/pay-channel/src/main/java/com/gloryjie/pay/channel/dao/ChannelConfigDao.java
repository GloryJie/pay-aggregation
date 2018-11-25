package com.gloryjie.pay.channel.dao;


import com.gloryjie.pay.channel.model.ChannelConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface ChannelConfigDao {

    int insert(ChannelConfig channelConfig);

    ChannelConfig load(Integer id);

    int update(ChannelConfig channelConfig);

    ChannelConfig loadByAppIdAndChannel(@Param("appId") Integer appId, @Param("channelType") String channelType);

}