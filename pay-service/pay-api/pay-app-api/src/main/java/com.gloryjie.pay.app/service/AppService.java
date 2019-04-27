/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.app.service
 *   Date Created: 2019/2/7
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2019/2/7      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.app.service;

import com.gloryjie.pay.app.dto.AppDto;
import com.gloryjie.pay.app.dto.AppUpdateParam;

import java.util.List;

/**
 * @author Jie
 * @since
 */
public interface AppService {

    /**
     * 创建主app
     * @param name
     * @param desc
     * @return
     */
    AppDto createMasterApp(String name, String desc, Long createUserNo, Long responseUserNo);

    /**
     * 创建子应用
     * @param name 应用名
     * @param desc
     * @param parentAppID 父节点
     * @param createUserNo 创建人
     * @param responseUserNo 负责人
     * @return
     */
    AppDto createSubApp(String name, String desc, Integer parentAppID, Long createUserNo, Long responseUserNo);

    /**
     * 查询主APP列表
     * @return
     */
    List<AppDto> queryMasterAppList();

    /**
     * 更新应用信息
     * @param updateParam
     * @return
     */
    Boolean updateAppInfo(AppUpdateParam updateParam);

    /**
     * 根据appId来获取应用信息
     * @param appId
     * @return
     */
    AppDto getSingleAppInfo(Integer appId);

    /**
     * 获取应用树所有的节点信息
     * @param rootAppId
     * @return
     */
    List<AppDto> getAppTreeAllNode(Integer rootAppId);
}
