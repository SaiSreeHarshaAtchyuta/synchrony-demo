package com.synchroncydemo.grocery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class GroeceryApplication {

    public static void main(String[] args) {
        SpringApplication.run(GroeceryApplication.class, args);
    }
}
