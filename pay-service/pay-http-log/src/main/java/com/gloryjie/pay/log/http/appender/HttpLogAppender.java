/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.gateway.log
 *   Date Created: 2019/3/18
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2019/3/18      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.log.http.appender;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import com.gloryjie.pay.log.http.model.HttpLogRecord;
import com.gloryjie.pay.log.http.service.HttpLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


/**
 * 输出日志进MongoDB
 *
 * @author Jie
 * @since
 */
@Slf4j
@Component
public class HttpLogAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {

    @Override
    protected void append(ILoggingEvent loggingEvent) {
        HttpLogService logService;
        try {
            logService = ApplicationContextProvider.getBean(HttpLogService.class);
        } catch (Exception e) {
            log.error("AsyncAppender get HttpLogService bean fail", e);
            return;
        }
        Object[] data = loggingEvent.getArgumentArray();
        if (data == null || data.length < 1) {
            return;
        }
        logService.record((HttpLogRecord) data[0]);
    }

}
