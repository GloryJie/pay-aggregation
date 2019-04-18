package com.gloryjie.pay.filter;

import org.springframework.core.annotation.Order;
import org.springframework.util.StreamUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;

/**
 * @author jie
 * @since 2019/4/17
 */
public class UriRewriteFilter implements Filter {

    public static final String TRADE_SERVICE_PREFIX = "/pay/trade";
    public static final String NOTIFICATION_SERVICE_PREFIX = "/pay/notification";
    public static final String AUTH_SERVICE_PREFIX = "/pay/auth";


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest servletRequest = (HttpServletRequest) request;

        HttpServletResponseWrapper responseWrapper = new HttpServletResponseWrapper((HttpServletResponse) response);
        String path = servletRequest.getRequestURI();
        if (path.contains(TRADE_SERVICE_PREFIX)) {
            path = path.replace(TRADE_SERVICE_PREFIX, "");
        } else if (path.contains(NOTIFICATION_SERVICE_PREFIX)) {
            path = path.replace(NOTIFICATION_SERVICE_PREFIX, "");
        } else if (path.contains(AUTH_SERVICE_PREFIX)) {
            path = path.replace(AUTH_SERVICE_PREFIX, "");
        } else {
            chain.doFilter(request, response);
        }
        MyHttpServletRequestWrapper requestWrapper = new MyHttpServletRequestWrapper(path, servletRequest);
        chain.doFilter(requestWrapper, responseWrapper);
    }

    class MyHttpServletRequestWrapper extends HttpServletRequestWrapper {
        String path;
        MyHttpServletRequestWrapper(String path, HttpServletRequest request){
            super(request);
            this.path = path;
        }
        @Override
        public String getServletPath() {
            return path;
        }
    }
}
