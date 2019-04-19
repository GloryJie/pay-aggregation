package com.gloryjie.pay.filter;


import com.gloryjie.pay.base.util.JsonUtil;
import com.gloryjie.pay.log.http.enums.HttpLogType;
import com.gloryjie.pay.log.http.model.HttpLogRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jie
 * @since 2019/4/18
 */
public class HttpLogFilter implements Filter {

    private static Logger HTTP_LOG = LoggerFactory.getLogger("HttpLogger");

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest servletRequest = (HttpServletRequest) request;
        HttpLogRecord logDocument = new HttpLogRecord();

        // 请求内容
        Map<String, String> reqHeaderMap = new HashMap<>(32);
        Enumeration<String> headerNames = servletRequest.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            reqHeaderMap.put(name, servletRequest.getHeader(name));
        }
        logDocument.setReqHeader(JsonUtil.toJson(reqHeaderMap));
        logDocument.setReqMethod(servletRequest.getMethod());
        logDocument.setReqUri(servletRequest.getServletPath());
        logDocument.setReqClientIp(request.getRemoteAddr());
        logDocument.setReqTimestamp(System.currentTimeMillis());

        // 响应体
        BodyCachingHttpServletResponseWrapper responseWrapper = new BodyCachingHttpServletResponseWrapper((HttpServletResponse) response);
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(servletRequest);
        chain.doFilter(requestWrapper, responseWrapper);

        // 响应内容
        HttpServletResponse servletResponse = (HttpServletResponse) response;
        String path = logDocument.getReqUri();

        if (path.contains(SignCheckFilter.API_FLAG)) {
            // 只记录通过签名的
            if (requestWrapper.getAttribute(SignCheckFilter.SIGN_CHECK_RESULT) == null) {
                return;
            }
        }

        // 响应头
        Map<String, String> respHeaderMap = new HashMap<>(32);
        servletResponse.getHeaderNames().forEach(name-> respHeaderMap.put(name, servletResponse.getHeader(name)));
        logDocument.setRespHeader(JsonUtil.toJson(respHeaderMap));
        logDocument.setRespHttpStatus(String.valueOf(servletResponse.getStatus()));
        long respTimestamp = System.currentTimeMillis();
        logDocument.setRespTimestamp(respTimestamp);
        logDocument.setRespMilli(respTimestamp - logDocument.getReqTimestamp());

        String respBody = new String(responseWrapper.getBody(),StandardCharsets.UTF_8);
        logDocument.setRespBody(respBody);

        logDocument.setAppId(String.valueOf(servletRequest.getAttribute("appId")));

        if (path.contains(SignCheckFilter.API_FLAG)) {
            logDocument.setReqBody(String.valueOf(servletRequest.getAttribute(SignCheckFilter.ORIGINAL_REQ_BODY)));
            logDocument.setType(HttpLogType.API_REQUEST.name().toLowerCase());
            HTTP_LOG.info(logDocument.getType(), logDocument);
        } else {
            logDocument.setAppId(respHeaderMap.get("appId"));
            logDocument.setReqBody(new String(requestWrapper.getContentAsByteArray(),StandardCharsets.UTF_8));
            logDocument.setPlatform(getPlatformName(path));
            logDocument.setType(HttpLogType.PLATFORM_NOTIFY_REQUEST.name().toLowerCase());
            HTTP_LOG.info(logDocument.getType(), logDocument);
        }
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

    class BodyCachingHttpServletResponseWrapper extends HttpServletResponseWrapper {

        private ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        private HttpServletResponse response;

        public BodyCachingHttpServletResponseWrapper(HttpServletResponse response) {
            super(response);
            this.response = response;
        }

        public byte[] getBody() {
            return byteArrayOutputStream.toByteArray();
        }

        @Override
        public ServletOutputStream getOutputStream() {
            return new ServletOutputStreamWrapper(this.byteArrayOutputStream , this.response);
        }

        @Override
        public PrintWriter getWriter() throws IOException {
            return new PrintWriter(new OutputStreamWriter(this.byteArrayOutputStream , this.response.getCharacterEncoding()));
        }

        private class ServletOutputStreamWrapper extends ServletOutputStream {

            private ByteArrayOutputStream outputStream;
            private HttpServletResponse response;

            public ServletOutputStreamWrapper(ByteArrayOutputStream outputStream, HttpServletResponse response) {
                this.outputStream = outputStream;
                this.response = response;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setWriteListener(WriteListener listener) {

            }

            @Override
            public void write(int b) throws IOException {
                this.outputStream.write(b);
            }

            @Override
            public void flush() throws IOException {
                if (! this.response.isCommitted()) {
                    byte[] body = this.outputStream.toByteArray();
                    ServletOutputStream outputStream = this.response.getOutputStream();
                    outputStream.write(body);
                    outputStream.flush();
                }
            }
        }
    }
}
