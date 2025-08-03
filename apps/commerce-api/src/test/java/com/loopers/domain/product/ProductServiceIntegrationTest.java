package com.loopers.domain.product;

import com.loopers.domain.brand.Brand;
import com.loopers.domain.brand.BrandRepository;
import com.loopers.domain.like.Like;
import com.loopers.domain.like.LikeRepository;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ProductServiceIntegrationTest {

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private LikeRepository likeRepository;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }


    @DisplayName("상품 조회 통합 테스트")
    @Nested
    public class Get {

        private static class ExpectedProduct {
            String name;
            Long price;
            Long quantity;

            public ExpectedProduct(String name, Long price, Long quantity) {
                this.name = name;
                this.price = price;
                this.quantity = quantity;
            }
        }

        /**
         * - [O] productId로 상품 조회시, 상품 정보를 반환한다.
         * - [0] productId로 주문 상품 정보 조회시, 상품 정보(이름, 가격, 구매 수량)을 반환한다.
         * - [0] userId, brandId, sort(price_asc), size(20, 50, 100)가 주어졌을 때 상품 목록을 size만큼 반환한다.
         * - [0] userId, brandId, sort(latest), size(20, 50, 100)가 주어졌을 때 상품 목록을 size만큼 반환한다.
         * - [0] userId, brandId, sort(likes_desc), size가 주어졌을 때 상품 목록을 size만큼 반환한다.
         */

        @Test
        @DisplayName("productId로 상품 조회시, 상품 정보를 반환한다.")
        public void returnProductInfo_whenProductIdRequest() throws Exception{
            //given
            Long brandId = 1L;
            Long price = 1000L;
            Long productId = 1L;
            String productName = "Test Product";
            productRepository.save(Product.create(1L, productName, price));

            //when
            ProductInfo.ProductDetail productDetail = productService.getProductDetail(productId);

            //then
            assertThat(productDetail.getBrandId()).isEqualTo(brandId);
            assertThat(productDetail.getName()).isEqualTo(productName);
            assertThat(productDetail.getPrice()).isEqualTo(price);
        }

        @Test
        @DisplayName("productId로 주문 상품 정보 조회시, 상품 정보(이름, 가격, 구매 수량)을 반환한다.")
        void returnProductOrderInfo_whenQueriedByProductId() {
            // given
            String userId = "test";
            int totalProducts = 100;
            Brand brand = brandRepository.save(Brand.create("Test Brand", "Test Description"));

            List<ProductCommand.OrderProduct> orderCommands = new ArrayList<>();
            Map<Long, ExpectedProduct> expectedMap = new HashMap<>();

            for (int i = 1; i <= totalProducts; i++) {
                String productName = "Product " + i;
                Long price = 1000L + i * 100;
                Long quantity = (long) i;

                Product saved = productRepository.save(Product.create(brand.getId(), productName, price));
                orderCommands.add(ProductCommand.OrderProduct.of(saved.getId(), quantity));
                expectedMap.put(saved.getId(), new ExpectedProduct(productName, price, quantity));
            }

            // when
            ProductInfo.OrderProducts result = productService.getOrderProducts(
                    ProductCommand.OrderProducts.of(orderCommands));

            // then
            assertThat(result.getOrderProducts()).hasSize(totalProducts);

            for (ProductInfo.OrderProduct actual : result.getOrderProducts()) {
                ExpectedProduct expected = expectedMap.get(actual.getProductId());

                assertThat(actual.getProductName()).isEqualTo(expected.name);
                assertThat(actual.getPrice()).isEqualTo(expected.price);
                assertThat(actual.getQuantity()).isEqualTo(expected.quantity);
            }
        }


        @ParameterizedTest
        @ValueSource(longs = {20L, 50L, 100L})
        @DisplayName("userId, brandId, sort(price_asc), size(20, 50, 100)가 주어졌을 때 상품 목록을 size 만큼 반환한다.")
        void shouldReturnProductListOfGivenSize_whenUserIdBrandIdSortAndSizeProvided(Long size) {
            // given
            int sizeInt = size.intValue();
            String userId = "test";
            Brand brand = brandRepository.save(Brand.create("Test Brand", "Test Description"));

            for (int i = 0; i < 100; i++) {
                productRepository.save(Product.create(
                        brand.getId(),
                        "Product " + i,
                        1000L + i * 100
                ));
            }

            // when
            List<ProductInfo.ProductQuery> products = productService.getProducts(ProductCommand.Search.of(
                    userId,
                    brand.getId(),
                    "price_asc",
                    0L,
                    size
            ));

            // then
            assertThat(products).hasSize(sizeInt);

            // 가격이 오름차순인지 확인
            List<Long> prices = products.stream()
                    .map(ProductInfo.ProductQuery::getPrice)
                    .toList();

            for (int i = 0; i < sizeInt; i++) {
                assertThat(prices.get(i)).isEqualTo(1000L + i * 100);
            }
        }

        @ParameterizedTest
        @ValueSource(longs = {20L, 50L, 100L})
        @DisplayName("userId, brandId, sort(latest), size(20, 50, 100)가 주어졌을 때 상품 목록을 size만큼 반환한다.")
        public void returnProductListWithCorrectSize_whenUserIdBrandIdAndLatestSortAreGiven(Long size) {
            // given
            int sizeInt = size.intValue();
            String userId = "test";
            Brand brand = brandRepository.save(Brand.create("Test Brand", "Test Description"));

            for (int i = 0; i < sizeInt; i++) {
                String productName = "Product " + i;
                Long price = 1000L + i * 100;
                productRepository.save(Product.create(brand.getId(), productName, price));
            }

            // when
            List<ProductInfo.ProductQuery> products = productService.getProducts(ProductCommand.Search.of(
                    userId,
                    brand.getId(),
                    "latest",
                    0L,
                    size
            ));

            // then
            assertThat(products).hasSize(sizeInt);

            List<Long> prices = products.stream()
                    .map(ProductInfo.ProductQuery::getPrice)
                    .toList();

            for (int i = 0; i < sizeInt; i++) {
                assertThat(prices.get(i)).isEqualTo(1000L + (sizeInt - 1 - i) * 100);
            }
        }

        @ParameterizedTest
        @ValueSource(longs = {20L, 50L, 100L})
        @DisplayName("userId, brandId, sort(likes_desc), size가 주어졌을 때 상품 목록을 size만큼 반환한다.")
        public void returnProductListWithCorrectSize_whenUserIdBrandIdAndSortIsLikesDesc(Long size) throws Exception {
            // given
            String userId = "test";
            Brand brand = brandRepository.save(Brand.create("Test Brand", "Test Description"));

            List<Product> products = new ArrayList<>();

            for (int i = 0; i < size; i++) {
                String productName = "Product " + i;
                Long price = 1000L + i * 100;
                Product product = productRepository.save(Product.create(brand.getId(), productName, price));
                products.add(product);
            }

            // 좋아요 분포 설정
            likeMultiple(userId, products.get(0), 5);
            likeMultiple(userId, products.get(1), 3);
            likeMultiple(userId, products.get(2), 1);

            // when
            List<ProductInfo.ProductQuery> result = productService.getProducts(ProductCommand.Search.of(
                    userId,
                    brand.getId(),
                    "likes_desc",
                    0L,
                    size
            ));

            // then
            assertThat(result).hasSize(size.intValue());
            assertThat(result.get(0).getProductId()).isEqualTo(products.get(0).getId());
            assertThat(result.get(1).getProductId()).isEqualTo(products.get(1).getId());
            assertThat(result.get(2).getProductId()).isEqualTo(products.get(2).getId());
        }

        private void likeMultiple(String userPrefix, Product product, int count) {
            for (int i = 0; i < count; i++) {
                likeRepository.save(Like.create(userPrefix + "_user_" + i + "_p" + product.getId(), product.getId()));
            }
        }
    }

}
