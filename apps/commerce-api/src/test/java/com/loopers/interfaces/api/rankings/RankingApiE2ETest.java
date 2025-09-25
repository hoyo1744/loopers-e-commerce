package com.loopers.interfaces.api.rankings;

import com.loopers.domain.brand.Brand;
import com.loopers.domain.brand.BrandRepository;
import com.loopers.domain.like.LikeRepository;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductRepository;
import com.loopers.domain.sender.MessageSender;
import com.loopers.domain.stock.StockRepository;
import com.loopers.fixture.rankings.RankingFixture;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RankingApiE2ETest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @MockBean
    private MessageSender messageSender;

    private RankingFixture fixture;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
        stringRedisTemplate.delete(todayAllKey());
    }

    @BeforeEach
    void setUp() {
        fixture = new RankingFixture(
                brandRepository,
                productRepository,
                stockRepository,
                likeRepository,
                stringRedisTemplate,
                this::todayAllKey
        );
    }

    private String todayAllKey() {
        return "rank:all:" + DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDate.now());
    }

    private ResponseEntity<ApiResponse<RankingsResponse.Rankings>> getRankings(
            HttpHeaders headers, String query
    ) {
        ParameterizedTypeReference<ApiResponse<RankingsResponse.Rankings>> type =
                new ParameterizedTypeReference<>() {
                };
        return restTemplate.exchange(
                "/api/v1/rankings" + query,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                type
        );
    }

    @Nested
    @DisplayName("랭킹 조회 E2E")
    class GetRankings {

        @Test
        @DisplayName("로그인 사용자가 요청한 페이지 사이즈만큼 상위 랭킹 상품을 순위대로 반환한다 (size=2, page=1)")
        void returnsTopRankedProductsInOrder_whenPageSizeIsSpecified_andLoggedIn() {
            // given
            String userId = "hoyo";
            Brand brand = fixture.brand("B");

            Product p1 = fixture.product(brand, "P1", 1000L);
            Product p2 = fixture.product(brand, "P2", 2000L);
            Product p3 = fixture.product(brand, "P3", 3000L);

            fixture.stock(p1, 100L);
            fixture.stock(p2, 100L);
            fixture.stock(p3, 100L);

            fixture.like(userId, p2);

            fixture.zadd(p1, 30.0);
            fixture.zadd(p2, 40.0);
            fixture.zadd(p3, 50.0);

            HttpHeaders headers = new HttpHeaders();
            headers.set("X-USER-ID", userId);
            String query = "?date=" + LocalDate.now() + "&size=2&page=1&period=daily";

            ParameterizedTypeReference<ApiResponse<RankingsResponse.Rankings>> responseType =
                    new ParameterizedTypeReference<>() {
                    };
            ResponseEntity<ApiResponse<RankingsResponse.Rankings>> response = restTemplate.exchange(
                    "/api/v1/rankings" + query,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    responseType
            );

            // then
            assertAll(
                    () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                    () -> assertThat(response.getBody()).isNotNull(),
                    () -> assertThat(response.getBody().data().getRankings()).hasSize(2),
                    () -> {
                        RankingsResponse.Product r1 = response.getBody().data().getRankings().get(0);
                        assertThat(r1.getProductName()).isEqualTo("P3");
                        assertThat(r1.getBrandName()).isEqualTo("B");
                        assertThat(r1.getPrice()).isEqualTo(3000L);
                        assertThat(r1.getRank()).isEqualTo(1L);
                        assertThat(r1.getIsLiked()).isFalse();
                    },

                    () -> {
                        RankingsResponse.Product r2 = response.getBody().data().getRankings().get(1);
                        assertThat(r2.getProductName()).isEqualTo("P2");
                        assertThat(r2.getBrandName()).isEqualTo("B");
                        assertThat(r2.getPrice()).isEqualTo(2000L);
                        assertThat(r2.getRank()).isEqualTo(2L);
                        assertThat(r2.getIsLiked()).isTrue();
                    }
            );
        }

        @Test
        @DisplayName("비로그인 사용자는 liked=false로 반환되며, 페이지네이션에 따라 다음 페이지가 올바르게 반환된다 (size=2, page=2)")
        void returnsSecondPageInOrder_whenPageIsTwo_andAnonymous() {
            // given
            Brand brand = fixture.brand("B");

            Product p1 = fixture.product(brand, "P1", 1000L);
            Product p2 = fixture.product(brand, "P2", 2000L);
            Product p3 = fixture.product(brand, "P3", 3000L);

            fixture.stock(p1, 100L);
            fixture.stock(p2, 100L);
            fixture.stock(p3, 100L);

            fixture.like("u1", p2);
            fixture.like("u2", p3);

            fixture.zadd(p1, 40.0);
            fixture.zadd(p2, 50.0);
            fixture.zadd(p3, 60.0);

            HttpHeaders headers = new HttpHeaders();
            String query = "?date=" + LocalDate.now() + "&size=2&page=2&period=daily";

            // when
            ResponseEntity<ApiResponse<RankingsResponse.Rankings>> response = getRankings(headers, query);

            // then
            assertAll(
                    () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                    () -> assertThat(response.getBody()).isNotNull(),
                    () -> assertThat(response.getBody().data().getRankings()).hasSize(1),
                    () -> {
                        RankingsResponse.Product last = response.getBody().data().getRankings().get(0);
                        assertThat(last.getProductName()).isEqualTo("P1");
                        assertThat(last.getRank()).isEqualTo(3L);
                        assertThat(last.getIsLiked()).isFalse();
                    }
            );
        }

        @Test
        @DisplayName("랭킹 데이터가 없으면 빈 목록을 반환한다")
        void returnsEmptyList_whenNoRankingData() {
            // given
            Brand brand = fixture.brand("B");
            Product p1 = fixture.product(brand, "P1", 1000L);
            fixture.stock(p1, 10L);

            HttpHeaders headers = new HttpHeaders();
            String query = "?date=" + LocalDate.now() + "&size=20&page=1&period=daily";

            // when
            ResponseEntity<ApiResponse<RankingsResponse.Rankings>> response = getRankings(headers, query);

            // then
            assertAll(
                    () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                    () -> assertThat(response.getBody()).isNotNull(),
                    () -> assertThat(response.getBody().data().getRankings()).isEmpty()
            );
        }
    }
}

