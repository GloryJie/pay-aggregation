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
import com.gloryjie.pay.base.enums.MqTagEnum;
import com.gloryjie.pay.base.util.JsonUtil;
import com.gloryjie.pay.channel.dto.ChannelRefundDto;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
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

    /**
     * 发送异步退款信息
     */
    public Boolean sendRefundMsg(@NonNull String refundNo) {
        try {
            Message message = new Message(tradeMqTopic, MqTagEnum.TRADE_ASYNC_REFUND.name(), refundNo.getBytes(DefaultConstant.CHARSET));
            // 异步退款消息使用同步发送,确保发送成功
            SendResult sendResult = mqProducer.send(message);
            return SendStatus.SEND_OK == sendResult.getSendStatus();
        } catch (UnsupportedEncodingException | MQClientException | RemotingException | InterruptedException | MQBrokerException e) {
            // 打印日志,不需要重试
            log.error("send refund msg error, errMsg={}", e.getMessage(), e);
        }
        return Boolean.FALSE;
    }

}
