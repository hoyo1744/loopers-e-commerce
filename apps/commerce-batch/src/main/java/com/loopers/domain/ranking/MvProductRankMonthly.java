package com.loopers.domain.ranking;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Table(
        name = "mv_product_rank_monthly",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_monthly",
                columnNames = {"month_id", "product_id"}
        )
)
@Entity
@Getter
@IdClass(MvProductRankMonthlyId.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MvProductRankMonthly {

    @Id
    @Column(name = "month_id", nullable = false)
    private Integer monthId;

    @Id
    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "like_sum")
    private Long likeSum;

    @Column(name = "sale_sum")
    private Long saleSum;

    @Column(name = "pv_sum")
    private Long pvSum;

    private Double score;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    private MvProductRankMonthly(
            Integer monthId,
            Long productId,
            Long likeSum,
            Long saleSum,
            Long pvSum,
            Double score,
            LocalDateTime updatedAt
    ) {
        this.monthId = monthId;
        this.productId = productId;
        this.likeSum = likeSum;
        this.saleSum = saleSum;
        this.pvSum = pvSum;
        this.score = score;
        this.updatedAt = updatedAt;
    }

    public static MvProductRankMonthly create(
            Integer monthId,
            Long productId,
            Long likeSum,
            Long saleSum,
            Long pvSum,
            Double score
    ) {
        return MvProductRankMonthly.builder()
                .monthId(monthId)
                .productId(productId)
                .likeSum(likeSum)
                .saleSum(saleSum)
                .pvSum(pvSum)
                .score(score)
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
