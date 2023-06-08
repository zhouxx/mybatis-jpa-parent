/*
 *    Copyright 2017-2023 the original author or authors.
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

import com.alilitech.mybatis.jpa.criteria.RenderContext;

/**
 * @author Zhou Xiaoxiang
 * @since 2.1
 */
public class SetExpression<T> implements Expression<T> {

    private VariableExpression<T> variable;
    private ParameterExpression<T> parameter;

    public SetExpression(VariableExpression<T> variable, ParameterExpression<T> parameter) {
        this.variable = variable;
        this.parameter = parameter;
    }

    @Override
    public void render(RenderContext renderContext, Expression<T>... expressions) {
        variable.render(renderContext);
        renderContext.renderBlank();

        renderContext.renderString("=");
        renderContext.renderBlank();

        parameter.render(renderContext);
    }

    public VariableExpression<T> getVariable() {
        return variable;
    }

}
