/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.notification.dao
 *   Date Created: 2019/1/31
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2019/1/31      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.notification.dao;

import com.gloryjie.pay.notification.model.EventNotify;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Jie
 * @since
 */
@Mapper
public interface EventNotifyDao {

    int insert(EventNotify record);

    EventNotify getByEventNo(String eventNo);

    EventNotify getBySourceNo(String sourceNo);

    int update(EventNotify record);

}
