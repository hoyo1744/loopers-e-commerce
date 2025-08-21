package com.loopers.config;

import feign.Request;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PgFeignClientConfig {

    @Bean
    public Request.Options feignOption() {
        return new Request.Options(1000, 3000);
    }
}
