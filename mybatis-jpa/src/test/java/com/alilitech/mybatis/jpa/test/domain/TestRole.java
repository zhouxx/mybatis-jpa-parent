package com.alilitech.mybatis.jpa.test.domain;

import com.alilitech.mybatis.jpa.anotation.GeneratedValue;
import com.alilitech.mybatis.jpa.parameter.GenerationType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.List;

/**
 * CREATE TABLE `t_role` (
 *   `id` varchar(255)  NOT NULL,
 *   `role_name` varchar(255)  DEFAULT NULL,
 *   `role_code` varchar(255)  DEFAULT NULL,
 *   `role_description` varchar(255) DEFAULT NULL COMMENT '角色描述',
 *   PRIMARY KEY (`id`)
 * )
 * COMMENT='角色';
 */
@Table(name = "t_role")
@Getter
@Setter
public class TestRole {

	/*
	 * 角色id
	 */
	@Id
	@GeneratedValue(value = GenerationType.AUTO)
	private String id;

	/*
	 * 角色名称
	 */
	//@Column(name = "role_name")
	private String roleName;

	/*
	 * 角色编码
	 */
	private String roleCode;

	/*
	 * 角色描述
	 */
	private String roleDescription;

	/**
	 * 用户列表
	 */
	@ManyToMany(mappedBy = "roles")
	private List<TestUser> users;

	@Override
	public String toString() {
		return "TestRole{" +
				"id='" + id + '\'' +
				", roleName='" + roleName + '\'' +
				", roleCode='" + roleCode + '\'' +
				", roleDescription='" + roleDescription + '\'' +
				", users=" + users +
				'}';
	}
}
