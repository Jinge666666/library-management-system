package com.jing.librarymanagementsystem;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@MapperScan("com.jing.librarymanagementsystem.mappers")
@SpringBootApplication
@EnableTransactionManagement   // //事务处理的时候启动类必须要加的注解
// 监听器实际也是servlet，须配置servlet扫描
@ServletComponentScan("com.jing.librarymanagementsystem.listener")
public class LibraryManagementSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(LibraryManagementSystemApplication.class, args);
    }

}
