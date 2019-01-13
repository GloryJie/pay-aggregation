package com.gloryjie.pay.trade.dao;


import com.gloryjie.pay.channel.enums.ChannelType;
import com.gloryjie.pay.trade.model.Charge;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChargeDao {

    int insert(Charge record);

    Charge load(String chargeNo);

    List<Charge> listByAppIdAndOrderNo(@Param("appId")Integer appId, @Param("orderNo") String orderNo);

    int update(Charge record);
}