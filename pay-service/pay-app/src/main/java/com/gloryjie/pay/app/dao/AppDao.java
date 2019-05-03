package com.gloryjie.pay.app.dao;


import com.gloryjie.pay.app.enums.AppType;
import com.gloryjie.pay.app.model.App;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppDao {

    int insert(App record);

    App getByAppId(Integer appId);

    int update(App record);

    App getMasterByName(String name);

    /**
     * 根据名称获取子应用
     * @param name
     * @param rootAppId 根应用id
     * @param maxAppId 当前树的最id
     * @return
     */
    App getSubByName(@Param("name") String name, @Param("rootAppId") Integer rootAppId, @Param("maxAppId") Integer maxAppId);

    Integer getMasterMaxAppId();

    Integer getSubMaxAppId(@Param("minAppId") Integer minAppId, @Param("maxAppId") Integer maxAppId);

    List<App> getMasterAppList();

    /**
     * 获取应用树内所有的节点
     * @param minAppId
     * @param maxAppId
     * @return
     */
    List<App> getAppTree(@Param("minAppId") Integer minAppId, @Param("maxAppId") Integer maxAppId);

    /**
     * 获取所有的指定类型的appId
     * @return
     */
    List<Integer> listAllAppIdByType(AppType appType);

}