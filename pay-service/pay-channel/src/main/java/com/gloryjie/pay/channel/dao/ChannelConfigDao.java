package com.gloryjie.pay.channel.dao;


import com.gloryjie.pay.channel.model.ChannelConfig;

public interface ChannelConfigDao {

    int insert(ChannelConfig channelConfig);

    ChannelConfig load(Integer id);

    int update(ChannelConfig channelConfig);

}