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
package com.alilitech.mybatis.jpa.pagination;

import com.alilitech.mybatis.MybatisJpaProperties;
import com.alilitech.mybatis.dialect.SqlDialectFactory;
import com.alilitech.mybatis.jpa.EntityMetaDataRegistry;
import com.alilitech.mybatis.jpa.StatementRegistry;
import com.alilitech.mybatis.jpa.definition.MethodDefinition;
import com.alilitech.mybatis.jpa.meta.ColumnMetaData;
import com.alilitech.mybatis.jpa.meta.EntityMetaData;
import com.alilitech.mybatis.jpa.pagination.sqlparser.ParseResult;
import com.alilitech.mybatis.jpa.pagination.sqlparser.SqlParser;
import com.alilitech.mybatis.jpa.pagination.sqlparser.SqlSelectBody;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.*;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.RowBounds;

import java.sql.Connection;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Pagination Plugin
 *
 * @author Zhou Xiaoxiang
 * @since 1.0
 */
@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
public class PaginationInterceptor implements Interceptor {

    private final Log log = LogFactory.getLog(PaginationInterceptor.class);

    private MybatisJpaProperties mybatisJpaProperties;

    public PaginationInterceptor(MybatisJpaProperties mybatisJpaProperties) {
        this.mybatisJpaProperties = mybatisJpaProperties;
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();

        MetaObject metaObject = SystemMetaObject.forObject(statementHandler);

        // determine whether it is a SELECT operation
        MappedStatement mappedStatement = (MappedStatement) metaObject.getValue("delegate.mappedStatement");
        if (!SqlCommandType.SELECT.equals(mappedStatement.getSqlCommandType())) {
            return invocation.proceed();
        }

        // if it is not the RowBounds parameter then skip it
        RowBounds rowBounds = (RowBounds) metaObject.getValue("delegate.rowBounds");
        if (rowBounds == null || rowBounds == RowBounds.DEFAULT) {
            return invocation.proceed();
        }

        BoundSql boundSql = (BoundSql) metaObject.getValue("delegate.boundSql");
        String originalSql = boundSql.getSql();

        if (rowBounds instanceof Pagination) {
            Pagination<?> page = (Pagination<?>) rowBounds;

            // determine whether the paging dialect is actively set or auto set by config
            // add since v1.2.7
            Connection connection = (Connection) invocation.getArgs()[0];
            SqlDialectFactory sqlDialectFactory;
            if (page.getDatabaseType() == null) {
                // add since v1.2.8 use autoDialect
                if (this.mybatisJpaProperties.getPage().isAutoDialect()) {
                    String databaseProductName = connection.getMetaData().getDatabaseProductName();
                    sqlDialectFactory = new SqlDialectFactory(databaseProductName);

                    if (sqlDialectFactory.getDatabaseType() == null) {
                        log.warn("The databaseId of current connection used auto dialect do not has databaseType in com.alilitech.mybatis.jpa.DatabaseTypeRegistry, it will use mybatis configuration's databaseId!");
                        // get the configuration and instantiate a SqlDialectFactory
                        Configuration configuration = (Configuration) metaObject.getValue("delegate.configuration");
                        sqlDialectFactory = new SqlDialectFactory(configuration.getDatabaseId());
                    }

                } else {
                    // get the configuration and instantiate a SqlDialectFactory
                    Configuration configuration = (Configuration) metaObject.getValue("delegate.configuration");
                    sqlDialectFactory = new SqlDialectFactory(configuration.getDatabaseId());
                }
            } else {
                sqlDialectFactory = new SqlDialectFactory(page.getDatabaseType());
            }
            originalSql = generateToPageSql(mappedStatement.getId(), originalSql, sqlDialectFactory, page);
        }
        // 替换成分页sql
        metaObject.setValue("delegate.boundSql.sql", originalSql);
        // 取消逻辑分页
        metaObject.setValue("delegate.rowBounds.offset", RowBounds.NO_ROW_OFFSET);
        metaObject.setValue("delegate.rowBounds.limit", RowBounds.NO_ROW_LIMIT);
        return invocation.proceed();
    }

    /**
     * 生成需要跟分页参数拼接的sql
     */
    private String generateToPageSql(String statementId, String originalSql, SqlDialectFactory sqlDialectFactory, Pagination<?> page) throws JSQLParserException {
        // custom sql
        if(!StatementRegistry.getInstance().contains(statementId)) {
            return sqlDialectFactory.buildPaginationSql(page, originalSql);
        }

        ParseResult parseResult = SqlParser.getInstance().parse(originalSql);

        // 如果没有关联查询，则直接拼接
        if(parseResult.isJoinEmpty()) {
            return sqlDialectFactory.buildPaginationSql(page, originalSql);
        }

        MethodDefinition methodDefinition = StatementRegistry.getInstance().getMethodDefinition(statementId);
        EntityMetaData entityMetaData = EntityMetaDataRegistry.getInstance().get(methodDefinition.getMapperDefinition().getGenericType().getDomainType());
        String mainTableAlias = entityMetaData.getTableAlias() + "_0";

        PlainSelect plainSelect = parseResult.getSelect();
        List<SelectItem> selectItems = plainSelect.getSelectItems();

        // 基于原始的解析copy一个，也是
        PlainSelect plainSelectPage = new PlainSelect()
                .withSelectItems(plainSelect.getSelectItems())
                .withJoins(plainSelect.getJoins())
                .withOrderByElements(plainSelect.getOrderByElements())
                .withFromItem(plainSelect.getFromItem())
                .withWhere(plainSelect.getWhere());


        PlainSelect plainSelectFrom;
        // 只有主表时，直接不需要join，加条件和排序，再加分页作为子表
        if (parseResult.isOnlyMainTable()) {
            List<SelectItem> mainTableItems = selectItems.stream()
                    .filter(selectItem -> Objects.equals(mainTableAlias, ((Column) ((SelectExpressionItem) selectItem).getExpression()).getTable().getName()))
                    .collect(Collectors.toList());
            plainSelectFrom = new PlainSelect()
                    .withSelectItems(mainTableItems)
                    .withFromItem(plainSelect.getFromItem())
                    .withWhere(plainSelect.getWhere())
                    .withOrderByElements(plainSelect.getOrderByElements());

        } else {
            // 非主表时分有没有子表的排序，如果没有
            // 则子查询是：查询主表字段，关联有条件的表，再加上条件，group by主表的主键，并limit
            // 最后外层再套一层查询，作为最终查询
            List<SelectItem> mainTableItems = selectItems.stream()
                    .filter(selectItem -> Objects.equals(mainTableAlias, ((Column) ((SelectExpressionItem) selectItem).getExpression()).getTable().getName()))
                    .collect(Collectors.toList());
            // 准备join
            Set<Join> joins = new LinkedHashSet<>();
            parseResult.getJoinMap().forEach((key, value) -> {
                if (parseResult.getWhereTables().contains(key)) {
                    joins.add(value);
                }
                if (parseResult.getOrderTables().contains(key)) {
                    joins.add(value);
                }
            });

            plainSelectFrom = new PlainSelect()
                    .withSelectItems(mainTableItems)
                    .withFromItem(plainSelect.getFromItem())
                    .withWhere(plainSelect.getWhere())
                    .withOrderByElements(plainSelect.getOrderByElements())
                    .withJoins(new ArrayList<>(joins));

            // 准备group by column
            Set<String> primaryKeys = entityMetaData.getPrimaryColumnMetaDatas().stream().map(ColumnMetaData::getColumnName).collect(Collectors.toSet());

            List<Column> primaryColumns = mainTableItems.stream()
                    .map(selectItem -> (Column) ((SelectExpressionItem) selectItem).getExpression())
                    .filter(column -> primaryKeys.contains(column.getColumnName()))
                    .collect(Collectors.toList());
            primaryColumns.forEach(plainSelectFrom::addGroupByColumnReference);

        }

        String paginationSql = sqlDialectFactory.buildPaginationSql(page, plainSelectFrom.toString());

        SqlSelectBody sqlSelectBody = new SqlSelectBody().withSql(paginationSql);

        SubSelect subSelect = new SubSelect().withSelectBody(sqlSelectBody).withAlias(new Alias(mainTableAlias, false));

        plainSelectPage.setFromItem(subSelect);
        // 外层查询不需要where与orderBy了
        plainSelectPage.setWhere(null);
        plainSelectPage.setOrderByElements(null);
        return new Select().withSelectBody(plainSelectPage).toString();

    }
}
