# CHANGELOG

## [v2.2.4] 2025.5.6

* feature: 提供TypedSort更方便地构建排序

## [v2.2.3] 2025.3.22

* feature: 方法查询支持仅排序，无需其它条件

## [v2.2.2] 2024.1.26

* feature: 支持子表条件关联查询，并适配分页查询

## [v2.2.1] 2023.10.22

* fix bug: 修复用UpdateSpecification更新无法触发trigger的问题

## [v2.2.0] 2023.8.19

* feature: 用join查询替换多次查询（关联），以提升性能
* feature: 提供AbstractObjectTypeHandler，用于对象与字符串之间的转换
* feature: 字段支持自定义jdbcTypes和TypeHandler，用@ColumnResult
* fix bug：移除了自定义的BooleanTypeHandler，直接用官方的，解决了部分场景下可能映射会出现错误的情况

## [v2.1.5] 2023.8.14

* fix bug: 修复只使用mybatis-jpa无法使用count分页的问题
* fix bug: 修复使用mybatis-jpa-spring相关的无法设置枚举属性值的问题

## [v2.1.4] 2023.7.9

* feature: 代码生成器支持联合主键

## [v2.1.3] 2023.6.28

* feature: 支持联合主键

## [v2.1.2] 2023.6.19

* feature: Specification支持lambda传入属性

## [v2.1.1] 2023.6.12

* feature: 枚举支持指定持久化的字段值

## [v2.1.0] 2023.6.8

* feature: Specification支持更新语句
* feature: spring boot version upgrade to 2.7.x

## [v2.0.3] 2022.8.25

* feature: 提供更多的方法去构造Sort
* feature: 代码生成器对数据库的date, datetime 生成对应的LocalDate, LocalDateTime, 对于只是Tyint长度是1的情况转换成Boolean
* feature: 让mybatis-jpa在tomcat端口启动之前启动，保证所有流量进来可用

## [v2.0.2] 2022.5.5

* feature: 主键生成支持Comb uuid，一种基于uuid的优化（相对有序）
* fix bug: 不强制依赖webmvc

## [v2.0.1] 2022.4.29

* feature: 主键生成器支持传入整个实体对象，从而方便生成主键时依赖其它字段
* fix bug: 某些情况下可能会使用Sort排序误报错

## [v2.0.0] 2022.4.2

* 从boot-plus里抽离出来，单独成为一个项目
* mybatis-jpa: 单独使用mybatis进行增强
* mybatis-jpa-spring: 将mybatis-jpa和spring结合使用
* mybatis-jpa-spring-boot-starter: 将mybatis-jpa结合spring boot使用
* version: 将spring boot版本升级至 2.3.12.RELEASE 

