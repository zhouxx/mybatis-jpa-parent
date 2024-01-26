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

import com.alilitech.mybatis.jpa.definition.GenericType;
import com.alilitech.mybatis.jpa.definition.JoinStatementDefinition;
import com.alilitech.mybatis.jpa.statement.MethodType;
import com.alilitech.mybatis.jpa.statement.PreMapperStatement;
import com.alilitech.mybatis.jpa.statement.PreMapperStatementBuilder;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.session.Configuration;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


/**
 *
 * @author Zhou Xiaoxiang
 * @since 2.2
 */
public abstract class BaseSelectPreMapperStatementBuilder extends PreMapperStatementBuilder {

    public BaseSelectPreMapperStatementBuilder(Configuration configuration, MapperBuilderAssistant builderAssistant, MethodType methodType) {
        super(configuration, builderAssistant, methodType);
    }

    @Override
    protected void buildPreMapperStatementExtend(PreMapperStatement preMapperStatement, GenericType genericType) {

        preMapperStatement.setResultType((Class)genericType.getDomainType());
        preMapperStatement.setSqlCommandType(SqlCommandType.SELECT);

        setNoKeyGenerator(preMapperStatement);

        setFindResultIdOrType(preMapperStatement, genericType);
    }

    @Override
    protected String buildSQL() {

//        RenderContext context = new RenderContext();
//        buildPartTree().render(context);

        //sql parts
        String mainTableName = entityMetaData.getTableName();
        String mainTableAlias = entityMetaData.getTableAlias() + "_0";

        List<JoinStatementDefinition> joinStatementDefinitions = methodDefinition.getJoinStatementDefinitions();

        String conditionScript = generateConditionScript(mainTableAlias, joinStatementDefinitions);
        conditionScript = conditionScript.contains("<where>") ? conditionScript : "<where>" + conditionScript + "</where>";
        List<String> sqlParts = Arrays.asList(
                "SELECT",
                entityMetaData.getColumnNamesString(mainTableAlias) + (joinStatementDefinitions.isEmpty() ? "" : ","),
                joinStatementDefinitions.stream().map(JoinStatementDefinition::buildColumnNamesString).collect(Collectors.joining(", ")),
                "FROM",
                mainTableName,
                mainTableAlias,
                joinStatementDefinitions.stream().map(joinStatementDefinition -> joinStatementDefinition.buildLeftJoinSqlPart(mainTableAlias)).collect(Collectors.joining(" ")),
                conditionScript,
                generateSortScript(mainTableAlias)
        );

        return buildScript(sqlParts);
    }

    /**
     * 生成查询条件sql脚本
     */
    protected abstract String generateConditionScript(String mainTableAlias, List<JoinStatementDefinition> joinStatementDefinitions);

    /**
     * 生成排序sql脚本
     */
    protected String generateSortScript(String mainTableAlias) {
        return "";
    }

}
