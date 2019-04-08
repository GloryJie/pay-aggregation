package com.gloryjie.pay.log.http.dao;

import com.gloryjie.pay.log.http.model.HttpLogRecord;
import com.gloryjie.pay.log.http.model.param.LogQueryParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author jie
 * @since 2019/4/7
 */
@Mapper
public interface HttpLogRecordDao {

    int insert(HttpLogRecord record);

    List<HttpLogRecord> listByParam(LogQueryParam queryParam);

    long countByParam(LogQueryParam param);

    int deleteBeforeTime(Long time);
}
