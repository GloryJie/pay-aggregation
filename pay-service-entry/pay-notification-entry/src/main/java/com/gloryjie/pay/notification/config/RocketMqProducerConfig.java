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
package com.gloryjie.pay.notification.config;

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
public class RocketMqProducerConfig {

    @Value("${pay.rocketmq.notify.producer}")
    private String notifyGroup;

    @Value("${pay.rocketmq.namesrv}")
    private String nameSrv;

    @Bean
    public DefaultMQProducer defaultMQProducer() {
        DefaultMQProducer producer = new DefaultMQProducer(notifyGroup);
        try {
            producer.setNamesrvAddr(nameSrv);
            log.debug("notify service init rocketmq producer config, group={}, namesrv={}", notifyGroup, nameSrv);
            producer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }
        return producer;
    }
}
