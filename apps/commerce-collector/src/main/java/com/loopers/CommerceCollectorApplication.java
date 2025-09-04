package com.loopers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan
@SpringBootApplication
public class CommerceCollectorApplication {
    public static void main(String[] args) {
        SpringApplication.run(CommerceCollectorApplication.class, args);
    }
}
