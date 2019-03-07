/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.app.service
 *   Date Created: 2019/3/7
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2019/3/7      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.app.service.api;

import com.gloryjie.pay.app.dto.AppDto;
import com.gloryjie.pay.app.service.AppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * 内部服务实现
 * @author Jie
 * @since
 */
@RestController
public class AppFeignApiImpl implements AppFeignApi {

    @Autowired
    private AppService appService;

    @Override
    public AppDto getAppInfo(Integer appId) {
        return appService.getSingleAppInfo(appId);
    }
}
