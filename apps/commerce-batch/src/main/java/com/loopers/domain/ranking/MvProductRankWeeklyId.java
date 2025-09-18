package com.loopers.domain.ranking;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@NoArgsConstructor
@EqualsAndHashCode
public class MvProductRankWeeklyId implements Serializable {
    private Integer weekId;
    private Long productId;

    public MvProductRankWeeklyId(Integer weekId, Long productId) {
        this.weekId = weekId;
        this.productId = productId;
    }
}
