/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.auth
 *   Date Created: 2019/3/13
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2019/3/13      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.auth;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author Jie
 * @since
 */
@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan("com.gloryjie.pay.*")
@MapperScan("com.gloryjie.pay.*.dao")
public class PayAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(PayAuthApplication.class, args);
    }
}
