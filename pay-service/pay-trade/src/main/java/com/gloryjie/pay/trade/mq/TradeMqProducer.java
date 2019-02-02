/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.trade.mq
 *   Date Created: 2019/1/24
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2019/1/24      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.trade.mq;

import com.gloryjie.pay.base.constant.DefaultConstant;
import com.gloryjie.pay.base.enums.MqDelayMsgLevel;
import com.gloryjie.pay.base.enums.MqTagEnum;
import com.gloryjie.pay.base.util.JsonUtil;
import com.gloryjie.pay.trade.dto.ChargeDto;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

/**
 * 交易数据的目前生产者
 *
 * @author Jie
 * @since 0.1
 */
@Slf4j
@Component
public class TradeMqProducer {

    @Value("${rocketmq.trade.topic:TRADE_CORE}")
    private String tradeMqTopic;

    @Autowired
    private DefaultMQProducer mqProducer;


    public void sendChargeSuccessMsg(ChargeDto chargeDto){
        try {
            String body = JsonUtil.toJson(chargeDto);
            Message message = new Message(tradeMqTopic, MqTagEnum.CHARGE_SUCCESS.name(), body.getBytes(DefaultConstant.CHARSET));
            mqProducer.send(message, new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {

                }

                @Override
                public void onException(Throwable e) {
                    // TODO: 2019/2/1 需要重试机制
                }
            });
        } catch (UnsupportedEncodingException | MQClientException | RemotingException | InterruptedException e) {
            // TODO: 2019/2/1
            log.error("send charge success msg error, errMsg={}", e.getMessage(), e);
        }
    }

    /**
     * 发送异步退款信息
     */
    public Boolean sendRefundMsg(@NonNull String refundNo) {
        try {
            Message message = new Message(tradeMqTopic, MqTagEnum.TRADE_ASYNC_REFUND.name(), refundNo.getBytes(DefaultConstant.CHARSET));
            // 同步步退款消息使用同步发送,确保发送成功
            SendResult sendResult = mqProducer.send(message);
            return SendStatus.SEND_OK == sendResult.getSendStatus();
        } catch (UnsupportedEncodingException | MQClientException | RemotingException | InterruptedException | MQBrokerException e) {
            // 打印日志,不需要重试
            log.error("send refund msg error, errMsg={}", e.getMessage(), e);
        }
        return Boolean.FALSE;
    }

    /**
     * 发送定时关单消息
     */
    public void sendTimingCloseMsg(@NonNull String chargeNo, MqDelayMsgLevel level) {
        try {
            Message message = new Message(tradeMqTopic, MqTagEnum.TRADE_CLOSE_CHARGE.name(), chargeNo.getBytes(DefaultConstant.CHARSET));
            message.setDelayTimeLevel(level.getLevel());
            // 使用回调发送
            mqProducer.send(message, new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    log.info("send timing close charge={} msg success", chargeNo);
                }

                @Override
                public void onException(Throwable throwable) {
                    log.warn("send timing close charge={} msg fail", chargeNo);
                }
            });
        } catch (UnsupportedEncodingException | MQClientException | RemotingException | InterruptedException e) {
            log.warn("send timing close charge={} msg fail", chargeNo, e);
        }
    }

}
