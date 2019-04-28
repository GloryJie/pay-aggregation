/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.user.controller
 *   Date Created: 2019/3/13
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2019/3/13      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.user.controller;

import com.gloryjie.pay.base.response.Response;
import com.gloryjie.pay.base.util.JsonUtil;
import com.gloryjie.pay.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author Jie
 * @since
 */
@RestController
@RequestMapping("/web/user")
public class UserWebController {

    @Autowired
    private UserService userService;

    @GetMapping("/info")
    public Response<Map> getInfo() {
        String json = "{\n" +
                "    \"name\": \"admin\",\n" +
                "    \"user_id\": \"2\",\n" +
                "    \"access\": [\n" +
                "        \"admin\"\n" +
                "    ],\n" +
                "    \"token\": \"admin\",\n" +
                "    \"avator\": \"https://avatars0.githubusercontent.com/u/20942571?s=460&v=4\"\n" +
                "}";
        return Response.success(JsonUtil.parse(json, Map.class));
    }
}
