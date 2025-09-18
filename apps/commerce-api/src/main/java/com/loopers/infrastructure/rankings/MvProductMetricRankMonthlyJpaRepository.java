package com.loopers.infrastructure.rankings;

import com.loopers.domain.rankings.MvProductRankMonthly;
import com.loopers.domain.rankings.MvProductRankMonthlyId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MvProductMetricRankMonthlyJpaRepository extends JpaRepository<MvProductRankMonthly, MvProductRankMonthlyId> {
    Page<MvProductRankMonthly> findByMonthIdOrderByScoreDescProductIdAsc(Integer weekId, Pageable pageable);
}
