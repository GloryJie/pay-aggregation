package com.gloryjie.pay.refund.dao;


import com.gloryjie.pay.refund.model.Refund;

public interface RefundDao {

    int insert(Refund record);

    Refund load(String refundNo);

    int update(Refund record);

}