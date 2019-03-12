/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.trade.mq
 *   Date Created: 2019/1/31
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2019/1/31      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.trade.mq;

import com.gloryjie.pay.base.constant.DefaultConstant;
import com.gloryjie.pay.base.enums.MqTagEnum;
import com.gloryjie.pay.trade.biz.ChargeBiz;
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
 * 关闭支付单
 *
 * @author Jie
 * @since
 */
@Slf4j
@Component
public class TradeCloseChargeConsumer implements MessageListenerConcurrently {

    @Value("${pay.rocketmq.trade.closeChargeConsumer}")
    private String closeChargeGroup;

    @Value("${pay.rocketmq.trade.topic}")
    private String tradeMqTopic;

    @Value("${pay.rocketmq.namesrv}")
    private String nameSrv;

    @Autowired
    private ChargeBiz chargeBiz;


    @PostConstruct
    public void initConsumer() {
        try {
            DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(closeChargeGroup);
            consumer.setNamesrvAddr(nameSrv);
            consumer.subscribe(tradeMqTopic, MqTagEnum.TRADE_CLOSE_CHARGE.name());
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
        String chargeNo = null;
        try {
            if (msgs != null && msgs.size() > 0) {
                for (MessageExt msg : msgs) {
                    chargeNo = new String(msg.getBody(), DefaultConstant.CHARSET);
                    chargeBiz.closeCharge(chargeNo);
                }
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        } catch (Exception e) {
            log.error("consume close charge={} msg fail", chargeNo);
        }
        return ConsumeConcurrentlyStatus.RECONSUME_LATER;
    }
}
