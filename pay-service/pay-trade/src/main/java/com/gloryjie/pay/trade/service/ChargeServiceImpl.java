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
import com.gloryjie.pay.base.util.BeanConverter;
import com.gloryjie.pay.channel.dto.ChannelPayQueryDto;
import com.gloryjie.pay.channel.dto.ChannelPayQueryResponse;
import com.gloryjie.pay.channel.dto.param.ChargeCreateParam;
import com.gloryjie.pay.channel.enums.ChannelType;
import com.gloryjie.pay.channel.service.ChannelGatewayService;
import com.gloryjie.pay.trade.biz.ChargeBiz;
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

import java.util.List;

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

    @Autowired
    private ChargeBiz chargeBiz;


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
    public ChargeDto queryPayment(Integer appId, String orderNo) {
        List<Charge> chargeList = chargeDao.listByAppIdAndOrderNo(appId, orderNo);
        if (chargeList == null) {
            return new ChargeDto();
        }
        Charge charge = chargeList.get(0);
        ChargeDto chargeDto;
        // 若当前为待支付状态,则交由渠道进行查询
        if (ChargeStatus.WAIT_PAY.equals(charge.getStatus())) {
            ChannelPayQueryDto queryDto = new ChannelPayQueryDto();
            queryDto.setAppId(appId);
            queryDto.setChargeNo(charge.getChargeNo());
            queryDto.setChannel(charge.getChannel());
            // TODO: 2019/1/13 渠道查询需要限制,避免流量穿透
            ChannelPayQueryResponse queryResponse = channelGatewayService.queryPayment(queryDto);
            // TODO: 2019/1/13 状态变化刷新需要抽取出来,异步通知,主动查询等也需要
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
        if (!ChargeStatus.WAIT_PAY.equals(chargeDto.getStatus())) {
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


    /**
     * 检查订单存在性
     *
     * @param chargeList
     * @param param
     * @return
     */
    private Charge checkChargeExist(List<Charge> chargeList, ChargeCreateParam param) {
        if (chargeList != null && chargeList.size() > 0) {
            for (Charge charge : chargeList) {
                // 简单处理: 若渠道也相同,则认为是同一订单
                if (charge.getChannel().equals(param.getChannel())) {
                    return charge;
                }
            }
        }
        return null;
    }

}
