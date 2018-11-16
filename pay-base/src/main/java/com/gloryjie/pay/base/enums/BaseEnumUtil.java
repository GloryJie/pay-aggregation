/* ------------------------------------------------------------------
 *   Product:      music-dj
 *   Module Name:  com.gloryjie.common.enums
 *   Package Name: com.gloryjie.common.enums
 *   Date Created: 2018-09-10
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2018-09-10      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.base.enums;


import com.gloryjie.pay.base.enums.base.BaseEnum;

import java.util.Optional;

/**
 * 枚举工具类
 * @author Jie
 * @since 0.1
 */
public class BaseEnumUtil {

    /**
     * 根据code来获取对应的枚举
     * @param code
     * @param enumClass 枚举类型
     * @return Optional对象,可能为空
     */
    public static <E extends Enum<?> & BaseEnum> Optional<E> codeOf(int code, Class<E> enumClass){
        // 遍历指定的枚举
        for (E target : enumClass.getEnumConstants()){
            if (target.getCode() == code){
                // 若匹配则返回相应枚举
                return Optional.of(target);
            }
        }
        return Optional.empty();
    }

}
