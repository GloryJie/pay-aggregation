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

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.gloryjie.pay.base.enums.MqDelayMsgLevel;
import com.gloryjie.pay.base.exception.error.BusinessException;
import com.gloryjie.pay.base.util.BeanConverter;
import com.gloryjie.pay.channel.dto.ChannelPayQueryResponse;
import com.gloryjie.pay.channel.dto.param.ChargeCreateParam;
import com.gloryjie.pay.channel.dto.response.ChannelRefundResponse;
import com.gloryjie.pay.channel.enums.ChannelType;
import com.gloryjie.pay.channel.enums.PlatformType;
import com.gloryjie.pay.channel.service.ChannelGatewayService;
import com.gloryjie.pay.trade.biz.ChargeBiz;
import com.gloryjie.pay.trade.biz.RefundBiz;
import com.gloryjie.pay.trade.dao.ChargeDao;
import com.gloryjie.pay.trade.dao.RefundDao;
import com.gloryjie.pay.trade.dto.ChargeDto;
import com.gloryjie.pay.trade.dto.RefreshChargeDto;
import com.gloryjie.pay.trade.dto.RefreshRefundDto;
import com.gloryjie.pay.trade.dto.RefundDto;
import com.gloryjie.pay.trade.dto.param.ChargeQueryParam;
import com.gloryjie.pay.trade.dto.param.RefundParam;
import com.gloryjie.pay.trade.dto.param.RefundQueryParam;
import com.gloryjie.pay.trade.enums.ChargeStatus;
import com.gloryjie.pay.trade.enums.RefundStatus;
import com.gloryjie.pay.trade.error.TradeError;
import com.gloryjie.pay.trade.model.Charge;
import com.gloryjie.pay.trade.model.Refund;
import com.gloryjie.pay.trade.mq.TradeMqProducer;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
            return null;
        }
        ChargeDto chargeDto;
        // 若当前为待支付状态,则交由渠道进行查询
        if (ChargeStatus.WAIT_PAY.equals(charge.getStatus())) {
            // TODO: 2019/3/8 对外服务需要进行查询限制，避免流量穿透至支付平台
            charge = chargeBiz.queryChannel(charge);
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
        if (refund == null) {
            return new ArrayList<>();
        }
        return Collections.singletonList(BeanConverter.covert(refund, RefundDto.class));
    }

    @Override
    public boolean handleChargeAsyncNotify(PlatformType platformType, Map<String, String> param) {
        // 业务检查
        String chargeNo = getChargeNoFromNotifyParam(platformType, param);
        if (StringUtils.isBlank(chargeNo)) {
            return false;
        }
        Charge charge = chargeDao.load(chargeNo);
        if (charge == null) {
            log.warn("platform={} async trade notify, chargeNo={} not exist ", platformType.name(), chargeNo);
            return false;
        }
        log.info("ready to handle platform={} async trade notify, chargeNo={}", platformType.name(), chargeNo);
        if (ChargeStatus.SUCCESS == charge.getStatus()) {
            log.info("chargeNo={} status already SUCCESS,ignore platform={} async notify ", chargeNo, platformType.name());
            return true;
        }
        // 交给渠道网关处理
        ChannelPayQueryResponse response = channelGatewayService.handleTradeAsyncNotify(charge.getAppId(), charge.getChannel(), param);
        RefreshChargeDto refreshChargeDto = chargeBiz.generateRefreshChargeDto(charge, response);
        charge = chargeBiz.refreshCharge(refreshChargeDto, charge);
        log.info("handle platform={} async trade notify, chargeNo={} completed chargeStatus={}", platformType.name(), chargeNo, charge.getStatus().name());
        param.put("appId", charge.getAppId().toString());
        return ChargeStatus.SUCCESS == charge.getStatus();
    }

    @Override
    public boolean handleRefundAsyncNotify(PlatformType platformType, Map<String, String> param) {
        String refundNo = getRefundNoFromNotifyParam(platformType, param);
        if (StringUtils.isBlank(refundNo)) {
            return false;
        }
        Refund refund = refundDao.load(refundNo);
        if (refund == null) {
            log.warn("platform={} async refund notify, refundNO={} not exist ", platformType.name(), refundNo);
            return false;
        }
        log.info("ready to handle platform={} async refund notify, refundNo={}", platformType.name(), refundNo);
        if (RefundStatus.SUCCESS == refund.getStatus()) {
            log.info("refundNo={} status already SUCCESS,ignore platform={} async refund notify ", refundNo, platformType.name());
            return true;
        }
        ChannelRefundResponse response = channelGatewayService.handleRefundAsyncNotify(refund.getAppId(), refund.getChannel(), param);
        RefreshRefundDto refreshRefundDto = refundBiz.generateRefreshRefundParam(refund, response);
        refund = refundBiz.refreshRefund(refreshRefundDto, refund);
        log.info("handle platform={} async refund notify, refundNo={} completed refundStatus={}", platformType.name(), refundNo, refund.getStatus().name());
        param.put("appId", refund.getAppId().toString());
        return RefundStatus.SUCCESS == refund.getStatus();
    }

    @Override
    public PageInfo<ChargeDto> queryPaymentList(ChargeQueryParam queryParam) {
        PageHelper.startPage(queryParam.getStartPage(), queryParam.getPageSize());
        // 未指定应用则为范围查找整棵树的交易
        queryParam.setMaxAppId(queryParam.getAppId() / 100000 * 100000 + 99999);
        List<Charge> chargeList = chargeDao.getByQueryParam(queryParam);
        PageInfo pageInfo = PageInfo.of(chargeList);
        pageInfo.setList(BeanConverter.batchCovertIgnore(chargeList, ChargeDto.class));
        return pageInfo;
    }

    @Override
    public PageInfo<RefundDto> queryRefundList(RefundQueryParam queryParam) {
        PageHelper.startPage(queryParam.getStartPage(), queryParam.getPageSize());
        // 未指定应用则为范围查找整棵树的交易
        queryParam.setMaxAppId(queryParam.getAppId() / 100000 * 100000 + 99999);
        List<Refund> refundList = refundDao.getByQueryParam(queryParam);
        PageInfo pageInfo = PageInfo.of(refundList);
        pageInfo.setList(BeanConverter.batchCovertIgnore(refundList, RefundDto.class));
        return pageInfo;
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

    /**
     * 从异步参数中获取chargeNo
     *
     * @param platformType
     * @param param
     * @return
     */
    private String getChargeNoFromNotifyParam(PlatformType platformType, Map<String, String> param) {
        switch (platformType) {
            case ALIPAY:
                return param.get("out_trade_no");
            case UNIONPAY:
                return param.get("orderId");
            default:
                return "";
        }
    }

    /**
     * 从异步通知参数中获取refundNo
     *
     * @param platformType
     * @param param
     * @return
     */
    private String getRefundNoFromNotifyParam(PlatformType platformType, Map<String, String> param) {
        switch (platformType) {
            case UNIONPAY:
                return param.get("orderId");
            default:
                return "";
        }
    }


}
