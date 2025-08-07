package com.loopers.application.like;

import com.loopers.domain.brand.Brand;
import com.loopers.domain.brand.BrandRepository;
import com.loopers.domain.like.LikeService;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductRepository;
import com.loopers.domain.stock.Stock;
import com.loopers.domain.stock.StockRepository;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class LikeFacadeIntegrationTest {

    @Autowired
    private LikeFacade likeFacade;

    @Autowired
    private LikeService likeService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }


    @DisplayName("상품 좋아요 조회 파사드 통합 테스트")
    @Nested
    public class Get {
        /**
         * - [O] 유저가 좋아요 등록한 상품 목록 조회시, 해당 상품 목록을 반환한다.
         */

        @Test
        @DisplayName("유저가 좋아요 등록한 상품 목록 조회시, 해당 상품 목록을 반환한다.")
        public void shouldReturnLikedProductList_whenUserRequestsLikedProducts() throws Exception {
            // given
            String userId = "hoyong";

            Brand brand = brandRepository.save(Brand.create("Nike", "운동 브랜드"));
            Product product = productRepository.save(Product.create(brand.getId(), "Air Max", 120000L));

            stockRepository.save(Stock.create(product.getId(), 50L));

            likeFacade.likeProduct(LikeCriteria.Like.of(userId, product.getId()));

            // when
            List<LikeResult.Product> likedProducts = likeFacade.getLikedProducts(LikeCriteria.User.of(userId));

            // then
            assertThat(likedProducts).hasSize(1);
            LikeResult.Product result = likedProducts.get(0);

            assertThat(result.getName()).isEqualTo("Air Max");
            assertThat(result.getPrice()).isEqualTo(120000L);
            assertThat(result.getStock().getQuantity()).isEqualTo(50L);
            assertThat(result.getLike().getLiked()).isTrue();
            assertThat(result.getLike().getCount()).isEqualTo(1L);
            assertThat(result.getBrand().getName()).isEqualTo("Nike");
        }

    }


    @DisplayName("좋아요 등록/해제 동시성 테스트")
    @Nested
    public class Concurrent {

        @Test
        @DisplayName("여러 유저가 동시에 1개의 상품에 좋아요 등록시, 좋아요 개수가 정상 반영된다.")
        public void shouldCountLikesCorrectly_whenMultipleUsersLikeSameProductConcurrently() throws Exception{
            int userCount = 100;
            List<String> userIds = new ArrayList<>(userCount);
            for (int i = 0; i < userCount; i++) {
                userIds.add("user-" + i);
            }

            ExecutorService pool = Executors.newFixedThreadPool(12);
            CountDownLatch startGate = new CountDownLatch(1);
            CountDownLatch doneGate = new CountDownLatch(userCount);

            // when
            for (String uid : userIds) {
                pool.submit(() -> {
                    try {
                        startGate.await();
                        likeFacade.likeProduct(LikeCriteria.Like.of(uid, 1L));
                    } catch (InterruptedException ignored) {
                        Thread.currentThread().interrupt();
                    } finally {
                        doneGate.countDown();
                    }
                });
            }

            startGate.countDown();
            boolean completed = doneGate.await(10, TimeUnit.SECONDS);
            pool.shutdown();

            // then
            assertThat(completed)
                    .as("모든 작업이 타임아웃 내에 완료되어야 한다")
                    .isTrue();

            Long likeCount = likeService.countLikes(1L);
            assertThat(likeCount)
                    .as("동시에 %s명의 유저가 같은 상품을 좋아요 → 최종 좋아요 수는 정확히 %s여야 한다", userCount, userCount)
                    .isEqualTo(userCount);
        }

        private void likeSetup(Long productId) {
            for (int i = 0; i < 30; i++) {
                likeFacade.likeProduct(LikeCriteria.Like.of("user-" + i, productId));
            }
            Long seeded = likeService.countLikes(productId);
            assertThat(seeded).isEqualTo(30L);
        }

        private List<String> users(int fromInclusive, int toExclusive) {
            List<String> list = new ArrayList<>(toExclusive - fromInclusive);
            for (int i = fromInclusive; i < toExclusive; i++) list.add("user-" + i);
            return list;
        }

    }

}
