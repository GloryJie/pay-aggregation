/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.trade
 *   Date Created: 2018/12/9
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2018/12/9      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.trade.controller;

import com.gloryjie.pay.channel.dto.param.ChargeCreateParam;
import com.gloryjie.pay.trade.api.ChargeControllerApi;
import com.gloryjie.pay.trade.dto.ChargeDto;
import com.gloryjie.pay.trade.dto.RefundDto;
import com.gloryjie.pay.trade.dto.param.RefundParam;
import com.gloryjie.pay.trade.service.ChargeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @author Jie
 * @since
 */
@RequestMapping("/api/v1")
@RestController
public class ChargeApiController implements ChargeControllerApi {

    @Autowired
    private ChargeService chargeService;

    /**
     * 创建支付单，返回渠道支付凭证
     *
     * @param createParam
     * @return
     */
    @PostMapping("/charge")
    public ChargeDto createCharge(@RequestBody @Valid ChargeCreateParam createParam) {
        return chargeService.pay(createParam);
    }

    /**
     * 查询支付单
     *
     * @param chargeNo 支付单号
     * @param appId    应用id
     * @return
     */
    @GetMapping("/charge/{chargeNo}")
    public ChargeDto queryCharge(@RequestHeader("appId") Integer appId, @PathVariable("chargeNo") String chargeNo) {
        return chargeService.queryPayment(appId, chargeNo);
    }

    /**
     * 对支付单进行退款
     *
     * @param chargeNo
     * @param refundParam
     * @return
     */
    @PostMapping("/charge/{chargeNo}/refund")
    public RefundDto refundCharge(@PathVariable("chargeNo") String chargeNo, @Validated @RequestBody RefundParam refundParam) {
        refundParam.setChargeNo(chargeNo);
        return chargeService.refund(refundParam);
    }

    /**
     * 查询单个退款单
     *
     * @param appId
     * @param chargeNo
     * @param refundNo
     * @return
     */
    @GetMapping("/charge/{chargeNo}/refund/{refundNo}")
    public RefundDto querySingleRefund(@RequestHeader("appId") Integer appId, @PathVariable("chargeNo") String chargeNo, @PathVariable("refundNo") String refundNo) {
        return chargeService.queryRefund(appId, chargeNo, refundNo).stream().findAny().orElse(new RefundDto());
    }

    /**
     * 查询支付单关联的所有退款单
     *
     * @param appId
     * @param chargeNo
     * @return
     */
    @GetMapping("/charge/{chargeNo}/refund")
    public List<RefundDto> queryChargeRefund(@RequestHeader("appId") Integer appId, @PathVariable("chargeNo") String chargeNo) {
        return chargeService.queryRefund(appId, chargeNo, null);
    }

}
