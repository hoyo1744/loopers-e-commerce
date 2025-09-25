package com.loopers.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "ranking")
@Getter @Setter
public class RankingProperties {

    private Weight weight = new Weight();

    @Getter @Setter
    public static class Weight {
        private double like = 0.2;
        private double sales = 0.7;
        private double pv = 0.1;
    }
}
