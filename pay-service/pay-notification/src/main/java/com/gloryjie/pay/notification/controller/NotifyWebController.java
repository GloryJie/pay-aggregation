/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.notification.controller
 *   Date Created: 2019/2/7
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2019/2/7      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.notification.controller;

import com.github.pagehelper.PageInfo;
import com.gloryjie.pay.base.response.Response;
import com.gloryjie.pay.notification.dto.EventNotifyDto;
import com.gloryjie.pay.notification.dto.EventSubscriptionDto;
import com.gloryjie.pay.notification.enums.EventType;
import com.gloryjie.pay.notification.service.EventNotifyService;
import com.gloryjie.pay.notification.service.EventSubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Jie
 * @since
 */
@RestController
@RequestMapping("/web/app")
public class NotifyWebController {

    @Autowired
    private EventSubscriptionService subscriptionService;

    @Autowired
    private EventNotifyService eventNotifyService;

    @GetMapping("/{appId}/subscription")
    public Response<Map<EventType, EventSubscriptionDto>> getAllSubscribeEvent(@PathVariable("appId") Integer appId) {
        List<EventSubscriptionDto> dtoList = subscriptionService.queryAllSubscribeEvent(appId);
        return Response.success(dtoList.stream().collect(Collectors.toMap(EventSubscriptionDto::getEventType, item -> item)));
    }

    @PostMapping("/{appId}/subscription")
    public Response<EventSubscriptionDto> addEvent(@PathVariable("appId") Integer appId, @Valid @RequestBody EventSubscriptionDto dto) {
        dto.setAppId(appId);
        return Response.success(subscriptionService.subscribeEvent(dto));
    }

    @DeleteMapping("/{appId}/subscription/{eventType}")
    public Response<Boolean> cancelEvent(@PathVariable("appId") Integer appId, @PathVariable("eventType") EventType eventType) {
        return Response.success(subscriptionService.cancelSubscribeEvent(appId, eventType));
    }

    @GetMapping("/{appId}/record")
    public Response<PageInfo<EventNotifyDto>> getNotifyRecordList(@PathVariable("appId") Integer appId,
                                                        @RequestParam(value = "startPage", required = false, defaultValue = "1") Integer startPage,
                                                        @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
        return Response.success(eventNotifyService.getRecord(appId, startPage, pageSize));
    }

}
