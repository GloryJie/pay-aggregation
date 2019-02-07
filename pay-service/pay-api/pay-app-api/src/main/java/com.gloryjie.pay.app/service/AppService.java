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
    AppDto createMasterApp(String name, String desc);

    /**
     * 查询主APP列表
     * @return
     */
    List<AppDto> queryMasterAppList();

    Boolean updateAppInfo(AppUpdateParam updateParam);
}
