package com.loopers.infrastructure.rankings;

import com.loopers.domain.rankings.RankingsCommand;
import com.loopers.domain.rankings.RankingsInfo;
import com.loopers.domain.rankings.RankingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RankingsRepositoryImpl implements RankingsRepository {

    private final RankingsCacheRepository rankingsCacheRepository;

    private final RankingsQueryDslRepository rankingsQueryDslRepository;

    @Override
    public List<RankingsInfo.Ranking> getProductsByRank(RankingsCommand.PageInfo page) {
        return rankingsCacheRepository.getProductRankings(
                page.getType().name().toLowerCase(),
                page.getDate(),
                page.getSize().intValue(),
                page.getPage().intValue()
        );
    }

    public List<RankingsInfo.Product> getProductsByIds(List<Long> productIds) {
        return rankingsQueryDslRepository.search(productIds);
    }

    @Override
    public Optional<RankingsInfo.Ranking> getProductRanking(Long productId) {
        return Optional.ofNullable(rankingsCacheRepository.getProductRanking(productId));
    }
}
