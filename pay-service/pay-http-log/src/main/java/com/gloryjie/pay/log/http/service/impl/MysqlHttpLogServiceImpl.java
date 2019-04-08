package com.gloryjie.pay.log.http.service.impl;

import com.gloryjie.pay.log.http.dao.HttpLogRecordDao;
import com.gloryjie.pay.log.http.model.HttpLogRecord;
import com.gloryjie.pay.log.http.model.PageInfo;
import com.gloryjie.pay.log.http.model.param.LogQueryParam;
import com.gloryjie.pay.log.http.service.HttpLogService;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author jie
 * @since 2019/4/7
 */
public class MysqlHttpLogServiceImpl implements HttpLogService {

    @Autowired
    private HttpLogRecordDao recordDao;

    @Override
    public void record( HttpLogRecord logRecord) {
        recordDao.insert(logRecord);
    }

    @Override
    public PageInfo<HttpLogRecord> find(LogQueryParam param) {
        param.setOffset((param.getStartPage() - 1) * param.getPageSize());
        long total = recordDao.countByParam(param);
        List<HttpLogRecord> recordList = recordDao.listByParam(param);

        PageInfo<HttpLogRecord> pageInfo = new PageInfo<>();
        pageInfo.setList(recordList);
        pageInfo.setTotal(total);
        pageInfo.setStartPage(param.getStartPage());
        pageInfo.setPageSize(param.getPageSize());
        return pageInfo;
    }

    @Override
    public void deleteBeforeTime(String type, @NonNull Long date) {
        recordDao.deleteBeforeTime(date);
    }
}
