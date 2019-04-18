package com.gloryjie.pay.filter;

import com.gloryjie.pay.app.dto.AppDto;
import com.gloryjie.pay.app.error.AppError;
import com.gloryjie.pay.app.service.AppService;
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
import com.gloryjie.pay.model.UniformRequestParam;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.*;

/**
 * @author jie
 * @since 2019/4/18
 */
@Slf4j
@Component
public class SignCheckFilter implements Filter {

    public static final String API_FLAG = "api";

    public static final String APP_ID_HEADER = "appId";

    public static final String SIGN_HEADER = "Pay-Sign";

    public static final String SIGN_CHECK_RESULT = "signCheckFilter.checkResult";

    public static final String ORIGINAL_REQ_BODY = "signCheckFilter.originalBody";


    @Value("${pay.trigger.signCheck:true}")
    public Boolean signCheckTrigger;

    @Autowired
    private AppService appService;

    @Autowired
    private AppFeignApi appFeignApi;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest servletRequest = (HttpServletRequest) request;
        String body = StreamUtils.copyToString(servletRequest.getInputStream(), StandardCharsets.UTF_8);
        servletRequest.setAttribute(ORIGINAL_REQ_BODY, body);
        // 1. 获取统一参数
        UniformRequestParam uniformRequestParam = getUniformRequestParam(body, servletRequest);

        // 2. 检查参数合法性
        checkUniformParam(uniformRequestParam);

        if (signCheckTrigger) {
            // 3. 签名验证
            String sign = servletRequest.getHeader(SIGN_HEADER);
            verifySign(sign, uniformRequestParam);
        }

        // 4. 验证修改请求体数据，只保留业务数据, 并将验证过的appId放进请求头
        servletRequest.setAttribute(APP_ID_HEADER, uniformRequestParam.getAppId());

        RewriteHttpServletRequestWrapper rewriteRequest = null;
        if (DefaultConstant.REQUEST_METHOD.POST.equalsIgnoreCase(servletRequest.getMethod()) && uniformRequestParam.getBizData() != null) {
            String newBody = JsonUtil.toJson(uniformRequestParam.getBizData());
            rewriteRequest = new RewriteHttpServletRequestWrapper(servletRequest, newBody);
            rewriteRequest.putHeader(APP_ID_HEADER, String.valueOf(uniformRequestParam.getAppId()));
        }
        servletRequest.setAttribute(SIGN_CHECK_RESULT, true);

        chain.doFilter(rewriteRequest == null ? servletRequest : rewriteRequest, response);
    }

    private void checkUniformParam(UniformRequestParam param) {
        // 必填项检查
        ParamValidator.validate(param);
        // TODO: 2019/3/6 时间窗口、随机字符串检查
    }

    /**
     * 签名验证
     *
     * @param param
     * @return
     */
    private void verifySign(String sign, UniformRequestParam param) {
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

    private UniformRequestParam getUniformRequestParam(String requestBody, HttpServletRequest request) {
        UniformRequestParam uniformRequestParam;
        try {
            if (DefaultConstant.REQUEST_METHOD.POST.equalsIgnoreCase(request.getMethod())) {
                uniformRequestParam = JsonUtil.parse(requestBody, UniformRequestParam.class);
            } else {
                Map<String, String[]> getParam = request.getParameterMap();
                Map<String, Object> queryParam = new HashMap<>(8);
                for (Map.Entry<String, String[]> entry : getParam.entrySet()) {
                    queryParam.put(entry.getKey(), entry.getValue()[0]);
                }
                uniformRequestParam = JsonUtil.parse(JsonUtil.toJson(queryParam), UniformRequestParam.class);
            }
            uniformRequestParam.setUri(request.getServletPath());
            return uniformRequestParam;
        } catch (Exception e) {
            // 能出现异常的此处只有json序列化了
            log.warn("SignCheckFilter transform json to UniformRequestParam fail");
            throw SystemException.create(CommonErrorEnum.ILLEGAL_ARGUMENT_ERROR, "请求体不合法");
        }
    }

    class RewriteHttpServletRequestWrapper extends HttpServletRequestWrapper {

        byte[] bodyData;

        private final Map<String, String> customHeaders;

        RewriteHttpServletRequestWrapper(HttpServletRequest request, String body) {
            super(request);
            bodyData = body.getBytes(StandardCharsets.UTF_8);
            customHeaders = new HashMap<>(3);
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

        public void putHeader(String name, String value) {
            this.customHeaders.put(name, value);
        }

        @Override
        public String getHeader(String name) {
            String headerValue = customHeaders.get(name);
            if (headerValue != null) {
                return headerValue;
            }
            return super.getHeader(name);
        }

        @Override
        public Enumeration<String> getHeaderNames() {
            Set<String> set = new HashSet<>(customHeaders.keySet());
            @SuppressWarnings("unchecked")
            Enumeration<String> e = ((HttpServletRequest) getRequest()).getHeaderNames();
            while (e.hasMoreElements()) {
                String n = e.nextElement();
                set.add(n);
            }
            return Collections.enumeration(set);
        }
    }

    class ServletInputStreamWrapper extends ServletInputStream {

        private byte[] data;
        private int idx;

        private InputStream inputStream;

        public ServletInputStreamWrapper(byte[] data) {
            this.data = data;
            inputStream = null;
        }

        private InputStream bodyDataInputStream() {
            if (inputStream == null) {
                inputStream = new ByteArrayInputStream(data);
            }
            return inputStream;
        }

        @Override
        public int read() throws IOException {
            idx = bodyDataInputStream().read();
            return idx;
        }

        @Override
        public boolean isFinished() {
            return idx == data.length || idx == -1;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener listener) {

        }
    }
}
