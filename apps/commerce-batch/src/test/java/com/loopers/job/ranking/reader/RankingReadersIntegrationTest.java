package com.loopers.job.ranking.reader;

import com.loopers.domain.ranking.RankRow;
import com.loopers.utils.DatabaseCleanUp;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.batch.test.StepScopeTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

@SpringBootTest
class RankingReadersIntegrationTest {

    @Autowired
    private EntityManagerFactory emf;

    @Autowired
    private EntityManager em;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @Autowired
    private ApplicationContext ctx;

    @BeforeEach
    void setup() {
        em = emf.createEntityManager();
        em.getTransaction().begin();

        insertPM(1L, "2025-09-01", 10, 2, 100);
        insertPM(1L, "2025-09-02",  5, 1,  50);

        insertPM(2L, "2025-09-01", 2, 5, 200);

        em.getTransaction().commit();
        em.clear();
    }

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }


    private void insertPM(Long pid, String d, int like, int sales, int pv) {
        em.createNativeQuery(
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
    }

    @DisplayName("랭킹 조회 테스트")
    @Nested
    public class Reader {

        @DisplayName("주간 랭킹 집계시, RankRow를 반환한다.")
        @Test
        void returnRankRow_when_weekly_reader() throws Exception {

            // given
            JobParameters params = new JobParametersBuilder()
                    .addString("startDate", "2025-09-01")
                    .addString("endDate",   "2025-09-02")
                    .addDouble("wLike",  0.2)
                    .addDouble("wSales", 0.7)
                    .addDouble("wPv",    0.1)
                    .toJobParameters();

            StepExecution stepExecution = MetaDataInstanceFactory.createStepExecution(params);

            StepScopeTestUtils.doInStepScope(stepExecution, () -> {
                // when
                JpaPagingItemReader<RankRow> reader =
                        (JpaPagingItemReader<RankRow>) ctx.getBean("weeklyReader");

                reader.afterPropertiesSet();
                reader.open(new ExecutionContext());

                List<RankRow> rows = readAll(reader);
                reader.close();

                // then
                assertThat(rows).hasSize(2);

                Map<Long, Double> collect = rows.stream().collect(Collectors.toMap(
                        RankRow::productId, RankRow::score
                ));


                assertThat(collect).containsKeys(1L, 2L);
                assertThat(collect.get(1L)).isCloseTo(20.1, within(0.0001));
                assertThat(collect.get(2L)).isCloseTo(23.9, within(0.0001));

                return null;
            });
        }

        @DisplayName("월간 랭킹 집계시, RankRow를 반환한다.")
        @Test
        void returnRankRow_when_monthly_reader() throws Exception {
            // given
            JobParameters params = new JobParametersBuilder()
                    .addString("startDate", "2025-09-01")
                    .addString("endDate",   "2025-09-02")
                    .addDouble("wLike",  0.2)
                    .addDouble("wSales", 0.7)
                    .addDouble("wPv",    0.1)
                    .toJobParameters();

            StepExecution stepExecution = MetaDataInstanceFactory.createStepExecution(params);

            StepScopeTestUtils.doInStepScope(stepExecution, () -> {
                JpaPagingItemReader<RankRow> reader =
                        (JpaPagingItemReader<RankRow>) ctx.getBean("monthlyReader");

                reader.afterPropertiesSet();
                reader.open(new ExecutionContext());
                try {
                    // when
                    List<RankRow> rows = readAll(reader);

                    // then
                    assertThat(rows).hasSize(2);


                    Map<Long, Double> scoreByPid = rows.stream()
                            .collect(Collectors.toMap(RankRow::productId, RankRow::score));

                    assertThat(scoreByPid).containsKeys(1L, 2L);
                    assertThat(scoreByPid.get(1L)).isCloseTo(20.1, within(0.0001));
                    assertThat(scoreByPid.get(2L)).isCloseTo(23.9, within(0.0001));

                    return null;
                } finally {
                    reader.close();
                }
            });
        }

    }


    private List<RankRow> readAll(JpaPagingItemReader<RankRow> reader) throws Exception {
        List<RankRow> list = new ArrayList<>();
        RankRow r;
        while ((r = reader.read()) != null) list.add(r);
        return list;
    }
}
