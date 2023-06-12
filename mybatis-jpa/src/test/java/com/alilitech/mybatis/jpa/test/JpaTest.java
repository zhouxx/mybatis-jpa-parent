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
package com.alilitech.mybatis.jpa.test;


import com.alilitech.mybatis.MybatisJpaBootstrap;
import com.alilitech.mybatis.jpa.criteria.UpdateSpecification;
import com.alilitech.mybatis.jpa.criteria.expression.PredicateExpression;
import com.alilitech.mybatis.jpa.criteria.specification.Specifications;
import com.alilitech.mybatis.jpa.domain.Direction;
import com.alilitech.mybatis.jpa.domain.Order;
import com.alilitech.mybatis.jpa.domain.Page;
import com.alilitech.mybatis.jpa.domain.Sort;
import com.alilitech.mybatis.jpa.test.domain.Sex;
import com.alilitech.mybatis.jpa.test.domain.TestDept;
import com.alilitech.mybatis.jpa.test.domain.TestUser;
import com.alilitech.mybatis.jpa.test.mapper.TestDeptMapper;
import com.alilitech.mybatis.jpa.test.mapper.TestUserMapper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author Zhou Xiaoxiang
 * @since 2.0
 */
public class JpaTest {

    private TestUserMapper testUserMapper;

    private TestDeptMapper testDeptMapper;

    private SqlSession sqlSession;

    @Before
    public void beforeClass() throws IOException {
        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

        // 加载jpa
        new MybatisJpaBootstrap(sqlSessionFactory.getConfiguration()).start();

        sqlSession = sqlSessionFactory.openSession();

        testUserMapper = sqlSession.getMapper(TestUserMapper.class);
        testDeptMapper = sqlSession.getMapper(TestDeptMapper.class);
    }

    /**
     * 插入演示
     * 可在实体类定义主键，主键的生成规则（包括写个方法自定义）
     * 可在实体类定义代码级触发器，在插入或更新的时候无需操作，自动生成该字段的值然后进行插入
     */
    @Test
    public void insertTest() {

        //全量插入
        TestUser testUser = new TestUser("1", "Jack", Sex.MALE, 20, "002");
        testUserMapper.insert(testUser);

        //非空插入
        TestUser testUser1 = new TestUser("2", "Hellen", Sex.FEMALE, 20, "003");
        TestUser testUser2 = new TestUser("3", "Tom", null, 20, "003");
        testUserMapper.insertSelective(testUser1);
        testUserMapper.insertSelective(testUser2);

        //批量插入，UUID
        testDeptMapper.insertBatch(Arrays.asList(new TestDept("002", "Dept2"), new TestDept("003", "Dept3")));

        //为了后面操作
        testUserMapper.insert(new TestUser("4", "Test", Sex.FEMALE, 18, "004"));
        testUserMapper.insert(new TestUser("5", "Test", Sex.FEMALE, 18, "004"));
        testUserMapper.insert(new TestUser("6", "Test", Sex.FEMALE, 18, "004"));
    }

    /**
     * 更新演示
     * 可在实体类定义代码级触发器，在插入或更新的时候无需操作，自动生成该字段的值然后进行更新
     */
    @Test
    public void updateTest() {

        //全量更新
        TestUser testUser = new TestUser("1", "Jack", Sex.FEMALE, 21, "002");
        testUserMapper.update(testUser);

        //非空更新
        testUser = new TestUser();
        testUser.setId("1");
        testUser.setName("Jackson");
        testUserMapper.updateSelective(testUser);

        //批量更新
        TestUser testUser1 = new TestUser("2", "Hellen", Sex.MALE, 22, "003");
        TestUser testUser2 = new TestUser("3", "Tom", Sex.FEMALE, 23, "003");
        testUserMapper.updateBatch(Arrays.asList(testUser1, testUser2));

    }

    /**
     * 查询演示
     * 在实体类上可定义一对一，一对多，多对多等关联查询
     * 在实体类上可定义哪些方法需要关联，哪些不需要关联
     * 在实体类上可定义子查询的条件和排序（静态，不传参）
     */
    @Test
    public void findTest() {

        //设置分页参数，如果是接口，可直接让前端传过来
        Page page = Page.get().page(1).size(2);

        //设置排序参数，如果是接口，可直接让前端传过来
        Sort sort = new Sort();
        sort.setOrders(Arrays.asList(new Order(Direction.DESC, "id")));

        //CrudMapper 自带的查询演示
        System.out.println(testUserMapper.findAll());
        System.out.println(testUserMapper.findAllById(Arrays.asList("1", "2")));
        System.out.println(testUserMapper.findById("2"));

        //分页演示，只需要传page对象就分布，不传就不分页，哪怕是自定义的sql
        System.out.println(testUserMapper.findAllPage(page));
        //分页+排序演示，只需要传page,sort，排序一般用于单表查询的时候排序
        System.out.println(testUserMapper.findAllPageSort(page, sort));
        //findxxxByxxx演示，根据mapper的方法名自动加载sql，可以在方法或参数上@IfTest来实现动态sql
        System.out.println(testUserMapper.findByName("Jackson").isPresent());
        System.out.println(testUserMapper.findByNameLike("Jack"));
        System.out.println(testUserMapper.findPageByNameLikeAndDeptNo(page, sort, null, "002"));
        System.out.println(testUserMapper.findByNameLikeAndDeptNo(null, null));
        System.out.println(testUserMapper.findByNameIn(Arrays.asList("Hellen", "Tom")));
        System.out.println(testUserMapper.findByNameStartsWithAndDeptNoLikeOrderByNameDesc("Jack", "002"));
        System.out.println(testUserMapper.findByNameStartsWithOrDeptNoAndAgeGreaterThan("Jack", "002", 18));
        System.out.println(testUserMapper.countByNameAndDeptNo("Jackson", "002"));
        System.out.println(testUserMapper.existsByNameAndDeptNo("Jackson", "002"));
        System.out.println(testUserMapper.existsById("1"));
        System.out.println(testUserMapper.findBySex(Sex.FEMALE));


        //自定义sql不会受影响，完全可以自定义
        System.out.println(testUserMapper.findList());

    }

    /**
     * 代码构建复杂查询
     * 同样可关联查询等，同上查询
     */
    @Test
    public void findSpecificationTest() {

        Integer age = 18;

        //代码构建，只需要传入{@link Specification}对象
        //WHERE ( dept_no = ? AND ( age > ? AND name like ?) ) order by name ASC
        List<TestUser> testUsers = testUserMapper.findAllSpecification(Specifications.<TestUser>and()
                .equal("deptNo", "002")
                .nested(builder -> {
                    builder.and()
                            .greaterThan(age != null, "age", age)
                            .like("name", "Jack");
                })
                .order().asc("name").build());

        System.out.println(testUsers);

        //同样传入page参数，即可分页
        Page page = new Page(1, 2);

        testUsers = testUserMapper.findPageSpecification(page, Specifications.<TestUser>and()
                .equal("deptNo", "002")
                .order().asc("name").build());

        System.out.println(testUsers);

        // 仅有排序的代码构建 order by name ASC
        testUsers = testUserMapper.findCustomSpecification(Specifications
                .order().asc("name").build());

        System.out.println(testUsers);

        UpdateSpecification<TestUser> updateSpecification = Specifications.<TestUser>update()
                .set("name", "jack4")
                .set("createTime", new Date(System.currentTimeMillis()-10000000000L))
                .where()
                .equal("name", "jack3")
                .buildUpdate();

        testUserMapper.updateSpecification(updateSpecification);

        testUserMapper.updateSpecification((cb, query) -> {
            PredicateExpression expression = cb.and(cb.in("deptNo", "002", "003"), cb.isNull("createTime"));
            PredicateExpression expression1 = cb.or(cb.lessThan("age", 18), expression);
            query.where(cb.equal("name", "Jack"), expression1);
            query.update(cb.set("name", "jack5"), cb.set("createTime", new Date(System.currentTimeMillis()-10000000000L)));
            return null;
        });
    }

    /**
     * 演示删除
     */
    @Test
    public void deleteTest() {
        testUserMapper.deleteById("4");
        testUserMapper.deleteBatch(Arrays.asList("5", "6"));
        // deleteByxxxx
        testUserMapper.deleteByNameAndDeptNo("Jackson", "002");
    }

    @After
    public void afterClass() {
        sqlSession.commit();
        sqlSession.close();
    }


}
