package com.alilitech.mybatis.jpa.test.domain;

import com.alilitech.mybatis.jpa.anotation.*;
import com.alilitech.mybatis.jpa.anotation.GeneratedValue;
import com.alilitech.mybatis.jpa.parameter.GenerationType;
import com.alilitech.mybatis.jpa.parameter.TriggerValueType;
import com.alilitech.mybatis.jpa.test.utils.CommonUtil;
import org.apache.ibatis.mapping.SqlCommandType;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 CREATE TABLE `t_user`(
 `test_user_id` VARCHAR(32) NOT NULL,
 `name` VARCHAR(100),
 `sex` TINYINT,
 `age` INT,
 `createTime` DATETIME,
 `dept_no` VARCHAR(50),
 PRIMARY KEY (`id`)
 );
 * Created by zhouxx on 2018/7/25.
 */
@Table(name = "t_user")
public class TestUser {

    @Id
    @GeneratedValue(GenerationType.AUTO)
    private String testUserId;

    private String name;

    private Sex sex;

    private Integer age;

    //代码触发器
    @TriggerValue(triggers = {
            @Trigger(triggerType = SqlCommandType.INSERT, valueType = TriggerValueType.JAVA_CODE, valueClass = CommonUtil.class, methodName = "getCurrentDate"),
            @Trigger(triggerType = SqlCommandType.UPDATE, valueType = TriggerValueType.JAVA_CODE, valueClass = CommonUtil.class, methodName = "getCurrentDate", force = false)
    })
    //自定义数据库字段
    @Column(name = "createTime")
    private Date createTime;

    private String deptNo;

    //ManyToOne演示
    @ManyToOne
    @JoinColumn(name = "deptNo", referencedColumnName = "deptNo")
    //子查询演示
    @SubQuery(
            predicates = @SubQuery.Predicate(property = "deptNo",condition = "> '0'"),
            orders = @SubQuery.Order(property = "deptNo"))
    //哪些方法关联演示
    @MappedStatement(include = {"findById"})
    private TestDept dept;

    //哪些方法关联演示
    @MappedStatement(exclude = {"findById"})
    //ManyToMany演示
    @ManyToMany
    @JoinTable(name = "user_role",
            joinColumns = @JoinColumn(name = "userId", referencedColumnName = "testUserId"),
            inverseJoinColumns = @JoinColumn(name = "roleId", referencedColumnName = "id"))
    //子查询演示
    @SubQuery(
            predicates = {@SubQuery.Predicate(property = "roleCode",condition = "<> '0'"), @SubQuery.Predicate(property = "roleCode",condition = "> '0'")},
            orders = @SubQuery.Order(property = "roleCode"))
    private List<TestRole> roles;

    public TestUser() {
    }

    public TestUser(String testUserId, String name, Sex sex, Integer age, String deptNo) {
        this.testUserId = testUserId;
        this.name = name;
        this.sex = sex;
        this.age = age;
        this.deptNo = deptNo;
    }

    public String getTestUserId() {
        return testUserId;
    }

    public void setTestUserId(String testUserId) {
        this.testUserId = testUserId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Sex getSex() {
        return sex;
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getDeptNo() {
        return deptNo;
    }

    public void setDeptNo(String deptNo) {
        this.deptNo = deptNo;
    }

    public TestDept getDept() {
        return dept;
    }

    public void setDept(TestDept dept) {
        this.dept = dept;
    }

    public List<TestRole> getRoles() {
        return roles;
    }

    public void setRoles(List<TestRole> roles) {
        this.roles = roles;
    }

    @Override
    public String toString() {
        return "TestUser{" +
                "testUserId='" + testUserId + '\'' +
                ", name='" + name + '\'' +
                ", sex=" + sex +
                ", age=" + age +
                ", createTime=" + createTime +
                ", deptNo='" + deptNo + '\'' +
                ", dept=" + dept +
                ", roles=" + roles +
                '}';
    }
}
