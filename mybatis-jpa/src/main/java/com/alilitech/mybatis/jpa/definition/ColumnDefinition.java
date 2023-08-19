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
package com.alilitech.mybatis.jpa.definition;

import com.alilitech.mybatis.jpa.meta.ColumnMetaData;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import java.util.Objects;

/**
 * 所有的definition都是可以会构造的。哪怕不存在
 * Column definition主要是为了构造查询列和构造resultMap
 *
 * @author Zhou Xiaoxiang
 * @since 2.2
 */
public class ColumnDefinition {

    /**
     * 实际查询的列
     */
    private String originalColumnName;

    /**
     * 跟resultMap里的property对应
     */
    private String property;

    /**
     * 跟resultMap里的column对应
     */
    private String columnName;

    /** fieldType */
    private Class<?> javaType;

    /** mybatis jdbcTypeAlias */
    private String jdbcTypeAlias;

    /** mybatis jdbcType */
    private JdbcType jdbcType;

    /** mybatis typeHandler */
    private Class<? extends TypeHandler<?>> typeHandler;

    private final boolean id;

    public ColumnDefinition(ColumnMetaData columnMetaData) {
        this.originalColumnName = columnMetaData.getColumnName();
        this.property = columnMetaData.getProperty();
        this.columnName = originalColumnName;
        this.javaType = columnMetaData.getType();
        this.jdbcType = columnMetaData.getJdbcType();
        this.jdbcTypeAlias = columnMetaData.getJdbcTypeAlias();
        this.typeHandler = columnMetaData.getTypeHandler();
        this.id = columnMetaData.isPrimaryKey();
    }

    public String getOriginalColumnName() {
        return originalColumnName;
    }

    public String getProperty() {
        return property;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public Class<?> getJavaType() {
        return javaType;
    }

    public String getJdbcTypeAlias() {
        return jdbcTypeAlias;
    }

    public JdbcType getJdbcType() {
        return jdbcType;
    }

    public Class<? extends TypeHandler<?>> getTypeHandler() {
        return typeHandler;
    }

    public boolean isId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ColumnDefinition that = (ColumnDefinition) o;
        return Objects.equals(originalColumnName, that.originalColumnName) && Objects.equals(columnName, that.columnName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(originalColumnName, columnName);
    }
}
