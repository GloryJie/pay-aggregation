/* ------------------------------------------------------------------
 *   Product:      music-dj
 *   Module Name:  com.gloryjie.common.annotation
 *   Package Name: com.gloryjie.common.annotation
 *   Date Created: 2018-09-16
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2018-09-16      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.base.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Model和Dto之间转换时忽略指定的字段
 *
 * @author Jie
 * @since 0.1
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface IgnoreCovertProperty {

}
