package com.loopers.domain.rankings;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DailyRankingsFetch implements RankingsFetch {

    @Override
    public Boolean supports(RankPeriod period) {
        return period == RankPeriod.DAILY;
    }

    @Override
    public List<RankingsInfo.Ranking> fetch(RankingsRepository repo, RankingsCommand.PageInfo page) {
        return repo.getProductsByDailyRank(page);
    }
}

