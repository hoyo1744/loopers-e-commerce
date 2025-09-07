package com.loopers.domain.metric;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public void insertPageView(Long productId, LocalDate metricDate, Long count, LocalDateTime eventTime) {
        productMetricRepository.upsertPageView(productId, metricDate, count, eventTime);
    }
}
