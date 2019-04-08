package com.gloryjie.pay.log.http.config;

import com.gloryjie.pay.log.http.service.HttpLogService;
import com.gloryjie.pay.log.http.service.impl.MysqlHttpLogServiceImpl;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

/**
 * 若使用 mysql 存储日志信息
 *
 * @author jie
 * @since 2019/4/8
 */
@Configuration
@ConditionalOnProperty(name = "pay.httplog.store", havingValue = "mysql")
@MapperScan(basePackages = "com.gloryjie.pay.**.dao")
public class MysqlHttpLogAutoConfig {

    /**
     * 此处需要使用 @Lazy 延迟加载, 需要DataSource和Mybatis相关配置后才j进行实例化
     * @return
     */
    @Bean
    @Lazy
    public HttpLogService mysqlHtpLogService() {
        return new MysqlHttpLogServiceImpl();
    }
}
