/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.gateway.filter
 *   Date Created: 2019/3/19
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2019/3/19      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.gateway.filter;

import com.gloryjie.pay.base.util.JsonUtil;
import com.gloryjie.pay.gateway.model.PlatformNotifyLog;
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
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * 支付平台异步通知日志记录
 *
 * @author Jie
 * @since
 */
@Component
public class PlatformNotifyLogFilter extends ZuulFilter {

    private static Logger log = LoggerFactory.getLogger("HttpLogger");

    private static final Logger PLATFORM_NOTIFY_FILTER_LOG = LoggerFactory.getLogger(PlatformNotifyLogFilter.class);

    public static final String NOTIFY_URI_PREFIX = "/platform/notify";

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
        return path.contains(NOTIFY_URI_PREFIX);
    }

    @Override
    public Object run() throws ZuulException {

        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();

        PlatformNotifyLog notifyLog = new PlatformNotifyLog();

        String path = request.getServletPath();
        notifyLog.setReqUri(path);
        notifyLog.setPlatform(getPlatformName(path));
        // 请求头
        Map<String, String> reqHeaderMap = new HashMap<>(32);
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            reqHeaderMap.put(name, request.getHeader(name));
        }
        notifyLog.setReqHeader(JsonUtil.toJson(reqHeaderMap));

        try {
            String reqBody = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
            String respBody = StreamUtils.copyToString(context.getResponseDataStream(),StandardCharsets.UTF_8);
            notifyLog.setReqBody(reqBody);
            notifyLog.setRespBody(respBody);
            context.setResponseBody(respBody);
        } catch (IOException e) {
            PLATFORM_NOTIFY_FILTER_LOG.warn("record platform async notify log fail", e);
        }
        for (Pair<String,String> pair: context.getOriginResponseHeaders()){
            if ("appId".equals(pair.first())){
                notifyLog.setAppId(pair.second());
                break;
            }
        }
        notifyLog.setRespHttpStatus(String.valueOf(context.getResponseStatusCode()));

        log.info("", notifyLog);
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
