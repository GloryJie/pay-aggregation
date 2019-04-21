/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.trade.biz
 *   Date Created: 2019/1/23
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2019/1/23      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.trade.biz;

import com.gloryjie.pay.base.constant.DefaultConstant;
import com.gloryjie.pay.base.enums.error.CommonErrorEnum;
import com.gloryjie.pay.base.exception.error.BusinessException;
import com.gloryjie.pay.base.exception.error.SystemException;
import com.gloryjie.pay.base.util.BeanConverter;
import com.gloryjie.pay.base.util.idGenerator.IdFactory;
import com.gloryjie.pay.channel.dto.ChannelRefundDto;
import com.gloryjie.pay.channel.dto.response.ChannelRefundResponse;
import com.gloryjie.pay.channel.service.ChannelGatewayService;
import com.gloryjie.pay.trade.dao.ChargeDao;
import com.gloryjie.pay.trade.dao.RefundDao;
import com.gloryjie.pay.trade.dto.RefreshRefundDto;
import com.gloryjie.pay.trade.dto.RefundDto;
import com.gloryjie.pay.trade.dto.param.RefundParam;
import com.gloryjie.pay.trade.enums.ChargeStatus;
import com.gloryjie.pay.trade.enums.RefundStatus;
import com.gloryjie.pay.trade.error.TradeError;
import com.gloryjie.pay.trade.manager.TradeManager;
import com.gloryjie.pay.trade.model.Charge;
import com.gloryjie.pay.trade.model.Refund;
import com.gloryjie.pay.trade.mq.TradeMqProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 退款业务相关
 *
 * @author Jie
 * @since 0.1
 */
@Slf4j
@Component
public class RefundBiz {

    @Autowired
    private RefundDao refundDao;

    @Autowired
    private ChargeDao chargeDao;

    @Autowired
    private TradeMqProducer tradeMqProducer;

    @Autowired
    private ChannelGatewayService channelGatewayService;

    @Autowired
    private TradeManager tradeManager;


    public Refund asyncRefund(Charge charge, RefundParam refundParam) {
        List<Refund> refundList = refundDao.getByAppIdAndChargeNo(refundParam.getAppId(), refundParam.getChargeNo());
        /*
         * 退款分三种情况
         * 1. 有退款单号，则进行重新退款
         * 2. 无退款金额，则进行全部退款
         * 3. 有退款金额，则进行部分退款
         */

        if (StringUtils.isNotBlank(refundParam.getRefundNo())) {
            Refund targetRefund = null;
            /*1. 有退款单号，则进行重新退款*/
            for (Refund refund : refundList) {
                targetRefund = refundParam.getRefundNo().equals(refund.getRefundNo()) ? refund : null;
            }
            if (targetRefund == null || RefundStatus.FAILURE != targetRefund.getStatus()) {
                throw BusinessException.create(TradeError.REFUND_STATUS_NOT_SUPPORT, "已退款或处理中");
            }
            targetRefund.setStatus(RefundStatus.PROCESSING);
            // 重新退款事务
            return tradeManager.reRefundTransactional(targetRefund);
        } else {
            Long refundAmount;
            if (refundParam.getAmount() == null) {
                /*2. 无退款金额，则进行全部退款*/
                if (!refundList.isEmpty()) {
                    throw BusinessException.create(TradeError.REFUND_EXISTS, "不允许全部退款");
                }
                refundAmount = charge.getActualAmount();
                charge.setStatus(ChargeStatus.REFUND_COMPLETED);
            } else {
                /*3. 有退款金额，则进行部分退款*/
                long existsRefundAmount = 0L;
                for (Refund refund : refundList) {
                    existsRefundAmount += refund.getAmount();
                }
                // 退款金额超支付单金额
                if (existsRefundAmount + refundParam.getAmount() > charge.getActualAmount()) {
                    throw BusinessException.create(TradeError.REFUND_AMOUNT_OUT_RANGE);
                }
                ChargeStatus chargeStatus = (existsRefundAmount + refundParam.getAmount()) == charge.getActualAmount() ? ChargeStatus.REFUND_COMPLETED : ChargeStatus.EXISTS_REFUND;
                charge.setStatus(chargeStatus);
                refundAmount = refundParam.getAmount();
            }

            Refund refund = BeanConverter.covert(refundParam, Refund.class);
            refund.setAmount(refundAmount);
            refund.setSubject(charge.getSubject());
            refund.setRefundNo(IdFactory.generateStringId());
            refund.setCurrency(DefaultConstant.CURRENCY);
            refund.setVersion(0);
            refund.setTimeCreated(LocalDateTime.now());
            refund.setChannel(charge.getChannel());
            refund.setStatus(RefundStatus.PROCESSING);

            return tradeManager.refundTransactional(refund, charge);
        }
    }

    /**
     * 实际执行退款
     *
     * @param refundNo
     */
    public void actualRefund(String refundNo) {
        Refund refund = refundDao.load(refundNo);
        if (RefundStatus.PROCESSING != refund.getStatus()) {
            return;
        }
        // TODO: 2019/4/21 临时解决,可考虑MQ发送消息携带更多信息,或者Refund冗余交易平台流水号和支付单amount
        Charge charge = chargeDao.getByAppIdAndChargeNo(refund.getAppId(), refund.getChargeNo());
        ChannelRefundDto channelRefundDto = BeanConverter.covert(refund, ChannelRefundDto.class);
        channelRefundDto.setAmount(charge.getAmount());
        channelRefundDto.setPlatformTradeNo(charge.getPlatformTradeNo());
        ChannelRefundResponse refundResponse = channelGatewayService.refund(channelRefundDto);
        RefreshRefundDto refreshRefundDto = BeanConverter.covert(refund, RefreshRefundDto.class);
        if (refundResponse.isSuccess()) {
            refreshRefundDto.setStatus(RefundStatus.SUCCESS);
            refreshRefundDto.setPlatformTradeNo(refundResponse.getPlatformTradeNo());
            refreshRefundDto.setTimeSucceed(refundResponse.getTimeSucceed());
        } else {
            refreshRefundDto.setFailureCode(refundResponse.getSubCode());
            refreshRefundDto.setFailureMsg(refundResponse.getSubMsg());
        }

        this.refreshRefund(refreshRefundDto, refund);
    }

    /**
     * 刷新退款单
     *
     * @param refreshRefundDto
     * @param refund
     * @return
     */
    public Refund refreshRefund(RefreshRefundDto refreshRefundDto, Refund refund) {
        if (refreshRefundDto.getStatus() == refund.getStatus()) {
            return refund;
        }
        if (checkChargeStatusChange(refund.getStatus(), refreshRefundDto.getStatus())) {
            refund.setStatus(refreshRefundDto.getStatus());
            if (RefundStatus.SUCCESS == refreshRefundDto.getStatus()) {
                refund.setPlatformTradeNo(refreshRefundDto.getPlatformTradeNo());
                refund.setTimeSucceed(refreshRefundDto.getTimeSucceed());
            } else if (RefundStatus.FAILURE == refreshRefundDto.getStatus()) {
                refund.setFailureCode(refreshRefundDto.getFailureCode());
                refund.setFailureMsg(refreshRefundDto.getFailureMsg());
            }
            refundDao.update(refund);
        }

        // TODO: 2019/1/26 若状态成功，需要异步计流水
        if (RefundStatus.SUCCESS == refund.getStatus()) {
            tradeMqProducer.sendRefundSuccessMsg(BeanConverter.covert(refund, RefundDto.class));
        }

        return refund;
    }

    /**
     * 允许的状态转变
     *
     * @param oldStatus
     * @param newStatus
     * @return
     */
    private boolean checkChargeStatusChange(RefundStatus oldStatus, RefundStatus newStatus) {
        if (oldStatus == RefundStatus.PROCESSING) {
            return newStatus == RefundStatus.SUCCESS || newStatus == RefundStatus.FAILURE;
        }
        if (oldStatus == RefundStatus.FAILURE) {
            return newStatus == RefundStatus.PROCESSING || newStatus == RefundStatus.SUCCESS;
        }
        if (oldStatus == RefundStatus.SUCCESS) {
            return false;
        }
        return false;
    }
}
