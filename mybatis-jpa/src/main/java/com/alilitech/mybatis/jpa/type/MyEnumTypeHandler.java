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
package com.alilitech.mybatis.jpa.type;

import com.alilitech.mybatis.jpa.anotation.PersistenceValue;
import com.alilitech.mybatis.jpa.exception.MybatisJpaException;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaClass;
import org.apache.ibatis.reflection.ReflectorFactory;
import org.apache.ibatis.reflection.invoker.Invoker;
import org.apache.ibatis.type.EnumTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.lang.reflect.Field;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Optional;

/**
 * @author Zhou Xiaoxiang
 * @since 2.1.1
 */
public class MyEnumTypeHandler<E extends Enum<E>> extends EnumTypeHandler<E> {

    protected static final ReflectorFactory REFLECTOR_FACTORY = new DefaultReflectorFactory();

    private final Class<E> enumType;

    private Class<?> fieldType;

    private Invoker filedGetInvoker;

    public MyEnumTypeHandler(Class<E> type) {
        super(type);
        this.enumType = type;
        MetaClass metaClass = MetaClass.forClass(type, REFLECTOR_FACTORY);
        this.fieldType = String.class;
        Optional<Field> persistenceField = Arrays.stream(type.getDeclaredFields()).filter(field -> field.isAnnotationPresent(PersistenceValue.class)).findFirst();
        persistenceField.ifPresent(field -> {
            this.fieldType= field.getType();
            filedGetInvoker = metaClass.getGetInvoker(field.getName());
        });
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, E parameter, JdbcType jdbcType) throws SQLException {
        if (jdbcType == null) {
            ps.setObject(i, this.getValue(parameter));
        } else {
            // see r3589
            ps.setObject(i, this.getValue(parameter), jdbcType.TYPE_CODE);
        }
    }

    @Override
    public E getNullableResult(ResultSet rs, String columnName) throws SQLException {
        Object value = rs.getObject(columnName, this.fieldType);
        return value == null ? null : this.valueOf(value);
    }

    @Override
    public E getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        Object value = rs.getObject(columnIndex, this.fieldType);
        return value == null ? null : this.valueOf(value);
    }

    @Override
    public E getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        Object value = cs.getObject(columnIndex, this.fieldType);
        return value == null ? null : this.valueOf(value);
    }

    private Object getValue(E parameter) {
        if(filedGetInvoker == null) {
            return parameter.name();
        }
        try {
            return this.filedGetInvoker.invoke(parameter, new Object[0]);
        } catch (ReflectiveOperationException e) {
            throw new MybatisJpaException(e);
        }
    }

    private E valueOf(Object value) {
        E[] enumConstants = this.enumType.getEnumConstants();
        return Arrays.stream(enumConstants).filter(e -> getValue(e).equals(value)).findFirst().orElse(null);
    }
}
