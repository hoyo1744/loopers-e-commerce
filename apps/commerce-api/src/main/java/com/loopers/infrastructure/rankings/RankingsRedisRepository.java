package com.loopers.infrastructure.rankings;

import com.loopers.domain.rankings.RankingsInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicLong;

@Repository
@RequiredArgsConstructor
public class RankingsRedisRepository implements RankingsCacheRepository{

    private final RedisTemplate<String, String> redisTemplate;

    private static final DateTimeFormatter keyFormat = DateTimeFormatter.ofPattern("yyyyMMdd");

    private String zkey(String type, LocalDate date) {
        String ymd = keyFormat.format(date);
        return switch (normalizeType(type)) {
            case "like"  -> "rank:like:" + ymd;
            case "sales" -> "rank:sales:" + ymd;
            case "pv"    -> "rank:pv:" + ymd;
            default      -> "rank:all:" + ymd;
        };
    }


    private String normalizeType(String type) {
        if (type == null) return "all";
        return switch (type.toLowerCase(Locale.ROOT)) {
            case "like", "sales", "pv", "all" -> type.toLowerCase(Locale.ROOT);
            default -> "all";
        };
     }

     @Override
    public List<RankingsInfo.Ranking> getProductRankings(String type, LocalDate date, Integer size, Integer page) {
        String key = zkey(type, date);
        int offset = (page - 1) * size;
        int end = offset + size - 1;
        AtomicLong rank = new AtomicLong(offset + 1);
        return redisTemplate.opsForZSet().reverseRangeWithScores(key, offset, end)
                .stream()
                .map(v ->
                        RankingsInfo.Ranking.of(Long.valueOf(v.getValue()), rank.getAndIncrement(), v.getScore()))
                .toList();
    }

    @Override
    public RankingsInfo.Ranking getProductRanking(Long productId) {
        String key = zkey("all", LocalDate.now());
        Double score = redisTemplate.opsForZSet().score(key, String.valueOf(productId));
        if (score == null) {
            return null;
        }
        Long rank = redisTemplate.opsForZSet().reverseRank(key, String.valueOf(productId));
        if (rank == null) {
            return null;
        }
        return RankingsInfo.Ranking.of(productId, rank + 1, score);
    }

}
