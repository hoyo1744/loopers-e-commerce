package com.loopers.infrastructure.rankings;

import com.loopers.domain.rankings.MvProductRankWeekly;
import com.loopers.domain.rankings.MvProductRankWeeklyId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MvProductMetricRankWeeklyJpaRepository extends JpaRepository<MvProductRankWeekly, MvProductRankWeeklyId> {

    Page<MvProductRankWeekly> findByWeekIdOrderByScoreDescProductIdAsc(Integer weekId, Pageable pageable);
}
