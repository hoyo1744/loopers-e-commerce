package com.loopers.interfaces.api.like;

import com.loopers.application.like.LikeCriteria;
import com.loopers.application.like.LikeFacade;
import com.loopers.domain.brand.Brand;
import com.loopers.domain.brand.BrandRepository;
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

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LikeApiE2ETest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private LikeFacade likeFacade;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @BeforeEach
    void setUp() {

        for (int i = 1; i <= 10; i++) {
            Long quantity = 100L;
            String brandName = "Test Brand " + i;
            String productName = "Test Product " + i;

            Brand brand = brandRepository.save(Brand.create(brandName, "Test brand description"));
            Product save = productRepository.save(Product.create(brand.getId(), productName, 1000L));
            stockRepository.save(Stock.create(save.getId(), quantity));
        }
    }


    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }


    @DisplayName("상품 좋아요 등록 E2E 테스트")
    @Nested
    public class Like {
        /**
         * - [O]  상품 좋아요 등록시, 이미 좋아요가 등록되어 있다면, 200 OK 와 Product already liked. 메시지가 전달된다.
         * - [O]  상품 좋아요 등록시, 좋아요가 등록되지 않았다면, 200 OK 와 Product liked successfully.메시지가 전달된다.
         */

        @Test
        @DisplayName("상품 좋아요 등록시, 이미 좋아요가 등록되어 있다면, 200 OK 와 Product already liked. 메시지가 전달된다.")
        public void return_200OkWithAlreadyLikeMessage_whenLikeProductLike() throws Exception{
            //given
            String userId = "test";
            Long productId = 1L;
            String requestUrl = "/api/v1/like/products/" + String.valueOf(productId);
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-USER-ID", userId);

            likeFacade.likeProduct(LikeCriteria.Like.of(userId, productId));

            //when
            ParameterizedTypeReference<ApiResponse<String>> responseType = new ParameterizedTypeReference<>() {
            };
            ResponseEntity<ApiResponse<String>> response = restTemplate.exchange(requestUrl, HttpMethod.POST, new HttpEntity<>(headers), responseType);

            //then
            Assertions.assertAll(
                    () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                    () -> assertThat(response.getBody().data()).isEqualTo("Product already liked.")
            );
        }

        @Test
        @DisplayName("상품 좋아요 등록시, 좋아요가 등록되지 않았다면, 200 OK 와 Product liked successfully.메시지가 전달된다.")
        public void return_200OkWithSuccessMessage_whenLikeProduct() throws Exception{
            //given
            String userId = "test";
            Long productId = 1L;
            String requestUrl = "/api/v1/like/products/" + String.valueOf(productId);
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-USER-ID", userId);

            //when
            ParameterizedTypeReference<ApiResponse<String>> responseType = new ParameterizedTypeReference<>() {
            };
            ResponseEntity<ApiResponse<String>> response = restTemplate.exchange(requestUrl, HttpMethod.POST, new HttpEntity<>(headers), responseType);

            //then
            Assertions.assertAll(
                    () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                    () -> assertThat(response.getBody().data()).isEqualTo("Product liked successfully.")
            );
        }

    }

    @DisplayName("상품 좋아요 취소 E2E 테스트")
    @Nested
    public class Unlike {
        /**
         * - [O] 상품 좋아요 취소시, 이미 좋아요가 등록되어 있다면, 200 OK 와 Product already unliked. 메시지가 전달된다.
         * - [O] 상품 좋아요 취소시, 좋아요가 등록되지 않았다면, 200 OK 와 Product unliked successfully.메시지가 전달된다.
         */

        @Test
        @DisplayName("상품 좋아요 취소시, 이미 좋아요가 등록되어 있다면, 200 OK 와 Product unliked successfully. 메시지가 전달된다.")
        public void return_200OOkWithNotLikedMessage_whenUnLikeProduct() throws Exception{
            //given
            String userId = "test";
            Long productId = 1L;
            String requestUrl = "/api/v1/like/products/" + String.valueOf(productId);
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-USER-ID", userId);

            likeFacade.likeProduct(LikeCriteria.Like.of(userId, productId));

            //when
            ParameterizedTypeReference<ApiResponse<String>> responseType = new ParameterizedTypeReference<>() {
            };
            ResponseEntity<ApiResponse<String>> response = restTemplate.exchange(requestUrl, HttpMethod.DELETE, new HttpEntity<>(headers), responseType);

            //then
            Assertions.assertAll(
                    () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                    () -> assertThat(response.getBody().data()).isEqualTo("Product unliked successfully.")
            );
        }

        @Test
        @DisplayName("상품 좋아요 취소시, 좋아요가 등록되지 않았다면, 200 OK 와 Product already unliked.메시지가 전달된다.")
        public void return_200OKWithSuccessMessage_whenUnlikeProduct() throws Exception{
            //given
            String userId = "test";
            Long productId = 1L;
            String requestUrl = "/api/v1/like/products/" + String.valueOf(productId);
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-USER-ID", userId);

            //when
            ParameterizedTypeReference<ApiResponse<String>> responseType = new ParameterizedTypeReference<>() {
            };
            ResponseEntity<ApiResponse<String>> response = restTemplate.exchange(requestUrl, HttpMethod.DELETE, new HttpEntity<>(headers), responseType);

            //then
            Assertions.assertAll(
                    () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                    () -> assertThat(response.getBody().data()).isEqualTo("Product not liked yet.")
            );
        }

    }

    @DisplayName("상품 좋아요 조회 E2E 테스트")
    @Nested
    public class Get {
        /**
         * - [O] 유저가 좋아요 등록한 상품 조회시, 상품 목록 리스트를 반환한다.
         */


        @Test
        @DisplayName("유저가 좋아요 등록한 상품 조회시, 상품 목록 리스트를 반환한다.")
        public void returnProductList_whenUserLikeProductListRequest() throws Exception{
            //given
            String userId = "test";
            String requestUrl = "/api/v1/like/products";
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-USER-ID", userId);

            likeFacade.likeProduct(LikeCriteria.Like.of(userId, 1L));
            likeFacade.likeProduct(LikeCriteria.Like.of(userId, 2L));
            likeFacade.likeProduct(LikeCriteria.Like.of(userId, 3L));


            //when
            ParameterizedTypeReference<ApiResponse<LikeResponse.Products>> responseType = new ParameterizedTypeReference<>() {
            };
            ResponseEntity<ApiResponse<LikeResponse.Products>> response = restTemplate.exchange(requestUrl, HttpMethod.GET, new HttpEntity<>(headers), responseType);

            //then
            Assertions.assertAll(
                    () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                    () -> assertThat(response.getBody().data().getProducts()).hasSize(3),
                    () -> assertThat(response.getBody().data().getProducts().get(0).getName()).isEqualTo("Test Product 1"),
                    () -> assertThat(response.getBody().data().getProducts().get(1).getName()).isEqualTo("Test Product 2"),
                    () -> assertThat(response.getBody().data().getProducts().get(2).getName()).isEqualTo("Test Product 3")
            );
        }



    }

}
