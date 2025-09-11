package com.loopers.interfaces.scheduler;

import com.loopers.domain.metric.ProductMetricService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class RankingScheduler {

    private final ProductMetricService productMetricService;

    @Scheduled(cron = "0 * * * * *")
    public void rebuildAllRankings() {
        productMetricService.rebuildAllRankings(LocalDate.now());
    }
}
