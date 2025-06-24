package com.star.swiftStart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;


@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class}, scanBasePackages = {"com.star"})
public class SwiftStartApplication {

    public static void main(String[] args) {
        SpringApplication.run(SwiftStartApplication.class, args);
        System.out.println("SwiftStartApplication started");
    }

}
