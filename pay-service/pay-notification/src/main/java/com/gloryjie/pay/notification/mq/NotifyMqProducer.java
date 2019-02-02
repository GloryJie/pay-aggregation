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
package com.gloryjie.pay.notification.mq;

import com.gloryjie.pay.base.constant.DefaultConstant;
import com.gloryjie.pay.base.enums.MqDelayMsgLevel;
import com.gloryjie.pay.base.enums.MqTagEnum;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
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
public class NotifyMqProducer {

    @Value("${rocketmq.notify.topic:PAY_EVENT}")
    private String eventMqTopic;

    @Autowired
    private DefaultMQProducer mqProducer;

    /**
     * 发送通知消息，为模块内部循环使用
     */
    public void sendEventNotifyMsg(@NonNull String eventNo, MqDelayMsgLevel level) {
        try {
            Message message = new Message(eventMqTopic, MqTagEnum.EVENT_NOTIFY.name(), eventNo.getBytes(DefaultConstant.CHARSET));
            message.setDelayTimeLevel(level.getLevel());
            mqProducer.send(message, new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    log.info("send notify msg to rmq success, event={}, level={}", eventNo, level.name());
                }

                @Override
                public void onException(Throwable e) {
                    log.error("send notify msg to rmq fail, event={}, level={}", eventNo, level.name());
                    // TODO: 2019/2/1 失败需要重试机制兜底
                }
            });
        } catch (UnsupportedEncodingException | MQClientException | RemotingException | InterruptedException e) {
            log.error("send notify msg to rmq fail, event={}, level={}", eventNo, level.name());
        }
    }


}
