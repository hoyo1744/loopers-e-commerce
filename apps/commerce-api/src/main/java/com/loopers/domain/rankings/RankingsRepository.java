package com.loopers.domain.rankings;

import java.util.List;
import java.util.Optional;

public interface RankingsRepository {
    List<RankingsInfo.Ranking> getProductsByDailyRank(RankingsCommand.PageInfo page);

    List<RankingsInfo.Ranking> getProductsByWeeklyRank(RankingsCommand.PageInfo page);

    List<RankingsInfo.Ranking> getProductsByMonthlyRank(RankingsCommand.PageInfo page);

    List<RankingsInfo.Product> getProductsByIds(List<Long> productIds);

    Optional<RankingsInfo.Ranking> getProductRanking(Long productId);
}
