package com.gloryjie.pay;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author jie
 * @since 2019/4/17
 */
@SpringBootApplication
@ComponentScan("com.gloryjie.pay.*")
@MapperScan(basePackages = "com.gloryjie.pay.*.dao")
@ServletComponentScan("com.gloryjie.pay")
public class PayAllApplication {

    public static void main(String[] args) {
        SpringApplication.run(PayAllApplication.class, args);
    }
}
