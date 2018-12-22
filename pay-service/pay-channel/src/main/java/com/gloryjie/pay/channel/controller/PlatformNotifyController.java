/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.channel.controller
 *   Date Created: 2018/12/2
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2018/12/2      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.channel.controller;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 接受支付渠道异步通知
 * @author Jie
 * @since
 */
@RestController
@RequestMapping("/platform/notify/")
public class PlatformNotifyController {

    @PostMapping("/alipay")
    public String alipayNotify(@RequestParam Map<String,String> param){
        System.out.println(param);
        return "success";
    }

}
