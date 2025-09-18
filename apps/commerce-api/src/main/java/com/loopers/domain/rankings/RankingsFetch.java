package com.loopers.domain.rankings;

import java.util.List;

public interface RankingsFetch {
    Boolean supports(RankPeriod period);
    List<RankingsInfo.Ranking> fetch(RankingsRepository repo, RankingsCommand.PageInfo page);
}
