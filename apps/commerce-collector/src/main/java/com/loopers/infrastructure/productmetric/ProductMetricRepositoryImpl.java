package com.loopers.infrastructure.productmetric;

import com.loopers.domain.metric.ProductMetricRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Repository
@RequiredArgsConstructor
public class ProductMetricRepositoryImpl implements ProductMetricRepository {

    private final ProductMetricJpaRepository productMetricJpaRepository;

    private final ProductMetricCacheRepository productMetricCacheRepository;

    @Override
    public void upsertLike(Long productId, LocalDate metricDate, Long count, LocalDateTime eventTime) {
        productMetricJpaRepository.upsertLike(productId, metricDate, count, eventTime);
        productMetricCacheRepository.upsertLike(productId, metricDate, count);
    }

    @Override
    public void upsertSales(Long productId, LocalDate metricDate, Long count, LocalDateTime eventTime) {
        productMetricJpaRepository.upsertSales(productId, metricDate, count, eventTime);
        productMetricCacheRepository.upsertSales(productId, metricDate, count);
    }

    @Override
    public void upsertPageView(Long productId, LocalDate metricDate, Long count, LocalDateTime eventTime) {
        productMetricJpaRepository.upsertPageView(productId, metricDate, count, eventTime);
        productMetricCacheRepository.upsertPageViews(productId, metricDate, count);
    }

    @Override
    public void carryOver(LocalDate today, double rate, Duration hour) {
        productMetricCacheRepository.carryOver(today, rate, hour);
    }

    @Override
    public void rebuildDailyAllRankings(LocalDate date) {
        productMetricCacheRepository.rebuildDailyAllRankings(date);
    }
}
