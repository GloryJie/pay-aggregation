/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.base.lock
 *   Date Created: 2018/11/11
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2018/11/11      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.base.lock;

import com.gloryjie.pay.base.enums.error.CommonErrorEnum;
import com.gloryjie.pay.base.exception.error.SystemException;
import lombok.Data;

import java.util.Iterator;

/**
 * @author Jie
 * @since
 */
@Data
public class ParamToken implements Iterator<ParamToken> {

    private String name;
    private String children;

    public ParamToken(String fullName) {
        int delim = fullName.indexOf('.');
        if (delim > -1) {
            name = fullName.substring(0, delim);
            children = fullName.substring(delim + 1);
        } else {
            name = fullName;
            children = null;
        }
    }

    @Override
    public boolean hasNext() {
        return children != null;
    }

    @Override
    public ParamToken next() {
        return new ParamToken(children);
    }

    @Override
    public void remove() {
        throw SystemException.create(CommonErrorEnum.INTERNAL_SYSTEM_ERROR, " ParamToken not support remove operation");
    }
}
