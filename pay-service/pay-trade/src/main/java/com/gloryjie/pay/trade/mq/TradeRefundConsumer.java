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
import com.gloryjie.pay.trade.biz.RefundBiz;
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
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * 交易的异步退款处理
 *
 * @author Jie
 * @since 0.1
 */
@Slf4j
@Component
public class TradeRefundConsumer implements MessageListenerConcurrently {

    @Value("${rocketmq.trade.refundConsumerGroup:CID_REFUND}")
    private String refundConsumerGroup;

    @Value("${rocketmq.trade.topic:TRADE_CORE}")
    private String tradeMqTopic;

    @Value("${rocketMq.namesrv:localhost:9876}")
    private String nameSrv;

    @Autowired
    private RefundBiz refundBiz;


    @PostConstruct
    public void initConsumer() {
        try {
            DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(refundConsumerGroup);
            consumer.setNamesrvAddr(nameSrv);
            consumer.subscribe(tradeMqTopic, MqTagEnum.TRADE_ASYNC_REFUND.name());
            // 从最新的开始消费
            consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
            consumer.registerMessageListener(this);
            consumer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }
    }


    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
        try {
            if (msgs != null && msgs.size() > 0) {
                for (MessageExt msg : msgs) {
                    String refundNo = new String(msg.getBody(), DefaultConstant.CHARSET);
                    refundBiz.actualRefund(refundNo);
                }
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ConsumeConcurrentlyStatus.RECONSUME_LATER;
    }
}
