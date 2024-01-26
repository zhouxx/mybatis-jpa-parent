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
import com.alilitech.mybatis.jpa.domain.Order;
import com.alilitech.mybatis.jpa.domain.Sort;
import com.alilitech.mybatis.jpa.exception.MybatisJpaException;
import com.alilitech.mybatis.jpa.meta.EntityMetaData;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.scripting.xmltags.DynamicSqlSource;
import org.apache.ibatis.scripting.xmltags.SqlNode;
import org.apache.ibatis.session.Configuration;

import java.util.Map;

/**
 * 扩展DynamicSqlSource，主要是扩展一些参数信息和转换
 * @author Zhou Xiaoxiang
 * @since 1.0
 */
public class MybatisJpaDynamicSqlSource extends DynamicSqlSource {

    private Class<?> domainType;

    public MybatisJpaDynamicSqlSource(Configuration configuration, SqlNode rootSqlNode) {
        super(configuration, rootSqlNode);
    }

    public MybatisJpaDynamicSqlSource(Configuration configuration, SqlNode rootSqlNode, Class<?> domainType) {
        super(configuration, rootSqlNode);
        this.domainType = domainType;
    }

    @Override
    public BoundSql getBoundSql(Object parameterObject) {
        // 以下操作是在${ }转换前需要操作的
        // 转换排序参数
        if(parameterObject instanceof Sort && domainType != null) {
            Sort sort = (Sort) parameterObject;
            for(Order order : sort.getOrders()) {
                doTransferOrderPropertyToColumnName(order);
            }
        } else if(parameterObject instanceof MapperMethod.ParamMap && domainType != null) {
            MapperMethod.ParamMap<?> paramMap = (MapperMethod.ParamMap<?>) parameterObject;
            for (Map.Entry<String, ?> entry : paramMap.entrySet()) {
                Object value = entry.getValue();
                if (value instanceof Sort) {
                    Sort sort = (Sort) value;
                    for (Order order : sort.getOrders()) {
                        doTransferOrderPropertyToColumnName(order);
                    }
                    // an execution only has one Sort
                    break;
                }
            }
        }

        return super.getBoundSql(parameterObject);
    }

    private void doTransferOrderPropertyToColumnName(Order order) {
        EntityMetaData entityMetaData = EntityMetaDataRegistry.getInstance().get(domainType);
        // 先判断是否是javaProperty，是的话，转换成数据库的columnName
        if (entityMetaData.getColumnMetaDataMap().containsKey(order.getProperty())) {
            String columnName = entityMetaData.getColumnMetaDataMap().get(order.getProperty()).getColumnName();
            order.setProperty(columnName);
        }
        // 有可能也直接是数据库的columnName, 如果不是的话说明既不是javaProperty, 也不是columnName， 这个时候才抛出异常
        else if(!entityMetaData.getColumnNames().contains(order.getProperty()))  {
            throw new MybatisJpaException("Order property=>" + order.getProperty() + " is not exist in class '" + entityMetaData.getEntityType().getName() + "'");
        }
    }
}
