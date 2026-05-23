package com.esportclub;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.esportclub.mapper")
public class EsportClubApplication {
    public static void main(String[] args) {
        SpringApplication.run(EsportClubApplication.class, args);
    }
}
