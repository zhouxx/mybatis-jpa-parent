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
package com.alilitech.mybatis.dialect.pagination.support;


import com.alilitech.mybatis.dialect.pagination.PaginationDialect;


/**
 * ORACLE Pagination
 *
 * @author Zhou Xiaoxiang
 * @since 1.0
 */
public class OraclePaginationDialect implements PaginationDialect {

    @Override
    public String buildPaginationSql(String originalSql, int offset, int limit) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM ( SELECT TMP.*, ROWNUM ROW_ID FROM ( ");
        sql.append(originalSql).append(" ) TMP WHERE ROWNUM <=").append((offset >= 1) ? (offset + limit) : limit);
        sql.append(") WHERE ROW_ID > ").append(offset);
        return sql.toString();
    }
}
