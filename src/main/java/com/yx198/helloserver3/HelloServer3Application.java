package com.yx198.helloserver3;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.yx198.helloserver3.mapper")
public class HelloServer3Application {
    public static void main(String[] args) {
        SpringApplication.run(HelloServer3Application.class, args);
    }
}