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

import com.gloryjie.pay.base.constant.DefaultConstant;
import com.gloryjie.pay.base.exception.ErrorInterface;
import com.gloryjie.pay.base.response.Response;
import com.gloryjie.pay.base.util.JsonUtil;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

/**
 * @author Jie
 * @since
 */
public abstract class BaseFilter extends ZuulFilter {

    /**
     * 过滤不通过，直接返回错误信息
     *
     * @param context
     */
    protected void alterResponseWithErrorMsg(RequestContext context, ErrorInterface error) {
        Response response = Response.failure(error);
        context.getResponse().setCharacterEncoding(DefaultConstant.CHARSET);
        context.getResponse().setContentType("application/json;charset=UTF-8");
        context.setSendZuulResponse(false);
        context.setResponseStatusCode(Integer.valueOf(response.getStatus().substring(0, 3)));
        context.setResponseBody(JsonUtil.toJson(response));
    }
}
