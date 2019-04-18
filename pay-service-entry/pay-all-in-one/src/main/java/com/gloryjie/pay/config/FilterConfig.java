package com.gloryjie.pay.config;

import com.gloryjie.pay.filter.HttpLogFilter;
import com.gloryjie.pay.filter.SignCheckFilter;
import com.gloryjie.pay.filter.UriRewriteFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author jie
 * @since 2019/4/18
 */
@Configuration
public class FilterConfig {

    @Autowired
    SignCheckFilter signCheckFilter;

    @Bean
    public FilterRegistrationBean rewriteUriFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new UriRewriteFilter());
        registration.addUrlPatterns("/pay/trade/*", "/pay/notification/*", "/pay/auth/*");
        registration.setName("rewriteUriFilter");
        registration.setOrder(Integer.MIN_VALUE + 2);
        return registration;
    }

    @Bean
    public FilterRegistrationBean signCheckFilterRegistrationBean() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(signCheckFilter);
        registration.addUrlPatterns("/pay/trade/api/*");
        registration.setName("signCheckFilter");
        registration.setOrder(Integer.MIN_VALUE+1);
        return registration;
    }

    @Bean
    public FilterRegistrationBean httpLogFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new HttpLogFilter());
        registration.addUrlPatterns("/pay/trade/api/*","/pay/trade/platform/notify/*");
        registration.setName("httpLogFilter");
        registration.setOrder(Integer.MIN_VALUE);
        return registration;
    }
}
