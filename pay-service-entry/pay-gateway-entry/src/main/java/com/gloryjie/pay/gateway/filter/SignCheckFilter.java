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

import com.gloryjie.pay.app.dto.AppDto;
import com.gloryjie.pay.app.error.AppError;
import com.gloryjie.pay.app.service.api.AppFeignApi;
import com.gloryjie.pay.base.constant.DefaultConstant;
import com.gloryjie.pay.base.enums.error.CommonErrorEnum;
import com.gloryjie.pay.base.exception.error.ExternalException;
import com.gloryjie.pay.base.exception.error.SystemException;
import com.gloryjie.pay.base.util.BeanConverter;
import com.gloryjie.pay.base.util.JsonUtil;
import com.gloryjie.pay.base.util.SignUtil;
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
import org.springframework.beans.factory.annotation.Autowired;
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

    public static final String API_FLAG = "api";

    public static final String APP_ID_HEADER = "app-id";

    public static final String SIGN_HEADER = "Pay-Sign";

    public static final int STREAM_BUFFER_SIZE = 1024;

    public static final String ORIGINAL_REQ_BODY = "signCheckFilter.originalBody";

    public static final String SIGN_CHECK_RESULT = "signCheckFilter.checkResult";



    @Value("${pay.trigger.signCheck:true}")
    private boolean signCheckTrigger;

    @Autowired
    private AppFeignApi appFeignApi;


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

        // 1. 获取统一参数
        UniformRequestParam uniformRequestParam = getUniformRequestParam(context);

        // 2. 检查参数合法性
        checkUniformParam(uniformRequestParam);

        if (signCheckTrigger) {
            // 3. 签名验证
            verifySign(context, uniformRequestParam);
        }

        // 4. 验证修改请求体数据，只保留业务数据, 并将验证过的appId放进请求头
        context.addZuulRequestHeader(APP_ID_HEADER, uniformRequestParam.getAppId().toString());
        HttpServletRequest request = context.getRequest();
        if (DefaultConstant.REQUEST_METHOD.POST.equalsIgnoreCase(request.getMethod()) && uniformRequestParam.getBizData() != null) {
            String newBody = JsonUtil.toJson(uniformRequestParam.getBizData());
            MyHttpServletRequestWrapper wrapper = new MyHttpServletRequestWrapper(request, newBody);
            context.setRequest(wrapper);
        }

        context.set(SIGN_CHECK_RESULT, true);

        return null;
    }

    /**
     * 签名验证
     *
     * @param context
     * @param param
     * @return
     */
    private void verifySign(RequestContext context, UniformRequestParam param) {
        String sign = context.getRequest().getHeader(SIGN_HEADER);
        if (StringUtils.isBlank(sign)) {
            throw SystemException.create(CommonErrorEnum.ILLEGAL_ARGUMENT_ERROR, "签名参数为空");
        }
        // 获取公钥
        AppDto appDto = appFeignApi.getAppInfo(param.getAppId());
        if (appDto == null || StringUtils.isBlank(appDto.getTradePublicKey())) {
            throw ExternalException.create(AppError.APP_NOT_EXISTS, "或公钥未配置");
        }
        byte[] signStrData = SignUtil.toSignStr(BeanConverter.beanToMap(param)).getBytes(StandardCharsets.UTF_8);
        boolean verifyResult = false;
        try {
            verifyResult = Rsa.verifySign(signStrData, appDto.getTradePublicKey(), sign);
        } catch (IllegalArgumentException | NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException | SignatureException e) {
            log.warn("SignCheckFilter verify sign error", e);
            throw ExternalException.create(CommonErrorEnum.SIGNATURE_NOT_PASS_ERROR, e.getMessage());
        }
        if (!verifyResult) {
            log.info("appId={} request={} sign not through", param.getAppId(), param.getUri());
            throw ExternalException.create(CommonErrorEnum.SIGNATURE_NOT_PASS_ERROR);
        }
    }


    /**
     * 检查必填参数的合法性
     *
     * @param param
     * @return
     */
    private void checkUniformParam(UniformRequestParam param) {
        // 必填项检查
        ParamValidator.validate(param);
        // TODO: 2019/3/6 时间窗口、随机字符串检查
    }


    /**
     * 获取统一格式的参数
     *
     * @param context
     * @return
     */
    private UniformRequestParam getUniformRequestParam(RequestContext context) {
        HttpServletRequest request = context.getRequest();
        UniformRequestParam uniformRequestParam;
        try {
            if (DefaultConstant.REQUEST_METHOD.POST.equalsIgnoreCase(request.getMethod())) {
                String requestBody = getRequestBody(request);
                context.set(ORIGINAL_REQ_BODY, requestBody);
                uniformRequestParam = JsonUtil.parse(requestBody, UniformRequestParam.class);
            } else {
                context.set(ORIGINAL_REQ_BODY, "N/A");
                Map<String, String[]> getParam = request.getParameterMap();
                Map<String, Object> queryParam = new HashMap<>(8);
                for (Map.Entry<String, String[]> entry : getParam.entrySet()) {
                    queryParam.put(entry.getKey(), entry.getValue()[0]);
                }
                // TODO: 2019/3/17 当前需要数据转换需要优化
                uniformRequestParam = JsonUtil.parse(JsonUtil.toJson(queryParam), UniformRequestParam.class);
            }
            uniformRequestParam.setUri(request.getServletPath());
            return uniformRequestParam;
        } catch (IOException e) {
            log.error("read request body content from InputStream fail", e);
            throw SystemException.create(CommonErrorEnum.INTERNAL_SYSTEM_ERROR);
        } catch (Exception e) {
            // 能出现异常的此处只有json序列化了
            log.warn("SignCheckFilter transform json to UniformRequestParam fail");
            throw SystemException.create(CommonErrorEnum.ILLEGAL_ARGUMENT_ERROR, "请求体不合法");
        }
    }

    /**
     * 从Request实例中获取请求体
     *
     * @param request
     * @return
     * @throws IOException
     */
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
