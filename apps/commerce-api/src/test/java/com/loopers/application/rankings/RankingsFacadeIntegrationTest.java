package com.loopers.application.rankings;

import com.loopers.domain.rankings.RankPeriod;
import com.loopers.fixture.rankings.RankingFixture;
import com.loopers.domain.brand.Brand;
import com.loopers.domain.brand.BrandRepository;
import com.loopers.domain.like.LikeRepository;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductRepository;
import com.loopers.domain.rankings.RankType;
import com.loopers.domain.stock.StockRepository;
import com.loopers.utils.DatabaseCleanUp;
import com.loopers.utils.RedisCleanUp;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class RankingsFacadeIntegrationTest {

    @Autowired
    private RankingsFacade rankingsFacade;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @Autowired
    private RedisCleanUp redisCleanUp;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private StockRepository stockRepository;

    private RankingFixture fx;

    @BeforeEach
    void setUp() {
        fx = new RankingFixture(
                brandRepository,
                productRepository,
                stockRepository,
                likeRepository,
                stringRedisTemplate,
                this::todayAllKey
        );
    }

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
        redisCleanUp.truncateAll();
    }

    private String todayAllKey() {
        return "rank:all:" + DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDate.now());
    }


    @DisplayName("랭킹 페이지 조회")
    @Nested
    public class GetRankingPage {

        @Test
        @DisplayName("상위 점수 순으로 페이징된 랭킹과 찜 여부가 매핑되어 반환된다 (size=2, page=1)")
        void returnsPagedRankingsWithLikedFlag_whenValidRequest() {
            // given
            String userId = "hoyo";
            Brand brand = fx.brand("B");

            Product p1 = fx.product(brand, "P1", 1000L);
            Product p2 = fx.product(brand, "P2", 2000L);
            Product p3 = fx.product(brand, "P3", 3000L);

            fx.stock(p1, 100L);
            fx.stock(p2, 100L);
            fx.stock(p3, 100L);

            fx.like(userId, p2);

            fx.zadd(Map.of(
                    p1, 30.0,
                    p2, 40.0,
                    p3, 50.0
            ));

            RankingsCriteria.PageInfo criteria = RankingsCriteria.PageInfo.of(
                    userId, LocalDate.now(), 1L, 2L, RankType.ALL_RANK.getType(), RankPeriod.DAILY.getPeriod()
            );

            // when
            List<RankingsResult.Rankings> result = rankingsFacade.getRankings(criteria);

            // then
            assertThat(result).hasSize(2);

            RankingsResult.Rankings r1 = result.get(0);
            assertThat(r1.getProductName()).isEqualTo("P3");
            assertThat(r1.getBrandName()).isEqualTo("B");
            assertThat(r1.getPrice()).isEqualTo(3000L);
            assertThat(r1.getRank()).isEqualTo(1L);
            assertThat(r1.getScore()).isEqualTo(50.0);
            assertThat(r1.getIsLiked()).isFalse();

            RankingsResult.Rankings r2 = result.get(1);
            assertThat(r2.getProductName()).isEqualTo("P2");
            assertThat(r2.getBrandName()).isEqualTo("B");
            assertThat(r2.getPrice()).isEqualTo(2000L);
            assertThat(r2.getRank()).isEqualTo(2L);
            assertThat(r2.getScore()).isEqualTo(40.0);
            assertThat(r2.getIsLiked()).isTrue();
        }

        @Test
        @DisplayName("페이지네이션: size=2, page=2 요청 시 3위와 4위 상품이 순서대로 반환되고 찜 여부도 반영된다")
        void returnsThirdAndFourthRankedProducts_onSecondPage_withLikedFlag() {
            // given
            String userId = "hoyo-2";
            Brand brand = fx.brand("B");

            Product p1 = fx.product(brand, "P1", 1000L);
            Product p2 = fx.product(brand, "P2", 2000L);
            Product p3 = fx.product(brand, "P3", 3000L);
            Product p4 = fx.product(brand, "P4", 4000L);
            Product p5 = fx.product(brand, "P5", 5000L);

            fx.stock(p1, 100L); fx.stock(p2, 100L); fx.stock(p3, 100L);
            fx.stock(p4, 100L); fx.stock(p5, 100L);

            fx.zadd(Map.of(p1, 50.0, p2, 60.0, p3, 70.0, p4, 80.0, p5, 90.0));

            fx.like(userId, p3);

            RankingsCriteria.PageInfo criteria = RankingsCriteria.PageInfo.of(
                    userId, LocalDate.now(), 2L, 2L, RankType.ALL_RANK.getType(), RankPeriod.DAILY.getPeriod()
            );

            // when
            List<RankingsResult.Rankings> result = rankingsFacade.getRankings(criteria);

            assertThat(result).hasSize(2);

            RankingsResult.Rankings r1 = result.get(0);
            assertThat(r1.getProductName()).isEqualTo("P3");
            assertThat(r1.getRank()).isEqualTo(3L);
            assertThat(r1.getScore()).isEqualTo(70.0);
            assertThat(r1.getIsLiked()).isTrue();

            RankingsResult.Rankings r2 = result.get(1);
            assertThat(r2.getProductName()).isEqualTo("P2");
            assertThat(r2.getRank()).isEqualTo(4L);
            assertThat(r2.getScore()).isEqualTo(60.0);
            assertThat(r2.getIsLiked()).isFalse();
        }

    }

}
