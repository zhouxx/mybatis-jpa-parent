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

import com.alilitech.mybatis.jpa.anotation.Trigger;
import com.alilitech.mybatis.jpa.meta.ColumnMetaData;
import com.alilitech.mybatis.jpa.meta.EntityMetaData;
import com.alilitech.mybatis.jpa.primary.key.GeneratorRegistry;
import com.alilitech.mybatis.jpa.primary.key.KeyGenerator;
import com.alilitech.mybatis.jpa.primary.key.KeyGenerator4Auto;
import com.alilitech.mybatis.jpa.primary.key.SnowflakeKeyGeneratorBuilder;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;


/**
 *
 * @author Zhou Xiaoxiang
 * @since 1.0
 */
public class ParameterAssistant {

    private static final Log log = LogFactory.getLog(ParameterAssistant.class);

    public Collection<Object> getParameters(Object parameter) {
        Collection<Object> parameters = null;
        if (parameter instanceof Collection) {
            parameters = (Collection) parameter;
        } else if (parameter instanceof Map) {
            Map parameterMap = (Map) parameter;
            if (parameterMap.containsKey("collection")) {
                parameters = (Collection) parameterMap.get("collection");
            } else if (parameterMap.containsKey("list")) {
                parameters = (List) parameterMap.get("list");
            } else if (parameterMap.containsKey("array")) {
                parameters = Arrays.asList((Object[]) parameterMap.get("array"));
            }
        }
        return parameters;
    }

    public Object populateKeyAndTriggerValue(MappedStatement mappedStatement,
                                             Object parameterObject,
                                             EntityMetaData entityMetaData) {
        // MetaObject to operate the parameter object
        MetaObject metaObject = mappedStatement.getConfiguration().newMetaObject(parameterObject);

        if (mappedStatement.getSqlCommandType() == SqlCommandType.INSERT && !entityMetaData.isCompositePrimaryKey()) {

            /**
             * set the primary key
             * first: according to {@link GenerationType} to get {@link KeyGenerator}
             */
            GenerationType idGenerationType = entityMetaData.getPrimaryColumnMetaData().getIdGenerationType();

            //get id generator class
            Class<? extends KeyGenerator> generatorClass = entityMetaData.getPrimaryColumnMetaData().getIdGeneratorClass();

            KeyGenerator keyGenerator = null;

            // UUID global
            if(idGenerationType == GenerationType.UUID || idGenerationType == GenerationType.COMB_UUID) {
                keyGenerator = GeneratorRegistry.getInstance().get(idGenerationType);
            }
            // SNOWFLAKE for every entity
            else if (idGenerationType == GenerationType.SNOWFLAKE) {
                keyGenerator = GeneratorRegistry.getInstance().get(entityMetaData.getEntityType());
                if(keyGenerator == null) {
                    keyGenerator = SnowflakeKeyGeneratorBuilder.getInstance().build(entityMetaData.getEntityType());
                }
            }
            // 自定义
            else if(generatorClass != KeyGenerator4Auto.class){
                keyGenerator = GeneratorRegistry.getInstance().getOrRegister(entityMetaData.getEntityType(), generatorClass);
            }

            if(keyGenerator != null) {
                try {
                    Object idValue = keyGenerator.generate(parameterObject);
                    metaObject.setValue(entityMetaData.getPrimaryColumnMetaData().getProperty(), idValue);
                } catch (Exception e) {
                    log.error("Primary key generate failed, check your id generator '" + keyGenerator.getClass() + "'", e);
                }
            } else {
                log.warn("The entity '" + entityMetaData.getEntityType() + "' do not have the key generator!");
            }
        }

        //set the trigger value
        for(ColumnMetaData columnMetaData : entityMetaData.getColumnMetaDataMap().values()) {
            if(CollectionUtils.isEmpty(columnMetaData.getTriggers())) {
                continue;
            }
            for(Trigger trigger : columnMetaData.getTriggers()) {
                if(trigger.triggerType() == mappedStatement.getSqlCommandType()
                        && trigger.valueType() == TriggerValueType.JAVA_CODE)
                    if (metaObject.getValue(columnMetaData.getProperty()) == null || trigger.force()) {
                        Object obj = getTriggerValue(columnMetaData, trigger);
                        metaObject.setValue(columnMetaData.getProperty(), obj);
                    }
            }
        }

        return metaObject.getOriginalObject();
    }


    public static Object getTriggerValue(ColumnMetaData columnMetaData, Trigger trigger) {
        Object obj = null;
        try {
            obj = trigger.valueClass().getMethod(trigger.methodName()).invoke(trigger.valueClass().newInstance());
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | InstantiationException e) {
            log.error(columnMetaData.getProperty() + " trigger failed, check your trigger method: " + trigger.valueClass().getName() + "." + trigger.methodName(), e);
        }
        return obj;
    }

    public static Trigger getTrigger(ColumnMetaData columnMeta, SqlCommandType sqlCommandType) {
        if(columnMeta.getTriggers() != null) {
            for(Trigger trigger : columnMeta.getTriggers()) {
                if(trigger.triggerType() == sqlCommandType && trigger.valueType() == TriggerValueType.JAVA_CODE) {
                    return trigger;
                }
            }
        }
        return null;
    }


}
