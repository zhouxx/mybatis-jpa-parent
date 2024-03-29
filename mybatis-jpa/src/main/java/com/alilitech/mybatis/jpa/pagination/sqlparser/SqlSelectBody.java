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

import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.select.SelectVisitor;

/**
 * @author Zhou Xiaoxiang
 * @since 2.2
 */
public class SqlSelectBody implements SelectBody {

    private String sql;

    public SqlSelectBody withSql(String sql) {
        this.sql = sql;
        return this;
    }
    @Override
    public void accept(SelectVisitor selectVisitor) {
    }

    @Override
    public String toString() {
        return sql;
    }
}
