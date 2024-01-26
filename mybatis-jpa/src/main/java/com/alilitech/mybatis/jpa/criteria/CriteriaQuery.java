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
package com.alilitech.mybatis.jpa.criteria;

import com.alilitech.mybatis.jpa.EntityMetaDataRegistry;
import com.alilitech.mybatis.jpa.anotation.Trigger;
import com.alilitech.mybatis.jpa.criteria.expression.*;
import com.alilitech.mybatis.jpa.definition.MethodDefinition;
import com.alilitech.mybatis.jpa.meta.ColumnMetaData;
import com.alilitech.mybatis.jpa.meta.EntityMetaData;
import com.alilitech.mybatis.jpa.parameter.TriggerValueType;
import com.alilitech.mybatis.jpa.statement.StatementAssistant;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.parsing.GenericTokenParser;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Zhou Xiaoxiang
 * @since 1.1
 */
public class CriteriaQuery<T> {

    private final Class<T> returnType;

    private PredicateExpression.BooleanOperator booleanOperator = PredicateExpression.BooleanOperator.AND;

    private final RenderContext renderContext = new RenderContext();

    private String setScript;

    private String whereScript;

    private Map<String, Object> paramValues;

    private String orderByScript;

    private MethodDefinition methodDefinition;

    public CriteriaQuery(Class<T> returnType, MethodDefinition methodDefinition) {
        this.returnType = returnType;
        this.methodDefinition = methodDefinition;
        renderContext.addJoinTableAliasMap(methodDefinition.getJoinStatementDefinitions());
    }

//    public CriteriaQuery(Class<T> returnType, PredicateExpression.BooleanOperator booleanOperator) {
//        this(returnType);
//        this.booleanOperator = booleanOperator;
//    }

    public CriteriaQuery<T> update(SetExpression<T>... setExpressions) {
        // 保存用户设置的更新列名
        Set<String> setColumnNames = new HashSet<>();

        EntityMetaData entityMetaData = EntityMetaDataRegistry.getInstance().get(returnType);

        String split = ", ";
        if(setExpressions != null) {
            for (SetExpression<T> setExpression : setExpressions) {
                // 有触发器，
                // 当valueType=databaseFunction时，忽略set
                // 当valueType=javaCode时，并且force=true,忽略此set
                ColumnMetaData columnMetaData = entityMetaData.getColumnMetaDataMap().get(setExpression.getVariable().getOriginalVariableName());
                Trigger codeTrigger = StatementAssistant.getTrigger(columnMetaData, SqlCommandType.UPDATE);
                if(codeTrigger != null && codeTrigger.valueType() == TriggerValueType.DATABASE_FUNCTION) {
                    continue;
                }

                if(codeTrigger != null && codeTrigger.valueType() == TriggerValueType.JAVA_CODE && codeTrigger.force()) {
                    continue;
                }

                setExpression.render(renderContext);
                renderContext.renderString(split);

                setColumnNames.add(setExpression.getVariable().getOriginalVariableName());
            }
        }

        // 设置数据库函数的触发器
        // 设置代码级触发器，预留位置
        for (ColumnMetaData columnMetaData : entityMetaData.getColumnMetaDataMap().values()) {
            String function = StatementAssistant.resolveSqlParameterByDatabaseFunction(columnMetaData, SqlCommandType.UPDATE);
            if(function != null) {
                renderContext.renderString(columnMetaData.getColumnName());
                renderContext.renderString(" = ");
                renderContext.renderString(function);
                renderContext.renderString(split);
                continue;
            }

            Trigger codeTrigger = StatementAssistant.getJavaCodeTrigger(columnMetaData, SqlCommandType.UPDATE);
            // 有触发器，并且前面没有设置set
            if(codeTrigger != null && !setColumnNames.contains(columnMetaData.getProperty())) {
                SetExpression<T> setExpression = new SetExpression<>(new VariableExpression<>(returnType, columnMetaData.getProperty(), methodDefinition), new ParameterExpression<>("@{" + columnMetaData.getProperty() + "}"));
                setExpression.render(renderContext);
                renderContext.renderString(split);
            }
        }

        setScript = renderContext.getScript();
        renderContext.clearScript();
        return this;
    }

    public CriteriaQuery<T> where(PredicateExpression ... predicateExpressions) {
        new CompoundPredicateExpression<T>(booleanOperator, predicateExpressions).render(renderContext);
        whereScript = renderContext.getScript();
        paramValues = renderContext.getParamValues();
        renderContext.clearScript();
        return this;
    }

    public CriteriaQuery<T> orderBy(OrderExpression ...orderExpressions) {
        if(orderExpressions != null && orderExpressions.length > 0) {
            renderContext.renderString("ORDER BY ");
            String split = "";
            for (OrderExpression<T> orderExpression : orderExpressions) {
                renderContext.renderString(split);
                orderExpression.render(renderContext);
                split = ", ";
            }
            orderByScript = renderContext.getScript();
        }
        return this;
    }

    public String getSetScript() {
        return setScript;
    }

    public String getWhereScript() {
        return whereScript;
    }

    public Map<String, Object> getParamValues() {
        return paramValues;
    }

    public String getOrderByScript() {
        return orderByScript;
    }

    public static void main(String[] args) {
        String s = "@{abc}";

        String parse = new GenericTokenParser("@{", "}", content -> content).parse(s);
        System.out.println(parse);
    }
}
