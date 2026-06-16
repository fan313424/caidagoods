package com.caida;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.caida.mapper")
public class CaidaApplication {

    public static void main(String[] args) {
        SpringApplication.run(CaidaApplication.class, args);
    }

}
