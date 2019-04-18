/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.gateway.model
 *   Date Created: 2019/3/6
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2019/3/6      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * 统一请求参数
 *
 * @author Jie
 * @since
 */
@Data
public class UniformRequestParam {

    @NotNull
    private Integer appId;

    @NotBlank
    private String uri;

    @NotBlank
    private String nonce;

    @NotNull
    private Long timestamp;

    private Map<String, Object> bizData;

}
