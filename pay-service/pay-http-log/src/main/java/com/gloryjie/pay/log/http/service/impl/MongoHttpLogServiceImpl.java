package com.gloryjie.pay.log.http.service.impl;

import com.gloryjie.pay.log.http.model.HttpLogRecord;
import com.gloryjie.pay.log.http.model.PageInfo;
import com.gloryjie.pay.log.http.model.param.LogQueryParam;
import com.gloryjie.pay.log.http.service.HttpLogService;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

/**
 * @author jie
 * @since 2019/4/7
 */
public class MongoHttpLogServiceImpl implements HttpLogService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void record(HttpLogRecord logRecord) {
        // 按类型分集合存储
        mongoTemplate.insert(logRecord, logRecord.getType());
    }

    @Override
    public PageInfo<HttpLogRecord> find(LogQueryParam param) {
        Query query = new Query();
        query.addCriteria(Criteria.where("appId").is(param.getAppId()));
        Pageable pageable = PageRequest.of(param.getStartPage() - 1, param.getPageSize(), new Sort(Sort.Direction.DESC, "reqTimestamp"));
        query.with(pageable);
        List<HttpLogRecord> dataList = mongoTemplate.find(query, HttpLogRecord.class, param.getType());
        long total = mongoTemplate.count(query, param.getType());

        PageInfo<HttpLogRecord> recordPageInfo = new PageInfo<>();
        recordPageInfo.setList(dataList);
        recordPageInfo.setStartPage(param.getStartPage());
        recordPageInfo.setPageSize(param.getPageSize());
        recordPageInfo.setTotal(total);

        return recordPageInfo;
    }

    @Override
    public void deleteBeforeTime(String type, @NonNull Long date) {
        Query query = new Query();
        query.addCriteria(Criteria.where("reqTimestamp").lt(date));
        mongoTemplate.remove(query, type);
    }

}
