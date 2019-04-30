/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.auth.config
 *   Date Created: 2019/3/13
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2019/3/13      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

/**
 * 资源配置
 * @author Jie
 * @since
 */
@Configuration
@EnableResourceServer
public class OauthResourceConfig extends ResourceServerConfigurerAdapter {


    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/feign-api/**").permitAll()
                .antMatchers("/web/**").authenticated();
//                        .antMatchers("/web/**").permitAll();

    }
}
