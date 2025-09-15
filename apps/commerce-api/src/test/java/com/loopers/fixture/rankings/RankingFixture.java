package com.loopers.fixture.rankings;

import com.loopers.domain.brand.Brand;
import com.loopers.domain.brand.BrandRepository;
import com.loopers.domain.like.Like;
import com.loopers.domain.like.LikeRepository;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductRepository;
import com.loopers.domain.stock.Stock;
import com.loopers.domain.stock.StockRepository;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;


public class RankingFixture {
    private final BrandRepository brandRepository;
    private final ProductRepository productRepository;
    private final StockRepository stockRepository;
    private final LikeRepository likeRepository;
    private final StringRedisTemplate redis;
    private final Supplier<String> todayAllKeySupplier;

    public RankingFixture(BrandRepository brandRepository,
            ProductRepository productRepository,
            StockRepository stockRepository,
            LikeRepository likeRepository,
            StringRedisTemplate redis,
            Supplier<String> todayAllKeySupplier) {
        this.brandRepository = brandRepository;
        this.productRepository = productRepository;
        this.stockRepository = stockRepository;
        this.likeRepository = likeRepository;
        this.redis = redis;
        this.todayAllKeySupplier = todayAllKeySupplier;
    }

    public Brand brand(String name) {
        return brandRepository.save(Brand.create(name, "DESC"));
    }

    public Product product(Brand brand, String name, long price) {
        return productRepository.save(Product.create(brand.getId(), name, price));
    }

    public Stock stock(Product product, long qty) {
        return stockRepository.save(Stock.create(product.getId(), qty));
    }

    public void like(String userId, Product product) {
        likeRepository.save(Like.create(userId, product.getId()));
    }

    public void zadd(Product product, double score) {
        redis.opsForZSet().add(todayAllKeySupplier.get(), String.valueOf(product.getId()), score);
    }

    public void zadd(Map<Product, Double> scores) {
        scores.forEach(this::zadd);
    }

    record ProdSet(Product p, Stock s, double score) {}
    List<RankingFixture.ProdSet> seedRankings(Brand brand, List<RankingFixture.ProdSet> sets) {
        for (RankingFixture.ProdSet set : sets) {
            stock(set.p(), set.s().getQuantity());
            zadd(set.p(), set.score());
        }
        return sets;
    }
}
