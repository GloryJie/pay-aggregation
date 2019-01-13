/* ------------------------------------------------------------------
 *   Product:      pay
 *   Module Name:  COMMON
 *   Package Name: com.gloryjie.pay.base.mybatis
 *   Date Created: 2019/1/13
 * ------------------------------------------------------------------
 * Modification History
 * DATE            Name           Contact
 * ------------------------------------------------------------------
 * 2019/1/13      Jie            GloryJie@163.com
 */
package com.gloryjie.pay.base.mybatis;

import com.fasterxml.jackson.core.type.TypeReference;
import com.gloryjie.pay.base.util.JsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 数据库中JSON数据字段和map转换
 *
 * @author Jie
 * @since 0.1
 */
@MappedTypes(Map.class)
@MappedJdbcTypes(JdbcType.VARCHAR)
public class MapTypeHandler<T> extends BaseTypeHandler<T> {

    private TypeReference<HashMap<String, String>> typeReference = new TypeReference<HashMap<String, String>>() {
    };

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException {
        if (parameter == null) {
            ps.setNull(i, Types.VARCHAR);
        } else {
            ps.setString(i, JsonUtil.toJson(parameter));
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public T getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String result = rs.getString(columnName);
        return (StringUtils.isBlank(result) ? null : (T) JsonUtil.parse(result, typeReference));
    }

    @Override
    @SuppressWarnings("unchecked")
    public T getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String result = rs.getString(columnIndex);
        return (StringUtils.isBlank(result) ? null : (T) JsonUtil.parse(result, typeReference));
    }

    @Override
    @SuppressWarnings("unchecked")
    public T getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String result = cs.getString(columnIndex);
        return (StringUtils.isBlank(result) ? null : (T) JsonUtil.parse(result, typeReference));
    }

}
