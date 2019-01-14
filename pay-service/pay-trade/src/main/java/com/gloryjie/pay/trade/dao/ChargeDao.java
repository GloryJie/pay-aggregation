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

    /**
     * 根据商户的订单号来查找支付单
     * @param appId 商户id
     * @param orderNo 订单号
     * @return
     */
    List<Charge> listByAppIdAndOrderNo(@Param("appId")Integer appId, @Param("orderNo") String orderNo);

    /**
     * 根据支付单号来查找支付单
     * @param appId 商户id
     * @param chargeNo 支付单号
     * @return
     */
    Charge getByAppIdAndChargeNo(@Param("appId")Integer appId, @Param("chargeNo") String chargeNo);


    int update(Charge record);
}