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
package com.alilitech.mybatis.jpa;

import com.alilitech.mybatis.jpa.meta.ColumnMetaData;
import com.alilitech.mybatis.jpa.meta.EntityMetaData;
import com.alilitech.mybatis.jpa.meta.JoinColumnMetaData;
import com.alilitech.mybatis.jpa.util.CommonUtils;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author Zhou Xiaoxiang
 * @since 1.0
 */
public class JoinColumnMetaDataAssistant {

    private final Log log = LogFactory.getLog(JoinColumnMetaDataAssistant.class);

    private final EntityMetaData entityMetaData;

    //哪些需要关联的方法
    private final List<JoinColumnMetaData> joinColumnList = new ArrayList<>();

    public JoinColumnMetaDataAssistant(EntityMetaData entityMetaData) {
        this.entityMetaData = entityMetaData;
    }

    public JoinColumnMetaDataAssistant init() {
        int joinTableIndex = 1;
        for(ColumnMetaData columnMetaData : entityMetaData.getColumnMetaDataMap().values()) {
            if(!columnMetaData.isJoin()) {
                continue;
            }

            //join column
            JoinColumnMetaData joinColumnMetaData = columnMetaData.getJoinColumnMetaData();

            EntityMetaData joinEntityMetaData = EntityMetaDataRegistry.getInstance().get(joinColumnMetaData.getJoinEntityType());
            joinColumnMetaData.setTableName(joinEntityMetaData.getTableName());
            // 设置tableIndexAlias
            joinColumnMetaData.setTableIndexAlias(joinEntityMetaData.getTableAlias() + "_" + (joinTableIndex ++));

            EntityMetaData referencedEntityMetaData = joinEntityMetaData;

            //直接关联，适用于OneToOne or OneToMany
            if(joinColumnMetaData.getJoinType() != JoinType.MANY_TO_MANY) {

                //校验相关配置，只做警告处理
                if(joinColumnMetaData.getJoinType() == JoinType.ONE_TO_MANY && StringUtils.isEmpty(joinColumnMetaData.getMappedProperty())) {
                    log.warn(entityMetaData.getEntityType().getName() + " property => " + columnMetaData.getColumnName() + ", 'OneToMany' config does not provide mappedBy property");
                }

                String referencedProperty;
                String property;

                //未使用mappedBy，说明是附表
                if(StringUtils.isEmpty(joinColumnMetaData.getMappedProperty())) {
                    //若未配置，则取对方的主键作为关联字段
                    referencedProperty = StringUtils.isEmpty(joinColumnMetaData.getReferencedProperty()) ? referencedEntityMetaData.getPrimaryColumnMetaData().getProperty() : joinColumnMetaData.getReferencedProperty();
                    property = StringUtils.isEmpty(joinColumnMetaData.getProperty()) ? referencedEntityMetaData.getPrimaryColumnMetaData().getProperty() : joinColumnMetaData.getProperty();

                } else {  //如果使用mappedBy,则使用对面的关联属性对调
                    ColumnMetaData referencedColumnMetaData = referencedEntityMetaData.getColumnMetaDataMap().get(joinColumnMetaData.getMappedProperty());
                    JoinColumnMetaData referencedJoinColumn = referencedColumnMetaData.getJoinColumnMetaData();

                    //property是关联类的ReferencedProperty
                    property = StringUtils.isEmpty(referencedJoinColumn.getReferencedProperty()) ? entityMetaData.getPrimaryColumnMetaData().getProperty() : referencedJoinColumn.getReferencedProperty();
                    //referencedProperty是关联类的Property，若不存在，则和property一样的
                    // referencedProperty = StringUtils.isEmpty(referencedJoinColumn.getProperty()) ? referencedColumnMetaData.getEntityMetaData().getPrimaryColumnMetaData().getProperty() : referencedJoinColumn.getProperty();
                    referencedProperty = StringUtils.isEmpty(referencedJoinColumn.getProperty()) ? property : referencedJoinColumn.getProperty();
                }

                joinColumnMetaData.setCurrentProperty(columnMetaData.getProperty());
                joinColumnMetaData.setProperty(property);
                joinColumnMetaData.setReferencedProperty(referencedProperty);
                joinColumnMetaData.setPropertyType(referencedEntityMetaData.getColumnMetaDataMap().get(joinColumnMetaData.getReferencedProperty()).getType());

                //关联字段，如果当前类属性里不存在，则去对面的关联属性里找
                String columnName = entityMetaData.getColumnMetaDataMap().get(property) == null
                        ? referencedEntityMetaData.getColumnMetaDataMap().get(property).getColumnName()
                        : entityMetaData.getColumnMetaDataMap().get(property).getColumnName();
                joinColumnMetaData.setColumnName(columnName);

                String referencedColumnName = referencedEntityMetaData.getColumnMetaDataMap().get(referencedProperty) == null
                        ? entityMetaData.getColumnMetaDataMap().get(referencedProperty).getColumnName()
                        : referencedEntityMetaData.getColumnMetaDataMap().get(referencedProperty).getColumnName();
                joinColumnMetaData.setReferencedColumnName(referencedColumnName);

            } else {  //间接关联，多对多
                String property;
                String referencedProperty;
                String inverseProperty;
                String inverseReferencedProperty;

                if(StringUtils.isEmpty(joinColumnMetaData.getMappedProperty())) {
                    //若未配置，则取主表的主键作为关联字段
                    property = StringUtils.isEmpty(joinColumnMetaData.getProperty()) ? entityMetaData.getPrimaryColumnMetaData().getProperty() : joinColumnMetaData.getProperty();
                    referencedProperty = StringUtils.isEmpty(joinColumnMetaData.getReferencedProperty()) ? entityMetaData.getPrimaryColumnMetaData().getProperty() : joinColumnMetaData.getReferencedProperty();
                    //若未配置，则取对方的主键作为关联字段
                    inverseProperty = StringUtils.isEmpty(joinColumnMetaData.getInverseProperty()) ? referencedEntityMetaData.getPrimaryColumnMetaData().getProperty() : joinColumnMetaData.getInverseProperty();
                    inverseReferencedProperty = StringUtils.isEmpty(joinColumnMetaData.getInverseReferencedProperty()) ? referencedEntityMetaData.getPrimaryColumnMetaData().getProperty() : joinColumnMetaData.getInverseReferencedProperty();

                } else {   //如果是mappedBy

                    ColumnMetaData referencedColumnMetaData = referencedEntityMetaData.getColumnMetaDataMap().get(joinColumnMetaData.getMappedProperty());
                    JoinColumnMetaData referencedJoinColumn = referencedColumnMetaData.getJoinColumnMetaData();

                    //如果关联的没有提供JoinTable,使用两个表的联合，并给出警告
                    if(StringUtils.isEmpty(referencedJoinColumn.getJoinTableName())) {
                        log.warn(referencedEntityMetaData.getEntityType().getName() + " property => " + referencedColumnMetaData.getColumnName() + ", 'ManyToMany' config does not provide joinTableName");
                    }

                    //property是关联类的InverseProperty
                    property = StringUtils.isEmpty(referencedJoinColumn.getInverseProperty()) ? entityMetaData.getPrimaryColumnMetaData().getProperty() : referencedJoinColumn.getInverseProperty();
                    //referencedProperty是关联类的InverseReferencedProperty
                    referencedProperty = StringUtils.isEmpty(referencedJoinColumn.getInverseReferencedProperty()) ? entityMetaData.getPrimaryColumnMetaData().getProperty() : referencedJoinColumn.getInverseReferencedProperty();
                    //inverseProperty是关联类的Property
                    inverseProperty = StringUtils.isEmpty(referencedJoinColumn.getProperty()) ? referencedEntityMetaData.getPrimaryColumnMetaData().getProperty() : referencedJoinColumn.getProperty();
                    //inverseReferencedProperty是关联类的ReferencedProperty
                    inverseReferencedProperty = StringUtils.isEmpty(referencedJoinColumn.getReferencedProperty()) ? referencedEntityMetaData.getPrimaryColumnMetaData().getProperty() : referencedJoinColumn.getReferencedProperty();

                    //设置JoinTable
                    joinColumnMetaData.setJoinTableName(referencedJoinColumn.getJoinTableName());
                }
                joinColumnMetaData.setCurrentProperty(columnMetaData.getProperty());

                joinColumnMetaData.setProperty(property);
                joinColumnMetaData.setReferencedProperty(referencedProperty);
                joinColumnMetaData.setReferencedPropertyType(entityMetaData.getColumnMetaDataMap().get(referencedProperty).getType());

                joinColumnMetaData.setInverseProperty(inverseProperty);
                joinColumnMetaData.setInverseReferencedProperty(inverseReferencedProperty);
                joinColumnMetaData.setInverseReferencedPropertyType(referencedEntityMetaData.getColumnMetaDataMap().get(inverseReferencedProperty).getType());

                String columnName = entityMetaData.getColumnMetaDataMap().get(property) == null ? CommonUtils.camelToUnderline(property) : entityMetaData.getColumnMetaDataMap().get(property).getColumnName();
                joinColumnMetaData.setColumnName(columnName);
                String referencedColumnName = entityMetaData.getColumnMetaDataMap().get(referencedProperty).getColumnName();
                joinColumnMetaData.setReferencedColumnName(referencedColumnName);

                String inverseColumName = referencedEntityMetaData.getColumnMetaDataMap().get(inverseProperty) == null ? CommonUtils.camelToUnderline(inverseProperty) : referencedEntityMetaData.getColumnMetaDataMap().get(inverseProperty).getColumnName();
                joinColumnMetaData.setInverseColumnName(inverseColumName);
                String inverseReferencedColumnName = referencedEntityMetaData.getColumnMetaDataMap().get(inverseReferencedProperty).getColumnName();
                joinColumnMetaData.setInverseReferencedColumnName(inverseReferencedColumnName);
            }

            joinColumnList.add(joinColumnMetaData);
        }

        return this;
    }

    public List<JoinColumnMetaData> getJoinColumnList() {
        return this.joinColumnList;
    }
}
