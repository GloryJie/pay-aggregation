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

import ch.qos.logback.core.UnsynchronizedAppenderBase;
import org.slf4j.event.LoggingEvent;

/**
 * @author Jie
 * @since
 */
public class MongoAsyncAppender extends UnsynchronizedAppenderBase<LoggingEvent> {


    @Override
    protected void append(LoggingEvent loggingEvent) {

    }
}
