package com.loopers.job.ranking.reader;

import com.loopers.domain.ranking.RankRow;
import com.loopers.domain.ranking.RankingParams;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class RankingReaders {

    private static final String JPQL = """
    SELECT new com.loopers.domain.ranking.RankRow(
      CAST(pm.productId              AS long),
      CAST(SUM(pm.likeCount)         AS long),
      CAST(SUM(pm.salesCount)        AS long),
      CAST(SUM(pm.pageViewCount)     AS long),
      CAST(
        (CAST(SUM(pm.likeCount)     AS double) * :wLike) +
        (CAST(SUM(pm.salesCount)    AS double) * :wSales) +
        (CAST(SUM(pm.pageViewCount) AS double) * :wPv)
      AS double)
    )
    FROM ProductMetric pm
    WHERE pm.metricDate BETWEEN :start AND :end
    GROUP BY pm.productId
    """;

    private JpaPagingItemReader<RankRow> build(EntityManagerFactory emf, String name, RankingParams params) {
        Map<String, Object> p = new HashMap<>();
        p.put("start", params.start());
        p.put("end", params.end());
        p.put("wLike", params.wLike());
        p.put("wSales", params.wSales());
        p.put("wPv", params.wPv());

        return new JpaPagingItemReaderBuilder<RankRow>()
                .name(name)
                .entityManagerFactory(emf)
                .queryString(JPQL)
                .parameterValues(p)
                .pageSize(500)
                .saveState(false)
                .build();
    }

    private static LocalDate parseDate(String s) {
        return LocalDate.parse(s);
    }

    @Bean
    @StepScope
    public JpaPagingItemReader<RankRow> weeklyReader(EntityManagerFactory emf,
                                                     @Value("#{jobParameters['startDate']}") String startDate,
                                                     @Value("#{jobParameters['endDate']}") String endDate,
                                                     @Value("#{jobParameters['wLike']}") Double wLike,
                                                     @Value("#{jobParameters['wSales']}") Double wSales,
                                                     @Value("#{jobParameters['wPv']}") Double wPv) {

        RankingParams params = new RankingParams(
                parseDate(startDate),
                parseDate(endDate),
                wLike,
                wSales,
                wPv
        );
        return this.build(emf, "weeklyReader", params);
    }

    @Bean
    @StepScope
    public JpaPagingItemReader<RankRow> monthlyReader(EntityManagerFactory emf,
                                                      @Value("#{jobParameters['startDate']}") String startDate,
                                                      @Value("#{jobParameters['endDate']}") String endDate,
                                                      @Value("#{jobParameters['wLike']}") Double wLike,
                                                      @Value("#{jobParameters['wSales']}") Double wSales,
                                                      @Value("#{jobParameters['wPv']}") Double wPv) {

        RankingParams params = new RankingParams(
                parseDate(startDate),
                parseDate(endDate),
                wLike,
                wSales,
                wPv
        );
        return this.build(emf, "monthlyReader", params);
    }
}
