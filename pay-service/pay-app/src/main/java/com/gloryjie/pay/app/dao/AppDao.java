package com.gloryjie.pay.app.dao;


import com.gloryjie.pay.app.model.App;
import org.springframework.stereotype.Repository;

@Repository
public interface AppDao {

    int insert(App record);

    App load(Integer appId);

    int update(App record);
}