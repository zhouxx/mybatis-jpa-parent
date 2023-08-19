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
package com.alilitech.mybatis.jpa.statement.support;

import com.alilitech.mybatis.jpa.meta.ColumnMetaData;
import com.alilitech.mybatis.jpa.statement.MethodType;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.session.Configuration;

import java.util.stream.Collectors;

/**
 *
 * @author Zhou Xiaoxiang
 * @since 1.0
 */
public class PreMapperStatementBuilder4findAllById extends BaseSelectPreMapperStatementBuilder {

    public PreMapperStatementBuilder4findAllById(Configuration configuration, MapperBuilderAssistant builderAssistant, MethodType methodType) {
        super(configuration, builderAssistant, methodType);
    }

    @Override
    protected String generateConditionScript(String mainTableAlias) {
        if(entityMetaData.isCompositePrimaryKey()) {
            String primaryKeys = entityMetaData.getPrimaryColumnMetaDatas().stream().map(columnMetaData -> {
                String columnName = columnMetaData.getColumnName();
                return mainTableAlias + "." + columnName;
            }).collect(Collectors.joining(", ", "(", ")"));
            String primaryItems = entityMetaData.getPrimaryColumnMetaDatas().stream().map(ColumnMetaData::getProperty).map(s -> "#{id." + s + "}").collect(Collectors.joining(", "));
            return String.join(" ",
                    primaryKeys,
                    "IN",
                    "<foreach item=\"id\" index=\"index\" open=\"(\" separator=\",\" close=\")\" collection=\"collection\">",
                    "(" + primaryItems + ")",
                    "</foreach>"
            );
        } else {
            return String.join(" ",
                    mainTableAlias + "." + entityMetaData.getPrimaryColumnMetaData().getColumnName(),
                    "IN",
                    "<foreach item=\"item\" index=\"index\" open=\"(\" separator=\",\" close=\")\" collection=\"collection\">",
                    "#{item}",
                    "</foreach>");
        }
    }


    protected Class<?> getParameterTypeClass() {
        return entityMetaData.getEntityType();
    }

}
