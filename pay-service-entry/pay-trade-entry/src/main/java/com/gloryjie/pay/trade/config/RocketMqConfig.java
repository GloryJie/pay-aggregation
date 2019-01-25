/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.trade.config
 *   Date Created: 2019/1/24
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2019/1/24      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.trade.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Jie
 * @since
 */
@Data
@Slf4j
@Configuration
public class RocketMqConfig {

    @Value("${rocketMq.trade.producer:PID_TRADE}")
    private String tradeProducerGroup;

    @Value("${rocketMq.namesrv:localhost:9876}")
    private String nameSrv;

    @Bean
    public DefaultMQProducer defaultMQProducer() {
        DefaultMQProducer producer = new DefaultMQProducer(tradeProducerGroup);
        try {
            producer.setNamesrvAddr(nameSrv);
            log.debug("trade service init rocketmq producer config, group={}, namesrv={}", tradeProducerGroup, nameSrv);
            producer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }
        return producer;
    }
}
