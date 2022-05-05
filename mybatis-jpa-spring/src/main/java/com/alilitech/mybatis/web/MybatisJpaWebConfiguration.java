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
package com.alilitech.mybatis.web;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * @author Zhou Xiaoxiang
 * @since 1.0
 */
@ConditionalOnClass(WebMvcConfigurer.class)
public class MybatisJpaWebConfiguration {

    @Bean
    public SortArgumentResolver mybatisSortResolver() {
        return new SortArgumentResolver();
    }

    @Bean
    public PageableArgumentResolver mybatisPageableResolver(SortArgumentResolver sortArgumentResolver) {
        return new PageableArgumentResolver(sortArgumentResolver);
    }

    @Bean
    WebMvcConfigurer jpaWebMvcConfigurer(
            PageableArgumentResolver pageableArgumentResolver,
            SortArgumentResolver sortArgumentResolver) {
        return new WebMvcConfigurer() {
            @Override
            public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
                resolvers.add(pageableArgumentResolver);
                resolvers.add(sortArgumentResolver);
            }
        };
    }


}
