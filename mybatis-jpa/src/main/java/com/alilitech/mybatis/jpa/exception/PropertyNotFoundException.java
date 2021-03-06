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
package com.alilitech.mybatis.jpa.exception;

/**
 * Jpa parse 'findBy' statement, can not found property
 * @author Zhou Xiaoxiang
 * @since 1.0
 */
public class PropertyNotFoundException extends RuntimeException {

    private final Class<?> domain;

    private final String property;

    private final String statement;

    public PropertyNotFoundException(Class<?> domain, String property, String statement, String msg) {
        super(msg);
        this.domain = domain;
        this.property = property;
        this.statement = statement;
    }

    public Class<?> getDomain() {
        return domain;
    }

    public String getProperty() {
        return property;
    }

    public String getStatement() {
        return statement;
    }
}
