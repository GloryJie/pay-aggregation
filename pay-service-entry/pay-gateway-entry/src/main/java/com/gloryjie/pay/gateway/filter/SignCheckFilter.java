/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.gateway.filter
 *   Date Created: 2019/3/6
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2019/3/6      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.gateway.filter;

import com.gloryjie.pay.base.constant.DefaultConstant;
import com.gloryjie.pay.base.enums.error.CommonErrorEnum;
import com.gloryjie.pay.base.exception.BaseException;
import com.gloryjie.pay.base.exception.error.ExternalException;
import com.gloryjie.pay.base.exception.error.SystemException;
import com.gloryjie.pay.base.response.Response;
import com.gloryjie.pay.base.util.JsonUtil;
import com.gloryjie.pay.base.util.cipher.Rsa;
import com.gloryjie.pay.base.util.validator.ParamValidator;
import com.gloryjie.pay.gateway.model.UniformRequestParam;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.netflix.zuul.http.HttpServletRequestWrapper;
import com.netflix.zuul.http.ServletInputStreamWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;

/**
 * 签名检查过滤器
 *
 * @author Jie
 * @since
 */
@Slf4j
@Component
public class SignCheckFilter extends ZuulFilter {

    private static final String API_FLAG = "api";

    public static final String APP_ID_HEADER = "app-id";

    public static final String SIGN_HEADER = "Pay-Sign";

    public static final int STREAM_BUFFER_SIZE = 1024;

    @Value("${trigger.signCheck:true}")
    private boolean signCheckTrigger;


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
        return path.contains(API_FLAG);
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();
        UniformRequestParam uniformRequestParam = null;
        boolean checkResult = false;
        try {
            uniformRequestParam = getUniformRequestParam(context);
            ParamValidator.validate(uniformRequestParam);

            // TODO: 2019/3/6 时间窗口、随机字符串重复、签名验证
            String sign = request.getHeader(SIGN_HEADER);
            if (StringUtils.isBlank(sign)) {
                alterResponseWithErrorMsg(context, Response.failure(SystemException.create(CommonErrorEnum.ILLEGAL_ARGUMENT_ERROR, "签名参数为空")));
            }

            if (!signCheckTrigger) {
                checkResult = true;
            } else {
                // TODO: 2019/3/6 通过app服务获取公钥进行延签
                String preparePublicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCAp99zRfykQx6LZvFVHzmSYzxhlz8EH0oECKIXJV/MNroZlClviM1eavn41FUjFH7s+V+vtTZriYAt98ff+jaXonC1G6Ip+aGmPh+ghKi3OtVc4wT4PxDTtIAQHScOF+V45yA9BN4lOD5sPLvFYWesrwGH0KbjeogTJtBiWBniwQIDAQAB";

                String signStr = uniformRequestParam.toSignString();
                byte[] signStrData = signStr.getBytes(StandardCharsets.UTF_8);
                checkResult = Rsa.verifySign(signStrData, preparePublicKey, sign);
                if (!checkResult) {
                    alterResponseWithErrorMsg(context, Response.failure(ExternalException.create(CommonErrorEnum.SIGNATURE_NOT_PASS_ERROR)));
                }
            }

        } catch (BaseException e) {
            // 可能出现的错误为json转换出错，参数校验出错
            alterResponseWithErrorMsg(context, Response.failure(e));
        } catch (NoSuchAlgorithmException | SignatureException | InvalidKeyException | InvalidKeySpecException e) {
            log.warn("SignCheckFilter verify sign fail, appId={}", uniformRequestParam.getAppId(), e);
            // 延签错误
            Response response = Response.failure(ExternalException.create(CommonErrorEnum.SIGNATURE_NOT_PASS_ERROR, e.getMessage()));
            alterResponseWithErrorMsg(context, response);
        } catch (Exception e) {
            // 其他未知错误返回系统内部错误
            log.error("SignCheckFilter unknown exception happen ", e);
            alterResponseWithErrorMsg(context, Response.failure(SystemException.create(CommonErrorEnum.INTERNAL_SYSTEM_ERROR)));
        }


        // 检查通过，则修改请求体数据，只保留业务数据
        if (checkResult) {
            // 将appId放进请求头
            context.addZuulRequestHeader(APP_ID_HEADER, uniformRequestParam.getAppId().toString());
            if (DefaultConstant.REQUEST_METHOD.POST.equalsIgnoreCase(request.getMethod()) && uniformRequestParam.getBizData() != null) {
                String newBody = JsonUtil.toJson(uniformRequestParam.getBizData());
                MyHttpServletRequestWrapper wrapper = new MyHttpServletRequestWrapper(request, newBody);
                context.setRequest(wrapper);
            }
        }

        return null;
    }

    /**
     * 过滤不通过，直接返回错误信息
     *
     * @param context
     * @param response
     */
    private void alterResponseWithErrorMsg(RequestContext context, Response response) {
        context.getResponse().setCharacterEncoding(DefaultConstant.CHARSET);
        context.getResponse().setContentType("application/json;charset=UTF-8");
        context.setSendZuulResponse(false);
        context.setResponseStatusCode(Integer.valueOf(response.getStatus().substring(0, 3)));
        context.setResponseBody(JsonUtil.toJson(response));
    }

    private UniformRequestParam getUniformRequestParam(RequestContext context) {
        HttpServletRequest request = context.getRequest();
        String method = request.getMethod();
        String requestBody;
        UniformRequestParam uniformRequestParam = null;
        try {
            if (DefaultConstant.REQUEST_METHOD.POST.equalsIgnoreCase(method)) {
                requestBody = getRequestBody(request);
                if (StringUtils.isBlank(requestBody)) {
                    // 不对当前请求进行路由
                    Response responseContent = Response.failure(ExternalException.create(CommonErrorEnum.ILLEGAL_ARGUMENT_ERROR, "请求体为空"));
                    alterResponseWithErrorMsg(context, responseContent);
                    return null;
                }
                uniformRequestParam = JsonUtil.parse(requestBody, UniformRequestParam.class);
            } else {
                Map<String, String[]> getParam = request.getParameterMap();
                Map<String, Object> queryParam = new HashMap<>(8);
                for (Map.Entry<String, String[]> entry : getParam.entrySet()) {
                    queryParam.put(entry.getKey(), entry.getValue()[0]);
                }
                uniformRequestParam = JsonUtil.parse(JsonUtil.toJson(queryParam), UniformRequestParam.class);
            }
        } catch (IOException e) {
            log.error("read request body content from InputStream fail", e);
            alterResponseWithErrorMsg(context, Response.failure(SystemException.create(CommonErrorEnum.INTERNAL_SYSTEM_ERROR)));
        }
        if (uniformRequestParam != null) {
            uniformRequestParam.setUri(request.getServletPath());
            return uniformRequestParam;
        }
        return new UniformRequestParam();
    }


    private String getRequestBody(HttpServletRequest request) throws IOException {
        // 此处不能关闭InputStream，否则报错
        InputStream is = request.getInputStream();
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            byte[] dataBuffer = new byte[STREAM_BUFFER_SIZE];
            int count;
            while ((count = is.read(dataBuffer, 0, STREAM_BUFFER_SIZE)) != -1) {
                os.write(dataBuffer, 0, count);
            }
            return os.toString(StandardCharsets.UTF_8.name());
        }
    }

    class MyHttpServletRequestWrapper extends HttpServletRequestWrapper {

        byte[] bodyData;

        MyHttpServletRequestWrapper(HttpServletRequest request, String body) {
            super(request);
            bodyData = body.getBytes(StandardCharsets.UTF_8);
        }

        @Override
        public ServletInputStream getInputStream() throws IOException {
            return new ServletInputStreamWrapper(bodyData);
        }

        @Override
        public int getContentLength() {
            return bodyData.length;
        }

        @Override
        public long getContentLengthLong() {
            return bodyData.length;
        }
    }

}
