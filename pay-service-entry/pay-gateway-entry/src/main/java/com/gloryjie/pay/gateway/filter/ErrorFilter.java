/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.gateway.filter
 *   Date Created: 2019/3/17
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2019/3/17      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.gateway.filter;

import com.gloryjie.pay.base.constant.DefaultConstant;
import com.gloryjie.pay.base.enums.error.CommonErrorEnum;
import com.gloryjie.pay.base.exception.BaseException;
import com.gloryjie.pay.base.exception.error.SystemException;
import com.gloryjie.pay.base.response.Response;
import com.gloryjie.pay.base.util.JsonUtil;
import com.netflix.client.ClientException;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.cloud.netflix.zuul.util.ZuulRuntimeException;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StreamUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;

/**
 * @author Jie
 * @since
 */
@Slf4j
@Component
public class ErrorFilter extends ZuulFilter {

    @Override
    public String filterType() {
        return FilterConstants.ERROR_TYPE;
    }

    @Override
    public int filterOrder() {
        return FilterConstants.SEND_ERROR_FILTER_ORDER;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();
        return ctx.getThrowable() != null;
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext ctx = RequestContext.getCurrentContext();
        BaseException exception = findException(ctx.getThrowable());
        Response responseContent = Response.failure(exception);
        try {
            HttpServletResponse response = ctx.getResponse();
            response.setContentType(DefaultConstant.CONTENT_TYPE);
            ctx.setResponseStatusCode(exception.getErrorEnum().getCode());
            OutputStream os = response.getOutputStream();
            StreamUtils.copy(JsonUtil.toJson(responseContent), StandardCharsets.UTF_8, os);
        } catch (Exception ex) {
            ReflectionUtils.rethrowRuntimeException(ex);
        }
        return null;
    }

    protected BaseException findException(Throwable throwable) {
        if (throwable.getCause() instanceof BaseException) {
            // 自定义异常在抛出处打印日志
            return (BaseException) throwable.getCause();
        }
        /*下面试仿造SendErrorFilter改写的*/
        if (throwable.getCause() instanceof ZuulRuntimeException) {
            log.warn("Error during filtering", throwable.getCause());
            Throwable cause = null;
            if (throwable.getCause().getCause() != null) {
                cause = throwable.getCause().getCause().getCause();
            }
            if (cause instanceof ClientException && cause.getCause() != null
                    && cause.getCause().getCause() instanceof SocketTimeoutException) {
                // Hystrix Read time out超时报系统繁忙
                return SystemException.create(CommonErrorEnum.SYSTEM_BUSY_ERROR);
            }
            // this was a failure initiated by one of the local filters
            if (throwable.getCause().getCause() instanceof ZuulException) {
                return SystemException.create(CommonErrorEnum.SYSTEM_BUSY_ERROR, throwable.getCause().getCause());
            }
        }

        if (throwable.getCause() instanceof ZuulException) {
            // wrapped zuul exception
            return SystemException.create(CommonErrorEnum.SYSTEM_BUSY_ERROR, throwable.getCause().getCause());
        }

        if (throwable instanceof ZuulException) {
            // exception thrown by zuul lifecycle
            return SystemException.create(CommonErrorEnum.INTERNAL_SYSTEM_ERROR, throwable);
        }

        // fallback
        return SystemException.create(CommonErrorEnum.INTERNAL_SYSTEM_ERROR, throwable);
    }
}
