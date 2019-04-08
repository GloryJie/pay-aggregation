package com.gloryjie.pay.log.http.enums;

import lombok.Getter;

/**
 * @author jie
 * @since 2019/4/7
 */
@Getter
public enum  HttpLogType {

    /**
     * 重要的通知日志类型
     */
    API_REQUEST(0,"api接口请求日志"),
    PLATFORM_NOTIFY_REQUEST(1,"支付平台的异步通知日志");

    private int code;

    private String desc;

    HttpLogType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
