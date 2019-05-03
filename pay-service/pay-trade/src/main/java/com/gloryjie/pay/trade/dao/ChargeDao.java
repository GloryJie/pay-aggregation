package com.gloryjie.pay.trade.dao;


import com.gloryjie.pay.channel.enums.ChannelType;
import com.gloryjie.pay.trade.dto.StatCountDto;
import com.gloryjie.pay.trade.dto.param.ChargeQueryParam;
import com.gloryjie.pay.trade.enums.ChargeStatus;
import com.gloryjie.pay.trade.model.Charge;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public interface ChargeDao {

    int insert(Charge record);

    Charge load(String chargeNo);

    /**
     * 根据商户的订单号来查找支付单
     *
     * @param appId   商户id
     * @param orderNo 订单号
     * @return
     */
    List<Charge> listByAppIdAndOrderNo(@Param("appId") Integer appId, @Param("orderNo") String orderNo);

    /**
     * 根据支付单号来查找支付单
     *
     * @param appId    商户id
     * @param chargeNo 支付单号
     * @return
     */
    Charge getByAppIdAndChargeNo(@Param("appId") Integer appId, @Param("chargeNo") String chargeNo);


    int update(Charge record);

    /**
     * 根据指定的APPID查找支付单
     *
     * @param queryParam 查询参数
     * @return
     */
    List<Charge> getByQueryParam(ChargeQueryParam queryParam);

    /**
     * 统计应用树内时间段的交易
     *
     * @param appId
     * @param maxAppId
     * @param status
     * @param minTime
     * @param maxTime
     * @return
     */
    StatCountDto countByAppTreeAndStatusAndTime(@Param("appId") Integer appId,
                                                @Param("maxAppId") Integer maxAppId,
                                                @Param("status") ChargeStatus status,
                                                @Param("minTime") LocalDateTime minTime,
                                                @Param("maxTime") LocalDateTime maxTime);

    /**
     * 统计一天内的app交易
     *
     * @param appId
     * @param status
     * @param dayTime
     * @return
     */
    StatCountDto countAppOneDayTrade(@Param("appId") Integer appId,
                                     @Param("status") ChargeStatus status,
                                     @Param("dayTime") LocalDate dayTime);

    /**
     * 统计应用树内支付渠道一天的交易
     *
     * @param appId
     * @param dayTime
     * @return
     */
    StatCountDto countAppTreeChannelOneDayTrade(@Param("appId") Integer appId,
                                                @Param("maxAppId") Integer maxAppId,
                                                @Param("status") ChargeStatus status,
                                                @Param("channel") ChannelType channel,
                                                @Param("dayTime") LocalDate dayTime);
}