package com.loopers.domain.metric;

import com.loopers.config.RankingProperties;
import com.loopers.utils.RedisCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ProductMetricServiceIntegrationTest {

    @Autowired
    private ProductMetricService productMetricService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private RankingProperties rankingProperties;

    @Autowired
    private RedisCleanUp redisCleanUp;

    private String likeKey(LocalDate d) {
        return "rank:like:" + DateTimeFormatter.ofPattern("yyyyMMdd").format(d);
    }

    private String salesKey(LocalDate d) {
        return "rank:sales:" + DateTimeFormatter.ofPattern("yyyyMMdd").format(d);
    }

    private String pvKey(LocalDate d) {
        return "rank:pv:" + DateTimeFormatter.ofPattern("yyyyMMdd").format(d);
    }

    private String allKey(LocalDate d) {
        return "rank:all:" + DateTimeFormatter.ofPattern("yyyyMMdd").format(d);
    }

    private void assertTtlRoughlyHours(String key, long expectedHours) {
        Long ttlSec = redisTemplate.getExpire(key);
        assertThat(ttlSec).isNotNull();
        assertThat(ttlSec).isGreaterThan(0);
    }

    @AfterEach
    void tearDown() {
        redisCleanUp.truncateAll();
    }

    @Nested
    @DisplayName("redis insert 테스트")
    public class RedisInsert {

        @Test
        @DisplayName("upsertLike 호출 시 당일 like ZSET 점수가 증가하고 TTL이 설정된다")
        void increasesLikeScoreAndSetsTtl_whenUpsertLike() {
            // given
            LocalDate today = LocalDate.now();
            Long productId = 101L;
            LocalDateTime eventTime = LocalDateTime.now();

            // when
            productMetricService.upsertLike(productId, today, 2L, eventTime);

            // then
            Double score = redisTemplate.opsForZSet()
                    .score(likeKey(today), productId.toString());
            assertThat(score).isNotNull();
            assertThat(score).isEqualTo(2.0);

            assertTtlRoughlyHours(likeKey(today), rankingProperties.getTtlHours());
        }

        @Test
        @DisplayName("upsertSales 호출 시 당일 sales ZSET 점수가 증가하고 TTL이 설정된다")
        void increasesSalesScoreAndSetsTtl_whenUpsertSales() {
            // given
            LocalDate today = LocalDate.now();
            Long productId = 202L;

            // when
            productMetricService.upsertSales(productId, today, 5L, LocalDateTime.now());

            // then
            Double score = redisTemplate.opsForZSet()
                    .score(salesKey(today), productId.toString());
            assertThat(score).isNotNull();
            assertThat(score).isEqualTo(5.0);

            assertTtlRoughlyHours(salesKey(today), rankingProperties.getTtlHours());
        }

        @Test
        @DisplayName("upsertPageView 호출 시 당일 pv ZSET 점수가 증가하고 TTL이 설정된다")
        void increasesPvScoreAndSetsTtl_whenUpsertPageView() {
            // given
            LocalDate today = LocalDate.now();
            Long productId = 303L;

            // when
            productMetricService.upsertPageView(productId, today, 7L, LocalDateTime.now());

            // then
            Double score = redisTemplate.opsForZSet()
                    .score(pvKey(today), productId.toString());
            assertThat(score).isNotNull();
            assertThat(score).isEqualTo(7.0);

            assertTtlRoughlyHours(pvKey(today), rankingProperties.getTtlHours());
        }


    }

    @Nested
    @DisplayName("carryOver 테스트")
    public class CarryOver {

        @Test
        @DisplayName("오늘 all이 비어있는 상태에서 전일 all * rate 가 오늘 all로 머지된다")
        void mergesYesterdayAllIntoEmptyToday_whenScoreCarryOverwithRate() {
            // given
            LocalDate today = LocalDate.now();
            LocalDate yesterday = today.minusDays(1);

            String tKey = allKey(today);
            String yKey = allKey(yesterday);

            redisTemplate.delete(tKey);

            redisTemplate.opsForZSet().add(yKey, "1", 20.0);
            redisTemplate.opsForZSet().add(yKey, "2", 30.0);

            double rate = 0.1;
            long ttlHours = rankingProperties.getTtlHours();

            // when
            productMetricService.scoreCarry(today, rate, ttlHours);

            // then
            Double p1 = redisTemplate.opsForZSet().score(tKey, "1");
            Double p2 = redisTemplate.opsForZSet().score(tKey, "2");
            assertThat(p1).isNotNull().isEqualTo(2.0);
            assertThat(p2).isNotNull().isEqualTo(3.0);

            Long zcard = redisTemplate.opsForZSet().size(tKey);
            assertThat(zcard).isEqualTo(2L);

            assertTtlRoughlyHours(tKey, ttlHours);
        }
    }

    @Nested
    @DisplayName("rebuildDailyAllRankings")
    class RebuildAll {

        @Test
        @DisplayName("like/sales/pv 가중치로 all ZSET를 재구성하고 TTL이 설정된다")
        void rebuildsAllWithWeights_whenRebuildDailyAllRankings() {
            // given
            LocalDate date = LocalDate.now();

            redisTemplate.opsForZSet().add(likeKey(date), "7", 1.0);
            redisTemplate.opsForZSet().add(salesKey(date), "7", 10.0);
            redisTemplate.opsForZSet().add(pvKey(date), "7", 100.0);

            double wLike  = rankingProperties.getWeight().getLike();
            double wSales = rankingProperties.getWeight().getSales();
            double wPv    = rankingProperties.getWeight().getPv();
            double expected = 1.0 * wLike + 10.0 * wSales + 100.0 * wPv;

            // when
            productMetricService.rebuildAllRankings(date);

            // then
            Double score = redisTemplate.opsForZSet()
                    .score(allKey(date), "7");
            assertThat(score).isNotNull();
            assertThat(score).isEqualTo(expected);

            assertTtlRoughlyHours(allKey(date), rankingProperties.getTtlHours());
        }
    }

}
