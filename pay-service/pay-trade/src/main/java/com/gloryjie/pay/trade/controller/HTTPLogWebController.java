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
import org.springframework.beans.factory.annotation.Autowired;
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
    private MongoTemplate mongoTemplate;

    @GetMapping("/{appId}/log/notification")
    public Response<Map> getPlatformNotifyLog(@PathVariable("appId") String appId,
                                              @RequestParam(value = "startPage", required = false, defaultValue = "1") int startPage,
                                              @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize) {
        Query query = new Query();
        query.addCriteria(Criteria.where("appId").is(appId));
        Pageable pageable = PageRequest.of(startPage - 1, pageSize, new Sort(Sort.Direction.DESC, "_id"));
        query.with(pageable);
        List<Map> dataList = mongoTemplate.find(query, Map.class, "platform_notify_log");
        long allNum = mongoTemplate.count(query, "platform_notify_log");

        Map<String, Object> pageInfo = new HashMap<>(6);
        pageInfo.put("total", allNum);
        pageInfo.put("list", dataList);
        pageInfo.put("startPage", startPage);
        pageInfo.put("pageSize", pageSize);
        return Response.success(pageInfo);
    }

    @RequestMapping("/{appId}/log/req")
    public Response<Map> getApiReqLog(@PathVariable("appId") String appId,
                                              @RequestParam(value = "startPage", required = false, defaultValue = "1") int startPage,
                                              @RequestParam(value = "pageSize", required = false, defaultValue = "10") int pageSize) {
        Query query = new Query();
        query.addCriteria(Criteria.where("appId").is(appId));
        Pageable pageable = PageRequest.of(startPage - 1, pageSize, new Sort(Sort.Direction.DESC, "_id"));
        query.with(pageable);
        List<Map> dataList = mongoTemplate.find(query, Map.class, "api_req_log");
        long allNum = mongoTemplate.count(query, "api_req_log");

        Map<String, Object> pageInfo = new HashMap<>(6);
        pageInfo.put("total", allNum);
        pageInfo.put("list", dataList);
        pageInfo.put("startPage", startPage);
        pageInfo.put("pageSize", pageSize);
        return Response.success(pageInfo);
    }

}
