package com.gloryjie.pay.log.http.model;

import lombok.Data;
import org.springframework.data.annotation.Id;

/**
 * @author jie
 * @since 2019/4/7
 */
@Data
public class HttpLogRecord {
    @Id
    private String id;

    private String type;

    private String appId;

    private String platform;

    private Boolean liveMode;

    private Long reqTimestamp;

    private String reqClientIp;

    private String reqUri;

    private String reqBody;

    private String reqMethod;

    private String reqHeader;

    private Long respMilli;

    private String respHttpStatus;

    private String respHeader;

    private String respBody;

    private Long respTimestamp;

}
