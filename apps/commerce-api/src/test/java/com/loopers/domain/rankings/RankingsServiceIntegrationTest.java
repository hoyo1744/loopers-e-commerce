package com.loopers.domain.rankings;

import com.loopers.domain.brand.Brand;
import com.loopers.domain.brand.BrandRepository;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductRepository;
import com.loopers.utils.DatabaseCleanUp;
import com.loopers.utils.RedisCleanUp;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class RankingsServiceIntegrationTest {

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @Autowired
    private RankingsService rankingsService;

    @Autowired
    private RedisCleanUp redisCleanUp;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private BrandRepository brandRepository;

    @AfterEach
    void tearDown() {
        redisCleanUp.truncateAll();
        databaseCleanUp.truncateAllTables();
    }



    private String buildTodayAllKey() {
        return "rank:all:" + DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDate.now());
    }

    private List<Long> addBrandAndProduct() {
        Brand brand = brandRepository.save(Brand.create("Test Brand", "Test Description"));

        List<Long> productIds = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Product p = productRepository.save(Product.create(
                    brand.getId(),
                    "Product " + i,
                    1000L + i * 100
            ));

            productIds.add(p.getId());

        }

        return productIds;
    }

    @DisplayName("상품 랭킹 조회")
    @Nested
    public class GetProductRanking {

        @Test
        @DisplayName("제품이 랭킹에 존재한다면 랭킹 정보를 반환한다.")
        void returnsRanking_whenProductIsExistRanking() {
            // given
            String key = buildTodayAllKey();
            Long productId = 101L;

            stringRedisTemplate.opsForZSet().add(key, productId.toString(), 12.5);

            // when
            Optional<RankingsInfo.Ranking> result = rankingsService.getRanking(productId);

            // then
            assertThat(result).isPresent();
            RankingsInfo.Ranking ranking = result.get();
            assertThat(ranking.getProductId()).isEqualTo(productId);
            assertThat(ranking.getScore()).isEqualTo(12.5);
            assertThat(ranking.getRank()).isEqualTo(1L);
        }

        @Test
        @DisplayName("제품이 랭킹에 존재하지 않는다면 Optional.empty를 반환한다.")
        public void returnEmpty_whenProductIsNotExistRanking() throws Exception{
            //given
            String key = buildTodayAllKey();
            Long productId = 101L;

            //when
            Optional<RankingsInfo.Ranking> ranking = rankingsService.getRanking(productId);

            //then
            assertThat(ranking).isNotPresent();
        }

        @Test
        @DisplayName("점수가 큰 제품이 높은 랭킹(낮은 숫자)의 랭킹 정보를 반환한다.")
        public void returnHighRanking_whenScoreIsGreater() throws Exception{
            //given
            String key = buildTodayAllKey();
            Long p1 = 1L;
            Long p2 = 2L;
            Long p3 = 3L;
            stringRedisTemplate.opsForZSet().add(key, p1.toString(), 50.0);
            stringRedisTemplate.opsForZSet().add(key, p2.toString(), 70.0);
            stringRedisTemplate.opsForZSet().add(key, p3.toString(), 60.0);

            //when
            Optional<RankingsInfo.Ranking> r1 = rankingsService.getRanking(p1);
            Optional<RankingsInfo.Ranking> r2 = rankingsService.getRanking(p2);
            Optional<RankingsInfo.Ranking> r3 = rankingsService.getRanking(p3);

            //then
            assertThat(r2).isPresent();
            assertThat(r2.get().getRank()).isEqualTo(1L);
            assertThat(r2.get().getScore()).isEqualTo(70.0);

            assertThat(r3).isPresent();
            assertThat(r3.get().getRank()).isEqualTo(2L);

            assertThat(r1).isPresent();
            assertThat(r1.get().getRank()).isEqualTo(3L);
        }
    }

    @DisplayName("상품 랭킹 요약 정보 조회")
    @Nested
    public class GetProductSummaryRanking {

        @Test
        @DisplayName("요청한 페이지 사이즈 만큼 상위 랭킹 상품을 순위대로 반환한다.")
        public void returnsTopRankedProductsInOrder_whenPageSizeIsSpecified() throws Exception{
            //given
            String key = buildTodayAllKey();
            Brand brand = brandRepository.save(Brand.create("Test Brand", "Test Description"));

            Product p1 = productRepository.save(Product.create(brand.getId(), "Product1", 100L));
            Product p2 = productRepository.save(Product.create(brand.getId(), "Product2", 100L));
            Product p3 = productRepository.save(Product.create(brand.getId(), "Product3", 100L));

            stringRedisTemplate.opsForZSet().add(key, String.valueOf(p1.getId()), 30);
            stringRedisTemplate.opsForZSet().add(key, String.valueOf(p2.getId()), 40);
            stringRedisTemplate.opsForZSet().add(key, String.valueOf(p3.getId()), 50);

            RankingsCommand.PageInfo page= RankingsCommand.PageInfo.of(LocalDate.now(), 1L, 2L, RankType.ALL_RANK, RankPeriod.DAILY);

            //when
            List<RankingsInfo.ProductSummary> productByRank = rankingsService.getProductByRank(page);


            //then
            assertThat(productByRank).hasSize(2);
            assertThat(productByRank.get(0).getProduct().getProductId()).isEqualTo(p3.getId());
            assertThat(productByRank.get(0).getRanking().getRank()).isEqualTo(1);
            assertThat(productByRank.get(1).getProduct().getProductId()).isEqualTo(p2.getId());
            assertThat(productByRank.get(1).getRanking().getRank()).isEqualTo(2);

        }

        @Test
        @DisplayName("페이지네이션: size=2, page=2 요청 시 3위와 4위 상품이 순서대로 반환된다.")
        void returnsThirdAndFourthRankedProducts_whenRequestingSecondPageWithPageSizeTwo() {
            // given
            String key = buildTodayAllKey();
            Brand brand = brandRepository.save(Brand.create("B", "D"));

            Product p1 = productRepository.save(Product.create(brand.getId(), "P1", 100L));
            Product p2 = productRepository.save(Product.create(brand.getId(), "P2", 100L));
            Product p3 = productRepository.save(Product.create(brand.getId(), "P3", 100L));
            Product p4 = productRepository.save(Product.create(brand.getId(), "P4", 100L));
            Product p5 = productRepository.save(Product.create(brand.getId(), "P5", 100L));

            stringRedisTemplate.opsForZSet().add(key, String.valueOf(p1.getId()), 10);
            stringRedisTemplate.opsForZSet().add(key, String.valueOf(p2.getId()), 20);
            stringRedisTemplate.opsForZSet().add(key, String.valueOf(p3.getId()), 30);
            stringRedisTemplate.opsForZSet().add(key, String.valueOf(p4.getId()), 40);
            stringRedisTemplate.opsForZSet().add(key, String.valueOf(p5.getId()), 50);

            RankingsCommand.PageInfo page = RankingsCommand.PageInfo.of(LocalDate.now(), 2L, 2L, RankType.ALL_RANK, RankPeriod.DAILY);

            // when
            List<RankingsInfo.ProductSummary> result = rankingsService.getProductByRank(page);

            // then
            assertThat(result).hasSize(2);
            assertThat(result.get(0).getProduct().getProductId()).isEqualTo(p3.getId());
            assertThat(result.get(0).getRanking().getRank()).isEqualTo(3L);
            assertThat(result.get(1).getProduct().getProductId()).isEqualTo(p2.getId());
            assertThat(result.get(1).getRanking().getRank()).isEqualTo(4L);
        }

        @Test
        @DisplayName("Redis에 존재하지만 DB에 존재하지 않는 상품 ID가 포함되면 IllegalArgumentException 예외를 던진다.")
        void throwsException_whenRedisHasUnknownProductId() {
            // given
            String key = buildTodayAllKey();
            Brand brand = brandRepository.save(Brand.create("B", "D"));
            Product p = productRepository.save(Product.create(brand.getId(), "P", 100L));

            stringRedisTemplate.opsForZSet().add(key, String.valueOf(p.getId()), 50);
            stringRedisTemplate.opsForZSet().add(key, String.valueOf(999999L), 60);

            RankingsCommand.PageInfo page = RankingsCommand.PageInfo.of(LocalDate.now(), 1L, 10L, RankType.ALL_RANK, RankPeriod.DAILY);

            // when & then
            Assertions.assertThatThrownBy(() -> rankingsService.getProductByRank(page))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Product not found");
        }
    }

}
