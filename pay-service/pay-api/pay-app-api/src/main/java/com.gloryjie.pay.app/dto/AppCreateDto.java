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

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * @author Jie
 * @since
 */
@Data
public class AppCreateDto {

    @NotNull
    @Size(min = 2, max = 32)
    @Pattern(regexp = "^[a-zA-Z\\u4e00-\\u9fa5]+$",message = "应用名只能是中英文")
    private String name;

    @NotNull
    @Size(min = 4, max = 128)
    @Pattern(regexp = "^[a-z0-9A-Z\\u4e00-\\u9fa5]+$",message = "描述只能是中英文或数字，不能有空格")
    private String description;

    /**
     * 负责人用户号
     */
    private Long responsibleUserNo;
}
