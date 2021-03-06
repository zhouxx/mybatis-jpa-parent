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
package com.alilitech.mybatis.jpa.util;

import javax.persistence.Column;
import java.lang.reflect.Field;


/**
 *
 * @author Zhou Xiaoxiang
 * @since 1.0
 */
public class ColumnUtils {

    private ColumnUtils() {
    }

    /**
     * 获取列名
     * 注解优先，{@link Column} name属性值。
     * 无注解,将字段名转为字符串,默认下划线风格.
     * @param field 字段对象
     * @return 字段名称
     */
    public static String getColumnName(Field field) {
        if (field.isAnnotationPresent(Column.class)) {
            // 获取注解对象
            Column column = field.getAnnotation(Column.class);
            // 设置了name属性
            if (!column.name().trim().equals("")) {
                return column.name().toUpperCase();
            }
        }
        return CommonUtils.camelToUnderline(field.getName());
    }

}
