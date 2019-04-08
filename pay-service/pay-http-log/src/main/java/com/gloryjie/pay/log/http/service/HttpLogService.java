package com.gloryjie.pay.log.http.service;

import com.gloryjie.pay.log.http.model.HttpLogRecord;
import com.gloryjie.pay.log.http.model.PageInfo;
import com.gloryjie.pay.log.http.model.param.LogQueryParam;
import lombok.NonNull;

/**
 * @author jie
 * @since 2019/4/7
 */
public interface HttpLogService {

    /**
     * 记录日志
     * @param logRecord 实际的日志记录
     */
    void record(HttpLogRecord logRecord);

    /**
     * 查询日志
     * @return 分页信息,内含实际数据
     */
    PageInfo<HttpLogRecord> find(LogQueryParam param);


    /**
     * 删除时间范围内的日志
     * @param type
     * @param date 13位的时间戳
     */
    void deleteBeforeTime(String type, @NonNull Long date);
}
