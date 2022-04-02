package com.alilitech.mybatis.jpa.test.domain;

import com.alilitech.mybatis.jpa.anotation.GeneratedValue;
import com.alilitech.mybatis.jpa.parameter.GenerationType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

/**
 * CREATE TABLE `t_dept`(
 `dept_id` VARCHAR(32) NOT NULL,
 `dept_no` VARCHAR(50),
 `dept_name` VARCHAR(100),
 PRIMARY KEY (`dept_id`)
 );
 * Created by zhouxx on 2018/4/8.
 */
@Table(name = "t_dept")
@Getter
@Setter
@ToString
public class TestDept {

    @Id
    //@GeneratedValue(generatorClass = MyGenerator.class)
    @GeneratedValue(GenerationType.UUID)
    private String deptId;

    private String deptNo;

    private String deptName;

    @OneToMany(mappedBy = "dept")
    private List<TestUser> testUserList;

    public TestDept() {
    }

    public TestDept(String deptNo, String deptName) {
        this.deptNo = deptNo;
        this.deptName = deptName;
    }


}
