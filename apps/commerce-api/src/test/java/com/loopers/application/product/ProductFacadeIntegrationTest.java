package com.loopers.application.product;

import com.loopers.domain.brand.Brand;
import com.loopers.domain.brand.BrandRepository;
import com.loopers.domain.like.Like;
import com.loopers.domain.like.LikeRepository;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductInfo;
import com.loopers.domain.product.ProductRepository;
import com.loopers.domain.stock.Stock;
import com.loopers.domain.stock.StockRepository;
import com.loopers.utils.DatabaseCleanUp;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProductFacadeIntegrationTest {

    @Autowired
    private ProductFacade productFacade;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private LikeRepository likeRepository;

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
         * - [0] 상품 상세 조회시, 상품 정보(이름, 가격, 브랜드명, 좋아요 여부)를 반환한다.
         * - [O] 로그인 한 유저가 좋아요 등록한 상품 상세 조회시, 좋아요 정보를 포함한 상품 정보를 반환한다.
         * - [0] 낮은 가격 순으로 상품 목록을 조회할 수 있다.
         * - [0] 상품 등록 순으로 상품 목록을 조회할 수 있다.
         * - [0] 상품 좋아요가 많은 순으로 상품 목록을 조회할 수 있다.
         * -
         */
        @Test
        @DisplayName("상품 상세 조회시, 상품 정보(이름, 가격, 브랜드명, 좋아요 여부)를 반환한다.")
        void shouldReturnProductDetailsIncludingNamePriceBrandNameAndLikeStatus_whenProductIsQueried() {
            // given
            String userId = "test-user";
            String productName = "테스트 상품";
            long price = 5000L;

            Brand brand = brandRepository.save(Brand.create("Test Brand", "브랜드 설명"));
            Product product = productRepository.save(Product.create(brand.getId(), productName, price));
            Stock stock = stockRepository.save(Stock.create(product.getId(), 100L));

            likeRepository.save(Like.create(userId, product.getId()));

            // when
            ProductResult.ProductDetail result = productFacade.getProductDetail(
                    ProductCriteria.ProductDetailRequest.of(userId, product.getId())
            );

            // then
            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo(productName);
            assertThat(result.getPrice()).isEqualTo(price);
            assertThat(result.getBrand().getName()).isEqualTo(brand.getName());
            assertThat(result.getLike().getLiked()).isTrue();
        }

        @Test
        @DisplayName("로그인 한 유저가 좋아요 등록한 상품 상세 조회시, 좋아요 정보를 포함한 상품 정보를 반환한다.")
        void shouldReturnProductDetailWithLikeInfo_whenLikedProductIsQueriedByLoggedInUser() {
            // given
            String userId = "test-user";
            String productName = "테스트 상품";
            long price = 15000L;

            Brand brand = brandRepository.save(Brand.create("테스트 브랜드", "설명"));
            Product product = productRepository.save(Product.create(brand.getId(), productName, price));
            Stock stock = stockRepository.save(Stock.create(product.getId(), 100L));

            likeRepository.save(Like.create(userId, product.getId()));

            // when
            ProductResult.ProductDetail result = productFacade.getProductDetail(
                    ProductCriteria.ProductDetailRequest.of(userId, product.getId())
            );

            // then
            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo(productName);
            assertThat(result.getPrice()).isEqualTo(price);
            assertThat(result.getBrand().getName()).isEqualTo(brand.getName());
            assertThat(result.getLike().getLiked()).isTrue();
        }

        private void createTestProductsWithLikes(Brand brand) {
            for (int i = 0; i < 3; i++) {
                Product product = productRepository.save(Product.create(
                        brand.getId(), "Product " + i, 1000L - i * 100));

                // 좋아요 개수: 2, 1, 0 순으로 부여
                for (int j = 0; j < 2 - i; j++) {
                    likeRepository.save(Like.create("user" + j, product.getId()));
                }
            }
        }

        @Test
        @DisplayName("낮은 가격 순으로 상품 목록을 조회할 수 있다.")
        void shouldReturnProductsSortedByAscendingPrice() {
            // given
            Brand brand = brandRepository.save(Brand.create("Brand", "desc"));
            createTestProductsWithLikes(brand);

            // when
            List<ProductResult.Product> result = productFacade.getProducts(
                    ProductCriteria.ProductRequest.of("test", brand.getId(), "price_asc", 0L, 10L)
            );

            // then
            List<Long> actual = result.stream().map(ProductResult.Product::getPrice).toList();
            List<Long> expected = new ArrayList<>(actual);
            expected.sort(Comparator.naturalOrder());

            assertThat(actual).isEqualTo(expected);


        }

        @Test
        @DisplayName("상품 등록 순으로 상품 목록을 조회할 수 있다.")
        void shouldReturnProductsSortedByCreatedDateDesc() {
            // given
            Brand brand = brandRepository.save(Brand.create("Brand", "desc"));
            createTestProductsWithLikes(brand);

            // when
            List<ProductResult.Product> result = productFacade.getProducts(
                    ProductCriteria.ProductRequest.of("test", brand.getId(), "latest", 0L, 10L)
            );

            // then
            List<ZonedDateTime> actual = result.stream().map(ProductResult.Product::getCreatedAt).toList();
            List<ZonedDateTime> expected = new ArrayList<>(actual);
            expected.sort(Comparator.reverseOrder());
            assertThat(actual).isEqualTo(expected);
        }

        @Test
        @DisplayName("상품 좋아요가 많은 순으로 상품 목록을 조회할 수 있다.")
        void shouldReturnProductsSortedByLikeCountDesc() {
            // given
            Brand brand = brandRepository.save(Brand.create("Brand", "desc"));
            createTestProductsWithLikes(brand);

            // when
            List<ProductResult.Product> result = productFacade.getProducts(
                    ProductCriteria.ProductRequest.of("test", brand.getId(), "likes_desc", 0L, 10L)
            );

            // then
            List<Long> likeCounts = result.stream()
                    .map(product -> product.getLike().getCount())
                    .toList();
            List<Long> expectedLikeCounts = new ArrayList<>(likeCounts);
            expectedLikeCounts.sort(Comparator.reverseOrder());
            assertThat(likeCounts).isEqualTo(expectedLikeCounts);
        }
    }

}
