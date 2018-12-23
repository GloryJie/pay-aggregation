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

import com.gloryjie.pay.base.exception.error.BusinessException;
import com.gloryjie.pay.base.exception.error.ExternalException;
import com.gloryjie.pay.base.util.BeanConverter;
import com.gloryjie.pay.base.util.JsonUtil;
import com.gloryjie.pay.base.util.idGenerator.IdFactory;
import com.gloryjie.pay.channel.dto.ChannelPayDto;
import com.gloryjie.pay.channel.dto.ChannelPayQueryDto;
import com.gloryjie.pay.channel.dto.ChannelPayQueryResponse;
import com.gloryjie.pay.channel.dto.param.ChargeCreateParam;
import com.gloryjie.pay.channel.dto.response.ChannelPayResponse;
import com.gloryjie.pay.channel.enums.ChannelType;
import com.gloryjie.pay.channel.error.ChannelError;
import com.gloryjie.pay.channel.service.ChannelGatewayService;
import com.gloryjie.pay.trade.dao.ChargeDao;
import com.gloryjie.pay.trade.dao.RefundDao;
import com.gloryjie.pay.trade.dto.ChargeDto;
import com.gloryjie.pay.trade.dto.RefundDto;
import com.gloryjie.pay.trade.enums.ChannelStatusToChargeStatus;
import com.gloryjie.pay.trade.enums.ChargeStatus;
import com.gloryjie.pay.trade.enums.TradeError;
import com.gloryjie.pay.trade.model.Charge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author Jie
 * @since
 */
@Service
public class ChargeServiceImpl implements ChargeService {

    @Autowired
    private ChargeDao chargeDao;

    @Autowired
    private RefundDao refundDao;

    @Autowired
    private ChannelGatewayService channelGatewayService;


    @Override
    public ChargeDto pay(ChargeCreateParam createParam) {
        if (createParam.getServiceAppId() == null) {
            createParam.setServiceAppId(createParam.getAppId());
        }
        if (createParam.getTimeExpire() == null) {
            createParam.setTimeExpire(120L);
        }

        ChannelType channel = createParam.getChannel();
        // 检查渠道额外参数是否正确
        channel.checkExtraParam(createParam.getExtra());
        // TODO: 2018/12/21 此处待商榷, 有可能会出现一笔订单出现两笔支付单的情况,例如收银台切换支付方式, 可用渠道加以区分
        // 检查订单是否已存在
        Charge charge = chargeDao.loadByAppIdAndOrderNo(createParam.getAppId(), createParam.getOrderNo());
        if (charge != null) {
            throw BusinessException.create(TradeError.ORDER_ALREADY_EXISTS);
        }
        charge = generateCharge(createParam);
        // 分发渠道支付
        ChannelPayDto payDto = BeanConverter.covert(charge, ChannelPayDto.class);
        ChannelPayResponse payResponse = channelGatewayService.pay(payDto);
        if (payResponse.isSuccess()) {
            if (channel.isGateway()) {
                charge.setCredential(payResponse.getCredential());
            }
        } else {
            throw ExternalException.create(ChannelError.PAY_PLATFORM_ERROR, payResponse.getMsg());
        }
        // 入库
        chargeDao.insert(charge);
        // TODO: 2018/12/22 需要定时关单

        ChargeDto chargeDto = BeanConverter.covert(charge, ChargeDto.class);
        chargeDto.setExtra(createParam.getExtra());
        return chargeDto;
    }

    @Override
    public ChargeDto queryPayment(Integer appId, String orderNo) {
        Charge charge = chargeDao.loadByAppIdAndOrderNo(appId, orderNo);
        if (charge == null) {
            return new ChargeDto();
        }
        ChargeDto chargeDto;
        // 若当前为待支付状态,则交由渠道进行查询
        if (ChargeStatus.WAIT_PAY.equals(charge.getStatus())) {
            ChannelPayQueryDto queryDto = new ChannelPayQueryDto();
            queryDto.setAppId(appId);
            queryDto.setChargeNo(charge.getChargeNo());
            queryDto.setChannel(charge.getChannel());
            ChannelPayQueryResponse queryResponse = channelGatewayService.queryPayment(queryDto);
            ChargeStatus status = ChannelStatusToChargeStatus.switchStatus(charge.getChannel(), queryResponse.getStatus());
            if (ChargeStatus.SUCCESS.equals(status) && charge.getAmount().equals(queryResponse.getAmount())) {
                charge.setStatus(ChargeStatus.SUCCESS);
                charge.setPlatformTradeNo(queryResponse.getPlatformTradeNo());
                charge.setActualAmount(queryResponse.getActualAmount());
                charge.setTimePaid(queryResponse.getTimePaid());
                // 更新数据库
                chargeDao.update(charge);
            }
        }
        chargeDto = BeanConverter.covert(charge, ChargeDto.class);
        chargeDto.setExtra(JsonUtil.parse(charge.getExtra(), Map.class));
        if (!ChargeStatus.WAIT_PAY.equals(chargeDto.getStatus())){
            chargeDto.setCredential(null);
        }
        return chargeDto;
    }

    @Override
    public RefundDto refund(RefundDto refundDto) {
        return null;
    }

    @Override
    public RefundDto queryRefund(Integer appId, String chargeNo, String refundNo) {
        return null;
    }


    private Charge generateCharge(ChargeCreateParam createParam) {
        Charge charge = BeanConverter.covert(createParam, Charge.class);
        charge.setChargeNo(IdFactory.generateStringId());
        charge.setVersion(0);
        charge.setLiveMode(createParam.getLiveMode());
        charge.setStatus(ChargeStatus.WAIT_PAY);
        charge.setExtra(createParam.getExtra() == null ? null : JsonUtil.toJson(createParam.getExtra()));
        charge.setTimeCreated(System.currentTimeMillis());
        charge.setExpireTimestamp(charge.getTimeCreated() + createParam.getTimeExpire() * 60 * 1000);
        return charge;
    }

}
