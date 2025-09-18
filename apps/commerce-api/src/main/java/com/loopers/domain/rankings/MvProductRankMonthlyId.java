package com.loopers.domain.rankings;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@NoArgsConstructor
@EqualsAndHashCode
public class MvProductRankMonthlyId implements Serializable {
    private Integer monthId;
    private Long productId;

    public MvProductRankMonthlyId(Integer monthId, Long productId) {
        this.monthId = monthId;
        this.productId = productId;
    }
}

