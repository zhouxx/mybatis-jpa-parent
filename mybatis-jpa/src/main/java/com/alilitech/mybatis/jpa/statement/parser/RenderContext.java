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
package com.alilitech.mybatis.jpa.statement.parser;

import com.alilitech.mybatis.jpa.definition.JoinStatementDefinition;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * render context
 * @author Zhou Xiaoxiang
 * @since 1.1
 */
public class RenderContext {

    /**
     * the builder to build script
     */
    private StringBuilder scriptBuilder = new StringBuilder();

    /**
     * variable alias
     */
    private String variableAlias;

    /**
     * argument alias
     */
    private String argAlias;

    /**
     * 关联表的或子表的RenderContext,  优先子表渲染
     */
    private final Map<Class<?>, RenderContext> renderContextMap = new HashMap<>();

    public RenderContext() {
    }

    public RenderContext(String variableAlias, String argAlias) {
        this.variableAlias = variableAlias;
        this.argAlias = argAlias;
    }

    public RenderContext(String variableAlias, String argAlias, List<JoinStatementDefinition> joinStatementDefinitions) {
        this(variableAlias, argAlias);
        for(JoinStatementDefinition joinStatementDefinition : joinStatementDefinitions) {
            renderContextMap.put(joinStatementDefinition.getResultType(), new RenderContext(joinStatementDefinition.getTableIndexAlias(), null));
        }
    }


    public String getVariableAlias() {
        return variableAlias;
    }

    public String getArgAlias() {
        return argAlias;
    }

    public String getScript() {
        return scriptBuilder.toString();
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

    public void renderPropertyPathVariable(PropertyPath propertyPath) {
        // no entity class, use main table variableAlias
        if(propertyPath.getEntityClass() == null) {
            scriptBuilder.append(StringUtils.isEmpty(this.variableAlias) ? propertyPath.getColumnName() : this.variableAlias + "." + propertyPath.getColumnName());
        }
        // sub or join tables
        else if(renderContextMap.containsKey(propertyPath.getEntityClass())){
            RenderContext joinContext = renderContextMap.get(propertyPath.getEntityClass());
            scriptBuilder.append(StringUtils.isEmpty(joinContext.variableAlias) ? propertyPath.getColumnName() : joinContext.variableAlias + "." + propertyPath.getColumnName());
        }
        // use main table variableAlias
        else {
            scriptBuilder.append(StringUtils.isEmpty(this.variableAlias) ? propertyPath.getColumnName() : this.variableAlias + "." + propertyPath.getColumnName());
        }
    }

    public void renderPropertyPathArg(PropertyPath propertyPath) {
        // no entity class, use main table variableAlias
        if(propertyPath.getEntityClass() == null) {
            scriptBuilder.append(StringUtils.isEmpty(this.argAlias) ? propertyPath.getName() : this.argAlias + "." + propertyPath.getName());
        }
        // sub or join tables
        else if(renderContextMap.containsKey(propertyPath.getEntityClass())){
            RenderContext joinContext = renderContextMap.get(propertyPath.getEntityClass());
            scriptBuilder.append(StringUtils.isEmpty(joinContext.argAlias) ? propertyPath.getName() : joinContext.argAlias + "." + propertyPath.getName());
        }
        // use main table variableAlias
        else {
            scriptBuilder.append(StringUtils.isEmpty(this.argAlias) ? propertyPath.getName() : this.argAlias + "." + propertyPath.getName());
        }
    }


}
