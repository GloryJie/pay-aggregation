package com.gloryjie.pay.log.http.config;

import com.gloryjie.pay.log.http.service.HttpLogService;
import com.gloryjie.pay.log.http.service.impl.MongoHttpLogServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;

/**
 *
 * @author jie
 * @since 2019/4/8
 */
@Configuration
@ConditionalOnProperty(name = "pay.httplog.store", havingValue = "mongodb")
public class MongoHttpLogAutoConfig {

    /**
     * 此处需要使用 @Lazy 延迟加载, 需要mongo相关配置后才j进行实例化
     * @return
     */
    @Bean
    @Lazy
    @Primary
    public HttpLogService mongoHttpLogService(){
        return new MongoHttpLogServiceImpl();
    }

}
