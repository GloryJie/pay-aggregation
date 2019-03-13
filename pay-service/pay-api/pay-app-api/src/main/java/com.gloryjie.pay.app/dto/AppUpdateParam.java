/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.app.dto
 *   Date Created: 2019/2/7
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2019/2/7      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.app.dto;

import com.gloryjie.pay.app.enums.AppStatus;
import lombok.Data;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * 运行app更新的字段
 * @author Jie
 * @since
 */
@Data
public class AppUpdateParam {

    /**
     * 应用id
     */
    private Integer appId;

    /**
     * 应用名
     */
    @Size(min = 2, max = 32)
    @Pattern(regexp = "^[a-zA-Z\\u4e00-\\u9fa5]+$",message = "应用名只能是中英文")
    private String name;

    /**
     * 应用描述
     */
    @Size(min = 4, max = 128)
    @Pattern(regexp = "^[a-z0-9A-Z\\u4e00-\\u9fa5]+$",message = "描述只能是中英文或数字，不得包含空格")
    private String description;

    /**
     * 应用状态 0 停用 1 启用
     */
    private AppStatus status;


    /**
     * 交易公钥
     */
    private String tradePublicKey;

}
