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
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Jie
 * @since
 */
@Repository
public interface EventNotifyDao {

    int insert(EventNotify record);

    EventNotify getByEventNo(String eventNo);

    EventNotify getBySourceNo(String sourceNo);

    int update(EventNotify record);

    /**
     * 列出树范围内的的记录
     * @return
     */
    List<EventNotify> listByAppTree(@Param("minAppId") Integer minAppId, @Param("maxAppId")Integer maxAppId);

}
