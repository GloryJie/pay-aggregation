package com.gloryjie.pay.trade.dao;


import com.gloryjie.pay.trade.model.Refund;
import org.springframework.stereotype.Repository;

@Repository
public interface RefundDao {

    int insert(Refund record);

    Refund load(String refundNo);

    int update(Refund record);

}