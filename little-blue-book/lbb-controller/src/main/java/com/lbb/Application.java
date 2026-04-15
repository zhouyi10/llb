package com.lbb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author: zhouyi
 * @date: 2024/03/08 11:00
 * @description:
 **/
@SpringBootApplication(scanBasePackages = "com.lbb")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }
}
