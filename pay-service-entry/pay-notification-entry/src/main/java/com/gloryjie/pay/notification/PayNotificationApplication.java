/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.notification
 *   Date Created: 2019/1/31
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2019/1/31      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.notification;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author Jie
 * @since 0.1
 */
@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan("com.gloryjie.pay.*")
@MapperScan(basePackages = "com.gloryjie.pay.*.dao")
@EnableFeignClients(basePackages = "com.gloryjie.pay")
public class PayNotificationApplication {

    public static void main(String[] args) {
        SpringApplication.run(PayNotificationApplication.class, args);
    }
}
