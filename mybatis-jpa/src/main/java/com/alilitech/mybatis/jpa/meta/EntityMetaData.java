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


import com.alilitech.mybatis.jpa.definition.GenericType;
import com.alilitech.mybatis.jpa.util.EntityUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;


/**
 *
 * @author Zhou Xiaoxiang
 * @since 1.0
 */
public class EntityMetaData {

    /** 实体类型 */
    private Class<?> entityType;

    private Class<?> idType;

    /** 表名 */
    private String tableName;

    private String tableAlias;

    private boolean compositePrimaryKey = false;

    /** 主键column元数据 */
    private final List<ColumnMetaData> primaryColumnMetaDatas = new ArrayList<>();

    /** column元数据集 {key-fieldName} */
    private Map<String, ColumnMetaData> columnMetaDataMap;

    private final Set<String> columnNames;

    public EntityMetaData(Class<?> clazz) {
        this.entityType = clazz;

        // 初始化集合
        columnMetaDataMap = new LinkedHashMap<>();
        columnNames = new LinkedHashSet<>();

        init();
    }

    public EntityMetaData(GenericType genericType) {
        this.entityType = (Class<?>)genericType.getDomainType();
        this.idType = (Class<?>)genericType.getIdType();

        // 初始化集合
        columnMetaDataMap = new LinkedHashMap<>();
        columnNames = new HashSet<>();

        init();
    }

    private void init() {
        this.tableName = EntityUtils.getTableName(entityType);
        this.tableAlias = entityType.getSimpleName().substring(0, 1).toLowerCase(Locale.ENGLISH);
        Class<?> compositePrimaryKeyClass = EntityUtils.getCompositePrimaryKeyClass(entityType);
        if(compositePrimaryKeyClass != null) {
//            if(!idType.equals(compositePrimaryKeyClass)) {
//                throw new MybatisJpaException("id type is not equals to composite primary key class!");
//            }
            compositePrimaryKey = true;
        }

        // 持久化字段集
        List<Field> fields = EntityUtils.getPersistentFields(entityType);

        for (Field field : fields) {
            ColumnMetaData columnMetaData = new ColumnMetaData(field, this);
            if(columnMetaData.isPrimaryKey()) {
                primaryColumnMetaDatas.add(columnMetaData);
            }

            columnMetaDataMap.put(field.getName(), columnMetaData);
            if(!columnMetaData.isJoin()) {
                columnNames.add(columnMetaData.getColumnName());
            }
        }
    }

    public Class<?> getEntityType() {
        return entityType;
    }

    public Class<?> getIdType() {
        return idType;
    }

    public String getTableName() {
        return tableName;
    }

    public String getTableAlias() {
        return tableAlias;
    }

    public ColumnMetaData getPrimaryColumnMetaData() {
        if(primaryColumnMetaDatas.size() == 1) {
            return primaryColumnMetaDatas.get(0);
        }
        return null;
    }

    public Map<String, ColumnMetaData> getColumnMetaDataMap() {
        return columnMetaDataMap;
    }

    public void setColumnMetaDataMap(Map<String, ColumnMetaData> columnMetaDataMap) {
        this.columnMetaDataMap = columnMetaDataMap;
    }

    public Set<String> getColumnNames() {
        return columnNames;
    }

    public String getColumnNamesString(){
        return String.join(", ", columnNames);
    }

    public boolean isCompositePrimaryKey() {
        return compositePrimaryKey;
    }

    public String getColumnNamesString(String alias) {
        return columnNames.stream().map(s -> alias + "." + s).collect(Collectors.joining(", "));
//        StringBuilder columnNamesTemp = new StringBuilder();
//        for(ColumnMetaData columnMetaData : columnMetaDataMap.values()) {
//            if(!columnMetaData.isJoin()) {
//                columnNamesTemp.append(", ").append(alias).append(".").append(columnMetaData.getColumnName());
//            }
//        }
//
//        return columnNamesTemp.substring(1);
    }

    public boolean hasPrimaryKey() {
        return !primaryColumnMetaDatas.isEmpty();
    }

    public String getPrimaryCondition() {
        return primaryColumnMetaDatas.stream().map(ColumnMetaData::getProperty).map(s -> Character.toUpperCase(s.charAt(0)) + s.substring(1)).collect(Collectors.joining("And"));
    }

    public List<ColumnMetaData> getPrimaryColumnMetaDatas() {
        return primaryColumnMetaDatas;
    }
}
