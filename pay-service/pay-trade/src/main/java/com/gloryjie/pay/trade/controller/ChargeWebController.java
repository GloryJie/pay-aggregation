/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.trade.controller
 *   Date Created: 2019/2/6
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2019/2/6      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.trade.controller;

import com.github.pagehelper.PageInfo;
import com.gloryjie.pay.trade.dto.ChargeDto;
import com.gloryjie.pay.trade.dto.RefundDto;
import com.gloryjie.pay.trade.dto.param.ChargeQueryParam;
import com.gloryjie.pay.trade.dto.param.RefundQueryParam;
import com.gloryjie.pay.trade.service.ChargeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author Jie
 * @since
 */
@RestController
@RequestMapping("/web")
public class ChargeWebController {

    @Autowired
    private ChargeService chargeService;


    /**
     * 根据APPID来获取支付单数据
     *
     * @return
     */
    @GetMapping("/charge/list")
    public PageInfo<ChargeDto> getChargeList(@Valid @ModelAttribute ChargeQueryParam queryParam) {
        return chargeService.queryPaymentList(queryParam);
    }

    /**
     * 根据APPID来获取退款单数据
     *
     * @return
     */
    @RequestMapping("/refund/list")
    public PageInfo<RefundDto> getRefundList(@Valid @ModelAttribute RefundQueryParam queryParam) {
        return chargeService.queryRefundList(queryParam);
    }


}
