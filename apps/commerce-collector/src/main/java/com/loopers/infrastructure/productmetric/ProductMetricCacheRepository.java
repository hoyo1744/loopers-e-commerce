package com.loopers.infrastructure.productmetric;

import java.time.Duration;
import java.time.LocalDate;

public interface ProductMetricCacheRepository {
    void upsertLike(Long productId, LocalDate metricDate, Long delta);
    void upsertSales(Long productId, LocalDate metricDate, Long delta);
    void upsertPageViews(Long productId, LocalDate metricDate, Long delta);
    void carryOver(LocalDate today, Double rate, Duration hour);
    void rebuildDailyAllRankings(LocalDate date);
}
