package com.loopers.application.like;

import com.loopers.domain.brand.Brand;
import com.loopers.domain.brand.BrandRepository;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class LikeFacadeIntegrationTest {

    @Autowired
    private LikeFacade likeFacade;

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

}
