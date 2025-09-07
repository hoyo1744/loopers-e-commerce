package com.loopers.infrastructure.productmetric;

import com.loopers.domain.metric.ProductMetric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface ProductMetricJpaRepository extends JpaRepository<ProductMetric, Long> {

    /**
     * 좋아요 증감 누적 (+delta / -delta)
     */
    @Modifying
    @Query(value = """
        INSERT INTO product_metric (product_id, metric_date, like_count, updated_at)
        VALUES (:productId, :metricDate, :delta, :eventTime)
        ON DUPLICATE KEY UPDATE
            like_count = like_count + VALUES(like_count),
            updated_at = GREATEST(updated_at, VALUES(updated_at))
        """, nativeQuery = true)
    void upsertLike(@Param("productId") Long productId,
                    @Param("metricDate") LocalDate metricDate,
                    @Param("delta") Long delta,
                    @Param("eventTime") LocalDateTime eventTime);

    /**
     * 판매량 증감 누적 (+delta / -delta 가능)
     */
    @Modifying
    @Query(value = """
        INSERT INTO product_metric (product_id, metric_date, sales_count, updated_at)
        VALUES (:productId, :metricDate, :delta, :eventTime)
        ON DUPLICATE KEY UPDATE
            sales_count = sales_count + VALUES(sales_count),
            updated_at = GREATEST(updated_at, VALUES(updated_at))
        """, nativeQuery = true)
    void upsertSales(@Param("productId") Long productId,
                     @Param("metricDate") LocalDate metricDate,
                     @Param("delta") Long delta,
                     @Param("eventTime") LocalDateTime eventTime);

    /**
     * 페이지뷰 증감 누적 (+delta)
     */
    @Modifying
    @Query(value = """
        INSERT INTO product_metric (product_id, metric_date, page_view_count, updated_at)
        VALUES (:productId, :metricDate, :delta, :eventTime)
        ON DUPLICATE KEY UPDATE
            page_view_count = page_view_count + VALUES(page_view_count),
            updated_at = GREATEST(updated_at, VALUES(updated_at))
        """, nativeQuery = true)
    void upsertPageView(@Param("productId") Long productId,
                        @Param("metricDate") LocalDate metricDate,
                        @Param("delta") Long delta,
                        @Param("eventTime") LocalDateTime eventTime);
}
