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
package com.alilitech.mybatis.jpa.criteria.expression;

import com.alilitech.mybatis.jpa.EntityMetaDataRegistry;
import com.alilitech.mybatis.jpa.criteria.RenderContext;
import com.alilitech.mybatis.jpa.criteria.SpecificationType;
import com.alilitech.mybatis.jpa.definition.MethodDefinition;
import com.alilitech.mybatis.jpa.exception.MybatisJpaException;
import com.alilitech.mybatis.jpa.meta.EntityMetaData;
import com.alilitech.mybatis.jpa.statement.parser.PropertyPath;

import java.util.Optional;

/**
 * @author Zhou Xiaoxiang
 * @since 1.1
 */
public class VariableExpression<T> implements AtomicExpression<T> {

    private String originalVariableName;
    private PropertyPath propertyPath;

    public VariableExpression() {
    }

    public VariableExpression(Class<T> domainClass, String variableName, MethodDefinition methodDefinition) {
        this.originalVariableName = variableName;
        this.propertyPath = PropertyPath.from(variableName, Optional.of(domainClass), methodDefinition);
        if(methodDefinition.getSpecificationType() == SpecificationType.UPDATE && domainClass != propertyPath.getEntityClass()) {
            throw new MybatisJpaException("Update specification not support relative property!");
        }
    }

    @Override
    public void render(RenderContext renderContext, Expression<T> ...expressions) {
        // 子表
        if(renderContext.getTableAliasMap().containsKey(propertyPath.getEntityClass())) {
            renderContext.renderString(renderContext.getTableAliasMap().get(propertyPath.getEntityClass()) + "." + propertyPath.getColumnName());
        }
        // 主表
        else {
            EntityMetaData entityMetaData = EntityMetaDataRegistry.getInstance().get(propertyPath.getEntityClass());
            renderContext.renderString(entityMetaData.getTableAlias() + "_0." + propertyPath.getColumnName());
        }
    }

    public String getOriginalVariableName() {
        return originalVariableName;
    }
}
