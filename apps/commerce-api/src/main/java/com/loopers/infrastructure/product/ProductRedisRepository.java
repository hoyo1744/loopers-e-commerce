package com.loopers.infrastructure.product;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopers.domain.product.ProductCommand;
import com.loopers.domain.product.ProductInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProductRedisRepository implements ProductCacheRepository{

    private final RedisTemplate<String, String> redisTemplate;

    private final ObjectMapper om;

    private long listTtlSeconds = 60;

    @Override
    public Optional<List<ProductInfo.ProductQuery>> getCachedProductList(ProductCommand.Search search) {
        String key = buildListKey(search);
        String cachedData = redisTemplate.opsForValue().get(key);

        if(cachedData == null) {
            return Optional.empty();
        }

        try {
            List<ProductInfo.ProductQuery> v =
                    om.readValue(cachedData, new TypeReference<List<ProductInfo.ProductQuery>>() {});
            return Optional.of(v);
        } catch (JsonProcessingException e) {
            return Optional.empty();
        }
    }

    @Override
    public void putCachedProductList(ProductCommand.Search search, List<ProductInfo.ProductQuery> value) {
        try {
            String key = buildListKey(search);
            String json = om.writeValueAsString(value);
            redisTemplate.opsForValue().set(key, json, Duration.ofSeconds(listTtlSeconds));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }

    private String buildListKey(ProductCommand.Search search) {
        String brand = nullSafe(search.getBrandId());
        String sort = nullSafe(search.getSort());
        long page = search.getPage() == null ? 0 : search.getPage();
        long size = search.getSize() == null ? 20 : search.getSize();

        return String.format(
                "v1:product:list:b=%s:s=%s:p=%d:z=%d",
                brand, sort, page, size
        );
    }

    private String nullSafe(Object v) {
        return v == null ? "-" : String.valueOf(v);
    }
}
