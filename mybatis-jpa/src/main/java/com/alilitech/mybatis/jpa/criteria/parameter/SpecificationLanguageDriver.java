/*
 *    Copyright 2017-2024 the original author or authors.
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
package com.alilitech.mybatis.jpa.criteria.parameter;

import com.alilitech.mybatis.jpa.definition.MethodDefinition;
import com.alilitech.mybatis.jpa.parameter.MybatisJpaParameterHandler;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;
import org.apache.ibatis.session.Configuration;


/**
 *
 * @author Zhou Xiaoxiang
 * @since 1.0
 */
public class SpecificationLanguageDriver extends XMLLanguageDriver {

    private MethodDefinition methodDefinition;

    public SpecificationLanguageDriver(MethodDefinition methodDefinition) {
        this.methodDefinition = methodDefinition;
    }


    @Override
    public ParameterHandler createParameterHandler(MappedStatement mappedStatement,
                                                   Object parameterObject,
                                                   BoundSql boundSql) {
        /* 使用自定义 ParameterHandler */
        return new MybatisJpaParameterHandler(mappedStatement, parameterObject, boundSql);
    }

    @Override
    public SqlSource createSqlSource(Configuration configuration, XNode script, Class<?> parameterType) {
        SpecificationXMLScriptBuilder builder = new SpecificationXMLScriptBuilder(configuration, script, parameterType, methodDefinition);
        return builder.parseScriptNode();
    }
}
