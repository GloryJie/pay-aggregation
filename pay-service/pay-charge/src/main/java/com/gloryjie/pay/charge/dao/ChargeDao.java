package com.gloryjie.pay.charge.dao;


import com.gloryjie.pay.charge.model.Charge;

public interface ChargeDao {

    int insert(Charge record);

    Charge load(String chargeNo);

    int update(Charge record);
}