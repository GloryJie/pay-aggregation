/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.gateway.model
 *   Date Created: 2019/3/18
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2019/3/18      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.gateway.model;

import lombok.Data;

/**
 * @author Jie
 * @since
 */
@Data
public class HttpLog {

    private String appId;

    private Boolean liveMode;

    private Long reqTimestamp;

    private String reqClientIp;

    private String reqUri;

    private String reqBody;

    private String reqMethod;

    private String reqHeader;

    private Long respMilli;

    private String respHttpStatus;

    private String respHeader;

    private String respBody;

    private Long respTimestamp;

}
