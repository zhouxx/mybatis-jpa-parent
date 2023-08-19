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
package com.alilitech.mybatis.jpa.meta;


import com.alilitech.mybatis.jpa.JoinType;
import com.alilitech.mybatis.jpa.anotation.SubQuery;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;


/**
 *
 * @author Zhou Xiaoxiang
 * @since 1.0
 */
public class JoinColumnMetaData {

    private JoinType joinType;

    private boolean isCollection;

    private Class<? extends Collection> collectionClass;

    /**
     * 当前类的类型
     */
    private Type entityType;

    /**
     * 被关联类的类型
     */
    private Type joinEntityType;

    /**
     * 当前实体类里的引用的属性名称
     * 比如； roles等，不是简单的与数据库映射的字段
     */
    private String currentProperty;

    /**
     * 中间表名称， many to many时候才有
     */
    private String joinTableName;

    /**
     * java字段名称
     * 当OneToMany, 则是关联表的字段
     * 当ManyToMany, 则是中间表的字段
     */
    private String property = "";

    /**
     * 被关联字段类型
     */
    private Class<?> propertyType;

    /**
     * java字段名称
     * 当OneToMany, ManyToMany则是主表的字段
     */
    private String referencedProperty = "";

    /**
     * 主表的关联字段类型
     */
    private Class<?> referencedPropertyType;
    /**
     * 被关联列（database name)
     * 当OneToMany, 则是关联表的列
     * 当ManyToMany, 则是中间表的列
     */
    private String columnName;

    /**
     * 关联字段（database name)
     * 当OneToMany, ManyToMany则是主表的字段
     */
    private String referencedColumnName;



    /**
     * 中间表的java字段名称
     */
    private String inverseProperty = "";

    /**
     * inverse 部分只是many to many 才有
     */
    private Class<?> inversePropertyType;

    /**
     * 与中间表关联的非主表的java字段名称
     */
    private String inverseReferencedProperty = "";

    /**
     * inverse 部分只是many to many 才有
     */
    private Class<?> inverseReferencedPropertyType;

    /**
     * 中间表的database字段名称
     */
    private String inverseColumnName;
    /**
     * 中间表与被关联表关联的database字段名称
     */
    private String inverseReferencedColumnName;

    private String mappedProperty;

    private JoinColumn joinColumn;

    private List<String> excludes;

    private List<String> includes;
    // 是否没有任何关联的查询，只是定义关联关系
    private boolean joinNothing = false;

    private SubQuery subQuery;

    private FetchType fetchType;

    /**
     * 关联表
     */
    private String tableName;

    /**
     * 带index的table alias
     */
    private String tableIndexAlias;

    public JoinType getJoinType() {
        return joinType;
    }

    public void setJoinType(JoinType joinType) {
        this.joinType = joinType;
    }

    public boolean isCollection() {
        return isCollection;
    }

    public void setCollection(boolean collection) {
        isCollection = collection;
    }

    public Class<? extends Collection> getCollectionClass() {
        return collectionClass;
    }

    public void setCollectionClass(Class<? extends Collection> collectionClass) {
        this.collectionClass = collectionClass;
    }

    public Type getEntityType() {
        return entityType;
    }

    public void setEntityType(Type entityType) {
        this.entityType = entityType;
    }

    public Type getJoinEntityType() {
        return joinEntityType;
    }

    public void setJoinEntityType(Type joinEntityType) {
        this.joinEntityType = joinEntityType;
    }

    public String getCurrentProperty() {
        return currentProperty;
    }

    public void setCurrentProperty(String currentProperty) {
        this.currentProperty = currentProperty;
    }

    public String getJoinTableName() {
        return joinTableName;
    }

    public void setJoinTableName(String joinTableName) {
        this.joinTableName = joinTableName;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public Class<?> getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(Class<?> propertyType) {
        this.propertyType = propertyType;
    }

    public String getReferencedProperty() {
        return referencedProperty;
    }

    public void setReferencedProperty(String referencedProperty) {
        this.referencedProperty = referencedProperty;
    }

    public Class<?> getReferencedPropertyType() {
        return referencedPropertyType;
    }

    public void setReferencedPropertyType(Class<?> referencedPropertyType) {
        this.referencedPropertyType = referencedPropertyType;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getReferencedColumnName() {
        return referencedColumnName;
    }

    public void setReferencedColumnName(String referencedColumnName) {
        this.referencedColumnName = referencedColumnName;
    }

    public Class<?> getInversePropertyType() {
        return inversePropertyType;
    }

    public void setInversePropertyType(Class<?> inversePropertyType) {
        this.inversePropertyType = inversePropertyType;
    }

    public String getInverseProperty() {
        return inverseProperty;
    }

    public void setInverseProperty(String inverseProperty) {
        this.inverseProperty = inverseProperty;
    }

    public String getInverseReferencedProperty() {
        return inverseReferencedProperty;
    }

    public void setInverseReferencedProperty(String inverseReferencedProperty) {
        this.inverseReferencedProperty = inverseReferencedProperty;
    }

    public Class<?> getInverseReferencedPropertyType() {
        return inverseReferencedPropertyType;
    }

    public void setInverseReferencedPropertyType(Class<?> inverseReferencedPropertyType) {
        this.inverseReferencedPropertyType = inverseReferencedPropertyType;
    }

    public String getInverseColumnName() {
        return inverseColumnName;
    }

    public void setInverseColumnName(String inverseColumnName) {
        this.inverseColumnName = inverseColumnName;
    }

    public String getInverseReferencedColumnName() {
        return inverseReferencedColumnName;
    }

    public void setInverseReferencedColumnName(String inverseReferencedColumnName) {
        this.inverseReferencedColumnName = inverseReferencedColumnName;
    }

    public String getMappedProperty() {
        return mappedProperty;
    }

    public void setMappedProperty(String mappedProperty) {
        this.mappedProperty = mappedProperty;
    }

    public JoinColumn getJoinColumn() {
        return joinColumn;
    }

    public void setJoinColumn(JoinColumn joinColumn) {
        this.joinColumn = joinColumn;
    }

    public List<String> getExcludes() {
        return excludes;
    }

    public void setExcludes(List<String> excludes) {
        this.excludes = excludes;
    }

    public List<String> getIncludes() {
        return includes;
    }

    public void setIncludes(List<String> includes) {
        this.includes = includes;
    }

    public boolean isJoinNothing() {
        return joinNothing;
    }

    public void setJoinNothing(boolean joinNothing) {
        this.joinNothing = joinNothing;
    }

    public SubQuery getSubQuery() {
        return subQuery;
    }

    public void setSubQuery(SubQuery subQuery) {
        this.subQuery = subQuery;
    }

    public FetchType getFetchType() {
        return fetchType;
    }

    public void setFetchType(FetchType fetchType) {
        this.fetchType = fetchType;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTableIndexAlias() {
        return tableIndexAlias;
    }

    public void setTableIndexAlias(String tableIndexAlias) {
        this.tableIndexAlias = tableIndexAlias;
    }
}
