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
    public List<AppDto> getMasterApp() {
        return appService.queryMasterAppList();
    }

    @PostMapping("/app")
    public AppDto createMasterApp(@Valid @RequestBody AppCreateDto createDto) {
        return appService.createMasterApp(createDto.getName(), createDto.getDescription());
    }

    @PutMapping("/app/{appId}")
    public Boolean updateAppInfo(@PathVariable("appId") Integer appId, @RequestBody AppUpdateParam updateParam) {
        updateParam.setAppId(appId);
        return appService.updateAppInfo(updateParam);
    }

}
