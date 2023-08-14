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
package com.alilitech.mybatis.jpa.test.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

/**
 * CREATE TABLE `user_role` (
 *   `user_id` varchar(36) NOT NULL COMMENT '用户ID',
 *   `role_id` varchar(36) NOT NULL COMMENT '角色id',
 *   `enabled` tinyint DEFAULT NULL COMMENT '是否启用',
 *   PRIMARY KEY (`user_id`,`role_id`)
 * );
 *
 * @author Zhou Xiaoxiang
 * @since 2.1
 */
@Table(name = "user_role")
@Getter
@Setter
@IdClass(UserRolePK.class)
public class UserRole {

    @Id
    private String userId;

    @Id
    private String roleId;

    private Boolean enabled;

}
