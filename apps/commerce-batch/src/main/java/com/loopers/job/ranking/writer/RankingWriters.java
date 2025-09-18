package com.loopers.job.ranking.writer;

import com.loopers.domain.ranking.MvProductRankMonthly;
import com.loopers.domain.ranking.MvProductRankWeekly;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RankingWriters {

    @Bean
    public JpaItemWriter<MvProductRankWeekly> weeklyWriter(EntityManagerFactory emf) {
        JpaItemWriter<MvProductRankWeekly> itemWriter = new JpaItemWriter<>();
        itemWriter.setEntityManagerFactory(emf);
        return itemWriter;
    }

    @Bean
    public JpaItemWriter<MvProductRankMonthly> monthlyWriter(EntityManagerFactory emf) {
        JpaItemWriter<MvProductRankMonthly> itemWriter = new JpaItemWriter<>();
        itemWriter.setEntityManagerFactory(emf);
        return itemWriter;
    }

}
