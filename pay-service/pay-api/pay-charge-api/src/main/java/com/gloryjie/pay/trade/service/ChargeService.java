/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.charge.service
 *   Date Created: 2018/12/9
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2018/12/9      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.trade.service;

import com.github.pagehelper.PageInfo;
import com.gloryjie.pay.channel.dto.param.ChargeCreateParam;
import com.gloryjie.pay.channel.enums.PlatformType;
import com.gloryjie.pay.trade.dto.ChargeDto;
import com.gloryjie.pay.trade.dto.RefundDto;
import com.gloryjie.pay.trade.dto.param.ChargeQueryParam;
import com.gloryjie.pay.trade.dto.param.RefundParam;
import com.gloryjie.pay.trade.dto.param.RefundQueryParam;

import java.util.List;
import java.util.Map;

/**
 * 支付单接口
 * @author Jie
 * @since 0.1
 */
public interface ChargeService {


    /**
     * 发起支付
     * @param createParam
     * @return
     */
    ChargeDto pay(ChargeCreateParam createParam);

    /**
     * 支付查询
     * @param appId
     * @param chargeNo
     * @return
     */
    ChargeDto queryPayment(Integer appId, String chargeNo);

    /**
     * 发起退款
     * @param refundParam
     * @return
     */
    RefundDto refund(RefundParam refundParam);

    /**
     * 退款查询
     * @param appId
     * @param chargeNo
     * @param refundNo
     */
    List<RefundDto> queryRefund(Integer appId, String chargeNo, String refundNo);

    /**
     * 处理支付的异步通知
     * @param platformType
     * @param param
     * @return
     */
    boolean handleChargeAsyncNotify(PlatformType platformType, Map<String,String> param);

    /**
     * 查询支付列表
     * @param queryParam
     * @return
     */
    PageInfo<ChargeDto> queryPaymentList(ChargeQueryParam queryParam);

    /**
     * 查询退款列表
     * @param queryParam
     * @return
     */
    PageInfo<RefundDto> queryRefundList(RefundQueryParam queryParam);

}
