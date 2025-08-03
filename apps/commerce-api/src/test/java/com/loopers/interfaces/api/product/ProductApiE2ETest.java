package com.loopers.interfaces.api.product;

import com.loopers.application.product.ProductFacade;
import com.loopers.application.product.ProductResult;
import com.loopers.domain.brand.Brand;
import com.loopers.domain.brand.BrandRepository;
import com.loopers.domain.like.Like;
import com.loopers.domain.like.LikeRepository;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductRepository;
import com.loopers.domain.stock.Stock;
import com.loopers.domain.stock.StockRepository;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductApiE2ETest {

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
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }


    @DisplayName("상품 조회 E2E 테스트")
    @Nested
    public class Get {

        /**
         * - [O] 미로그인 상태에서 상품 상세 정보 조회시, 사용자 좋아요 여부가 false인 상품 정보(이름, 가격, 브랜드명, 좋아요 여부)를 반환한다.
         * - [O] 로그인 상태에서 상품 상세 정보 조회시, 사용자 좋아요 여부에 따른 상품 정보(이름, 가격, 브랜드명, 좋아요 여부)를 반환한다.
         * - [0] 상품의 등록일이 가장 최근인 순서로 상품 목록 조회시, 상품 목록 리스트를 가장 최근에 등록한 상품 순서로 반환한다.
         * - [0] 상품의 좋아요가 많은 순서로 상품 목록 조회시, 상풍 목록 리스트를 좋아요가 많은 순서로 반환한다.
         * - [0] 상품의 가격이 낮은 순서로 상품 목록 조회시, 상품 목록 리스트를 가격이 낮은 순서로 반환한다.
         */

        @Test
        @DisplayName("미로그인 상태에서 상품 상세 정보 조회시, 사용자 좋아요 여부가 포함되지 않은 상품 정보(이름, 가격, 브랜드명, 좋아요 여부)를 반환한다.")
        public void returnProductInfo_whenNotLogin() throws Exception {
            //given
            Long productId = 1L;
            Long quantity = 100L;
            Long price = 1000L;
            String userId = "test";
            String brandName = "Test Brand";
            String productName = "Test Product";

            Brand brand = brandRepository.save(Brand.create(brandName, "Test brand description"));
            Product save = productRepository.save(Product.create(brand.getId(), productName, price));
            stockRepository.save(Stock.create(save.getId(), quantity));
            String requestUrl = "/api/v1/products/" + String.valueOf(productId);

            //when
            ParameterizedTypeReference<ApiResponse<ProductResponse.ProductDetail>> responseType = new ParameterizedTypeReference<>() {
            };
            ResponseEntity<ApiResponse<ProductResponse.ProductDetail>> response = restTemplate.exchange(requestUrl, HttpMethod.GET, HttpEntity.EMPTY, responseType);

            //then
            Assertions.assertAll(
                    () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                    () -> assertThat(response.getBody()).isNotNull(),
                    () -> assertThat(response.getBody().data().getBrand().getName()).isEqualTo("Test Brand"),
                    () -> assertThat(response.getBody().data().getName()).isEqualTo("Test Product"),
                    () -> assertThat(response.getBody().data().getPrice()).isEqualTo(1000L),
                    () -> assertThat(response.getBody().data().getStock().getQuantity()).isEqualTo(100L),
                    () -> assertThat(response.getBody().data().getLike().getLiked()).isFalse()
            );
        }

        @Test
        @DisplayName("로그인 상태에서 상품 상세 정보 조회시, 사용자 좋아요 여부에 따른 상품 정보(이름, 가격, 브랜드명, 좋아요 여부)를 반환한다.")
        public void returnProductInfo_whenLogin() throws Exception{
            //given
            String userId = "test";
            Long productId = 1L;
            Long quantity = 100L;
            Long price = 1000L;
            String brandName = "Test Brand";
            String productName = "Test Product";

            Brand brand = brandRepository.save(Brand.create(brandName, "Test brand description"));
            Product product = productRepository.save(Product.create(brand.getId(), productName, price));
            stockRepository.save(Stock.create(product.getId(), quantity));
            likeRepository.save(Like.create(userId, product.getId()));

            String requestUrl = "/api/v1/products/" + String.valueOf(productId);
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-USER-ID", userId);

            //when
            ParameterizedTypeReference<ApiResponse<ProductResponse.ProductDetail>> responseType = new ParameterizedTypeReference<>() {
            };
            ResponseEntity<ApiResponse<ProductResponse.ProductDetail>> response = restTemplate.exchange(requestUrl, HttpMethod.GET, new HttpEntity<>(headers), responseType);

            //then
            Assertions.assertAll(
                    () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                    () -> assertThat(response.getBody()).isNotNull(),
                    () -> assertThat(response.getBody().data().getBrand().getName()).isEqualTo("Test Brand"),
                    () -> assertThat(response.getBody().data().getName()).isEqualTo("Test Product"),
                    () -> assertThat(response.getBody().data().getPrice()).isEqualTo(1000L),
                    () -> assertThat(response.getBody().data().getStock().getQuantity()).isEqualTo(100L),
                    () -> assertThat(response.getBody().data().getLike().getLiked()).isTrue()
            );
        }

        @Test
        @DisplayName("상품의 등록일이 가장 최근인 순서로 상품 목록 조회시, 상품 목록 리스트를 가장 최근에 등록한 상품 순서로 반환한다.")
        public void shouldReturnProductListSortedByCreatedDateDesc_whenQueriedByLatest() throws Exception{
            //given

            Brand brand = brandRepository.save(Brand.create("Test Brand", "Desc"));

            for (int i = 0; i < 20; i++) {
                Product product = productRepository.save(Product.create(brand.getId(), "Product " + i, 1000L + i * 100));
                stockRepository.save(Stock.create(product.getId(), 10L + i));
                Thread.sleep(10);
            }

            String requestUrl = "/api/v1/products?" + "brandId=" + brand.getId() + "&sort=latest" + "&page=0" + "&size=20";

            //when
            ParameterizedTypeReference<ApiResponse<ProductResponse.Products>> responseType = new ParameterizedTypeReference<>() {};
            ResponseEntity<ApiResponse<ProductResponse.Products>> response = restTemplate.exchange(
                    requestUrl,
                    HttpMethod.GET,
                    HttpEntity.EMPTY,
                    responseType
            );

            //then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            ProductResponse.Products products = response.getBody().data();
            assertThat(products.getProducts()).hasSize(20);

            List<ZonedDateTime> createdTimes = products.getProducts().stream()
                    .map(ProductResponse.Product::getCreatedAt)
                    .toList();

            List<ZonedDateTime> sorted = new ArrayList<>(createdTimes);
            sorted.sort(Comparator.reverseOrder());

            assertThat(createdTimes).isEqualTo(sorted);
        }

        @Test
        @DisplayName("상품의 좋아요가 많은 순서로 상품 목록 조회시, 상품 목록 리스트를 좋아요가 많은 순서로 반환한다.")
        public void shouldReturnProductListSortedByLikeCountDesc_whenQueriedByLikesDesc() throws Exception {
            // given
            Brand brand = brandRepository.save(Brand.create("Test Brand", "Desc"));
            String userId = "user";

            for (int i = 0; i < 20; i++) {
                Product product = productRepository.save(Product.create(brand.getId(), "Product " + i, 1000L + i * 100));
                stockRepository.save(Stock.create(product.getId(), 10L + i));

                // i번 만큼 좋아요 등록
                for (int j = 0; j < i; j++) {
                    likeRepository.save(Like.create(userId + j, product.getId()));
                }
            }

            String requestUrl = "/api/v1/products?" + "brandId=" + brand.getId() + "&sort=likes_desc" + "&page=0" + "&size=20";

            // when
            ParameterizedTypeReference<ApiResponse<ProductResponse.Products>> responseType = new ParameterizedTypeReference<>() {};
            ResponseEntity<ApiResponse<ProductResponse.Products>> response = restTemplate.exchange(
                    requestUrl,
                    HttpMethod.GET,
                    HttpEntity.EMPTY,
                    responseType
            );

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            ProductResponse.Products products = response.getBody().data();
            assertThat(products.getProducts()).hasSize(20);

            List<Long> likeCounts = products.getProducts().stream()
                    .map(product -> product.getLike().getCount())
                    .toList();

            List<Long> sorted = new ArrayList<>(likeCounts);
            sorted.sort(Comparator.reverseOrder());

            assertThat(likeCounts).isEqualTo(sorted);
        }

        @Test
        @DisplayName("상품의 가격이 낮은 순서로 상품 목록 조회시, 상품 목록 리스트를 가격이 낮은 순서로 반환한다.")
        public void shouldReturnProductListSortedByPriceAsc_whenQueriedByPriceAsc() throws Exception {
            // given
            Brand brand = brandRepository.save(Brand.create("Test Brand", "Desc"));

            for (int i = 0; i < 20; i++) {
                Product product = productRepository.save(Product.create(brand.getId(), "Product " + i, 1000L + i * 100));
                stockRepository.save(Stock.create(product.getId(), 10L + i));
            }

            String requestUrl = "/api/v1/products?" + "brandId=" + brand.getId() + "&sort=price_asc" + "&page=0" + "&size=20";

            // when
            ParameterizedTypeReference<ApiResponse<ProductResponse.Products>> responseType = new ParameterizedTypeReference<>() {};
            ResponseEntity<ApiResponse<ProductResponse.Products>> response = restTemplate.exchange(
                    requestUrl,
                    HttpMethod.GET,
                    HttpEntity.EMPTY,
                    responseType
            );

            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            ProductResponse.Products products = response.getBody().data();
            assertThat(products.getProducts()).hasSize(20);

            List<Long> prices = products.getProducts().stream()
                    .map(ProductResponse.Product::getPrice)
                    .toList();

            List<Long> sorted = new ArrayList<>(prices);
            sorted.sort(Comparator.naturalOrder());

            assertThat(prices).isEqualTo(sorted);
        }
    }

}
