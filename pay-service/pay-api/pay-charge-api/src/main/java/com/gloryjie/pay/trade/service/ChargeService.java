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
import com.gloryjie.pay.channel.enums.ChannelType;
import com.gloryjie.pay.channel.enums.PlatformType;
import com.gloryjie.pay.trade.dto.ChargeDto;
import com.gloryjie.pay.trade.dto.RefundDto;
import com.gloryjie.pay.trade.dto.StatCountDto;
import com.gloryjie.pay.trade.dto.param.ChargeQueryParam;
import com.gloryjie.pay.trade.dto.param.RefundParam;
import com.gloryjie.pay.trade.dto.param.RefundQueryParam;
import com.gloryjie.pay.trade.enums.ChargeStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 支付单接口
 *
 * @author Jie
 * @since 0.1
 */
public interface ChargeService {


    /**
     * 发起支付
     *
     * @param createParam
     * @return
     */
    ChargeDto pay(ChargeCreateParam createParam);

    /**
     * 支付查询
     *
     * @param appId
     * @param chargeNo
     * @return
     */
    ChargeDto queryPayment(Integer appId, String chargeNo);

    /**
     * 发起退款
     *
     * @param refundParam
     * @return
     */
    RefundDto refund(RefundParam refundParam);

    /**
     * 退款查询
     *
     * @param appId
     * @param chargeNo
     * @param refundNo
     */
    List<RefundDto> queryRefund(Integer appId, String chargeNo, String refundNo);

    /**
     * 处理支付的异步通知
     *
     * @param platformType
     * @param param
     * @return
     */
    boolean handleChargeAsyncNotify(PlatformType platformType, Map<String, String> param);

    /**
     * 处理退款的异步通知结果
     *
     * @param platformType
     * @param param
     * @return
     */
    boolean handleRefundAsyncNotify(PlatformType platformType, Map<String, String> param);

    /**
     * 查询支付列表
     *
     * @param queryParam
     * @return
     */
    PageInfo<ChargeDto> queryPaymentList(ChargeQueryParam queryParam);

    /**
     * 查询退款列表
     *
     * @param queryParam
     * @return
     */
    PageInfo<RefundDto> queryRefundList(RefundQueryParam queryParam);


    /************************统计************************/

    /**
     * 统计给定时间范围内的状态统计, list中第一个为总笔数, 第二个为总金额
     *
     * @param appId
     * @param minTime
     * @param maxTime
     * @return
     */
    Map<ChargeStatus, StatCountDto> countPlatformTrade(Integer appId, LocalDateTime minTime, LocalDateTime maxTime);

    /**
     * 统计单个应用的一天交易
     *
     * @param appId
     * @param status
     * @param date
     * @return
     */
    StatCountDto countSubAppTradeInDay(Integer appId, ChargeStatus status, LocalDate date);

    /**
     * 统计支付渠道一天的交易
     *
     * @return
     */
    Map<ChannelType, StatCountDto> countAllChannelTradeInDay(Integer appId, ChargeStatus status, LocalDate date);
}
