package com.loopers.job.ranking.config;


import com.loopers.domain.ranking.MvProductRankMonthly;
import com.loopers.domain.ranking.MvProductRankWeekly;
import com.loopers.domain.ranking.RankRow;
import com.loopers.job.listener.JpaClearListener;
import com.loopers.job.ranking.processor.MonthlyProcessor;
import com.loopers.job.ranking.processor.WeeklyProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class RankingJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager tx;


    @Bean
    public Job rankingWeeklyAggregationJob(Step weeklyStep) {
        return new JobBuilder("rankingWeeklyAggregationJob", jobRepository)
                .start(weeklyStep)
                .build();
    }

    @Bean
    public Job rakingMonthlyAggregationJob(Step monthlyStep) {
        return new JobBuilder("rankingMonthlyAggregationJob", jobRepository)
                .start(monthlyStep)
                .build();
    }


    @Bean
    public Step weeklyStep(JpaPagingItemReader<RankRow> weeklyReader,
                           WeeklyProcessor weeklyProcessor,
                           JpaItemWriter<MvProductRankWeekly> weeklyWriter,
                           JpaClearListener clear){
        return new StepBuilder("weeklyStep", jobRepository)
                .<RankRow, MvProductRankWeekly>chunk(100, tx)
                .reader(weeklyReader)
                .processor(weeklyProcessor)
                .writer(weeklyWriter)
                .listener(clear)
                .build();
    }

    @Bean
    public Step monthlyStep(JpaPagingItemReader<RankRow> monthlyReader,
                           MonthlyProcessor monthlyProcessor,
                           JpaItemWriter<MvProductRankMonthly> monthlyWriter,
                           JpaClearListener clear){
        return new StepBuilder("monthlyStep", jobRepository)
                .<RankRow, MvProductRankMonthly>chunk(100, tx)
                .reader(monthlyReader)
                .processor(monthlyProcessor)
                .writer(monthlyWriter)
                .listener(clear)
                .build();
    }






}
