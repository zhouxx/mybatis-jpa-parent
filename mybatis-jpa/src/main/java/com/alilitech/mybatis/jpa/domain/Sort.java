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
package com.alilitech.mybatis.jpa.domain;

import com.alilitech.mybatis.jpa.criteria.SerializableFunction;
import org.apache.ibatis.reflection.property.PropertyNamer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.alilitech.mybatis.jpa.domain.Order.DEFAULT_DIRECTION;

/**
 * @author Zhou Xiaoxiang
 * @since 1.0
 */
public class Sort {

    private List<Order> orders;

    public Sort() {
    }

    public Sort(Order... orders) {
        this(Arrays.asList(orders));
    }

    public Sort(List<Order> orders) {
        this.orders = orders;
    }

    public Sort(String... properties) {
        this(DEFAULT_DIRECTION, properties);
    }

    public Sort(Direction direction, String... properties) {
        this(direction, properties == null ? new ArrayList<>() : Arrays.asList(properties));
    }

    public Sort(Direction direction, List<String> properties) {

        if (properties == null || properties.isEmpty()) {
            throw new IllegalArgumentException("You have to provide at least one property to sort by!");
        }

        this.orders = new ArrayList<>(properties.size());

        for (String property : properties) {
            this.orders.add(new Order(direction, property));
        }
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public boolean hasOrders() {
        return orders != null && !orders.isEmpty();
    }

    /**
     * create a new sort
     * @param properties sorted properties
     * @return a new sort
     */
    public static Sort by(String... properties) {
        if (properties == null || properties.length == 0) {
            throw new IllegalArgumentException("You have to provide at least one property to sort by!");
        }
        return new Sort(DEFAULT_DIRECTION, Arrays.asList(properties));
    }

    /**
     * set the properties sort direction desc
     * @return Sort
     */
    public Sort descending() {
        return withDirection(Direction.DESC);
    }

    /**
     * set the properties sort direction asc
     * @return Sort
     */
    public Sort ascending() {
        return withDirection(Direction.ASC);
    }

    /**
     * merge this sort and those sort
     * @param sort those sort
     * @return this sort
     */
    public Sort and(Sort sort) {
        if(sort == null) {
            return this;
        }
        this.orders.addAll(sort.getOrders());
        return this;
    }

    private Sort withDirection(Direction direction) {
        this.orders.forEach(order -> order.setDirection(direction));
        return this;
    }

    public static <T> TypedSort<T> sort(Class<T> type) {
        return new TypedSort<>(type);
    }

    public static class TypedSort<T> {

        private final Class<T> type;

        private TypedSort(Class<T> type) {
            this.type = type;
        }

        public <R> Sort by(SerializableFunction<T, R> function) {
            return new Sort(PropertyNamer.methodToProperty(function.getImplMethodName()));
        }

    }

}
