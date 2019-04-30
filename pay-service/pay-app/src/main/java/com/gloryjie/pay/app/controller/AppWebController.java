/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.app.controller
 *   Date Created: 2019/2/7
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2019/2/7      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.app.controller;

import com.gloryjie.pay.app.dto.AppCreateDto;
import com.gloryjie.pay.app.dto.AppDto;
import com.gloryjie.pay.app.dto.AppUpdateParam;
import com.gloryjie.pay.app.service.AppService;
import com.gloryjie.pay.base.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @author Jie
 * @since
 */
@RestController
@RequestMapping("/web")
public class AppWebController {

    @Autowired
    private AppService appService;

    @GetMapping("/app/master")
    public Response<List<AppDto>> getMasterApp() {
        return Response.success(appService.queryMasterAppList());
    }

    @GetMapping("/app/{appId}/info")
    public Response<AppDto> getAppInfo(@PathVariable("appId") Integer appId) {
        AppDto appDto = appService.getSingleAppInfo(appId);
        if (appDto != null) {
            appDto.setNotifyPrivateKey(null);
        }
        return Response.success(appDto);
    }

    @PostMapping("/app")
    public Response<AppDto> createMasterApp(@Valid @RequestBody AppCreateDto createDto) {
        // TODO: 2019/4/26 需要获取真正用户,创建主应用只有super_admin才可
        long userNo = 123456;
        return Response.success(appService.createMasterApp(createDto.getName(), createDto.getDescription(), userNo, userNo));
    }

    @PutMapping("/app/{appId}")
    public Response<Boolean> updateAppInfo(@PathVariable("appId") Integer appId, @RequestBody AppUpdateParam updateParam) {
        updateParam.setAppId(appId);
        return Response.success(appService.updateAppInfo(updateParam));
    }

    @PostMapping("/app/{parentAppId}/sub")
    public Response<AppDto> createSubApp(@Valid @RequestBody AppCreateDto createDto,
                                         @PathVariable("parentAppId") Integer parentAppId) {
        // TODO: 2019/4/26 需要获取真正用户,创建主应用只有super_admin才可
        long userNo = 123456;
        return Response.success(appService.createSubApp(createDto.getName(), createDto.getDescription(), parentAppId, userNo, userNo));
    }

    @GetMapping("/app/{rootAppId}/tree")
    public Response<List<AppDto>> getAppTree(@PathVariable("rootAppId") Integer rootAppId){
        List<AppDto> list = appService.getAppTreeAllNode(rootAppId);
        list.forEach(item ->{
            item.setNotifyPublicKey(null);
            item.setTradePublicKey(null);
        });
        return Response.success(list);
    }

}
