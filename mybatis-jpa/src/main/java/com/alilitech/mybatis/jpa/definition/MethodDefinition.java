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
package com.alilitech.mybatis.jpa.definition;

import com.alilitech.mybatis.jpa.anotation.IfTest;
import com.alilitech.mybatis.jpa.criteria.SpecificationType;
import com.alilitech.mybatis.jpa.exception.ParameterNumberNotMatchException;
import com.alilitech.mybatis.jpa.statement.parser.PartTree;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;


/**
 *
 * @author Zhou Xiaoxiang
 * @since 1.0
 */
public class MethodDefinition {

    private MapperDefinition mapperDefinition;

    private String namespace;

    private String methodName;

    /**
     * 在findWith 和 findJoinWith需要定义列，用于构建resultMap
     */
    private Set<ColumnDefinition> columnDefinitions;

    /**
     * 是否是一个参数
     */
    private boolean oneParameter;

    /**
     * 方法上是否有if test
     */
    private boolean methodIfTest = false;

    private IfTest ifTest;

    private List<ParameterDefinition> parameterDefinitions = new ArrayList<>();

    /**
     * 返回类型
     */
    private Class<?> returnType;

    /**
     * 是否是单个查询，不会关联查询
     */
    private boolean baseResultMap;

    /**
     * 返回复合resultMap(是否是复合查询）
     */
    private boolean compositeResultMap;

    /**
     * 传入的Page对象的index
     */
    private int pageIndex = -1;

    /**
     * 传入的Sort对象index
     */
    private int sortIndex = -1;

    /**
     * 是否是specification查询
     */
    private boolean specification;

    /**
     * specification查询类型
     */
    private SpecificationType specificationType;

    /**
     * 此方法需要关联查询的部分，可以有多个关联
     */
    private List<JoinStatementDefinition> joinStatementDefinitions = new ArrayList<>();

    //===========以下字段关联表需要====================
    //中间表名称
    private String joinTableName;

    private String columnName;

    private String referencedColumnName;

    private String inverseColumnName;

    private String inverseReferencedColumnName;

    public MethodDefinition(MapperDefinition mapperDefinition, Method method) {
        this(method);
        this.mapperDefinition = mapperDefinition;
        this.namespace = mapperDefinition.getNamespace();
    }

    public MethodDefinition(String namespace, String methodName, Set<ColumnDefinition> columnDefinitions) {
        this(methodName);
        this.namespace = namespace;
        this.columnDefinitions = columnDefinitions;
    }

    public MethodDefinition(String namespace, String methodName) {
        this(namespace, methodName, Collections.emptySet());
    }

    public MethodDefinition(String methodName) {
        this.methodName = methodName;
    }

    public MethodDefinition(Method method) {
        this.methodName = method.getName();
        methodIfTest = method.isAnnotationPresent(IfTest.class);
        if(methodIfTest) {
            ifTest = method.getAnnotation(IfTest.class);
        }

        Parameter[] parameters = method.getParameters();
        for(int index = 0; index < parameters.length; index ++) {
            parameterDefinitions.add(new ParameterDefinition(index, parameters[index]));
        }
        int size = calculate(parameterDefinitions);

        if(specification && size != 1) {
            throw new ParameterNumberNotMatchException(this.getNamespace(), this.methodName, 1, size);
        }

        this.oneParameter = size == 1;
        returnType = method.getReturnType();

        if(PartTree.QUERY_PREFIX_TEMPLATE.matcher(methodName).find()
                && !methodName.startsWith("findWith")
                && !methodName.startsWith("findJoinWith")) {
            compositeResultMap = true;
        }
    }

    public MapperDefinition getMapperDefinition() {
        return mapperDefinition;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getMethodName() {
        return methodName;
    }

    public Set<ColumnDefinition> getColumnDefinitions() {
        return columnDefinitions;
    }

    public boolean isOneParameter() {
        return oneParameter;
    }

    public void setOneParameter(boolean oneParameter) {
        this.oneParameter = oneParameter;
    }

    public boolean isMethodIfTest() {
        return methodIfTest;
    }

    public void setMethodIfTest(boolean methodIfTest) {
        this.methodIfTest = methodIfTest;
    }

    public IfTest getIfTest() {
        return ifTest;
    }

    public void setIfTest(IfTest ifTest) {
        this.ifTest = ifTest;
    }

    public List<ParameterDefinition> getParameterDefinitions() {
        return parameterDefinitions;
    }

    public void setParameterDefinitions(List<ParameterDefinition> parameterDefinitions) {
        this.parameterDefinitions = parameterDefinitions;
    }

    public Class<?> getReturnType() {
        return returnType;
    }

    public boolean isBaseResultMap() {
        return baseResultMap;
    }

    public void setBaseResultMap(boolean baseResultMap) {
        this.baseResultMap = baseResultMap;
    }

    /**
     * 是否是关联查询的子查询
     */
    public boolean isJoinMethod() {
        return methodName.startsWith("findWith") || methodName.startsWith("findJoinWith");
    }

    public boolean isCompositeResultMap() {
        return compositeResultMap;
    }

    public int getSortIndex() {
        return sortIndex;
    }

    public boolean isSpecification() {
        return specification;
    }

    public void setSpecification(boolean specification) {
        this.specification = specification;
    }

    public SpecificationType getSpecificationType() {
        return specificationType;
    }

    public void setSpecificationType(SpecificationType specificationType) {
        this.specificationType = specificationType;
    }

    public List<JoinStatementDefinition> getJoinStatementDefinitions() {
        return joinStatementDefinitions;
    }

    public void setJoinStatementDefinitions(List<JoinStatementDefinition> joinStatementDefinitions) {
        this.joinStatementDefinitions = joinStatementDefinitions;
    }

    public String getJoinTableName() {
        return joinTableName;
    }

    public void setJoinTableName(String joinTableName) {
        this.joinTableName = joinTableName;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getReferencedColumnName() {
        return referencedColumnName;
    }

    public void setReferencedColumnName(String referencedColumnName) {
        this.referencedColumnName = referencedColumnName;
    }

    public String getInverseColumnName() {
        return inverseColumnName;
    }

    public void setInverseColumnName(String inverseColumnName) {
        this.inverseColumnName = inverseColumnName;
    }

    public String getInverseReferencedColumnName() {
        return inverseReferencedColumnName;
    }

    public void setInverseReferencedColumnName(String inverseReferencedColumnName) {
        this.inverseReferencedColumnName = inverseReferencedColumnName;
    }

    private int calculate(List<ParameterDefinition> parameterDefinitions) {
        int count = 0;
        for (ParameterDefinition parameterDefinition : parameterDefinitions) {
            if (parameterDefinition.isPage()) {
                pageIndex = parameterDefinition.getIndex();
                continue;
            }

            if(parameterDefinition.isSort()) {
                sortIndex = parameterDefinition.getIndex();
            }

            if(parameterDefinition.isSpecification()) {
                specification = true;
                specificationType = parameterDefinition.getSpecificationType();
            }

            count++;
        }
        return count;
    }

    public String getStatementId() {
        return namespace + "." + methodName;
    }

    public boolean hasPage() {
        return pageIndex > -1;
    }

}
