# CHANGELOG

## [v2.1.2] 2023.6.19

* feature:  Specification支持lambda传入属性

## [v2.1.1] 2023.6.12

* feature:  枚举支持指定持久化的字段值

## [v2.1.0] 2023.6.8

* feature:  Specification支持更新语句
* feature:  spring boot version upgrade to 2.7.x

## [v2.0.3] 2022.8.25

* feature:  提供更多的方法去构造Sort
* feature:  代码生成器对数据库的date, datetime 生成对应的LocalDate, LocalDateTime, 对于只是Tyint长度是1的情况转换成Boolean
* feature:  让mybatis-jpa在tomcat端口启动之前启动，保证所有流量进来可用

## [v2.0.2] 2022.5.5

* feature:  主键生成支持Comb uuid，一种基于uuid的优化（相对有序）
* fix bug:  不强制依赖webmvc

## [v2.0.1] 2022.4.29

* feature:  主键生成器支持传入整个实体对象，从而方便生成主键时依赖其它字段
* fix bug:  某些情况下可能会使用Sort排序误报错

## [v2.0.0] 2022.4.2

* 从boot-plus里抽离出来，单独成为一个项目
* mybatis-jpa: 单独使用mybatis进行增强
* mybatis-jpa-spring: 将mybatis-jpa和spring结合使用
* mybatis-jpa-spring-boot-starter: 将mybatis-jpa结合spring boot使用
* version: 将spring boot版本升级至 2.3.12.RELEASE 

