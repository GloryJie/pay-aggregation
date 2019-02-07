package com.gloryjie.pay.app.dao;


import com.gloryjie.pay.app.model.App;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppDao {

    int insert(App record);

    App load(Integer appId);

    int update(App record);

    App getByName(String name);

    Integer getMaxAppId();

    List<App> getMasterAppList();
}