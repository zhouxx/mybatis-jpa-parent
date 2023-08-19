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

import com.alilitech.mybatis.jpa.StatementRegistry;
import com.alilitech.mybatis.jpa.definition.MethodDefinition;
import com.alilitech.mybatis.jpa.util.LRUCache;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
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
        ParseResult parseResult = parse(originalSql, statementId);

        PlainSelect plainSelect = (PlainSelect) parseResult.getSelect().getSelectBody();

        PlainSelect plainSelectCount = new PlainSelect()
                .withSelectItems(plainSelect.getSelectItems())
                .withJoins(plainSelect.getJoins())
                .withOrderByElements(plainSelect.getOrderByElements())
                .withFromItem(plainSelect.getFromItem())
                .withWhere(plainSelect.getWhere());
        Select selectCount = new Select().withSelectBody(plainSelectCount);
        if(parseResult.isOnlyMainTable()) {
            plainSelectCount.setOrderByElements(null);
            plainSelectCount.setJoins(null);
            plainSelectCount.setSelectItems(COUNT_SELECT_COLUMNS);
            return selectCount.toString();
        }
        return null;
    }

    public ParseResult parse(String originalSql, String statementId) throws JSQLParserException {
        if(sqlParserCache.containsKey(originalSql)) {
            return sqlParserCache.get(originalSql);
        }

        Select select = (Select) CCJSqlParserUtil.parse(originalSql);

        PlainSelect plainSelect = (PlainSelect) select.getSelectBody();

        Expression where = plainSelect.getWhere();
        List<OrderByElement> orderByElements = plainSelect.getOrderByElements();

        // 如果没有where和排序，直接用主表查询，orderBy和join都不要
        ParseResult parseResult = new ParseResult(select, true);
        if(where == null && orderByElements == null) {
            sqlParserCache.put(originalSql, parseResult);
            return parseResult;
        }
        MethodDefinition methodDefinition = StatementRegistry.getInstance().getMethodDefinition(statementId);

        // 是否主表就能查出count, 判断有没有字表的条件参与查询
        boolean mainTableCount = judgeIsMainTable(where, orderByElements, methodDefinition);

        if(mainTableCount) {
            sqlParserCache.put(originalSql, parseResult);
            return parseResult;
        }

        ParseResult result = new ParseResult(select, false);
        sqlParserCache.put(originalSql, result);
        return result;
    }

    /**
     * 判断是否是主表查询
     */
    private boolean judgeIsMainTable(Expression where, List<OrderByElement> orderByElements, MethodDefinition methodDefinition) {
        if(methodDefinition.getJoinStatementDefinitions().isEmpty()) {
            return true;
        }
        List<String> joinTablesAndDot = methodDefinition.getJoinStatementDefinitions().stream().map(joinStatementDefinition -> joinStatementDefinition.getTableIndexAlias() + ".").collect(Collectors.toList());
        if(where != null) {
            String whereString = where.toString();
            Optional<String> optional = joinTablesAndDot.stream().filter(whereString::contains).findFirst();
            if (optional.isPresent()) {
                return false;
            }
        }

        if(orderByElements != null) {
            List<String> orderStrings = orderByElements.stream().map(orderByElement -> orderByElement.getExpression().toString()).collect(Collectors.toList());
            for (String orderString : orderStrings) {
                Optional<String> optional = joinTablesAndDot.stream().filter(orderString::contains).findFirst();
                if (optional.isPresent()) {
                    return false;
                }
            }
        }

        return true;
    }

}
