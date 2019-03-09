/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.notification.mq
 *   Date Created: 2019/2/1
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2019/2/1      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.notification.mq;

import com.gloryjie.pay.base.constant.DefaultConstant;
import com.gloryjie.pay.base.enums.MqTagEnum;
import com.gloryjie.pay.notification.service.WebhookProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @author Jie
 * @since 0.1
 */
@Slf4j
@Component
public class EventNotifyConsumer implements MessageListenerConcurrently {

    @Value("${rocketmq.trade.closeChargeGroup:CID_EVENT_NOTIFY}")
    private String eventNotifyGroup;

    @Value("${rocketmq.notify.topic:PAY_EVENT}")
    private String eventMqTopic;

    @Value("${rocketMq.namesrv:localhost:9876}")
    private String nameSrv;

    @Autowired
    private WebhookProcessor webhookProcessor;


    @PostConstruct
    public void initConsumer() {
        try {
            DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(eventNotifyGroup);
            consumer.setNamesrvAddr(nameSrv);
            consumer.subscribe(eventMqTopic, MqTagEnum.EVENT_NOTIFY.name());
            // 从最新的开始消费
            consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
            consumer.registerMessageListener(this);
            consumer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
        String eventNo = null;
        try {
            if (msgs != null && msgs.size() > 0) {
                for (MessageExt msg : msgs) {
                    eventNo = new String(msg.getBody(), DefaultConstant.CHARSET);
                    webhookProcessor.handleEventNotify(eventNo);
                }
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        } catch (Exception e) {
            log.error("consume event notify msg fail, eventNo={}", eventNo, e);
        }
        return ConsumeConcurrentlyStatus.RECONSUME_LATER;
    }

}
