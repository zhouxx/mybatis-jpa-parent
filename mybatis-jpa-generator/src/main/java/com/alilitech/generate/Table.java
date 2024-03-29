/*
 *    Copyright 2017-2021 the original author or authors.
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
package com.alilitech.generate;

import com.alilitech.generate.config.TableConfig;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Zhou Xiaoxiang
 * @since 1.0
 */
public class Table {

    private List<TableColumn> tableColumns;

    private TableConfig tableConfig;

    private List<TableColumn> primaryKeyColumns;

    public Table(List<TableColumn> tableColumns, TableConfig tableConfig) {
        this.tableColumns = tableColumns;
        this.tableConfig = tableConfig;
        this.primaryKeyColumns = tableColumns.stream().filter(TableColumn::isPrimary).collect(Collectors.toList());
    }

    public List<TableColumn> getTableColumns() {
        return tableColumns;
    }

    public TableConfig getTableConfig() {
        return tableConfig;
    }

    public List<TableColumn> getPrimaryKeyColumns() {
        return primaryKeyColumns;
    }

    public boolean isCompositeKeys() {
        return primaryKeyColumns.size() > 1;
    }
}
