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

import org.apache.ibatis.builder.MapperBuilderAssistant;


/**
 *
 * @author Zhou Xiaoxiang
 * @since 1.0
 */
public class ResultMapIdUtils {

    private ResultMapIdUtils() {
    }

    public static String buildId(String namespace, String id) {
        return namespace + "." + id;
    }

    public static String buildId(MapperBuilderAssistant builderAssistant, String id) {
        return buildId(builderAssistant.getCurrentNamespace(), id);
    }

    public static String buildBaseResultMapId(String namespace) {
        return buildId(namespace, "BaseResultMap");
    }

    public static String buildBaseResultMapId(MapperBuilderAssistant builderAssistant) {
        return buildBaseResultMapId(builderAssistant.getCurrentNamespace());
    }

}
