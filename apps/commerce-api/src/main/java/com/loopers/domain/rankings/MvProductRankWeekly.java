package com.loopers.domain.rankings;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "mv_product_rank_weekly",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_weekly",
                columnNames = {"week_id", "product_id"}
        )
)
@IdClass(MvProductRankWeeklyId.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MvProductRankWeekly {

    @Id
    @Column(name = "week_id", nullable = false)
    private Integer weekId;

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
    private MvProductRankWeekly(Integer weekId, Long productId,
                                Long likeSum, Long saleSum, Long pvSum,
                                Double score, LocalDateTime updatedAt) {
        this.weekId = weekId;
        this.productId = productId;
        this.likeSum = likeSum;
        this.saleSum = saleSum;
        this.pvSum = pvSum;
        this.score = score;
        this.updatedAt = updatedAt;
    }

    public static MvProductRankWeekly create(Integer weekId, Long productId,
                                             Long likeSum, Long saleSum, Long pvSum,
                                             Double score) {
        return MvProductRankWeekly.builder()
                .weekId(weekId)
                .productId(productId)
                .likeSum(likeSum)
                .saleSum(saleSum)
                .pvSum(pvSum)
                .score(score)
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
