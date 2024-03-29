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
package com.alilitech.mybatis.jpa.parameter;

import com.alilitech.mybatis.jpa.EntityMetaDataRegistry;
import com.alilitech.mybatis.jpa.criteria.Specification;
import com.alilitech.mybatis.jpa.meta.EntityMetaData;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;

import java.sql.Statement;
import java.util.Collection;


/**
 * 设置默认值
 *
 * @author Zhou Xiaoxiang
 * @since 1.0
 */
public class TriggerValue4NoKeyGenerator extends NoKeyGenerator {

    private final ParameterAssistant parameterAssistant = new ParameterAssistant();

    @Override
    public void processBefore(Executor executor, MappedStatement mappedStatement, Statement stmt, Object parameterObject) {

        if (!(mappedStatement.getSqlCommandType() == SqlCommandType.INSERT || mappedStatement.getSqlCommandType() == SqlCommandType.UPDATE)) {
            return;
        }

        if(parameterObject instanceof Specification) {
            return;
        }

        Collection<Object> parameters = parameterAssistant.getParameters(parameterObject);
        //trigger auto set value
        if (parameters != null) {
            for (Object parameter : parameters) {
                EntityMetaData entityMetaData = EntityMetaDataRegistry.getInstance().get(parameter.getClass());
                parameterAssistant.populateKeyAndTriggerValue(mappedStatement, parameter, entityMetaData);
            }
        } else {
            EntityMetaData entityMetaData = EntityMetaDataRegistry.getInstance().get(parameterObject.getClass());
            parameterAssistant.populateKeyAndTriggerValue(mappedStatement, parameterObject, entityMetaData);
        }
    }

}
