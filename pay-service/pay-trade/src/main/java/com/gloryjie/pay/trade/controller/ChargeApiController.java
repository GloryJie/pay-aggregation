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

import com.gloryjie.pay.base.enums.error.CommonErrorEnum;
import com.gloryjie.pay.base.exception.error.ExternalException;
import com.gloryjie.pay.base.response.Response;
import com.gloryjie.pay.channel.dto.param.ChargeCreateParam;
import com.gloryjie.pay.trade.api.ChargeControllerApi;
import com.gloryjie.pay.trade.dto.ChargeDto;
import com.gloryjie.pay.trade.dto.RefundDto;
import com.gloryjie.pay.trade.dto.param.RefundParam;
import com.gloryjie.pay.trade.service.ChargeService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
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
    public Response<ChargeDto> createCharge(@RequestBody @Valid ChargeCreateParam createParam) {
        createParam.setAppId(getValidAppId());
        return Response.success(chargeService.pay(createParam));
    }

    /**
     * 查询支付单
     *
     * @param chargeNo 支付单号
     * @return
     */
    @GetMapping("/charge/{chargeNo}")
    public Response<ChargeDto> queryCharge(@PathVariable("chargeNo") String chargeNo) {
        return Response.success(chargeService.queryPayment(getValidAppId(), chargeNo));
    }

    /**
     * 对支付单进行退款
     *
     * @param chargeNo
     * @param refundParam
     * @return
     */
    @PostMapping("/charge/{chargeNo}/refund")
    public Response<RefundDto> refundCharge(@PathVariable("chargeNo") String chargeNo, @Validated @RequestBody RefundParam refundParam) {
        refundParam.setChargeNo(chargeNo);
        refundParam.setAppId(getValidAppId());
        return Response.success(chargeService.refund(refundParam));
    }

    /**
     * 查询单个退款单
     *
     * @param chargeNo
     * @param refundNo
     * @return
     */
    @GetMapping("/charge/{chargeNo}/refund/{refundNo}")
    public Response<RefundDto> querySingleRefund(@PathVariable("chargeNo") String chargeNo, @PathVariable("refundNo") String refundNo) {
        return Response.success(chargeService.queryRefund(getValidAppId(), chargeNo, refundNo).stream().findAny().orElse(new RefundDto()));
    }

    /**
     * 查询支付单关联的所有退款单
     *
     * @param chargeNo
     * @return
     */
    @GetMapping("/charge/{chargeNo}/refund")
    public Response<List<RefundDto>> queryChargeRefund(@PathVariable("chargeNo") String chargeNo) {
        return Response.success(chargeService.queryRefund(getValidAppId(), chargeNo, null));
    }

    /**
     * 获取经过验证的appId
     *
     * @return
     */
    private Integer getValidAppId() {
        HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
        Object appId = request.getAttribute("appId");
        String appIdStr = request.getHeader("appId");
        if (appId == null && StringUtils.isBlank(appIdStr)) {
            throw ExternalException.create(CommonErrorEnum.ILLEGAL_ARGUMENT_ERROR, "appId不能为空");
        }
        return appId == null ? Integer.valueOf(appIdStr) : (Integer) appId;
    }

}
