package com.gloryjie.pay.trade.dao;


import com.gloryjie.pay.trade.model.Refund;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RefundDao {

    int insert(Refund record);

    Refund load(String refundNo);

    List<Refund> getByAppIdAndChargeNo(@Param("appId")Integer appId,@Param("chargeNo")String chargeNo);

    int update(Refund record);

}