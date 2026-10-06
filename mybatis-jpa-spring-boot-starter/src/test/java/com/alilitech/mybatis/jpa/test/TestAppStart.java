package com.alilitech.mybatis.jpa.test;

import com.alilitech.mybatis.jpa.test.domain.User;
import com.alilitech.mybatis.jpa.test.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;

@SpringBootApplication
public class TestAppStart implements ApplicationRunner {
    @Autowired
    private UserMapper userMapper;

    public static void main(String[] args) {
        SpringApplication.run(TestAppStart.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        User userJack = new User();
        userJack.setId(1L);
        userJack.setName("Jack");
        userJack.setAge(18);
        userJack.setEmail("jack@123.com");

        User userHellen = new User();
        userHellen.setId(2L);
        userHellen.setName("Hellen");
        userHellen.setAge(19);
        userHellen.setEmail("Hellen@123.com");
        userMapper.insertBatch(Arrays.asList(userJack, userHellen));

        System.out.println(userMapper.findAll());
    }
}
