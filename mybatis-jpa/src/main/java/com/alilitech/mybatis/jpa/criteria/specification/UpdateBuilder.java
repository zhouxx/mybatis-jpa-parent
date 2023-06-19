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
package com.alilitech.mybatis.jpa.criteria.specification;

import com.alilitech.mybatis.jpa.criteria.CriteriaBuilder;
import com.alilitech.mybatis.jpa.criteria.CriteriaQuery;
import com.alilitech.mybatis.jpa.criteria.SerializableFunction;
import com.alilitech.mybatis.jpa.criteria.expression.SetExpression;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Zhou Xiaoxiang
 * @since 2.1
 */
public class UpdateBuilder<T> extends AbstractSpecificationBuilder<T> {

    private List<SetExpression<T>> setExpressions = new ArrayList<>();

    protected UpdateBuilder(SpecificationBuilder<T> specificationBuilder) {
        super(specificationBuilder);
    }

    public UpdateBuilder<T> set(String property, Object value) {
        specifications.add((cb, query) -> {
            setExpressions.add(cb.set(property, value));
            return null;
        });
        return this;
    }

    public <R> UpdateBuilder<T> set(SerializableFunction<T, R> property, Object value) {
        specifications.add((cb, query) -> {
            setExpressions.add(cb.set(property, value));
            return null;
        });
        return this;
    }

    @Override
    public void build(CriteriaBuilder<T> cb, CriteriaQuery<T> query) {
        specificationBuilder.build(cb, query);
        // 通过调用toPredicate, 从而达到填充setExpressions的目的
        for (int i = 0; i < specifications.size(); i++) {
            specifications.get(i).toPredicate(cb, query);
        }
        if (setExpressions != null && !setExpressions.isEmpty()) {
            query.update(setExpressions.toArray(new SetExpression[setExpressions.size()]));
        }
    }
}
