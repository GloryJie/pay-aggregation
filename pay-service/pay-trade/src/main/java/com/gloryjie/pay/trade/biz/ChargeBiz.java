/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.trade.biz
 *   Date Created: 2019/1/13
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2019/1/13      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.trade.biz;

import com.gloryjie.pay.base.exception.error.BusinessException;
import com.gloryjie.pay.base.exception.error.ExternalException;
import com.gloryjie.pay.base.exception.error.SystemException;
import com.gloryjie.pay.base.util.BeanConverter;
import com.gloryjie.pay.base.util.idGenerator.IdFactory;
import com.gloryjie.pay.channel.dto.ChannelPayDto;
import com.gloryjie.pay.channel.dto.param.ChargeCreateParam;
import com.gloryjie.pay.channel.dto.response.ChannelPayResponse;
import com.gloryjie.pay.channel.error.ChannelError;
import com.gloryjie.pay.channel.service.ChannelGatewayService;
import com.gloryjie.pay.trade.dao.ChargeDao;
import com.gloryjie.pay.trade.dto.RefreshChargeDto;
import com.gloryjie.pay.trade.enums.ChargeStatus;
import com.gloryjie.pay.trade.error.TradeError;
import com.gloryjie.pay.trade.model.Charge;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 处理支付单相关的业务
 *
 * @author Jie
 * @since 0.1
 */
@Slf4j
@Component
public class ChargeBiz {

    @Value("${pay.charge.defaultExpireTime:120}")
    private Long defaultExpireTime;

    @Autowired
    private ChannelGatewayService channelGatewayService;

    @Autowired
    private ChargeDao chargeDao;

    /**
     * 创建支付单并分发渠道支付
     *
     * @param param 支付参数
     * @return charge
     */
    public Charge createChargeAndDistribute(ChargeCreateParam param) {
        // 参数默认值
        param.setServiceAppId(param.getServiceAppId() == null ? param.getAppId() : param.getServiceAppId());
        param.setTimeExpire(param.getTimeExpire() == null ? defaultExpireTime : param.getTimeExpire());

        // 生成charge对象
        Charge charge = generateCharge(param);

        // 缓存chargeNo和APPId的关系

        // 分发到支付网关
        ChannelPayDto payDto = BeanConverter.covert(charge, ChannelPayDto.class);
        ChannelPayResponse payResponse = channelGatewayService.pay(payDto);
        if (payResponse.isSuccess()) {
            // 网关支付
            if (param.getChannel().isGateway()) {
                charge.setCredential(payResponse.getCredential());
            }
        } else {
            throw ExternalException.create(ChannelError.PAY_PLATFORM_ERROR, payResponse.getMsg());
        }
        // 入库
        chargeDao.insert(charge);

        return charge;
    }

    private Charge generateCharge(ChargeCreateParam createParam) {
        Charge charge = BeanConverter.covert(createParam, Charge.class);
        charge.setChargeNo(IdFactory.generateStringId());
        charge.setVersion(0);
        charge.setLiveMode(createParam.getLiveMode());
        charge.setStatus(ChargeStatus.WAIT_PAY);
        charge.setTimeCreated(LocalDateTime.now());
        return charge;
    }


    /**
     * 状态变更需要刷新支付单,商户查询,主动轮询,渠道通知等需要调用
     */
    public Charge refreshCharge(RefreshChargeDto refreshChargeDto, Charge charge) {
        charge = charge == null ? chargeDao.getByAppIdAndChargeNo(refreshChargeDto.getAppId(), refreshChargeDto.getChargeNo()) : charge;
        if (charge == null) {
            throw BusinessException.create(TradeError.CHARGE_NOT_EXISTS);
        }

        // 状态不变则直接返回
        if (refreshChargeDto.getStatus() == charge.getStatus()) {
//            if (!charge.getAmount().equals(refreshChargeDto.getAmount())) {
//                // 金额不一致异常较为严重,涉及资金安全,有可能为系统内部计算错误
//                log.error("refresh charge={} error, total amount not consistent, old={}, new={}", charge.getChargeNo(), charge.getAmount(), refreshChargeDto.getAmount());
//                throw SystemException.create(CommonErrorEnum.INTERNAL_SYSTEM_ERROR, "total amount not consistent");
//            }
            return charge;
        }

        if (checkChargeStatusChange(charge.getStatus(), refreshChargeDto.getStatus())) {
            charge.setStatus(refreshChargeDto.getStatus());
            charge.setPlatformTradeNo(refreshChargeDto.getPlatformTradeNo());
            charge.setActualAmount(refreshChargeDto.getActualAmount());
            charge.setTimePaid(refreshChargeDto.getTimePaid());
            // 更新数据库
            int result = chargeDao.update(charge);
            if (result <= 0) {
                throw SystemException.create(TradeError.UPDATE_CHARGE_ERROR);
            }
        }

        // TODO: 2019/1/13 若状态为成功,需要异步记录流水
        return charge;
    }

    /**
     * 检查支付单状态变化的可行性
     *
     * @param oldStatus
     * @param newStatus
     * @return
     */
    private boolean checkChargeStatusChange(ChargeStatus oldStatus, ChargeStatus newStatus) {
        if (oldStatus == ChargeStatus.WAIT_PAY) {
            return newStatus == ChargeStatus.SUCCESS || newStatus == ChargeStatus.CLOSED || newStatus == ChargeStatus.FAILURE;
        }
        if (oldStatus == ChargeStatus.CLOSED) {
            return newStatus == ChargeStatus.SUCCESS;
        }
        if (oldStatus == ChargeStatus.SUCCESS) {
            return newStatus == ChargeStatus.CLOSED || newStatus == ChargeStatus.EXISTS_REFUND;
        }
        return false;
    }
}
