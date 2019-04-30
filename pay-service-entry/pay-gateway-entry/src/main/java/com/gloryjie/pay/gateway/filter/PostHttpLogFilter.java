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
import com.gloryjie.pay.log.http.enums.HttpLogType;
import com.gloryjie.pay.log.http.model.HttpLogRecord;
import com.netflix.util.Pair;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 记录API接口请求日志
 *
 * @author Jie
 * @since
 */
@Slf4j
@Component
public class PostHttpLogFilter extends ZuulFilter {

    private static Logger HTTP_LOG = LoggerFactory.getLogger("HttpLogger");

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
        return path.contains(SignCheckFilter.API_FLAG) || path.contains(PreHttpLogFilter.NOTIFY_URI_PREFIX);
    }

    @Override
    public Object run() throws ZuulException {

        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();
        HttpLogRecord logDocument = (HttpLogRecord) context.get(PreHttpLogFilter.REQUEST_PRE_LOG);
        String path = request.getServletPath();

        if (path.contains(SignCheckFilter.API_FLAG)) {
            // 只记录通过签名的
            if (!context.getBoolean(SignCheckFilter.SIGN_CHECK_RESULT, false)) {
                return null;
            }
        }
        // 响应头
        Map<String, String> respHeaderMap = new HashMap<>(32);
        List<Pair<String, String>> headerList = context.getOriginResponseHeaders();
        for (Pair<String, String> pair : headerList) {
            if ("appId".equals(pair.first())) {
                logDocument.setAppId(Integer.valueOf(pair.second()));
                continue;
            }
            if (!pair.first().contains("Zuul")) {
                respHeaderMap.put(pair.first(), pair.second());
            }
        }
        logDocument.setReqHeader(JsonUtil.toJson(respHeaderMap));
        logDocument.setRespHttpStatus(String.valueOf(context.getResponseStatusCode()));
        long respTimestamp = System.currentTimeMillis();
        logDocument.setRespTimestamp(respTimestamp);
        logDocument.setRespMilli(respTimestamp - logDocument.getReqTimestamp());


        try {
            String respBody = StreamUtils.copyToString(context.getResponseDataStream(), StandardCharsets.UTF_8);
            context.setResponseBody(respBody);
            logDocument.setRespBody(respBody);

            if (path.contains(SignCheckFilter.API_FLAG)) {
                logDocument.setAppId(Integer.valueOf(context.getZuulRequestHeaders().get(SignCheckFilter.APP_ID_HEADER)));
                logDocument.setReqBody(String.valueOf(context.get(SignCheckFilter.ORIGINAL_REQ_BODY)));
                logDocument.setType(HttpLogType.API_REQUEST.name().toLowerCase());
                HTTP_LOG.info(logDocument.getType(), logDocument);
            } else {
                String reqBody = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
                logDocument.setReqBody(reqBody);
                logDocument.setPlatform(getPlatformName(path));
                logDocument.setType(HttpLogType.PLATFORM_NOTIFY_REQUEST.name().toLowerCase());
                HTTP_LOG.info(logDocument.getType(), logDocument);
            }
        } catch (IOException e) {
            log.error("post log filter read reqBody or respBody fail", e);
        }

        return null;
    }

    private String getPlatformName(String path) {
        if (path.contains("alipay")) {
            return "支付宝";
        } else if (path.contains("wxpay")) {
            return "微信支付";
        } else if (path.contains("unionpay")) {
            return "银联";
        }
        return "";
    }
}
