package com.loopers.job.ranking.writer;

import com.loopers.domain.ranking.MvProductRankMonthly;
import com.loopers.domain.ranking.MvProductRankMonthlyId;
import com.loopers.domain.ranking.MvProductRankWeekly;
import com.loopers.domain.ranking.MvProductRankWeeklyId;
import com.loopers.utils.DatabaseCleanUp;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class RankingWritersIntegrationTest {

    @Autowired
    private ApplicationContext ctx;

    @Autowired
    private EntityManagerFactory emf;

    @Autowired
    private EntityManager em;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }
    
    @DisplayName("랭킹 MV 저장 테스트")
    @Nested
    public class Writer {

        @DisplayName("mv_product_rank_weekly 테이블에 저장된다.")
        @Test
        @Transactional
        void insertMvProductRankWeekly_whenWeeklyWriter() throws Exception {
            // given
            JpaItemWriter<MvProductRankWeekly> writer =
                    (JpaItemWriter<MvProductRankWeekly>) ctx.getBean("weeklyWriter");
            writer.afterPropertiesSet();

            // when
            MvProductRankWeekly mvProductRankWeekly = MvProductRankWeekly.create(
                    202537,
                    1L,
                    15L,
                    3L,
                    150L,
                    20.1
            );
            writer.write(Chunk.of(mvProductRankWeekly));
            em.flush();
            em.clear();

            // then
            MvProductRankWeeklyId id = new MvProductRankWeeklyId(202537, 1L);
            MvProductRankWeekly found = em.find(MvProductRankWeekly.class, id);

            assertThat(found).isNotNull();
            assertThat(found.getScore()).isEqualTo(20.1);
        }

        @Test
        @DisplayName("mv_product_rank_monthly 테이블에 저장된다.")
        @Transactional
        public void insertMvProductRankMonthly_whenMonthlyWriter() throws Exception{
            // given
            JpaItemWriter<MvProductRankMonthly> writer =
                    (JpaItemWriter<MvProductRankMonthly>) ctx.getBean("monthlyWriter");
            writer.afterPropertiesSet();

            // when
            MvProductRankMonthly mvProductRankMonthly = MvProductRankMonthly.create(
                    202501,
                    1L,
                    15L,
                    3L,
                    150L,
                    20.1
            );
            writer.write(Chunk.of(mvProductRankMonthly));
            em.flush();
            em.clear();

            // then
            MvProductRankMonthlyId id = new MvProductRankMonthlyId(202501, 1L);
            MvProductRankMonthly found = em.find(MvProductRankMonthly.class, id);

            assertThat(found).isNotNull();
            assertThat(found.getScore()).isEqualTo(20.1);
        }

    }
}
