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
package com.alilitech.mybatis.jpa.anotation;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.UnknownTypeHandler;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author Zhou Xiaoxiang
 * @since 2.2
 */
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface ColumnResult {

    /**
     * Return the jdbc type for column that map to this argument.
     *
     * @return the jdbc type
     */
    JdbcType jdbcType() default JdbcType.UNDEFINED;

    /**
     * Returns the {@link TypeHandler} type for retrieving a column value from result set.
     *
     * @return the {@link TypeHandler} type
     */
    Class<? extends TypeHandler> typeHandler() default UnknownTypeHandler.class;

}
