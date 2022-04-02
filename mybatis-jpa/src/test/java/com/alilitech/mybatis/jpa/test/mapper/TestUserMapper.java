package com.alilitech.mybatis.jpa.test.mapper;

import com.alilitech.mybatis.jpa.test.domain.TestUser;
import com.alilitech.mybatis.jpa.anotation.IfTest;
import com.alilitech.mybatis.jpa.criteria.Specification;
import com.alilitech.mybatis.jpa.domain.Pageable;
import com.alilitech.mybatis.jpa.domain.Sort;
import com.alilitech.mybatis.jpa.mapper.CrudMapper;
import com.alilitech.mybatis.jpa.mapper.PageMapper;
import com.alilitech.mybatis.jpa.mapper.SpecificationMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Optional;

/**
 * Created by zhouxx on 2018/7/25.
 */
public interface TestUserMapper extends CrudMapper<TestUser, String>, PageMapper<TestUser, String>, SpecificationMapper<TestUser, String> {

    @Select("select * from t_user")
    List<TestUser> findList();

    List<TestUser> findByNameStartsWithAndDeptNoLikeOrderByNameDesc(String name, String deptNo);

    List<TestUser> findByNameStartsWithOrDeptNoAndAgeGreaterThan(String name, String deptNo, int age);

    @IfTest(notEmpty = true)
    List<TestUser> findPageByNameLikeAndDeptNo(Pageable pageable, Sort sort, String name, String deptNo);

    @IfTest(notEmpty = true)
    List<TestUser> findByNameLikeAndDeptNo(String name, String deptNo);

    List<TestUser> findByNameIn(@IfTest(notNull = true) List<String> names);

    Optional<TestUser> findByName(String name);

    List<TestUser> findByNameLike(String name);

    Integer countByNameAndDeptNo(String name, String deptNo);

    Boolean existsByNameAndDeptNo(String name, String deptNo);

    int deleteByNameAndDeptNo(String name, String deptNo);

    List<TestUser> findCustomSpecification(Specification specification);

}
