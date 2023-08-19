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
package com.alilitech.mybatis.jpa;

import com.alilitech.mybatis.jpa.definition.MethodDefinition;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Zhou Xiaoxiang
 * @since 1.2
 */
public class StatementRegistry {

    /**
     * key: statementId
     */
    private final Map<String, MethodDefinition> statements = new ConcurrentHashMap<>();

    private static final StatementRegistry REGISTRY = new StatementRegistry();

    public static StatementRegistry getInstance() {
        return REGISTRY;
    }

    public void addStatement(String statementId, MethodDefinition methodDefinition) {
        statements.put(statementId, methodDefinition);
    }

    public boolean contains(String statementId) {
        return statements.containsKey(statementId);
    }

    public MethodDefinition getMethodDefinition(String statementId) {
        return statements.get(statementId);
    }

}
