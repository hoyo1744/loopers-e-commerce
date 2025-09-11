package com.loopers.infrastructure.rankings;

import com.loopers.domain.rankings.RankingsInfo;

import java.util.List;

public interface RankingsQueryDslRepository {
    List<RankingsInfo.Product> search(List<Long> productIds);
}
