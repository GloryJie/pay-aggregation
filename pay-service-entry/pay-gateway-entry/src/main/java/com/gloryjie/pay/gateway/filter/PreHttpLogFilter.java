/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.gateway.filter
 *   Date Created: 2019/3/23
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2019/3/23      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.gateway.filter;

import com.gloryjie.pay.base.util.JsonUtil;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.bson.Document;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Jie
 * @since
 */
@Component
public class PreHttpLogFilter extends ZuulFilter {

    public static final String REQ_TIMESTAMP = "reqTimestamp";

    public static final String REQUEST_PRE_LOG = "PreLogFilter.preLog";

    public static final String NOTIFY_URI_PREFIX = "/platform/notify";


    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return FilterConstants.SEND_RESPONSE_FILTER_ORDER - 1;
    }

    @Override
    public boolean shouldFilter() {
        HttpServletRequest request = RequestContext.getCurrentContext().getRequest();
        String path = request.getServletPath();
        return path.contains(SignCheckFilter.API_FLAG) || path.contains(NOTIFY_URI_PREFIX);
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext context = RequestContext.getCurrentContext();
        Document logDocument = new Document();

        // 请求内容
        Map<String, String> reqHeaderMap = new HashMap<>(32);
        HttpServletRequest request = context.getRequest();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            reqHeaderMap.put(name, request.getHeader(name));
        }
        logDocument.put("reqHeader", JsonUtil.toJson(reqHeaderMap));
        logDocument.put("reqMethod", request.getMethod());
        logDocument.put("reqUri", request.getServletPath());
        logDocument.put("reqClientIp", request.getRemoteAddr());
        logDocument.put(REQ_TIMESTAMP, System.currentTimeMillis());

        context.set(REQUEST_PRE_LOG, logDocument);
        return null;
    }
}
