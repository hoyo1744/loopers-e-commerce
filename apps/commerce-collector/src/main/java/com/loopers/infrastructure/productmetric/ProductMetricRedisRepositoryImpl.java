package com.loopers.infrastructure.productmetric;

import com.loopers.config.RankingProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.zset.Aggregate;
import org.springframework.data.redis.connection.zset.Weights;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProductMetricRedisRepositoryImpl implements ProductMetricCacheRepository {

    private final RedisTemplate<String, String> redisTemplate;

    private final RankingProperties rankingProperties;

    private static final DateTimeFormatter KEY_DATE = DateTimeFormatter.ofPattern("yyyyMMdd");


    @Override
    public void upsertLike(Long productId, LocalDate metricDate, Long delta) {
        final String ymd = dateKey(metricDate);
        final String member = productId.toString();

        zincrBy(likeKey(ymd), member, delta);
        // TODO : 스케쥴러로 안하고 바로 all에 추가하면 어떨까?
        //zincrBy(allKey(ymd), member, delta * rankingProperties.getWeight().getLike());
    }

    @Override
    public void upsertSales(Long productId, LocalDate metricDate, Long delta) {
        final String ymd = dateKey(metricDate);
        final String member = productId.toString();

        zincrBy(salesKey(ymd), member, delta);
        //zincrBy(allKey(ymd), member, delta * rankingProperties.getWeight().getSales());
    }

    @Override
    public void upsertPageViews(Long productId, LocalDate metricDate, Long delta) {
        final String ymd = dateKey(metricDate);
        final String member = productId.toString();

        zincrBy(pvKey(ymd), member, delta);
        //zincrBy(allKey(ymd), member, delta * rankingProperties.getWeight().getPv());
    }

    @Override
    public void carryOver(LocalDate today, Double rate, Duration hour) {
        String todayKey = allKey(KEY_DATE.format(today));
        String yestKey  = allKey(KEY_DATE.format(today.minusDays(1)));

        redisTemplate.opsForZSet()
                .unionAndStore(
                        todayKey,
                        java.util.Collections.singleton(yestKey),
                        todayKey,
                        Aggregate.SUM,
                        Weights.of(1.0, rate)
                );

        ensureTtl(todayKey);

    }

    @Override
    public void rebuildDailyAllRankings(LocalDate date) {
        final String ymd = dateKey(date);

        redisTemplate.opsForZSet().unionAndStore(
                likeKey(ymd),
                List.of(salesKey(ymd), pvKey(ymd)),
                allKey(ymd),
                Aggregate.SUM,
                Weights.of(
                        rankingProperties.getWeight().getLike(),
                        rankingProperties.getWeight().getSales(),
                        rankingProperties.getWeight().getPv()
                )
        );

        ensureTtl(allKey(ymd));
    }

    private String dateKey(LocalDate date) {
        return KEY_DATE.format(date);
    }

    private void zincrBy(String key, String productId, Long delta) {
        ZSetOperations<String, String> z = redisTemplate.opsForZSet();
        Double newScore = z.incrementScore(key, productId, delta);
        if (newScore < 0) {
            z.add(key, productId, 0);
        }
        ensureTtl(key);
    }

    private void ensureTtl(String key) {
        Long ttlSeconds = redisTemplate.getExpire(key);
        if (ttlSeconds == null || ttlSeconds < 0) {
            Duration ttl = Duration.ofHours(rankingProperties.getTtlHours());
            redisTemplate.expire(key, ttl);
        }
    }

    private String likeKey(String ymd) {
        return "rank:like:" + ymd;
    }

    private String salesKey(String ymd) {
        return "rank:sales:" + ymd;
    }

    private String pvKey(String ymd) {
        return "rank:pv:" + ymd;
    }

    private String allKey(String ymd) {
        return "rank:all:" + ymd;
    }
}
