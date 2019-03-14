/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.app.service.rpc
 *   Date Created: 2019/3/7
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2019/3/7      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.app.service.api;

import com.gloryjie.pay.app.dto.AppDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 内部服务
 * @author Jie
 * @since 0.1
 */
@FeignClient(name = "auth-service")
public interface AppFeignApi {

    /**
     * 根据appId来获取应用信息
     * @param appId
     * @return
     */
    @GetMapping(value = "/feign-api/{appId}/info")
    AppDto getAppInfo(@PathVariable("appId") Integer appId);

}
