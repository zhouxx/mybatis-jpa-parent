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

import com.alilitech.mybatis.jpa.criteria.parameter.SpecificationLanguageDriver;
import com.alilitech.mybatis.jpa.criteria.parameter.SpecificationXMLScriptBuilder;
import com.alilitech.mybatis.jpa.definition.GenericType;
import com.alilitech.mybatis.jpa.definition.JoinStatementDefinition;
import com.alilitech.mybatis.jpa.statement.MethodType;
import com.alilitech.mybatis.jpa.statement.PreMapperStatement;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.Configuration;

import java.util.List;


/**
 *
 * @author Zhou Xiaoxiang
 * @since 1.0
 */
public class PreMapperStatementBuilder4FindSpecification extends BaseSelectPreMapperStatementBuilder {

    public PreMapperStatementBuilder4FindSpecification(Configuration configuration, MapperBuilderAssistant builderAssistant, MethodType methodType) {
        super(configuration, builderAssistant, methodType);
    }

    @Override
    protected void buildPreMapperStatementExtend(PreMapperStatement preMapperStatement, GenericType genericType) {
        preMapperStatement.setSqlCommandType(SqlCommandType.SELECT);
        setNoKeyGenerator(preMapperStatement);
        setFindResultIdOrType(preMapperStatement, genericType);
    }

//    @Override
//    protected String buildSQL() {
//
//        String operation = "SELECT";
//        String selectPart = entityMetaData.getColumnNamesString();
//        List<String> sqlParts = Arrays.asList(
//                operation,
//                selectPart,
//                "FROM",
//                entityMetaData.getTableName(),
//                "<where>",
//                "<if test=\"_parameter != null and _parameter.whereScript != null\">",
//                "${_parameter.whereScript}",
//                "</if>",
//                "</where>",
//                "<if test=\"_parameter != null and _parameter.orderByScript != null\">",
//                "${_parameter.orderByScript}",
//                "</if>"
//        );
//
//        return buildScript(sqlParts);
//    }

    @Override
    protected String generateConditionScript(String mainTableAlias, List<JoinStatementDefinition> joinStatementDefinitions) {
        return String.join(" ",
                "<if test=\"_parameter != null and _parameter.whereScript != null\">",
                "${_parameter.whereScript}",
                "</if>");
    }

    @Override
    protected String generateSortScript(String mainTableAlias) {
        return String.join(" ",
                "<if test=\"_parameter != null and _parameter.orderByScript != null\">",
                "${_parameter.orderByScript}",
                "</if>");
    }

    @Override
    protected Class<?> getParameterTypeClass() {
        return entityMetaData.getEntityType();
    }

    @Override
    protected LanguageDriver getLanguageDriver(String lang) {
        return new SpecificationLanguageDriver(methodDefinition);
    }
}
