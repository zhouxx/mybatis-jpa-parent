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
package com.alilitech.mybatis.jpa.pagination.sqlparser;

import com.alilitech.mybatis.jpa.EntityMetaDataRegistry;
import com.alilitech.mybatis.jpa.StatementRegistry;
import com.alilitech.mybatis.jpa.definition.MethodDefinition;
import com.alilitech.mybatis.jpa.meta.ColumnMetaData;
import com.alilitech.mybatis.jpa.meta.EntityMetaData;
import com.alilitech.mybatis.jpa.util.LRUCache;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Zhou Xiaoxiang
 * @since 2.2
 */
public class SqlParser {

    protected static final List<SelectItem> COUNT_SELECT_COLUMNS = Collections.singletonList(
            new SelectExpressionItem(new Column().withColumnName("COUNT(*)"))
    );

    /**
     * key: original sql
     */
    private final LRUCache<String, ParseResult> sqlParserCache = new LRUCache<>(10000);

    private static final SqlParser SQL_PARSER = new SqlParser();
    public static SqlParser getInstance() {
        return SQL_PARSER;
    }
    private SqlParser() {
    }

    public String parseCountSql(String originalSql, String statementId) throws JSQLParserException {
        // custom sql
        if(!StatementRegistry.getInstance().contains(statementId)) {
            return String.format("SELECT COUNT(*) FROM ( %s ) TOTAL", originalSql);
        }

        ParseResult parseResult = parse(originalSql);

        PlainSelect plainSelect = parseResult.getSelect();

        PlainSelect plainSelectCount = new PlainSelect()
                .withSelectItems(plainSelect.getSelectItems())
                .withJoins(plainSelect.getJoins())
                .withFromItem(plainSelect.getFromItem())
                .withWhere(plainSelect.getWhere());
        Select selectCount = new Select().withSelectBody(plainSelectCount);
        // 只有主表时
        if(parseResult.isOnlyMainTable()) {
            plainSelectCount.setOrderByElements(null);
            plainSelectCount.setJoins(null);
            plainSelectCount.setSelectItems(COUNT_SELECT_COLUMNS);
        } else {
            // 有子表时将查询用 count(distinct t_0.id1, t_0.id2), 并将要关联的join带出即可，其它不需要
            List<SelectItem> countSelectColumnsWithSub = Collections.singletonList(
                    new SelectExpressionItem(new Column().withColumnName(buildCountSelect(statementId)))
            );

            plainSelectCount.setSelectItems(countSelectColumnsWithSub);

            List<Join> joins = new ArrayList<>();

            parseResult.getJoinMap().forEach((key, value) -> {
                if(parseResult.getWhereTables().contains(key)) {
                    joins.add(value);
                }
            });
            plainSelectCount.setJoins(joins);
        }
        return selectCount.toString();
    }

    public ParseResult parse(String originalSql) throws JSQLParserException {
        if(sqlParserCache.containsKey(originalSql)) {
            return sqlParserCache.get(originalSql);
        }

        return new ParseResult(originalSql);
    }

    private String buildCountSelect(String statementId) {
        MethodDefinition methodDefinition = StatementRegistry.getInstance().getMethodDefinition(statementId);

        EntityMetaData entityMetaData = EntityMetaDataRegistry.getInstance().get(methodDefinition.getMapperDefinition().getGenericType().getDomainType());

        String mainTableAliasAndDot = entityMetaData.getTableAlias() + "_0.";

        List<ColumnMetaData> primaryColumnMetaDatas = entityMetaData.getPrimaryColumnMetaDatas();
        String selectColumns = primaryColumnMetaDatas.stream().map(columnMetaData -> mainTableAliasAndDot + columnMetaData.getColumnName()).collect(Collectors.joining(", "));
        return "COUNT(distinct " + selectColumns + ")";
    }

    /**
     * 判断是否是主表查询
     */
//    private boolean judgeIsMainTable(Expression where, List<OrderByElement> orderByElements, MethodDefinition methodDefinition) {
//        if(methodDefinition.getJoinStatementDefinitions().isEmpty()) {
//            return true;
//        }
//        List<String> joinTablesAndDot = methodDefinition.getJoinStatementDefinitions().stream().map(joinStatementDefinition -> joinStatementDefinition.getTableIndexAlias() + ".").collect(Collectors.toList());
//        if(where != null) {
//            String whereString = where.toString();
//            Optional<String> optional = joinTablesAndDot.stream().filter(whereString::contains).findFirst();
//            if (optional.isPresent()) {
//                return false;
//            }
//        }
//
//        if(orderByElements != null) {
//            List<String> orderStrings = orderByElements.stream().map(orderByElement -> orderByElement.getExpression().toString()).collect(Collectors.toList());
//            for (String orderString : orderStrings) {
//                Optional<String> optional = joinTablesAndDot.stream().filter(orderString::contains).findFirst();
//                if (optional.isPresent()) {
//                    return false;
//                }
//            }
//        }
//
//        return true;
//    }

}
