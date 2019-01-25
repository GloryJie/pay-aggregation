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

import com.gloryjie.pay.base.constant.DefaultConstant;
import com.gloryjie.pay.base.enums.error.CommonErrorEnum;
import com.gloryjie.pay.base.exception.error.BusinessException;
import com.gloryjie.pay.base.exception.error.SystemException;
import com.gloryjie.pay.base.util.BeanConverter;
import com.gloryjie.pay.base.util.idGenerator.IdFactory;
import com.gloryjie.pay.channel.dto.ChannelPayQueryDto;
import com.gloryjie.pay.channel.dto.ChannelPayQueryResponse;
import com.gloryjie.pay.channel.dto.ChannelRefundDto;
import com.gloryjie.pay.channel.dto.param.ChargeCreateParam;
import com.gloryjie.pay.channel.dto.response.ChannelRefundResponse;
import com.gloryjie.pay.channel.enums.ChannelType;
import com.gloryjie.pay.channel.service.ChannelGatewayService;
import com.gloryjie.pay.trade.biz.ChargeBiz;
import com.gloryjie.pay.trade.dao.ChargeDao;
import com.gloryjie.pay.trade.dao.RefundDao;
import com.gloryjie.pay.trade.dto.ChargeDto;
import com.gloryjie.pay.trade.dto.RefreshChargeDto;
import com.gloryjie.pay.trade.dto.RefundDto;
import com.gloryjie.pay.trade.dto.param.RefundParam;
import com.gloryjie.pay.trade.enums.ChannelStatusToChargeStatus;
import com.gloryjie.pay.trade.enums.ChargeStatus;
import com.gloryjie.pay.trade.enums.RefundStatus;
import com.gloryjie.pay.trade.error.TradeError;
import com.gloryjie.pay.trade.model.Charge;
import com.gloryjie.pay.trade.model.Refund;
import com.gloryjie.pay.trade.mq.TradeMqProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.BooleanSupplier;

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
    private TradeMqProducer tradeMqProducer;


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

        // TODO: 2018/12/22 需要定时关单


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
            throw BusinessException.create(TradeError.CHARGE_NOT_EXISTS);
        }
        List<Refund> refundList = refundDao.getByAppIdAndChargeNo(refundParam.getAppId(), refundParam.getChargeNo());
        Long existsRefundAmount = 0L;
        // 无论是否成功的退款单的金额都相加
        for (Refund refund : refundList) {
            existsRefundAmount += refund.getAmount();
        }
        // TODO: 2019/1/24 和实际金额对比还是总金额对比有待确定
        // 退款金额超支付单金额
        if (existsRefundAmount + refundParam.getAmount() > charge.getActualAmount()) {
            throw BusinessException.create(TradeError.REFUND_AMOUNT_OUT_RANGE);
        }

        // TODO: 2019/1/24 入库和发送mq消息需要在一个事务中
        // 入库
        Refund refund = BeanConverter.covert(refundParam, Refund.class);
        refund.setRefundNo(IdFactory.generateStringId());
        refund.setCurrency(DefaultConstant.CURRENCY);
        refund.setVersion(0);
        refund.setChannel(charge.getChannel());
        refund.setStatus(RefundStatus.PROCESSING);

        refundDao.insert(refund);

        // 异步退款
        ChannelRefundDto channelRefundDto = BeanConverter.covert(refund, ChannelRefundDto.class);
        if (!tradeMqProducer.sendRefundMsg(channelRefundDto)) {
            // 发送消息失败则告知系统繁忙
            log.warn("send refund msg to mq fail,appId={} orderNo={},chargeNo={}", refundParam.getAppId(), refundParam.getOrderNo(), refundParam.getChargeNo());
            throw SystemException.create(CommonErrorEnum.SYSTEM_BUSY_ERROR);
        }

        return BeanConverter.covert(refund, RefundDto.class);
    }

    @Override
    public RefundDto queryRefund(Integer appId, String chargeNo, String refundNo) {
        return null;
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

        refreshChargeDto.setAmount(queryResponse.getAmount());
        refreshChargeDto.setActualAmount(queryResponse.getActualAmount());
        refreshChargeDto.setTimePaid(queryResponse.getTimePaid());
        // 渠道状态转换为系统定义状态
        ChargeStatus status = ChannelStatusToChargeStatus.switchStatus(charge.getChannel(), queryResponse.getStatus());
        refreshChargeDto.setStatus(status);

        return refreshChargeDto;
    }

}
