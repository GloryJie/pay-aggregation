/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.gateway.model
 *   Date Created: 2019/3/19
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2019/3/19      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.gateway.model;

import lombok.Data;

/**
 * @author Jie
 * @since
 */
@Data
public class PlatformNotifyLog {

    private String appId;

    private String reqUri;

    private String reqBody;

    private String platform;

    private String reqHeader;

    private String respHttpStatus;

    private String respBody;

}
