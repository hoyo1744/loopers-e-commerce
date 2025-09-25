package com.loopers.job.ranking.processor;

import com.loopers.domain.ranking.MvProductRankMonthly;
import com.loopers.domain.ranking.RankRow;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@StepScope
public class MonthlyProcessor implements ItemProcessor<RankRow, MvProductRankMonthly> {

    @Value("#{jobParameters['monthId']}")
    private Integer monthId;

    @Override
    public MvProductRankMonthly process(RankRow item) {
        return MvProductRankMonthly.create(
                monthId,
                item.productId(),
                item.likeSum(),
                item.saleSum(),
                item.pvSum(),
                item.score()
        );
    }
}
