package com.loopers.domain.metric;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

public interface ProductMetricRepository {

    void upsertLike(Long productId,
                    LocalDate metricDate,
                    Long count,
                    LocalDateTime eventTime);

    void upsertSales(Long productId,
                     LocalDate metricDate,
                     Long count,
                     LocalDateTime eventTime);

    void upsertPageView(Long productId,
                        LocalDate metricDate,
                        Long count,
                        LocalDateTime eventTime);

    void carryOver(LocalDate today, double rate, Duration hour);

    void rebuildDailyAllRankings(LocalDate date);
}
