package com.loopers.job.ranking.processor;

import com.loopers.domain.ranking.MvProductRankWeekly;
import com.loopers.domain.ranking.RankRow;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@StepScope
public class WeeklyProcessor implements ItemProcessor<RankRow, MvProductRankWeekly> {

    @Value("#{jobParameters['weekId']}")
    private Integer weekId;

    @Override
    public MvProductRankWeekly process(RankRow item) {
        return MvProductRankWeekly.create(
                weekId,
                item.productId(),
                item.likeSum(),
                item.saleSum(),
                item.pvSum(),
                item.score()
        );
    }
}
