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

import com.alilitech.mybatis.jpa.criteria.expression.*;
import com.alilitech.mybatis.jpa.criteria.expression.operator.OperatorExpression;
import com.alilitech.mybatis.jpa.criteria.expression.operator.comparison.*;
import com.alilitech.mybatis.jpa.criteria.expression.operator.like.*;
import com.alilitech.mybatis.jpa.definition.MethodDefinition;
import com.alilitech.mybatis.jpa.domain.Direction;
import org.apache.ibatis.reflection.property.PropertyNamer;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Zhou Xiaoxiang
 * @since 1.1
 */
public class CriteriaBuilder<T> {

    private final Class<T> domainClass;

    private MethodDefinition methodDefinition;

    public CriteriaBuilder(Class<T> domainClass, MethodDefinition methodDefinition) {
        this.domainClass = domainClass;
        this.methodDefinition = methodDefinition;
    }

    public PredicateExpression<T> and(PredicateExpression<T> ...predicates) {
        return new CompoundPredicateExpression<>(PredicateExpression.BooleanOperator.AND, predicates);
    }

    public PredicateExpression<T> or(PredicateExpression<T> ...predicates) {
        return new CompoundPredicateExpression<>(PredicateExpression.BooleanOperator.OR, predicates);
    }

    @SuppressWarnings("java:S1221")
    public PredicateExpression<T> equal(String property, Object value) {
        return this.buildPredicate(property, new EqualExpression<>(), value);
    }

    public <R> PredicateExpression<T> equal(SerializableFunction<T, R> propertyFunction, Object value) {
        return this.equal(getProperty(propertyFunction), value);
    }

    public PredicateExpression<T> notEqual(String property, Object value) {
        return this.buildPredicate(property, new NotEqualExpression<>(), value);
    }

    public <R> PredicateExpression<T> notEqual(SerializableFunction<T, R> propertyFunction, Object value) {
        return this.notEqual(getProperty(propertyFunction), value);
    }

    public PredicateExpression<T> greaterThan(String property, Object value) {
        return this.buildPredicate(property, new GreaterThanExpression<>(), value);
    }

    public <R> PredicateExpression<T> greaterThan(SerializableFunction<T, R> propertyFunction, Object value) {
        return this.greaterThan(getProperty(propertyFunction), value);
    }

    public PredicateExpression<T> greaterThanEqual(String property, Object value) {
        return this.buildPredicate(property, new GreaterThanEqualExpression<>(), value);
    }

    public <R> PredicateExpression<T> greaterThanEqual(SerializableFunction<T, R> propertyFunction, Object value) {
        return this.greaterThanEqual(getProperty(propertyFunction), value);
    }

    public PredicateExpression<T> lessThan(String property, Object value) {
        return this.buildPredicate(property, new LessThanExpression<>(), value);
    }

    public <R> PredicateExpression<T> lessThan(SerializableFunction<T, R> propertyFunction, Object value) {
        return this.lessThan(getProperty(propertyFunction), value);
    }

    public PredicateExpression<T> lessThanEqual(String property, Object value) {
        return this.buildPredicate(property, new LessThanEqualExpression<>(), value);
    }

    public <R> PredicateExpression<T> lessThanEqual(SerializableFunction<T, R> propertyFunction, Object value) {
        return this.lessThanEqual(getProperty(propertyFunction), value);
    }

    public PredicateExpression<T> isNull(String property) {
        return this.buildPredicate(property, new IsNullExpression<>());
    }

    public <R> PredicateExpression<T> isNull(SerializableFunction<T, R> propertyFunction) {
        return this.isNull(getProperty(propertyFunction));
    }

    public PredicateExpression<T> isNotNull(String property) {
        return this.buildPredicate(property, new IsNotNullExpression<>());
    }

    public <R> PredicateExpression<T> isNotNull(SerializableFunction<T, R> propertyFunction) {
        return this.isNotNull(getProperty(propertyFunction));
    }

    public PredicateExpression<T> between(String property, Object value1, Object value2) {
        return this.buildPredicate(property, new BetweenExpression<>(), value1, value2);
    }

    public <R> PredicateExpression<T> between(SerializableFunction<T, R> propertyFunction, Object value1, Object value2) {
        return this.between(getProperty(propertyFunction), value1, value2);
    }

    public PredicateExpression<T> notBetween(String property, Object value1, Object value2) {
        return this.buildPredicate(property, new NotBetweenExpression<>(), value1, value2);
    }

    public <R> PredicateExpression<T> notBetween(SerializableFunction<T, R> propertyFunction, Object value1, Object value2) {
        return this.notBetween(getProperty(propertyFunction), value1, value2);
    }

    public PredicateExpression<T> in(String property, Object ...values) {
        return this.buildPredicate(property, new InExpression<>(), values);
    }

    public <R> PredicateExpression<T> in(SerializableFunction<T, R> propertyFunction, Object ...values) {
        return this.in(getProperty(propertyFunction), values);
    }

    public PredicateExpression<T> in(String property, Collection<?> values) {
        return this.buildPredicate(property, new InExpression<>(), values.toArray());
    }

    public <R> PredicateExpression<T> in(SerializableFunction<T, R> propertyFunction, Collection<?> values) {
        return this.in(getProperty(propertyFunction), values);
    }

    public PredicateExpression<T> freeLike(String property, Object value) {
        return this.buildPredicate(property, new FreeLikeExpression<>(), value);
    }

    public <R> PredicateExpression<T> freeLike(SerializableFunction<T, R> propertyFunction, Object value) {
        return this.freeLike(getProperty(propertyFunction), value);
    }

    public PredicateExpression<T> notFreeLike(String property, Object value) {
        return this.buildPredicate(property, new NotFreeLikeExpression<>(), value);
    }

    public <R> PredicateExpression<T> notFreeLike(SerializableFunction<T, R> propertyFunction, Object value) {
        return this.notFreeLike(getProperty(propertyFunction), value);
    }

    public PredicateExpression<T> like(String property, Object value) {
        return this.buildPredicate(property, new LikeExpression<>(), value);
    }

    public <R> PredicateExpression<T> like(SerializableFunction<T, R> propertyFunction, Object value) {
        return this.like(getProperty(propertyFunction), value);
    }

    public PredicateExpression<T> notLike(String property, Object value) {
        return this.buildPredicate(property, new NotLikeExpression<>(), value);
    }

    public <R> PredicateExpression<T> notLike(SerializableFunction<T, R> propertyFunction, Object value) {
        return this.notLike(getProperty(propertyFunction), value);
    }

    public PredicateExpression<T> startsWith(String property, Object value) {
        return this.buildPredicate(property, new StartsWithExpression<>(), value);
    }

    public <R> PredicateExpression<T> startsWith(SerializableFunction<T, R> propertyFunction, Object value) {
        return this.startsWith(getProperty(propertyFunction), value);
    }

    public PredicateExpression<T> endsWith(String property, Object value) {
        return this.buildPredicate(property, new EndsWithExpression<>(), value);
    }

    public <R> PredicateExpression<T> endsWith(SerializableFunction<T, R> propertyFunction, Object value) {
        return this.endsWith(getProperty(propertyFunction), value);
    }

    private PredicateExpression<T> buildPredicate(String property, OperatorExpression<T> operator, Object ...values) {
        VariableExpression<T> variable = new VariableExpression<>(domainClass, property, methodDefinition);
        if(values != null && values.length > 0) {
            List<ParameterExpression<T>> parameters = Arrays.stream(values).map((Function<Object, ParameterExpression<T>>) ParameterExpression::new).collect(Collectors.toList());
            return new SinglePredicateExpression<>(variable, operator, parameters);
        } else {
            return new SinglePredicateExpression<>(variable, operator);
        }
    }

    private String getProperty(SerializableFunction<T, ?> function) {
        return PropertyNamer.methodToProperty(function.getImplMethodName());
    }

    public OrderExpression<T> desc(String property) {
        VariableExpression<T> variable = new VariableExpression<>(domainClass, property, methodDefinition);
        return new OrderExpression<>(variable, Direction.DESC);
    }

    public <R> OrderExpression<T> desc(SerializableFunction<T, R> propertyFunction) {
        VariableExpression<T> variable = new VariableExpression<>(domainClass, getProperty(propertyFunction), methodDefinition);
        return new OrderExpression<>(variable, Direction.DESC);
    }

    public OrderExpression<T> asc(String property) {
        VariableExpression<T> variable = new VariableExpression<>(domainClass, property, methodDefinition);
        return new OrderExpression<>(variable, Direction.ASC);
    }

    public <R> OrderExpression<T> asc(SerializableFunction<T, R> propertyFunction) {
        VariableExpression<T> variable = new VariableExpression<>(domainClass, getProperty(propertyFunction), methodDefinition);
        return new OrderExpression<>(variable, Direction.ASC);
    }

    public SetExpression<T> set(String property, Object value) {
        return new SetExpression<>(new VariableExpression<>(domainClass, property, methodDefinition), new ParameterExpression<>(value));
    }

    public <R> SetExpression<T> set(SerializableFunction<T, R> propertyFunction, Object value) {
        return new SetExpression<>(new VariableExpression<>(domainClass, getProperty(propertyFunction), methodDefinition), new ParameterExpression<>(value));
    }
}
