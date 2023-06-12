/*
 *    Copyright 2017-2023 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.alilitech.mybatis.jpa.type;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Zhou Xiaoxiang
 * @since 2.1.1
 */
public class DefaultEnumTypeHandler<E extends Enum<E>> extends BaseTypeHandler<E> {

    private static final Map<Class<? extends Enum<?>>, MyEnumTypeHandler<? extends Enum<?>>> HANDLER_MAP_CACHE = new ConcurrentHashMap<>();

    private final MyEnumTypeHandler<E> delegateHandler;

    public DefaultEnumTypeHandler(Class<E> type) {
        delegateHandler = (MyEnumTypeHandler<E>) HANDLER_MAP_CACHE.computeIfAbsent(type, enumType -> new MyEnumTypeHandler<>((Class<E>) enumType));
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, E parameter, JdbcType jdbcType) throws SQLException {
        delegateHandler.setParameter(ps, i, parameter, jdbcType);
    }

    @Override
    public E getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return delegateHandler.getNullableResult(rs, columnName);
    }

    @Override
    public E getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return delegateHandler.getNullableResult(rs, columnIndex);
    }

    @Override
    public E getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return delegateHandler.getNullableResult(cs, columnIndex);
    }
}
