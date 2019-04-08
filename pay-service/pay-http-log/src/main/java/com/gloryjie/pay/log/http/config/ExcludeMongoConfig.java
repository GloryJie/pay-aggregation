package com.gloryjie.pay.log.http.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.Configuration;

/**
 * 排除MongoDB配置
 * @author jie
 * @since 2019/4/8
 */
@Configuration
@ConditionalOnProperty(name = "pay.httplog.exclude", havingValue = "mongodb")
@EnableAutoConfiguration(exclude = {MongoAutoConfiguration.class})
public class ExcludeMongoConfig {

}
