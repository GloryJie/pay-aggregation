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
import com.gloryjie.pay.base.util.JsonUtil;
import com.gloryjie.pay.notification.service.EventNotifyService;
import com.gloryjie.pay.trade.dto.ChargeDto;
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
 * 消费交易成功的消息
 *
 * @author Jie
 * @since 0.1
 */
@Slf4j
@Component
public class TradeSuccessConsumer implements MessageListenerConcurrently {

    @Value("${rocketmq.trade.closeChargeGroup:CID_TRADE_SUCCESS_NOTIFY}")
    private String tradeSuccessNotifyGroup;

    @Value("${rocketmq.trade.topic:TRADE_CORE}")
    private String tradeMqTopic;

    @Value("${rocketMq.namesrv:localhost:9876}")
    private String nameSrv;

    @Autowired
    private EventNotifyService eventNotifyService;


    @PostConstruct
    public void initConsumer() {
        try {
            DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(tradeSuccessNotifyGroup);
            consumer.setNamesrvAddr(nameSrv);
            consumer.subscribe(tradeMqTopic, MqTagEnum.CHARGE_SUCCESS.name() + "||" + MqTagEnum.REFUND_SUCCESS);
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
        String sourceNo = null;
        try {
            if (msgs != null && msgs.size() > 0) {
                for (MessageExt msg : msgs) {
                    MqTagEnum tag = MqTagEnum.valueOf(msg.getTags());
                    switch (tag) {
                        case CHARGE_SUCCESS:
                            ChargeDto chargeDto = JsonUtil.parse(new String(msg.getBody(), DefaultConstant.CHARSET), ChargeDto.class);
                            sourceNo = chargeDto.getChargeNo();
                            // TODO: 2019/2/1 需要检查dto必填项是否齐全
                            eventNotifyService.handleChargeSuccessEvent(chargeDto);
                            break;
                        case REFUND_SUCCESS:
                            break;
                        default:
                            break;
                    }
                }
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        } catch (Exception e) {
            log.error("consume trade success sourceNo={} msg fail", sourceNo, e);
        }
        return ConsumeConcurrentlyStatus.RECONSUME_LATER;
    }
}
