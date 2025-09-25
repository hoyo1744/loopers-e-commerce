package com.loopers.domain.rankings;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MonthlyRankingFetch implements RankingsFetch{

    @Override
    public Boolean supports(RankPeriod period) {
        return RankPeriod.MONTHLY == period;
    }

    @Override
    public List<RankingsInfo.Ranking> fetch(RankingsRepository repo, RankingsCommand.PageInfo page) {
        return repo.getProductsByMonthlyRank(page);
    }
}
