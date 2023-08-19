/*
 *    Copyright 2017-2022 the original author or authors.
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


import com.alilitech.mybatis.jpa.meta.JoinColumnMetaData;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * 用于生成关联查询的mapping，一个语句可能会有多个mapping
 *
 * @author Zhou Xiaoxiang
 * @since 1.0
 */
public class JoinStatementDefinition {

    private Class<?> resultType;

    private String property;

    private String nestedSelect;

    private Class<?> javaType;

    private MethodDefinition referencedMethodDefinition;

    private String tableName;

    /**
     * 带index的table alias
     */
    private String tableIndexAlias;

    /**
     * 用于设置查询列
     */
    private Set<ColumnDefinition> columnDefinitions;

    public JoinStatementDefinition(JoinColumnMetaData joinColumnMetaData, MethodDefinition referencedMethodDefinition) {
        this.resultType = (Class<?>) joinColumnMetaData.getJoinEntityType();
        this.property = joinColumnMetaData.getCurrentProperty();

        if(joinColumnMetaData.isCollection()) {
            this.javaType = joinColumnMetaData.getCollectionClass();
        } else {
            this.javaType = (Class<?>) joinColumnMetaData.getJoinEntityType();
        }

        this.nestedSelect = referencedMethodDefinition.getNamespace() + "." + referencedMethodDefinition.getMethodName();
        this.referencedMethodDefinition = referencedMethodDefinition;
        this.tableName = joinColumnMetaData.getTableName();
        this.tableIndexAlias = joinColumnMetaData.getTableIndexAlias();
    }

    public Class<?> getResultType() {
        return resultType;
    }

    public String getProperty() {
        return property;
    }

    public String getNestedSelect() {
        return nestedSelect;
    }

    public Class<?> getJavaType() {
        return javaType;
    }

    public MethodDefinition getReferencedMethodDefinition() {
        return referencedMethodDefinition;
    }

    public String getNamespace() {
        return referencedMethodDefinition.getNamespace();
    }

    public Set<ColumnDefinition> getColumnDefinitions() {
        return columnDefinitions;
    }

    public void setColumnDefinitions(Set<ColumnDefinition> columnDefinitions) {
        this.columnDefinitions = columnDefinitions;
    }

    public String getTableName() {
        return tableName;
    }

    public String getTableIndexAlias() {
        return tableIndexAlias;
    }

    /**
     * 构建查询列
     */
    public String buildColumnNamesString() {
        return this.columnDefinitions.stream().map(columnDefinition -> {
            if(columnDefinition.getColumnName().equals(columnDefinition.getOriginalColumnName())) {
                return tableIndexAlias + "." + columnDefinition.getOriginalColumnName();
            } else {
                return tableIndexAlias + "." + columnDefinition.getOriginalColumnName() + " as " + columnDefinition.getColumnName();
            }
        }).collect(Collectors.joining(", "));
    }

    public String buildLeftJoinSqlPart(String mainTableAlias) {
        String middleTableName = referencedMethodDefinition.getJoinTableName();

        String joinTableString;
        String onRightString;
        String onLeftString;
        // 多对多
        if(middleTableName != null && !middleTableName.isEmpty()) {
            String middleTableAlias = this.tableIndexAlias + "_0";
            joinTableString = "( "
                    + middleTableName + " " + middleTableAlias + " join " + tableName + " " + tableIndexAlias
                    + " on " + tableIndexAlias + "." + referencedMethodDefinition.getInverseReferencedColumnName() + " = " + middleTableAlias + "." + referencedMethodDefinition.getInverseColumnName()
                    + " )";

            onRightString = middleTableAlias + "." + referencedMethodDefinition.getColumnName();
            onLeftString = mainTableAlias + "." + referencedMethodDefinition.getReferencedColumnName();
        } else {
            joinTableString = tableName + " " + tableIndexAlias;

            onRightString = tableIndexAlias + "." + referencedMethodDefinition.getReferencedColumnName();
            onLeftString = mainTableAlias + "." + referencedMethodDefinition.getColumnName();
        }

        return "LEFT JOIN " + joinTableString + " ON " + onLeftString + " = " + onRightString;
    }
}
