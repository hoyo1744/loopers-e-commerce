package com.loopers.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "ranking")
@Getter @Setter
public class RankingProperties {
    private long ttlHours = 36;

    private Weight weight = new Weight();

    private CarryOver carryOver = new CarryOver();

    @Getter @Setter
    public static class Weight {
        private double like = 0.2;
        private double sales = 0.7;
        private double pv = 0.1;
    }

    @Getter @Setter
    public static class CarryOver {
        /** 캐리오버 기능 on/off */
        private boolean enabled = true;

        /** 스케줄 크론 (예: "0 0 0 * * *") */
        private String cron = "0 0 0 * * *";

        /** 타임존 (예: "Asia/Seoul") */
        private String zone = "Asia/Seoul";

        /** 전일 점수 이월 비율 (0.1 = 10%) */
        private double rate = 0.10;

        /** 분산락 키 */
        private String lockKey = "_lock:ranking:carryover:daily";

        /** 분산락 TTL(초) */
        private long lockTtlSeconds = 300L;
    }
}
