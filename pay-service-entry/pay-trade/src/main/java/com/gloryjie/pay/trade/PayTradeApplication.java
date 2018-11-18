/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.trade
 *   Date Created: 2018/11/18
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2018/11/18      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.trade;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Jie
 * @since
 */
@SpringBootApplication
@MapperScan(basePackages = "com.gloryjie.pay.*.dao")
public class PayTradeApplication {

    public static void main(String[] args) {
        SpringApplication.run(PayTradeApplication.class, args);
    }
}
