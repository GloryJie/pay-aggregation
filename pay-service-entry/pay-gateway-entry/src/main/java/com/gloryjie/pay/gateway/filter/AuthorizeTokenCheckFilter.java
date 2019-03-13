/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.gateway.filter
 *   Date Created: 2019/3/13
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2019/3/13      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.gateway.filter;

import com.gloryjie.pay.base.enums.error.CommonErrorEnum;
import com.gloryjie.pay.base.exception.error.ExternalException;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * token检查
 *
 * @author Jie
 * @since
 */
@Component
public class AuthorizeTokenCheckFilter extends BaseFilter {

    private static final String AUTH_WEB_FLAG = "web";

    private static final String AUTH_HEADER = "Authorization";

    private static final String TOKEN_PREFIX = "Bearer";

    @Value("${pay.trigger.token-check:true}")
    private boolean tokenCheckTrigger;

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        HttpServletRequest request = RequestContext.getCurrentContext().getRequest();
        String path = request.getServletPath();
        return path.contains(AUTH_WEB_FLAG);
    }

    @Override
    public Object run() throws ZuulException {
        if (!tokenCheckTrigger) {
            return null;
        }
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();
        String token = request.getHeader(AUTH_HEADER);
        // TODO: 2019/3/13 还需要验证token的合法性，后续使用JWT自检
        if (StringUtils.isBlank(token) || !token.contains(TOKEN_PREFIX)) {
            alterResponseWithErrorMsg(context, ExternalException.create(CommonErrorEnum.NOT_LOGIN_ERROR));
        }
        return null;
    }
}
