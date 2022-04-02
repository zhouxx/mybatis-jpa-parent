package com.alilitech.mybatis.spring;

import org.mybatis.logging.Logger;
import org.mybatis.logging.LoggerFactory;
import org.mybatis.spring.mapper.ClassPathMapperScanner;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.StringUtils;

public class MybatisJpaMapperScanner implements BeanDefinitionRegistryPostProcessor, ResourceLoaderAware, EnvironmentAware {

    public static final String SCAN_KEY = "mybatis.mapper-scan.base-packages";

    private static final Logger logger = LoggerFactory.getLogger(MybatisJpaMapperScanner.class);

    private ResourceLoader resourceLoader;

    private String mapperScanPackages;

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {

        if(StringUtils.isEmpty(mapperScanPackages)) {
            logger.warn(() -> "mybatis.mapper-scan.base-packages not found!");
        } else {
            ClassPathMapperScanner scanner = new ClassPathMapperScanner(registry);

            if (resourceLoader != null) {
                scanner.setResourceLoader(resourceLoader);
            }
            scanner.registerFilters();
            scanner.doScan(StringUtils.tokenizeToStringArray(mapperScanPackages, ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS));
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    /**
     * set scan packages
     * @param environment
     */
    @Override
    public void setEnvironment(Environment environment) {
        this.mapperScanPackages = environment.getProperty(SCAN_KEY);
    }
}
