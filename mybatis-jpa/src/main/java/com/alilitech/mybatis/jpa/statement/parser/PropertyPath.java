/*
 *    Copyright 2008-2019 the original author or authors.
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
package com.alilitech.mybatis.jpa.statement.parser;


import com.alilitech.mybatis.jpa.EntityMetaDataRegistry;
import com.alilitech.mybatis.jpa.definition.MethodDefinition;
import com.alilitech.mybatis.jpa.exception.PropertyNotFoundException;
import com.alilitech.mybatis.jpa.meta.ColumnMetaData;
import com.alilitech.mybatis.jpa.meta.EntityMetaData;
import com.alilitech.mybatis.jpa.util.CommonUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 *
 * @author Oliver Gierke
 * @author Martin Baumgartner
 * @author Zhou Xiaoxiang
 * @since 1.0
 */
public class PropertyPath {

    private static final Pattern NESTED_PROPERTY_PATTERN = Pattern.compile("(?=[A-Z])|(?<=[a-z])(?=[A-Z])");

    private Class<?> entityClass;

    private String name;

    private String columnName;

    public PropertyPath(String name) {
        this.name = name;
    }

    public Class<?> getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(Class<?> entityClass) {
        this.entityClass = entityClass;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public static PropertyPath from(String sourceToParse, Optional<Class<?>> clazzOptional, MethodDefinition methodDefinition) {
        PropertyPath propertyPath = new PropertyPath(sourceToParse);
        String columnName = "";
        Class<?> entityClass = null;
        if(!clazzOptional.isPresent()) {
            columnName = CommonUtils.camelToUnderline(sourceToParse);
        } else {
            EntityMetaData entityMetaData = EntityMetaDataRegistry.getInstance().get(clazzOptional.get());
            Map<String, ColumnMetaData> columnMetaDataMap = entityMetaData.getColumnMetaDataMap();
            // 直接依赖
            if(columnMetaDataMap.containsKey(sourceToParse)) {
                columnName = columnMetaDataMap.get(sourceToParse).getColumnName();
                entityClass = clazzOptional.get();
            } else {
                // 有可能是关联表的查询字段
                // 注意这里目前只支持驼峰切割，不支持下划线之类的
                String[] splitCamelCases = NESTED_PROPERTY_PATTERN.split(sourceToParse, -1);
                String propertyMaybe = splitCamelCases[0];
                if(columnMetaDataMap.containsKey(propertyMaybe)) {
                    entityClass = (Class<?>) columnMetaDataMap.get(propertyMaybe).getJoinColumnMetaData().getJoinEntityType();
                    String joinPropertyName = Arrays.stream(splitCamelCases).skip(1).collect(Collectors.joining());
                    // 首字母小写
                    joinPropertyName = StringUtils.uncapitalize(joinPropertyName);
                    columnName = EntityMetaDataRegistry.getInstance().get(entityClass).getColumnMetaDataMap().get(joinPropertyName).getColumnName();
                } else {
                    for (int i = 1; i < splitCamelCases.length; i++) {
                        propertyMaybe += splitCamelCases[i];
                        if (columnMetaDataMap.containsKey(propertyMaybe)) {
                            entityClass = (Class<?>) columnMetaDataMap.get(propertyMaybe).getJoinColumnMetaData().getJoinEntityType();
                            String joinPropertyName = Arrays.stream(splitCamelCases).skip(i + 1).collect(Collectors.joining());
                            // 首字母小写
                            joinPropertyName = StringUtils.uncapitalize(joinPropertyName);
                            if(joinPropertyName.isEmpty()) {
                                throw new PropertyNotFoundException(clazzOptional.get(), sourceToParse, methodDefinition.getStatementId(), "Can not found property!");
                            }
                            columnName = EntityMetaDataRegistry.getInstance().get(entityClass).getColumnMetaDataMap().get(joinPropertyName).getColumnName();
                            break;
                        }
                    }
                }
            }
        }

        // 如果没解析到直接抛异常
        if(ObjectUtils.isEmpty(columnName)) {
            throw new PropertyNotFoundException(clazzOptional.get(), sourceToParse, methodDefinition.getStatementId(), "Can not found property!");
        }

        propertyPath.setColumnName(columnName);
        propertyPath.setEntityClass(entityClass);
        return propertyPath;
    }
}
