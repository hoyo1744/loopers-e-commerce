package com.loopers.interfaces.scheduler;

import com.loopers.config.RankingProperties;
import com.loopers.domain.metric.ProductMetricService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class RankingCarryOverScheduler {

    private final ProductMetricService productMetricService;

    private final RankingProperties rankingProperties;

    @Scheduled(cron = "0 50 23 * * *", zone = "Asia/Seoul")
    public void prewarmTomorrow() {
        scoreCarryOver();
    }

    private void scoreCarryOver() {
        if(!rankingProperties.getCarryOver().isEnabled()) {
            return;
        }

        LocalDate today = LocalDate.now();
        double rate = rankingProperties.getCarryOver().getRate();
        long ttlHours = rankingProperties.getTtlHours();
        productMetricService.scoreCarry(today, rate, ttlHours);
    }

}
