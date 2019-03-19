/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.trade.controller
 *   Date Created: 2019/3/19
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2019/3/19      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.trade.controller;

import com.gloryjie.pay.channel.enums.PlatformType;
import com.gloryjie.pay.trade.service.ChargeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author Jie
 * @since
 */
@RestController
@RequestMapping("/platform/notify")
public class PlatformNotifyController {

    public static final String ALIPAY_SUCCESS_RESPONSE = "success";

    public static final String UNIFORM_HANDLE_FAIL_FLAG = "false";

    @Autowired
    private ChargeService chargeService;

    /**
     * 接受支付宝异步通知
     *
     * @param param
     * @return
     */
    @PostMapping("/alipay")
    public String asyncNotify(@RequestParam Map<String, String> param) {
        boolean result = chargeService.handleChargeAsyncNotify(PlatformType.ALIPAY, param);
        return result ? ALIPAY_SUCCESS_RESPONSE : UNIFORM_HANDLE_FAIL_FLAG;
    }

}
