package com.loopers.domain.metric;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ProductMetricService {

    private final ProductMetricRepository productMetricRepository;

    @Transactional
    public void upsertLike(Long productId, LocalDate metricDate, Long count, LocalDateTime eventTime) {
        productMetricRepository.upsertLike(productId, metricDate, count, eventTime);
    }

    @Transactional
    public void upsertSales(Long productId, LocalDate metricDate, Long count, LocalDateTime eventTime) {
        productMetricRepository.upsertSales(productId, metricDate, count, eventTime);
    }

    @Transactional
    public void upsertPageView(Long productId, LocalDate metricDate, Long count, LocalDateTime eventTime) {
        productMetricRepository.upsertPageView(productId, metricDate, count, eventTime);
    }

    /**
     * 캐리오버 실행
     * @param today 기준 날짜 (보통 LocalDate.now(zone))
     * @param rate 전일 점수 이월 비율 (0.1 = 10%)
     * @param ttlHours 캐리오버된 ZSET의 TTL (시간 단위)
     */
    @Transactional
    public void scoreCarry(LocalDate today, double rate, long ttlHours) {
        productMetricRepository.carryOver(today, rate, Duration.ofHours(ttlHours));
    }

    @Transactional
    public void rebuildAllRankings(LocalDate date) {
        productMetricRepository.rebuildDailyAllRankings(date);
    }
}
