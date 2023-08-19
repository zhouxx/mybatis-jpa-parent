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

import net.sf.jsqlparser.statement.select.Select;

/**
 * @author Zhou Xiaoxiang
 * @since 2.2
 */
public class ParseResult {

    private Select select;

    private boolean onlyMainTable;

    public ParseResult(Select select) {
        this.select = select;
    }

    public ParseResult(Select select, boolean onlyMainTable) {
        this.select = select;
        this.onlyMainTable = onlyMainTable;
    }

    public Select getSelect() {
        return select;
    }

    public boolean isOnlyMainTable() {
        return onlyMainTable;
    }

    public void setOnlyMainTable(boolean onlyMainTable) {
        this.onlyMainTable = onlyMainTable;
    }
}
