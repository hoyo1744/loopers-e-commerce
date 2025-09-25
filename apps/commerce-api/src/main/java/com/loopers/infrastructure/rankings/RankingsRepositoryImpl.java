package com.loopers.infrastructure.rankings;

import com.loopers.domain.rankings.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@Repository
@RequiredArgsConstructor
public class RankingsRepositoryImpl implements RankingsRepository {

    private final RankingsCacheRepository rankingsCacheRepository;

    private final RankingsQueryDslRepository rankingsQueryDslRepository;

    private final MvProductMetricRankMonthlyJpaRepository mvProductMetricRankMonthlyJpaRepository;

    private final MvProductMetricRankWeeklyJpaRepository mvProductMetricRankWeeklyJpaRepository;

    @Override
    public List<RankingsInfo.Ranking> getProductsByDailyRank(RankingsCommand.PageInfo page) {
        return rankingsCacheRepository.getProductRankings(
                page.getType().name().toLowerCase(),
                page.getDate(),
                page.getSize().intValue(),
                page.getPage().intValue()
        );
    }

    @Override
    public List<RankingsInfo.Ranking> getProductsByWeeklyRank(RankingsCommand.PageInfo page) {

        LocalDate base = page.getDate();

        WeekFields wf = WeekFields.ISO;
        int week = base.get(wf.weekOfWeekBasedYear());
        int year = base.get(wf.weekBasedYear());
        int weekId = year * 100 + week;

        int pageIndex = Math.toIntExact(Math.max(page.getPage() - 1, 0));
        int size = Math.toIntExact(Math.min(Math.max(page.getSize(), 1), 100));
        Pageable pageable = PageRequest.of(pageIndex, size);

        Page<MvProductRankWeekly> result =
                mvProductMetricRankWeeklyJpaRepository
                        .findByWeekIdOrderByScoreDescProductIdAsc(weekId, pageable);

        int startRank = pageIndex * size + 1;

        return IntStream.range(0, result.getContent().size())
                .mapToObj(i -> {
                    MvProductRankWeekly e = result.getContent().get(i);
                    return RankingsInfo.Ranking.of(
                            e.getProductId(),
                            (long)(startRank + i),
                            e.getScore()
                    );
                })
                .toList();
    }

    @Override
    public List<RankingsInfo.Ranking> getProductsByMonthlyRank(RankingsCommand.PageInfo page) {

        LocalDate base = page.getDate();

        int year  = base.getYear();
        int month = base.getMonthValue();
        int monthId = year * 100 + month;

        int pageIndex = Math.toIntExact(Math.max(page.getPage() - 1, 0));
        int size      = Math.toIntExact(Math.min(Math.max(page.getSize(), 1), 100));
        Pageable pageable = PageRequest.of(pageIndex, size);

        Page<MvProductRankMonthly> result =
                mvProductMetricRankMonthlyJpaRepository
                        .findByMonthIdOrderByScoreDescProductIdAsc(monthId, pageable);

        long startRank = (long) pageIndex * size + 1L;

        return IntStream.range(0, result.getContent().size())
                .mapToObj(i -> {
                    MvProductRankMonthly e = result.getContent().get(i);
                    return RankingsInfo.Ranking.of(
                            e.getProductId(),
                            startRank + i,
                            e.getScore()
                    );
                })
                .toList();
    }

    public List<RankingsInfo.Product> getProductsByIds(List<Long> productIds) {
        return rankingsQueryDslRepository.search(productIds);
    }

    @Override
    public Optional<RankingsInfo.Ranking> getProductRanking(Long productId) {
        return Optional.ofNullable(rankingsCacheRepository.getProductRanking(productId));
    }
}
