package com.itmang;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.itmang.mapper")
public class PartySystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(PartySystemApplication.class, args);
    }

}
