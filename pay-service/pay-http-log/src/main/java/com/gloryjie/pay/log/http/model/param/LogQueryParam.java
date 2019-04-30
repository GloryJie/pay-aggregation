package com.gloryjie.pay.log.http.model.param;

import lombok.Data;

/**
 * @author jie
 * @since 2019/4/7
 */
@Data
public class LogQueryParam {

    private Integer appId;

    private String type;

    private Integer startPage;

    private Integer pageSize;

    private Integer offset;

    private Long startTime;

    private Long endTime;

    private Integer maxAppId;

}
