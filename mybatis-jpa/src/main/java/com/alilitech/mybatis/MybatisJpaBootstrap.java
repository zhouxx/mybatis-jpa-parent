/**
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
package com.alilitech.mybatis;

import com.alilitech.mybatis.dialect.KeySqlDialectRegistry;
import com.alilitech.mybatis.dialect.PaginationDialectRegistry;
import com.alilitech.mybatis.extension.DatabaseRegistration;
import com.alilitech.mybatis.extension.DatabaseRegistry;
import com.alilitech.mybatis.jpa.DatabaseTypeRegistry;
import com.alilitech.mybatis.jpa.JpaInitializer;
import com.alilitech.mybatis.jpa.pagination.PaginationInterceptor;
import com.alilitech.mybatis.jpa.parameter.MybatisJpaLanguageDriver;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.mapping.VendorDatabaseIdProvider;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.Configuration;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Mybatis jpa bootstrap
 *
 * @author Zhou Xiaoxiang
 * @since 2.0
 */
public class MybatisJpaBootstrap {

    private static final Log log = LogFactory.getLog(MybatisJpaBootstrap.class);

    private final AtomicBoolean started = new AtomicBoolean(false);

    private final Configuration configuration;

    private final DatabaseRegistry databaseRegistry = DatabaseRegistry.getInstance();

    private String databaseId;

    private MybatisJpaProperties mybatisJpaProperties = new MybatisJpaProperties();

    public MybatisJpaBootstrap(Configuration configuration) {
        this.configuration = configuration;
    }

    public void start() {

        // only once
        if(!started.compareAndSet(false, true)) {
            return;
        }

        // set databaseId
        if(databaseId == null) {
            // 强制设置databaseId
            databaseId = new VendorDatabaseIdProvider().getDatabaseId(configuration.getEnvironment().getDataSource());

            if(databaseId == null) {
                log.warn("Datasource's databaseId is not exist!!!");
            }

            configuration.setDatabaseId(databaseId);
        }

        configuration.setDefaultScriptingLanguage(MybatisJpaLanguageDriver.class);

        // add pagination interceptor
        List<Interceptor> interceptors = configuration.getInterceptors();
        if(!CollectionUtils.isEmpty(interceptors)) {
            boolean containPageInterceptor = false;

            for(Interceptor interceptor : interceptors) {
                if(interceptor instanceof PaginationInterceptor) {
                    containPageInterceptor = true;
                    break;
                }
            }

            if(!containPageInterceptor) {
                configuration.addInterceptor(new PaginationInterceptor(mybatisJpaProperties));
            }

        } else {
            configuration.addInterceptor(new PaginationInterceptor(mybatisJpaProperties));
        }

        JpaInitializer jpaInitializer = new JpaInitializer(configuration);
        jpaInitializer.buildJoinMetaDataAndRelationMethodDefinition().invokeJpaMapperStatementBuilder();

        //really start register
        List<DatabaseRegistration> databaseRegistrations = databaseRegistry.getDatabaseRegistrations();

        if(!databaseRegistrations.isEmpty()) {
            DatabaseTypeRegistry databaseTypeRegistry = DatabaseTypeRegistry.getInstance();
            KeySqlDialectRegistry keySqlDialectRegistry = KeySqlDialectRegistry.getInstance();
            PaginationDialectRegistry paginationDialectRegistry = PaginationDialectRegistry.getInstance();
            databaseRegistrations.forEach(databaseRegistration -> {
                databaseTypeRegistry.register(databaseRegistration.getDatabaseType());
                if(databaseRegistration.getKeySqlGenerator() != null) {
                    keySqlDialectRegistry.register(databaseRegistration.getDatabaseType(), databaseRegistration.getKeySqlGenerator());
                }
                if(databaseRegistration.getPaginationDialect() == null) {
                    log.warn("DatabaseType: " + databaseRegistration.getDatabaseType().getDatabaseId() + " does not provider pagination dialect, see class com.alilitech.integration.dialect.pagination.PaginationDialect");
                } else {
                    paginationDialectRegistry.register(databaseRegistration.getDatabaseType(), databaseRegistration.getPaginationDialect());
                }
            });
        }
    }

    public MybatisJpaBootstrap addDatabaseRegistration(DatabaseRegistration databaseRegistration) {
        databaseRegistry.addDatabase(databaseRegistration);
        return this;
    }

    public MybatisJpaBootstrap setDatabaseId(String databaseId) {
        this.databaseId = databaseId;
        return this;
    }

    public MybatisJpaBootstrap setMybatisJpaProperties(MybatisJpaProperties mybatisJpaProperties) {
        this.mybatisJpaProperties = mybatisJpaProperties;
        return this;
    }
}
