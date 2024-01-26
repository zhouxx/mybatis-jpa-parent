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

import com.alilitech.mybatis.jpa.statement.parser.PropertyPath;
import io.swagger.annotations.ApiModelProperty;

/**
 *
 * @author Zhou Xiaoxiang
 * @since 1.0
 */
public class Order {
    @ApiModelProperty(hidden = true)
    private Direction direction;
    @ApiModelProperty(hidden = true)
    private String property;

    private transient PropertyPath propertyPath;

    public static final Direction DEFAULT_DIRECTION = Direction.ASC;

    public Order() {
    }

    public Order(Direction direction,String property) {
        this.direction = direction;
        this.property = property;
    }

    public Order(Direction direction, PropertyPath propertyPath) {
        this.direction = direction;
        this.propertyPath = propertyPath;
        this.property = propertyPath.getColumnName();
    }

    public Order(String property) {
        this(DEFAULT_DIRECTION, property);
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public PropertyPath getPropertyPath() {
        return propertyPath;
    }

    @Override
    public String toString() {
        return String.format("%s %s", property, direction);
    }

}
