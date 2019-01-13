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

import com.gloryjie.pay.base.exception.error.ExternalException;
import com.gloryjie.pay.base.util.BeanConverter;
import com.gloryjie.pay.base.util.idGenerator.IdFactory;
import com.gloryjie.pay.channel.dto.ChannelPayDto;
import com.gloryjie.pay.channel.dto.param.ChargeCreateParam;
import com.gloryjie.pay.channel.dto.response.ChannelPayResponse;
import com.gloryjie.pay.channel.error.ChannelError;
import com.gloryjie.pay.channel.service.ChannelGatewayService;
import com.gloryjie.pay.trade.dao.ChargeDao;
import com.gloryjie.pay.trade.enums.ChargeStatus;
import com.gloryjie.pay.trade.model.Charge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 处理支付单相关的业务
 *
 * @author Jie
 * @since 0.1
 */
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
        charge.setTimeCreated(System.currentTimeMillis());
        charge.setExpireTimestamp(charge.getTimeCreated() + createParam.getTimeExpire() * 60 * 1000);
        return charge;
    }


    // 状态变更, 刷新支付单
    public Charge refreshCharge(){

        return null;
    }
}
