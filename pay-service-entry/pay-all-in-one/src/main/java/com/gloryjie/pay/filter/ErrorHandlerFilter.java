package com.gloryjie.pay.filter;

import com.gloryjie.pay.base.constant.DefaultConstant;
import com.gloryjie.pay.base.enums.error.CommonErrorEnum;
import com.gloryjie.pay.base.exception.BaseException;
import com.gloryjie.pay.base.exception.error.ExternalException;
import com.gloryjie.pay.base.response.Response;
import com.gloryjie.pay.base.util.JsonUtil;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 过滤器错误处理
 * @author jie
 * @since 2019/4/18
 */
public class ErrorHandlerFilter implements Filter {

    public static final String ERROR_FLAG = "ErrorHandlerFilter.error";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse servletResponse = (HttpServletResponse) response;
        try {
            chain.doFilter(request, response);
        } catch (BaseException e) {
            // 发生异常，保存异常栈
            Response customResponse = Response.failure(e);
            handle(customResponse,servletResponse);
        }catch (Exception e){
            ExternalException exception = ExternalException.create(CommonErrorEnum.ILLEGAL_ARGUMENT_ERROR, e.getMessage());
            handle(Response.failure(exception), servletResponse);
        }
    }

    private void handle(Response customResponse, HttpServletResponse servletResponse) throws IOException {
        if (servletResponse.isCommitted()){
            return;
        }
        servletResponse.setStatus(Integer.valueOf(customResponse.getStatus().substring(0,3)));
        servletResponse.setContentType(DefaultConstant.CONTENT_TYPE);
        servletResponse.getWriter().write(JsonUtil.toJson(customResponse));
    }
}
