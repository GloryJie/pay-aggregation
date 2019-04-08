package com.gloryjie.pay.log.http.config;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Configuration;

/**
 * 排除MySQL数据库配置
 * @author jie
 * @since 2019/4/8
 */
@Configuration
@ConditionalOnProperty(name = "pay.httplog.exclude", havingValue = "mysql")
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class, DruidDataSourceAutoConfigure.class, MybatisAutoConfiguration.class})
public class ExcludeDataSourceConfig {

}
