/*
 *    Copyright 2017-2024 the original author or authors.
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
package com.alilitech.mybatis.jpa.criteria.parameter;

import com.alilitech.mybatis.jpa.EntityMetaDataRegistry;
import com.alilitech.mybatis.jpa.anotation.Trigger;
import com.alilitech.mybatis.jpa.criteria.CriteriaBuilder;
import com.alilitech.mybatis.jpa.criteria.CriteriaQuery;
import com.alilitech.mybatis.jpa.criteria.Specification;
import com.alilitech.mybatis.jpa.criteria.UpdateSpecification;
import com.alilitech.mybatis.jpa.criteria.expression.PredicateExpression;
import com.alilitech.mybatis.jpa.definition.MethodDefinition;
import com.alilitech.mybatis.jpa.meta.ColumnMetaData;
import com.alilitech.mybatis.jpa.parameter.ParameterAssistant;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.parsing.GenericTokenParser;
import org.apache.ibatis.scripting.xmltags.DynamicSqlSource;
import org.apache.ibatis.scripting.xmltags.SqlNode;
import org.apache.ibatis.session.Configuration;

import java.util.Map;

/**
 * 扩展DynamicSqlSource，主要是扩展一些参数信息和转换
 * @author Zhou Xiaoxiang
 * @since 1.0
 */
public class SpecificationDynamicSqlSource extends DynamicSqlSource {

    private Class<?> domainType;

    private MethodDefinition methodDefinition;

    public SpecificationDynamicSqlSource(Configuration configuration, SqlNode rootSqlNode) {
        super(configuration, rootSqlNode);
    }

    public SpecificationDynamicSqlSource(Configuration configuration, SqlNode rootSqlNode, Class<?> domainType, MethodDefinition methodDefinition) {
        super(configuration, rootSqlNode);
        this.domainType = domainType;
        this.methodDefinition = methodDefinition;
    }

    @Override
    public BoundSql getBoundSql(Object parameterObject) {
        // 以下操作是在${ }转换前需要操作的
        // 转换规格查询参数
        if(parameterObject instanceof Specification && domainType != null) {
            Specification specification = (Specification<?>) parameterObject;

            CriteriaBuilder<?> cb = new CriteriaBuilder<>(domainType, methodDefinition);

            CriteriaQuery<?> query = new CriteriaQuery<>(domainType, methodDefinition);

            PredicateExpression<?> predicate = specification.toPredicate(cb, query);

            if(predicate != null) {
                query.where(predicate);
            }

            // 更新时替换触发器相关值
            if(parameterObject instanceof UpdateSpecification) {
                Map<String, Object> paramValues = query.getParamValues();
                Map<String, ColumnMetaData> columnMetaDataMap = EntityMetaDataRegistry.getInstance().get(domainType).getColumnMetaDataMap();
                for (Map.Entry<String, Object> entry : paramValues.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    if (value instanceof String) {
                        String valString = (String) value;
                        if (valString.startsWith("@{") && valString.endsWith("}")) {
                            String property = new GenericTokenParser("@{", "}", content -> content).parse(valString);
                            ColumnMetaData columnMetaData = columnMetaDataMap.get(property);
                            Trigger trigger = ParameterAssistant.getTrigger(columnMetaData, SqlCommandType.UPDATE);
                            if(trigger == null) {
                                continue;
                            }
                            Object triggerValue = ParameterAssistant.getTriggerValue(columnMetaData, trigger);
                            paramValues.put(key, triggerValue);
                        }
                    }
                }
            }

            parameterObject = query;
        }

        return super.getBoundSql(parameterObject);
    }

}
