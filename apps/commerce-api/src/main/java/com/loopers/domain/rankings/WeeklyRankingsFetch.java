package com.loopers.domain.rankings;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WeeklyRankingsFetch implements RankingsFetch {


    @Override
    public Boolean supports(RankPeriod period) {
        return RankPeriod.WEEKLY == period;
    }

    @Override
    public List<RankingsInfo.Ranking> fetch(RankingsRepository repo, RankingsCommand.PageInfo page) {
        return repo.getProductsByWeeklyRank(page);
    }
}
