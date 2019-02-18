/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.server.register
 *   Date Created: 2019/2/18
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2019/2/18      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.server.register;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * @author Jie
 * @since
 */
@SpringBootApplication
@EnableEurekaServer
public class ServerRegisterApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerRegisterApplication.class, args);
    }
}
