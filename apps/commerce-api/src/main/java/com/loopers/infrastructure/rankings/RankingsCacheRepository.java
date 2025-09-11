package com.loopers.infrastructure.rankings;

import com.loopers.domain.rankings.RankingsInfo;

import java.time.LocalDate;
import java.util.List;

public interface RankingsCacheRepository {
    List<RankingsInfo.Ranking> getProductRankings(String type, LocalDate date, Integer size, Integer page);

    RankingsInfo.Ranking getProductRanking(Long productId);
}
