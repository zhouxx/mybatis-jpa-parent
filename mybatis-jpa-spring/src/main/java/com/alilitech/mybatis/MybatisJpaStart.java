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
package com.alilitech.mybatis;

import com.alilitech.mybatis.dialect.KeySqlDialectRegistry;
import com.alilitech.mybatis.dialect.PaginationDialectRegistry;
import com.alilitech.mybatis.extension.DatabaseRegistration;
import com.alilitech.mybatis.extension.DatabaseRegistry;
import com.alilitech.mybatis.jpa.DatabaseTypeRegistry;
import com.alilitech.mybatis.jpa.JpaInitializer;
import com.alilitech.mybatis.jpa.parameter.MybatisJpaLanguageDriver;
import com.alilitech.mybatis.jpa.type.DefaultEnumTypeHandler;
import com.alilitech.mybatis.spring.MybatisJpaConfigurer;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.SmartLifecycle;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * initialize Mybatis Jpa
 * @author Zhou Xiaoxiang
 * @since 1.0
 */
public class MybatisJpaStart implements SmartLifecycle, ApplicationContextAware {

    private static final Log log = LogFactory.getLog(MybatisJpaStart.class);

    private ApplicationContext applicationContext;

    private final SqlSessionFactory sqlSessionFactory;
    private final DatabaseRegistry databaseRegistry;
    private final DatabaseTypeRegistry databaseTypeRegistry;
    private final KeySqlDialectRegistry keySqlDialectRegistry;
    private final PaginationDialectRegistry paginationDialectRegistry;
    private final List<MybatisJpaConfigurer> mybatisJpaConfigurers;

    private final AtomicBoolean started = new AtomicBoolean(false);

    public MybatisJpaStart(SqlSessionFactory sqlSessionFactory, DatabaseRegistry databaseRegistry, DatabaseTypeRegistry databaseTypeRegistry, KeySqlDialectRegistry keySqlDialectRegistry, PaginationDialectRegistry paginationDialectRegistry, List<MybatisJpaConfigurer> mybatisJpaConfigurers) {
        this.sqlSessionFactory = sqlSessionFactory;
        this.databaseRegistry = databaseRegistry;
        this.databaseTypeRegistry = databaseTypeRegistry;
        this.keySqlDialectRegistry = keySqlDialectRegistry;
        this.paginationDialectRegistry = paginationDialectRegistry;
        this.mybatisJpaConfigurers = mybatisJpaConfigurers;
    }

    @Override
    public void start() {
        // only execute once
        if(!started.compareAndSet(false, true)) {
            return;
        }

        // mybatis configuration
        org.apache.ibatis.session.Configuration configuration = sqlSessionFactory.getConfiguration();
        configuration.setDefaultScriptingLanguage(MybatisJpaLanguageDriver.class);
        configuration.setDefaultEnumTypeHandler(DefaultEnumTypeHandler.class);

        //start mybatis jpa
        StopWatch watch = new StopWatch();
        watch.start();

        JpaInitializer jpaInitializer = new JpaInitializer(configuration);
        jpaInitializer.buildJoinMetaDataAndRelationMethodDefinition().invokeJpaMapperStatementBuilder();

        // add custom database
        if(!CollectionUtils.isEmpty(mybatisJpaConfigurers)) {
            mybatisJpaConfigurers.forEach(mybatisJpaConfigurer -> mybatisJpaConfigurer.addDatabase(databaseRegistry));
        }

        //really start register
        List<DatabaseRegistration> databaseRegistrations = databaseRegistry.getDatabaseRegistrations();

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

        watch.stop();
        if(log.isDebugEnabled()) {
            log.debug("Started Mybatis Jpa in " + watch.getTotalTimeMillis() + " ms");
        }
        applicationContext.publishEvent(new MybatisJpaStartedEvent(applicationContext));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void stop() {

    }

    @Override
    public boolean isRunning() {
        return false;
    }

    @Override
    public int getPhase() {
        return Integer.MAX_VALUE - 2;
    }
}
