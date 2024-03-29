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

import com.alilitech.mybatis.jpa.definition.JoinStatementDefinition;
import com.alilitech.mybatis.jpa.statement.parser.PropertyPath;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Zhou Xiaoxiang
 * @since 1.1
 */
public class RenderContext {

    private StringBuilder scriptBuilder = new StringBuilder();

    private final AtomicInteger paramIndex = new AtomicInteger(0);

    private String paramPrefixPrefix = "_parameter.paramValues.";

    private String paramPrefix = "param";

    private final Map<String, Object> paramValues = new ConcurrentHashMap<>();

    /**
     * 联表的或子表的表别名, 优先子表渲染
     */
    private final Map<Class<?>, String> tableAliasMap = new HashMap<>();

    public RenderContext() {
    }

    public RenderContext(String paramPrefix) {
        this.paramPrefix = paramPrefix;
    }

    public RenderContext addJoinTableAliasMap(List<JoinStatementDefinition> joinStatementDefinitions) {
        for(JoinStatementDefinition joinStatementDefinition : joinStatementDefinitions) {
            tableAliasMap.put(joinStatementDefinition.getResultType(), joinStatementDefinition.getTableIndexAlias());
        }
        return this;
    }

    public Integer getParamIndex() {
        return paramIndex.get();
    }

    public Integer getParamIndexAndIncrement() {
        return paramIndex.getAndIncrement();
    }

    public String getScript() {
        return scriptBuilder.toString();
    }

    public String getParamPrefixPrefix() {
        return paramPrefixPrefix;
    }

    public String getParamPrefix() {
        return paramPrefix;
    }

    public Map<String, Object> getParamValues() {
        return paramValues;
    }

    public Map<Class<?>, String> getTableAliasMap() {
        return tableAliasMap;
    }

    public void clearScript() {
        scriptBuilder = new StringBuilder();
    }

    public void renderString(String render) {
        scriptBuilder.append(render);
    }

    public void renderBlank() {
        scriptBuilder.append(" ");
    }

}
