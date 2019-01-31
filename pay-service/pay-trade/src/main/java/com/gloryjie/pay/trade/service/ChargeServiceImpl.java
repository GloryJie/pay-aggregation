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

import com.gloryjie.pay.base.enums.MqDelayMsgLevel;
import com.gloryjie.pay.base.exception.error.BusinessException;
import com.gloryjie.pay.base.util.BeanConverter;
import com.gloryjie.pay.channel.dto.ChannelPayQueryDto;
import com.gloryjie.pay.channel.dto.ChannelPayQueryResponse;
import com.gloryjie.pay.channel.dto.param.ChargeCreateParam;
import com.gloryjie.pay.channel.enums.ChannelType;
import com.gloryjie.pay.channel.service.ChannelGatewayService;
import com.gloryjie.pay.trade.biz.ChargeBiz;
import com.gloryjie.pay.trade.biz.RefundBiz;
import com.gloryjie.pay.trade.dao.ChargeDao;
import com.gloryjie.pay.trade.dao.RefundDao;
import com.gloryjie.pay.trade.dto.ChargeDto;
import com.gloryjie.pay.trade.dto.RefreshChargeDto;
import com.gloryjie.pay.trade.dto.RefundDto;
import com.gloryjie.pay.trade.dto.param.RefundParam;
import com.gloryjie.pay.trade.enums.ChannelStatusToChargeStatus;
import com.gloryjie.pay.trade.enums.ChargeStatus;
import com.gloryjie.pay.trade.error.TradeError;
import com.gloryjie.pay.trade.model.Charge;
import com.gloryjie.pay.trade.model.Refund;
import com.gloryjie.pay.trade.mq.TradeMqProducer;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * @author Jie
 * @since
 */
@Slf4j
@Service
public class ChargeServiceImpl implements ChargeService {

    @Autowired
    private ChargeDao chargeDao;

    @Autowired
    private RefundDao refundDao;

    @Autowired
    private ChannelGatewayService channelGatewayService;

    @Autowired
    private ChargeBiz chargeBiz;

    @Autowired
    private RefundBiz refundBiz;

    @Autowired
    private TradeMqProducer mqProducer;


    @Override
    public ChargeDto pay(ChargeCreateParam createParam) {
        ChannelType channel = createParam.getChannel();
        // 检查渠道额外参数是否正确
        channel.checkExtraParam(createParam.getExtra());
        // 检查支付单是否已存在
        List<Charge> chargeList = chargeDao.listByAppIdAndOrderNo(createParam.getAppId(), createParam.getOrderNo());
        Charge charge = checkChargeExist(chargeList, createParam);
        if (charge == null) {
            charge = chargeBiz.createChargeAndDistribute(createParam);
        }

        // 定时关单
        mqProducer.sendTimingCloseMsg(charge.getChargeNo(), MqDelayMsgLevel.computeLevel(charge.getTimeExpire() * 60));

        return BeanConverter.covert(charge, ChargeDto.class);
    }

    @Override
    public ChargeDto queryPayment(Integer appId, String chargeNo) {
        Charge charge = chargeDao.getByAppIdAndChargeNo(appId, chargeNo);
        if (charge == null) {
            return new ChargeDto();
        }
        ChargeDto chargeDto;
        // 若当前为待支付状态,则交由渠道进行查询
        if (ChargeStatus.WAIT_PAY.equals(charge.getStatus())) {
            // TODO: 2019/1/13 渠道查询需要限制,避免流量穿透
            ChannelPayQueryResponse queryResponse = channelGatewayService.queryPayment(BeanConverter.covert(charge, ChannelPayQueryDto.class));
            RefreshChargeDto refreshChargeDto = generateRefreshChargeDto(charge, queryResponse);
            charge = chargeBiz.refreshCharge(refreshChargeDto, charge);
        }
        chargeDto = BeanConverter.covert(charge, ChargeDto.class);
        // 若非待支付状态则不返回支付凭证
        if (!ChargeStatus.WAIT_PAY.equals(chargeDto.getStatus())) {
            chargeDto.setCredential(null);
        }
        return chargeDto;
    }


    @Override
    public RefundDto refund(RefundParam refundParam) {
        Charge charge = chargeDao.getByAppIdAndChargeNo(refundParam.getAppId(), refundParam.getChargeNo());
        if (charge == null || !charge.getStatus().canRefund()) {
            throw BusinessException.create(TradeError.CHARGE_NOT_EXISTS, "或状态不允许退款");
        }
        // 异步退款
        Refund refund = refundBiz.asyncRefund(charge, refundParam);
        // 更新支付单状态
        charge.setStatus(ChargeStatus.EXISTS_REFUND);
        chargeDao.update(charge);

        return BeanConverter.covert(refund, RefundDto.class);
    }

    @Override
    public List<RefundDto> queryRefund(@NonNull Integer appId, @NonNull String chargeNo, String refundNo) {
        // 简单处理，只从自己的数据库查询,因为部分渠道为同步退款
        if (StringUtils.isBlank(refundNo)) {
            List<Refund> refundList = refundDao.getByAppIdAndChargeNo(appId, chargeNo);
            return BeanConverter.batchCovert(refundList, RefundDto.class);
        }
        Refund refund = refundDao.getByAppIdAndRefundNo(appId, refundNo);
        return Collections.singletonList(BeanConverter.covert(refund, RefundDto.class));
    }


    /**
     * 检查订单存在性
     */
    private Charge checkChargeExist(List<Charge> chargeList, ChargeCreateParam param) {
        Charge existCharge = null;
        if (chargeList != null && chargeList.size() > 0) {
            for (Charge charge : chargeList) {
                // 存在已支付则抛异常
                if (charge.getStatus().isPaid()) {
                    throw BusinessException.create(TradeError.ORDER_ALREADY_PAY);
                }
                // 简单处理: 若渠道也相同,则认为是同一订单
                if (charge.getChannel().equals(param.getChannel())) {
                    existCharge = charge;
                }
            }

        }
        return existCharge;
    }

    private RefreshChargeDto generateRefreshChargeDto(Charge charge, ChannelPayQueryResponse queryResponse) {
        RefreshChargeDto refreshChargeDto = new RefreshChargeDto();
        refreshChargeDto.setChargeNo(charge.getChargeNo());
        refreshChargeDto.setAppId(charge.getAppId());
        refreshChargeDto.setChannel(charge.getChannel());

        if (queryResponse.isSuccess()) {
            refreshChargeDto.setAmount(queryResponse.getAmount());
            refreshChargeDto.setActualAmount(queryResponse.getActualAmount());
            refreshChargeDto.setTimePaid(queryResponse.getTimePaid());
        } else {
            refreshChargeDto.setAmount(charge.getAmount());
            refreshChargeDto.setFailureCode(queryResponse.getSubCode());
            refreshChargeDto.setFailureMsg(queryResponse.getSubMsg());
        }

        // 渠道状态转换为系统定义状态
        ChargeStatus status = ChannelStatusToChargeStatus.switchStatus(charge.getChannel(), queryResponse.getStatus());
        refreshChargeDto.setStatus(status);

        return refreshChargeDto;
    }

}
