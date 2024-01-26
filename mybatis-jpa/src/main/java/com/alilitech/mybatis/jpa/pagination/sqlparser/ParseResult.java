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

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.arithmetic.*;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.conditional.XorExpression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Zhou Xiaoxiang
 * @since 2.2
 */
public class ParseResult {

    private final PlainSelect select;

    private Map<String, Join> joinMap;

    private final Set<String> whereTables = new LinkedHashSet<>();
    private final Set<String> orderTables = new LinkedHashSet<>();

    /**
     * 分页只需要根据主表查询
     */
    private boolean onlyMainTable = true;

    public ParseResult(String originalSql) throws JSQLParserException {

        Select parse = (Select) CCJSqlParserUtil.parse(originalSql);

        this.select = (PlainSelect) parse.getSelectBody();

        // 如果没有join，直接无需解析了
        if(select.getJoins() == null) {
            return;
        }

        joinMap = new HashMap<>(select.getJoins().size());

        for(Join join : select.getJoins()) {
            FromItem rightItem = join.getRightItem();
            String name = null;
            if(rightItem instanceof SubJoin) {
                SubJoin subJoin = (SubJoin) rightItem;
                name = subJoin.getJoinList().get(0).getRightItem().getAlias().getName();
            } else {
                name = rightItem.getAlias().getName();
            }
            joinMap.put(name, join);
        }
        Set<String> whereAndOrderTables = new LinkedHashSet<>();
        List<OrderByElement> orderByElements = select.getOrderByElements();
        if(orderByElements != null) {
            Set<String> orderTablesTmp = orderByElements.stream().map(orderByElement -> ((Column) orderByElement.getExpression()).getTable().getName()).collect(Collectors.toSet());
            orderTables.addAll(orderTablesTmp);
            whereAndOrderTables.addAll(orderTablesTmp);
        }
        Expression where = select.getWhere();
        if(where != null) {
            WhereVisitor whereVisitor = new WhereVisitor();
            getWhereTable(where, whereVisitor);

            whereTables.addAll(whereVisitor.getTables());
            whereAndOrderTables.addAll(whereVisitor.getTables());
        }

        Optional<String> optional = whereAndOrderTables.stream().filter(joinMap::containsKey).findFirst();
        if(optional.isPresent()) {
            this.onlyMainTable = false;
        }
    }

    public PlainSelect getSelect() {
        return select;
    }

    /**
     * 判断是否有关联查询
     */
    public boolean isJoinEmpty() {
        return joinMap == null || joinMap.isEmpty();
    }

    public boolean isOnlyMainTable() {
        return onlyMainTable;
    }

    public Map<String, Join> getJoinMap() {
        return joinMap;
    }

    public Set<String> getWhereTables() {
        return whereTables;
    }

    public Set<String> getOrderTables() {
        return orderTables;
    }

    /**
     * 判断是否只有主表的排序
     */
    public boolean isOrderMainTable() {
        Optional<String> optional = orderTables.stream().filter(joinMap::containsKey).findFirst();
        return !optional.isPresent();

    }

    private void getWhereTable(Expression where, WhereVisitor whereVisitor) {
        if(where instanceof AndExpression) {
            Expression leftExpression = ((AndExpression) where).getLeftExpression();
            Expression rightExpression = ((AndExpression) where).getRightExpression();
            getWhereTable(leftExpression, whereVisitor);
            getWhereTable(rightExpression, whereVisitor);
        } else if(where instanceof OrExpression) {
            Expression leftExpression = ((OrExpression) where).getLeftExpression();
            Expression rightExpression = ((OrExpression) where).getRightExpression();
            getWhereTable(leftExpression, whereVisitor);
            getWhereTable(rightExpression, whereVisitor);
        } else {
            where.accept(whereVisitor);
        }
    }

    private static class WhereVisitor implements ExpressionVisitor {

        private final Set<String> tables = new LinkedHashSet<>();

        public Set<String> getTables() {
            return tables;
        }

        @Override
        public void visit(BitwiseRightShift aThis) {

        }

        @Override
        public void visit(BitwiseLeftShift aThis) {

        }

        @Override
        public void visit(NullValue nullValue) {

        }

        @Override
        public void visit(Function function) {

        }

        @Override
        public void visit(SignedExpression signedExpression) {

        }

        @Override
        public void visit(JdbcParameter jdbcParameter) {

        }

        @Override
        public void visit(JdbcNamedParameter jdbcNamedParameter) {

        }

        @Override
        public void visit(DoubleValue doubleValue) {

        }

        @Override
        public void visit(LongValue longValue) {

        }

        @Override
        public void visit(HexValue hexValue) {

        }

        @Override
        public void visit(DateValue dateValue) {

        }

        @Override
        public void visit(TimeValue timeValue) {

        }

        @Override
        public void visit(TimestampValue timestampValue) {

        }

        @Override
        public void visit(Parenthesis parenthesis) {

        }

        @Override
        public void visit(StringValue stringValue) {

        }

        @Override
        public void visit(Addition addition) {

        }

        @Override
        public void visit(Division division) {

        }

        @Override
        public void visit(IntegerDivision division) {

        }

        @Override
        public void visit(Multiplication multiplication) {

        }

        @Override
        public void visit(Subtraction subtraction) {

        }

        @Override
        public void visit(AndExpression andExpression) {

        }

        @Override
        public void visit(OrExpression orExpression) {

        }

        @Override
        public void visit(XorExpression orExpression) {

        }

        @Override
        public void visit(Between between) {
            tables.add(((Column)between.getLeftExpression()).getTable().getName());
        }

        @Override
        public void visit(OverlapsCondition overlapsCondition) {

        }

        @Override
        public void visit(EqualsTo equalsTo) {
            tables.add(((Column)equalsTo.getLeftExpression()).getTable().getName());
        }

        @Override
        public void visit(GreaterThan greaterThan) {
            tables.add(((Column)greaterThan.getLeftExpression()).getTable().getName());
        }

        @Override
        public void visit(GreaterThanEquals greaterThanEquals) {
            tables.add(((Column)greaterThanEquals.getLeftExpression()).getTable().getName());
        }

        @Override
        public void visit(InExpression inExpression) {
            tables.add(((Column)inExpression.getLeftExpression()).getTable().getName());
        }

        @Override
        public void visit(FullTextSearch fullTextSearch) {

        }

        @Override
        public void visit(IsNullExpression isNullExpression) {
            tables.add(((Column)isNullExpression.getLeftExpression()).getTable().getName());
        }

        @Override
        public void visit(IsBooleanExpression isBooleanExpression) {
            tables.add(((Column)isBooleanExpression.getLeftExpression()).getTable().getName());
        }

        @Override
        public void visit(LikeExpression likeExpression) {
            tables.add(((Column)likeExpression.getLeftExpression()).getTable().getName());
        }

        @Override
        public void visit(MinorThan minorThan) {
            tables.add(((Column)minorThan.getLeftExpression()).getTable().getName());
        }

        @Override
        public void visit(MinorThanEquals minorThanEquals) {
            tables.add(((Column)minorThanEquals.getLeftExpression()).getTable().getName());
        }

        @Override
        public void visit(NotEqualsTo notEqualsTo) {
            tables.add(((Column)notEqualsTo.getLeftExpression()).getTable().getName());
        }

        @Override
        public void visit(Column tableColumn) {

        }

        @Override
        public void visit(SubSelect subSelect) {

        }

        @Override
        public void visit(CaseExpression caseExpression) {

        }

        @Override
        public void visit(WhenClause whenClause) {

        }

        @Override
        public void visit(ExistsExpression existsExpression) {

        }

        @Override
        public void visit(AnyComparisonExpression anyComparisonExpression) {

        }

        @Override
        public void visit(Concat concat) {

        }

        @Override
        public void visit(Matches matches) {

        }

        @Override
        public void visit(BitwiseAnd bitwiseAnd) {

        }

        @Override
        public void visit(BitwiseOr bitwiseOr) {

        }

        @Override
        public void visit(BitwiseXor bitwiseXor) {

        }

        @Override
        public void visit(CastExpression cast) {

        }

        @Override
        public void visit(TryCastExpression cast) {

        }

        @Override
        public void visit(SafeCastExpression cast) {

        }

        @Override
        public void visit(Modulo modulo) {

        }

        @Override
        public void visit(AnalyticExpression aexpr) {

        }

        @Override
        public void visit(ExtractExpression eexpr) {

        }

        @Override
        public void visit(IntervalExpression iexpr) {

        }

        @Override
        public void visit(OracleHierarchicalExpression oexpr) {

        }

        @Override
        public void visit(RegExpMatchOperator rexpr) {

        }

        @Override
        public void visit(JsonExpression jsonExpr) {

        }

        @Override
        public void visit(JsonOperator jsonExpr) {

        }

        @Override
        public void visit(RegExpMySQLOperator regExpMySQLOperator) {

        }

        @Override
        public void visit(UserVariable var) {

        }

        @Override
        public void visit(NumericBind bind) {

        }

        @Override
        public void visit(KeepExpression aexpr) {

        }

        @Override
        public void visit(MySQLGroupConcat groupConcat) {

        }

        @Override
        public void visit(ValueListExpression valueList) {

        }

        @Override
        public void visit(RowConstructor rowConstructor) {

        }

        @Override
        public void visit(RowGetExpression rowGetExpression) {

        }

        @Override
        public void visit(OracleHint hint) {

        }

        @Override
        public void visit(TimeKeyExpression timeKeyExpression) {

        }

        @Override
        public void visit(DateTimeLiteralExpression literal) {

        }

        @Override
        public void visit(NotExpression aThis) {

        }

        @Override
        public void visit(NextValExpression aThis) {

        }

        @Override
        public void visit(CollateExpression aThis) {

        }

        @Override
        public void visit(SimilarToExpression aThis) {

        }

        @Override
        public void visit(ArrayExpression aThis) {

        }

        @Override
        public void visit(ArrayConstructor aThis) {

        }

        @Override
        public void visit(VariableAssignment aThis) {

        }

        @Override
        public void visit(XMLSerializeExpr aThis) {

        }

        @Override
        public void visit(TimezoneExpression aThis) {

        }

        @Override
        public void visit(JsonAggregateFunction aThis) {

        }

        @Override
        public void visit(JsonFunction aThis) {

        }

        @Override
        public void visit(ConnectByRootOperator aThis) {

        }

        @Override
        public void visit(OracleNamedFunctionParameter aThis) {

        }

        @Override
        public void visit(AllColumns allColumns) {

        }

        @Override
        public void visit(AllTableColumns allTableColumns) {

        }

        @Override
        public void visit(AllValue allValue) {

        }

        @Override
        public void visit(IsDistinctExpression isDistinctExpression) {

        }

        @Override
        public void visit(GeometryDistance geometryDistance) {

        }
    }

}
