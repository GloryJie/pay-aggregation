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
package com.gloryjie.pay.gateway.log;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;


/**
 * 输出日志进MongoDB
 *
 * @author Jie
 * @since
 */
@Component
public class MongoAsyncAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {


    @Override
    protected void append(ILoggingEvent loggingEvent) {
        MongoTemplate mongoTemplate = ApplicationContextProvider.getBean(MongoTemplate.class);
        Object[] data = loggingEvent.getArgumentArray();
        if (data == null || data.length < 1) {
            return;
        }
        String collectionName = loggingEvent.getMessage();
        mongoTemplate.insert(data[0], collectionName);

    }

}
