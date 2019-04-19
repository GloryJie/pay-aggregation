package com.gloryjie.pay.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
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

        String path = servletRequest.getServletPath();
        if (path.contains(TRADE_SERVICE_PREFIX)) {
            path = path.replace(TRADE_SERVICE_PREFIX, "");
        } else if (path.contains(NOTIFICATION_SERVICE_PREFIX)) {
            path = path.replace(NOTIFICATION_SERVICE_PREFIX, "");
        } else if (path.contains(AUTH_SERVICE_PREFIX)) {
            path = path.replace(AUTH_SERVICE_PREFIX, "");
        } else {
            chain.doFilter(request, response);
        }
        RewritePathHttpServletRequestWrapper requestWrapper = new RewritePathHttpServletRequestWrapper(path, servletRequest);
        chain.doFilter(requestWrapper, response);
    }

    class RewritePathHttpServletRequestWrapper extends HttpServletRequestWrapper {
        String path;

        RewritePathHttpServletRequestWrapper(String path, HttpServletRequest request) {
            super(request);
            this.path = path;
        }

        @Override
        public String getServletPath() {
            return path;
        }
    }
}
