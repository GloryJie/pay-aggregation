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

import com.gloryjie.pay.base.enums.MqDelayMsgLevel;
import com.gloryjie.pay.base.enums.error.CommonErrorEnum;
import com.gloryjie.pay.base.exception.error.BusinessException;
import com.gloryjie.pay.base.exception.error.ExternalException;
import com.gloryjie.pay.base.exception.error.SystemException;
import com.gloryjie.pay.base.util.BeanConverter;
import com.gloryjie.pay.base.util.idGenerator.IdFactory;
import com.gloryjie.pay.channel.dto.ChannelPayDto;
import com.gloryjie.pay.channel.dto.ChannelPayQueryDto;
import com.gloryjie.pay.channel.dto.ChannelPayQueryResponse;
import com.gloryjie.pay.channel.dto.param.ChargeCreateParam;
import com.gloryjie.pay.channel.dto.response.ChannelPayResponse;
import com.gloryjie.pay.channel.error.ChannelError;
import com.gloryjie.pay.channel.service.ChannelGatewayService;
import com.gloryjie.pay.trade.dao.ChargeDao;
import com.gloryjie.pay.trade.dto.ChargeDto;
import com.gloryjie.pay.trade.dto.RefreshChargeDto;
import com.gloryjie.pay.trade.enums.ChannelStatusToChargeStatus;
import com.gloryjie.pay.trade.enums.ChargeStatus;
import com.gloryjie.pay.trade.error.TradeError;
import com.gloryjie.pay.trade.model.Charge;
import com.gloryjie.pay.trade.mq.TradeMqProducer;
import com.gloryjie.pay.trade.task.ChargeQueryExecutors;
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

    @Value("${pay.trade.defaultExpireTime:120}")
    private Long defaultExpireTime;

    @Autowired
    private ChannelGatewayService channelGatewayService;

    @Autowired
    private ChargeDao chargeDao;

    @Autowired
    private TradeMqProducer mqProducer;

    @Autowired
    private ChargeQueryExecutors chargeQueryExecutors;

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

        // 轮询查询支付状态
        chargeQueryExecutors.executeQueryTask(charge.getChargeNo(), charge.getChannel(), charge.getTimeExpire());

        return charge;
    }


    /**
     * 关闭支付单
     *
     * @param chargeNo
     */
    public void closeCharge(String chargeNo) {
        Charge charge = chargeDao.load(chargeNo);
        if (charge == null || ChargeStatus.WAIT_PAY != charge.getStatus()) {
            return;
        }
        charge.setStatus(ChargeStatus.CLOSED);
        // 若更新失败，则再次异步关单
        if (chargeDao.update(charge) <= 0) {
            mqProducer.sendTimingCloseMsg(charge.getChargeNo(), MqDelayMsgLevel.FIRST);
        }
        log.info("timing close charge={} success", chargeNo);
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
            boolean compareResult = charge.getTimeCreated().plusMinutes(charge.getTimeExpire()).isBefore(LocalDateTime.now());
            // 时间超过，则主动关单
            if (ChargeStatus.WAIT_PAY == charge.getStatus() && compareResult) {
                charge.setStatus(ChargeStatus.CLOSED);
                chargeDao.update(charge);
            }
            return charge;
        }

        if (checkChargeStatusChange(charge.getStatus(), refreshChargeDto.getStatus())) {
            if (!charge.getAmount().equals(refreshChargeDto.getAmount())) {
                // 金额不一致异常较为严重,涉及资金安全,有可能为系统内部计算错误
                log.error("refresh charge={} error, total amount not consistent, old={}, new={}", charge.getChargeNo(), charge.getAmount(), refreshChargeDto.getAmount());
                throw SystemException.create(CommonErrorEnum.INTERNAL_SYSTEM_ERROR, "total amount not consistent");
            }

            charge.setStatus(refreshChargeDto.getStatus());
            if (ChargeStatus.SUCCESS == refreshChargeDto.getStatus()) {
                charge.setPlatformTradeNo(refreshChargeDto.getPlatformTradeNo());
                charge.setActualAmount(refreshChargeDto.getActualAmount());
                charge.setTimePaid(refreshChargeDto.getTimePaid());
            }
            // 更新数据库
            int result = chargeDao.update(charge);
            if (result <= 0) {
                throw SystemException.create(TradeError.UPDATE_CHARGE_ERROR);
            }
        }

        // TODO: 2019/1/13 若状态为成功,需要异步记录流水
        if (ChargeStatus.SUCCESS == charge.getStatus()) {
            mqProducer.sendChargeSuccessMsg(BeanConverter.covertIgnore(charge, ChargeDto.class));
        }

        return charge;
    }


    /**
     * 从渠道查询支付状态, 并根据查询结果刷新支付单
     *
     * @param charge
     * @return
     */
    public Charge queryChannel(Charge charge) {
        ChannelPayQueryResponse queryResponse = channelGatewayService.queryPayment(BeanConverter.covert(charge, ChannelPayQueryDto.class));
        RefreshChargeDto refreshChargeDto = generateRefreshChargeDto(charge, queryResponse);
        return refreshCharge(refreshChargeDto, charge);
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

    /**
     * 生成并初始化支付单数据
     *
     * @param createParam
     * @return
     */
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
     * 生成刷新支付单所需要的数据
     *
     * @param charge
     * @param queryResponse
     * @return
     */
    private RefreshChargeDto generateRefreshChargeDto(Charge charge, ChannelPayQueryResponse queryResponse) {
        RefreshChargeDto refreshChargeDto = new RefreshChargeDto();
        refreshChargeDto.setChargeNo(charge.getChargeNo());
        refreshChargeDto.setAppId(charge.getAppId());
        refreshChargeDto.setChannel(charge.getChannel());

        if (queryResponse.isSuccess()) {
            refreshChargeDto.setAmount(queryResponse.getAmount());
            refreshChargeDto.setActualAmount(queryResponse.getActualAmount());
            refreshChargeDto.setTimePaid(queryResponse.getTimePaid());
            refreshChargeDto.setPlatformTradeNo(queryResponse.getPlatformTradeNo());
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
