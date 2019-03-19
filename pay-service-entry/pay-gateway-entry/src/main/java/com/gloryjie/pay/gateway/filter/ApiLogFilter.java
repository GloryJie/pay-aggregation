/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.gateway.filter
 *   Date Created: 2019/3/18
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2019/3/18      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.gateway.filter;

import com.gloryjie.pay.base.util.JsonUtil;
import com.gloryjie.pay.gateway.model.HttpLog;
import com.netflix.util.Pair;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 记录API接口请求日志
 *
 * @author Jie
 * @since
 */
@Component
public class ApiLogFilter extends ZuulFilter {

    private static Logger log = LoggerFactory.getLogger("HttpLogger");

    private static final Logger API_Filter_Log = LoggerFactory.getLogger(ApiLogFilter.class);


    @Override
    public String filterType() {
        return FilterConstants.POST_TYPE;
    }

    @Override
    public int filterOrder() {
        return FilterConstants.SEND_RESPONSE_FILTER_ORDER - 1;
    }

    @Override
    public boolean shouldFilter() {
        HttpServletRequest request = RequestContext.getCurrentContext().getRequest();
        String path = request.getServletPath();
        return path.contains(SignCheckFilter.API_FLAG);
    }

    @Override
    public Object run() throws ZuulException {

        RequestContext context = RequestContext.getCurrentContext();
        // 只记录通过签名的
        if (!context.getBoolean(SignCheckFilter.SIGN_CHECK_RESULT, false)) {
            return null;
        }
        // 请求头
        Map<String, String> reqHeaderMap = new HashMap<>(32);
        HttpServletRequest request = context.getRequest();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            reqHeaderMap.put(name, request.getHeader(name));
        }
        // 响应头
        Map<String, String> respHeaderMap = new HashMap<>(32);
        List<Pair<String, String>> headerList = context.getOriginResponseHeaders();
        for (Pair<String, String> pair : headerList) {
            if (!pair.first().contains("Zuul")) {
                respHeaderMap.put(pair.first(), pair.second());
            }
        }
        HttpLog httpLog = new HttpLog();
        httpLog.setAppId(context.getZuulRequestHeaders().get(SignCheckFilter.APP_ID_HEADER));

        // request部分
        httpLog.setReqTimestamp((Long) context.get(SignCheckFilter.REQ_TIMESTAMP));
        httpLog.setReqClientIp(request.getRemoteAddr());
        httpLog.setReqMethod(request.getMethod().toUpperCase());
        httpLog.setReqHeader(JsonUtil.toJson(reqHeaderMap));
        httpLog.setReqUri(request.getRequestURI());
        httpLog.setReqBody(String.valueOf(context.get(SignCheckFilter.ORIGINAL_REQ_BODY)));

        // response部分
        try {
            InputStream inputStream = context.getResponseDataStream();
            String respBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
            httpLog.setRespBody(respBody);
            context.setResponseBody(respBody);
        } catch (IOException e) {
            // 不抛异常，不影响请求响应
            API_Filter_Log.error("ApiLogFilter read responseBody fail", e);
        }
        httpLog.setRespHeader(JsonUtil.toJson(respHeaderMap));
        httpLog.setRespHttpStatus(String.valueOf(context.getResponseStatusCode()));
        httpLog.setRespTimestamp(System.currentTimeMillis());
        httpLog.setRespMilli(httpLog.getRespTimestamp() - httpLog.getReqTimestamp());

        log.info("", httpLog);
        return null;
    }
}
