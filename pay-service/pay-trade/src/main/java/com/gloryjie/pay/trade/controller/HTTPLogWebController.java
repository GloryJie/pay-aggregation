/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.trade.controller
 *   Date Created: 2019/3/23
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2019/3/23      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.trade.controller;

import com.gloryjie.pay.base.response.Response;
import com.gloryjie.pay.log.http.enums.HttpLogType;
import com.gloryjie.pay.log.http.model.HttpLogRecord;
import com.gloryjie.pay.log.http.model.PageInfo;
import com.gloryjie.pay.log.http.model.param.LogQueryParam;
import com.gloryjie.pay.log.http.service.HttpLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Jie
 * @since
 */
@RestController
@RequestMapping("/web")
public class HTTPLogWebController {

    @Autowired
    private HttpLogService httpLogService;

    @GetMapping("/{appId}/log/notification")
    public Response<PageInfo<HttpLogRecord>> getPlatformNotifyLog(@PathVariable("appId") String appId,
                                                                  @RequestParam(value = "startPage", required = false, defaultValue = "1") int startPage,
                                                                  @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize) {
        LogQueryParam param = new LogQueryParam();
        param.setAppId(appId);
        param.setStartPage(startPage);
        param.setPageSize(pageSize);
        param.setType(HttpLogType.PLATFORM_NOTIFY_REQUEST.name().toLowerCase());
        return Response.success(httpLogService.find(param));
    }

    @RequestMapping("/{appId}/log/req")
    public Response<PageInfo<HttpLogRecord>> getApiReqLog(@PathVariable("appId") String appId,
                                              @RequestParam(value = "startPage", required = false, defaultValue = "1") int startPage,
                                              @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize) {

        LogQueryParam param = new LogQueryParam();
        param.setAppId(appId);
        param.setStartPage(startPage);
        param.setPageSize(pageSize);
        param.setType(HttpLogType.API_REQUEST.name().toLowerCase());
        return Response.success(httpLogService.find(param));
    }

}
