package com.loopers.domain.ranking;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(
        name = "product_metric",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_product_metric", columnNames = {"product_id", "metric_date"})
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductMetric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_metric_id")
    private Long id;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "metric_date")
    private LocalDate metricDate;

    @Column(name = "like_count")
    private Long likeCount = 0L;

    @Column(name = "sales_count")
    private Long salesCount = 0L;

    @Column(name = "page_view_count")
    private Long pageViewCount = 0L;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


}
