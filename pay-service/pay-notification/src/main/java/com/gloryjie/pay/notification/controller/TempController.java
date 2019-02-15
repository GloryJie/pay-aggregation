/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.trade.controller
 *   Date Created: 2019/2/9
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2019/2/9      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.notification.controller;

import com.gloryjie.pay.base.util.JsonUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author Jie
 * @since
 */
@RestController
@RequestMapping("/web")
public class TempController {

    @PostMapping("/login")
    public Map login() {
        String json = "{\n" +
                "    \"token\": \"admin\"\n" +
                "}";
        return JsonUtil.parse(json, Map.class);
    }

    @GetMapping("/get_info")
    public Map getInfo() {
        String json = "{\n" +
                "    \"name\": \"admin\",\n" +
                "    \"user_id\": \"2\",\n" +
                "    \"access\": [\n" +
                "        \"admin\"\n" +
                "    ],\n" +
                "    \"token\": \"admin\",\n" +
                "    \"avator\": \"https://avatars0.githubusercontent.com/u/20942571?s=460&v=4\"\n" +
                "}";
        return JsonUtil.parse(json, Map.class);
    }

}
