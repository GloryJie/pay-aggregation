/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.trade.manager
 *   Date Created: 2019/3/14
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2019/3/14      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.trade.manager;

import com.gloryjie.pay.base.enums.error.CommonErrorEnum;
import com.gloryjie.pay.base.exception.error.SystemException;
import com.gloryjie.pay.trade.dao.ChargeDao;
import com.gloryjie.pay.trade.dao.RefundDao;
import com.gloryjie.pay.trade.model.Charge;
import com.gloryjie.pay.trade.model.Refund;
import com.gloryjie.pay.trade.mq.TradeMqProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 交易的事物处理
 *
 * @author Jie
 * @since
 */
@Slf4j
@Component
public class TradeManager {

    @Autowired
    private RefundDao refundDao;

    @Autowired
    private ChargeDao chargeDao;

    @Autowired
    private TradeMqProducer tradeMqProducer;

    @Transactional(rollbackFor = Exception.class)
    public Refund refundTransactional(Refund refund, Charge charge) {
        int refundCount = refundDao.insert(refund);
        // 避免不必要的字段更新
        Charge updateChargeModel = new Charge();
        updateChargeModel.setChargeNo(charge.getChargeNo());
        updateChargeModel.setStatus(charge.getStatus());
        updateChargeModel.setVersion(charge.getVersion());
        int chargeCount = chargeDao.update(updateChargeModel);

        if (refundCount < 1 || chargeCount < 1) {
            throw SystemException.create(CommonErrorEnum.SYSTEM_BUSY_ERROR);
        }

        // 异步退款
        if (!tradeMqProducer.sendRefundMsg(refund.getRefundNo())) {
            log.error("send refund msg to mq fail,appId={} orderNo={},chargeNo={}", refund.getAppId(), refund.getOrderNo(), refund.getChargeNo());
            throw SystemException.create(CommonErrorEnum.SYSTEM_BUSY_ERROR);
        }
        return refund;
    }


    /**
     * 重新退款事务
     *
     * @param refund
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Refund reRefundTransactional(Refund refund) {
        Refund updateRefundModel = new Refund();
        updateRefundModel.setRefundNo(refund.getRefundNo());
        updateRefundModel.setStatus(refund.getStatus());
        updateRefundModel.setVersion(refund.getVersion());
        if (refundDao.update(updateRefundModel) != 1) {
            throw SystemException.create(CommonErrorEnum.SYSTEM_BUSY_ERROR);
        }

        // 异步退款
        if (!tradeMqProducer.sendRefundMsg(refund.getRefundNo())) {
            log.error("send refund msg to mq fail,appId={} orderNo={},chargeNo={}", refund.getAppId(), refund.getOrderNo(), refund.getChargeNo());
            throw SystemException.create(CommonErrorEnum.SYSTEM_BUSY_ERROR);
        }
        return refund;
    }

}
