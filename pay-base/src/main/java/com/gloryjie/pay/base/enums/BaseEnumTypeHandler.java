/* ------------------------------------------------------------------
 *   Product:      music-dj
 *   Module Name:  music-common
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
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.NoSuchElementException;

/**
 * Mybatis统一的枚举类型处理
 * @author Jie
 * @since 0.1
 */
@Slf4j
public class BaseEnumTypeHandler<E extends Enum<?> & BaseEnum> extends BaseTypeHandler<BaseEnum> {

    private Class<E> type;

    /**
     * 获取配置文件中需要转换的枚举类
     * @param type 配置文件中指定的枚举类型
     */
    public BaseEnumTypeHandler(Class<E> type) {
        if (type == null) {
            log.error("music-common[]BaseEnumTypeHandler() type cannot be null");
            throw new IllegalArgumentException("type cannot be null");
        }
        this.type = type;
    }

    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, BaseEnum baseEnum, JdbcType jdbcType) throws SQLException {
        preparedStatement.setInt(i, baseEnum.getCode());
    }

    @Override
    public E getNullableResult(ResultSet resultSet, String columnName) throws SQLException {
        int code = resultSet.getInt(columnName);
        return resultSet.wasNull() ? null : this.codeOf(code);
    }

    @Override
    public E getNullableResult(ResultSet resultSet, int i) throws SQLException {
        int code = resultSet.getInt(i);
        return resultSet.wasNull() ? null : this.codeOf(code);
    }

    @Override
    public E getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        int code = callableStatement.getInt(i);
        return callableStatement.wasNull() ? null : this.codeOf(code);
    }

    private E codeOf(int code) {
        try {
            return BaseEnumUtil.codeOf(code, type).get();
        } catch (NoSuchElementException e) {
            log.info("music-common[]BaseEnumTypeHandler.codeOf cannot convert code={} to type={}", code, type.getSimpleName());
            throw new IllegalArgumentException("cannot convert " + code + " to " + type.getSimpleName() + " by code value", e);
        }
    }
}
