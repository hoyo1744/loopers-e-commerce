package com.loopers.job.ranking.job;

import com.loopers.domain.ranking.MvProductRankMonthly;
import com.loopers.domain.ranking.MvProductRankMonthlyId;
import com.loopers.domain.ranking.MvProductRankWeekly;
import com.loopers.domain.ranking.MvProductRankWeeklyId;
import com.loopers.utils.DatabaseCleanUp;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(properties = {
        "spring.batch.job.enabled=false",
        "spring.batch.jdbc.initialize-schema=always",
        "spring.batch.jdbc.platform=mysql"
})
@SpringBatchTest
class RankingJobE2ETest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private JobRepositoryTestUtils jobRepositoryTestUtils;

    @Autowired
    @Qualifier("rankingWeeklyAggregationJob")
    private Job rankingWeeklyAggregationJob;

    @Autowired
    @Qualifier("rakingMonthlyAggregationJob")
    private Job rakingMonthlyAggregationJob;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @Autowired
    private EntityManager em;

    @Autowired
    private EntityManagerFactory emf;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    private void insertPM(Long pid, String d, int like, int sales, int pv) {
        EntityManager localEm = emf.createEntityManager();
        localEm.getTransaction().begin();
        localEm.createNativeQuery(
                        "INSERT INTO product_metric " +
                                "(product_id, metric_date, like_count, sales_count, page_view_count) " +
                                "VALUES (:pid, :d, :l, :s, :p)"
                )
                .setParameter("pid", pid)
                .setParameter("d", java.sql.Date.valueOf(LocalDate.parse(d)))
                .setParameter("l", like)
                .setParameter("s", sales)
                .setParameter("p", pv)
                .executeUpdate();

        localEm.getTransaction().commit();
        localEm.close();
    }


    @DisplayName("주간 랭킹")
    @Nested
    public class WeeklyJob {

        @Test
        @DisplayName("주간 랭킹 적재 테스트")
        public void weekly_ranking_aggregation_when_weekly_job_execute() throws Exception{
            //given
            insertPM(1L, "2025-09-01", 10, 2, 100);
            insertPM(1L, "2025-09-02",  5, 1,  50);
            insertPM(2L, "2025-09-01",  2, 5, 200);

            JobParameters params = new JobParametersBuilder()
                    .addString("startDate", "2025-09-01")
                    .addString("endDate", "2025-09-02")
                    .addDouble("wLike", 0.2)
                    .addDouble("wSales", 0.7)
                    .addDouble("wPv", 0.1)
                    .addLong("weekId", 202537L)
                    .toJobParameters();

            jobLauncherTestUtils.setJob(rankingWeeklyAggregationJob);

            //when
            JobExecution jobExecution = jobLauncherTestUtils.launchJob(params);

            //then
            assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
            StepExecution step = jobExecution.getStepExecutions().iterator().next();
            assertThat(step.getReadCount()).isGreaterThan(0);
            assertThat(step.getWriteCount()).isGreaterThan(0);

            MvProductRankWeekly mvProductRankWeekly1 = em.find(MvProductRankWeekly.class, new MvProductRankWeeklyId(202537, 1L));
            MvProductRankWeekly mvProductRankWeekly2 = em.find(MvProductRankWeekly.class, new MvProductRankWeeklyId(202537, 2L));

            assertThat(mvProductRankWeekly1).isNotNull();
            assertThat(mvProductRankWeekly2).isNotNull();
            assertThat(mvProductRankWeekly1.getScore()).isCloseTo(20.1, Assertions.within(1e-4));
            assertThat(mvProductRankWeekly2.getScore()).isCloseTo(23.9, Assertions.within(1e-4));

        }

    }

    @DisplayName("월간 랭킹")
    @Nested
    public class MonthlyJob {

        @Test
        @DisplayName("월간 랭킹 적재 테스트")
        public void monthly_ranking_aggregation_when_monthly_job_execute() throws Exception{
            //given
            insertPM(1L, "2025-09-01", 10, 2, 100);
            insertPM(1L, "2025-09-02",  5, 1,  50);
            insertPM(2L, "2025-09-01",  2, 5, 200);

            JobParameters params = new JobParametersBuilder()
                    .addString("startDate", "2025-09-01")
                    .addString("endDate", "2025-09-02")
                    .addDouble("wLike", 0.2)
                    .addDouble("wSales", 0.7)
                    .addDouble("wPv", 0.1)
                    .addLong("monthId", 202501L)
                    .toJobParameters();

            jobLauncherTestUtils.setJob(rakingMonthlyAggregationJob);

            //when
            JobExecution jobExecution = jobLauncherTestUtils.launchJob(params);

            //then
            assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
            StepExecution step = jobExecution.getStepExecutions().iterator().next();
            assertThat(step.getReadCount()).isGreaterThan(0);
            assertThat(step.getWriteCount()).isGreaterThan(0);

            MvProductRankMonthly mvProductRankMonthly1 = em.find(MvProductRankMonthly.class, new MvProductRankMonthlyId(202501, 1L));
            MvProductRankMonthly mvProductRankMonthly2 = em.find(MvProductRankMonthly.class, new MvProductRankMonthlyId(202501, 2L));

            assertThat(mvProductRankMonthly1).isNotNull();
            assertThat(mvProductRankMonthly2).isNotNull();
            assertThat(mvProductRankMonthly1.getScore()).isCloseTo(20.1, Assertions.within(1e-4));
            assertThat(mvProductRankMonthly2.getScore()).isCloseTo(23.9, Assertions.within(1e-4));

        }

    }





}
