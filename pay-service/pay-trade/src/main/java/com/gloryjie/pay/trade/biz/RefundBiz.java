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
import com.gloryjie.pay.trade.dao.RefundDao;
import com.gloryjie.pay.trade.dto.RefreshRefundDto;
import com.gloryjie.pay.trade.dto.param.RefundParam;
import com.gloryjie.pay.trade.enums.RefundStatus;
import com.gloryjie.pay.trade.error.TradeError;
import com.gloryjie.pay.trade.model.Charge;
import com.gloryjie.pay.trade.model.Refund;
import com.gloryjie.pay.trade.mq.TradeMqProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
    private TradeMqProducer tradeMqProducer;

    @Autowired
    private ChannelGatewayService channelGatewayService;

    @Transactional(rollbackFor = Exception.class)
    public Refund asyncRefund(Charge charge, RefundParam refundParam) {
        List<Refund> refundList = refundDao.getByAppIdAndChargeNo(refundParam.getAppId(), refundParam.getChargeNo());
        /*
         * 退款分三种情况
         * 1. 有退款单号，则进行重新退款
         * 2. 无退款金额，则进行全部退款
         * 3. 有退款金额，则进行部分退款
         */
        Refund targetRefund = null;

        if (StringUtils.isNotBlank(refundParam.getRefundNo())) {
            /*1. 有退款单号，则进行重新退款*/
            for (Refund refund : refundList) {
                targetRefund = refundParam.getRefundNo().equals(refund.getRefundNo()) ? refund : null;
            }
            if (targetRefund == null || RefundStatus.FAILURE != targetRefund.getStatus()) {
                throw BusinessException.create(TradeError.REFUND_NOT_EXISTS);
            }
            // 状态更新
            targetRefund.setStatus(RefundStatus.PROCESSING);
            refundDao.update(targetRefund);
        } else {
            Long refundAmount;
            if (refundParam.getAmount() == null) {
                /*2. 无退款金额，则进行全部退款*/
                if (!refundList.isEmpty()) {
                    throw BusinessException.create(TradeError.REFUND_EXISTS, "不允许全部退款");
                }
                refundAmount = charge.getActualAmount();
            } else {
                /*3. 有退款金额，则进行部分退款*/
                Long existsRefundAmount = 0L;
                for (Refund refund : refundList) {
                    existsRefundAmount += refund.getAmount();
                }
                // 退款金额超支付单金额
                if (existsRefundAmount + refundParam.getAmount() > charge.getActualAmount()) {
                    throw BusinessException.create(TradeError.REFUND_AMOUNT_OUT_RANGE);
                }
                refundAmount = refundParam.getAmount();
            }

            Refund refund = BeanConverter.covert(refundParam, Refund.class);
            refund.setAmount(refundAmount);
            refund.setRefundNo(IdFactory.generateStringId());
            refund.setCurrency(DefaultConstant.CURRENCY);
            refund.setVersion(0);
            refund.setChannel(charge.getChannel());
            refund.setStatus(RefundStatus.PROCESSING);

            refundDao.insert(refund);

            targetRefund = refund;
        }

        // 异步退款
        if (!tradeMqProducer.sendRefundMsg(targetRefund.getRefundNo())) {
            // 发送消息失败则告知系统繁忙
            log.warn("send refund msg to mq fail,appId={} orderNo={},chargeNo={}", refundParam.getAppId(), refundParam.getOrderNo(), refundParam.getChargeNo());
            throw SystemException.create(CommonErrorEnum.SYSTEM_BUSY_ERROR);
        }
        return targetRefund;
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
        ChannelRefundDto channelRefundDto = BeanConverter.covert(refund, ChannelRefundDto.class);
        ChannelRefundResponse refundResponse = channelGatewayService.refund(channelRefundDto);
        RefreshRefundDto refreshRefundDto = BeanConverter.covert(refund,RefreshRefundDto.class);
        if (refundResponse.isSuccess()){
            refreshRefundDto.setPlatformTradeNo(refundResponse.getPlatformTradeNo());
            refreshRefundDto.setTimeSucceed(refundResponse.getTimeSucceed());
        }else{
            refreshRefundDto.setFailureCode(refundResponse.getSubCode());
            refreshRefundDto.setFailureMsg(refundResponse.getSubMsg());
        }

        this.refreshRefund(refreshRefundDto,refund);
    }

    /**
     * 刷新退款单
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
