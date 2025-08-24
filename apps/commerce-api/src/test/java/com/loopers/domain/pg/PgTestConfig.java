package com.loopers.domain.pg;

import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@SpringBootTest
@TestConfiguration
public class PgTestConfig {

    @Bean
    PgClient pgClient() {
        return Mockito.mock(PgClient.class);
    }
}
