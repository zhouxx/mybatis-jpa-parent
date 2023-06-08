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
package com.alilitech.mybatis.jpa.criteria;

/**
 * @author Zhou Xiaoxiang
 * @since 2.1
 */
public enum SpecificationType {

    SELECT(SelectSpecification.class),

    UPDATE(UpdateSpecification.class);

    private final Class<?> specificationClass;

    SpecificationType(Class<?> specificationClass) {
        this.specificationClass = specificationClass;
    }

    public Class<?> getSpecificationClass() {
        return specificationClass;
    }

    public static SpecificationType fromClass(Class<?> specificationClass) {
        SpecificationType[] specificationTypes = SpecificationType.values();
        for (SpecificationType type : specificationTypes) {
            if(type.getSpecificationClass() == specificationClass) {
                return type;
            }
        }
        return SELECT;
    }
}
