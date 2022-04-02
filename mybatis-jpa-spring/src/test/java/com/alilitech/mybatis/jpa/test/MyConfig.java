package com.alilitech.mybatis.jpa.test;

import com.alilitech.mybatis.MyBatisJpaConfiguration;
import com.alilitech.mybatis.jpa.DatabaseIdProviderImpl;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;

import javax.sql.DataSource;
import java.io.IOException;

@ImportAutoConfiguration(MyBatisJpaConfiguration.class)
@Configuration
@PropertySource(value = {"classpath:application.properties"})
public class MyConfig {

    @Value("${hikaricp.configurationFile}")
    String configurationFile;

    @Bean
    public DataSource dataSource() throws IOException {
        System.setProperty("hikaricp.configurationFile",new ClassPathResource(configurationFile).getURL().getPath());
        HikariDataSource hikariDataSource = new HikariDataSource(new HikariConfig());
        return hikariDataSource;
    }

    @Bean(name = "sqlSessionFactory")
    public SqlSessionFactoryBean sqlSessionFactory(DataSource dataSource) {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource);
        factoryBean.setDatabaseIdProvider(new DatabaseIdProviderImpl());
//        factoryBean.setConfigLocation(new ClassPathResource(resource));
        return factoryBean;
    }
}
