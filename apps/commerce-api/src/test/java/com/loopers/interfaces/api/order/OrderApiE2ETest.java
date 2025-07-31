package com.loopers.interfaces.api.order;

import com.loopers.application.order.OrderCriteria;
import com.loopers.application.order.OrderFacade;
import com.loopers.domain.brand.Brand;
import com.loopers.domain.brand.BrandRepository;
import com.loopers.domain.point.Point;
import com.loopers.domain.point.PointRepository;
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

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderApiE2ETest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private OrderFacade orderFacade;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private PointRepository pointRepository;

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

    @DisplayName("주문 등록 E2E 테스트")
    @Nested
    public class Order {
        /**
         * - [O] 주문 시, 정상적으로 주문이 등록된다.
         * - [O] 주문 시, 유효하지 않은 userId일 경우, 401 Unauthorized 예외가 발생한다.
         */

        @Test
        @DisplayName("주문 시, 정상적으로 주문이 등록된다.")
        public void return_200OkAndSuccessMessage_whenCreateOrder() throws Exception {
            // given
            String userId = "test";
            Long productId = 1L;
            String requestUrl = "/api/v1/orders";
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-USER-ID", userId);

            pointRepository.save(Point.create(userId, 1000L));

            OrderRequest.Order orderRequest = new OrderRequest.Order();

            orderRequest.setOrderProducts(
                    List.of(new OrderRequest.OrderProduct(productId, 1L))
            );

            // when
            ParameterizedTypeReference<ApiResponse<Void>> responseType = new ParameterizedTypeReference<>() {};
            ResponseEntity<ApiResponse<Void>> response = restTemplate.exchange(
                    requestUrl, HttpMethod.POST, new HttpEntity<>(orderRequest, headers), responseType
            );

            // then
            Assertions.assertAll(
                    () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                    () -> assertThat(response.getBody()).isNotNull()
            );
        }

        @Test
        @DisplayName("주문 시, 사용자 포인트가 존재하지 않는 경우, 400 Bad Request 예외가 발생한다.")
        public void return_40BadRequest_whenUserPointIsNotExist() throws Exception {
            // given
            String invalidUserId = "invalidTest";
            Long productId = 1L;
            String requestUrl = "/api/v1/orders";
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-USER-ID", invalidUserId);

            OrderRequest.Order orderRequest = new OrderRequest.Order();
            orderRequest.setOrderProducts(
                    List.of(new OrderRequest.OrderProduct(productId, 1L))
            );

            // when
            ParameterizedTypeReference<ApiResponse<Void>> responseType = new ParameterizedTypeReference<>() {};
            ResponseEntity<ApiResponse<Void>> response = restTemplate.exchange(
                    requestUrl, HttpMethod.POST, new HttpEntity<>(orderRequest, headers), responseType
            );

            // then
            Assertions.assertAll(
                    () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST)
            );
        }
    }

    @DisplayName("주문 목록 조회 E2E 테스트")
    @Nested
    public class GetOrders {

        /**
         * - [O] 주문 목록 조회 시, 정상적으로 주문 목록을 반환한다.
         * - [O] 유효하지 않은 userId일 경우, 401 Unauthorized 예외가 발생한다.
         */

        @Test
        @DisplayName("주문 목록 조회 시, 정상적으로 주문 목록을 반환한다.")
        public void return_200OkAndOrderList_whenGetOrders() throws Exception {
            // given
            String userId = "test";
            pointRepository.save(Point.create(userId, 1000L));
            String requestUrl = "/api/v1/orders";
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-USER-ID", userId);

            // 주문 생성
            Long productId = 1L;
            orderFacade.order(OrderCriteria.Order.of(userId, List.of(
                    OrderCriteria.OrderProduct.of(productId, 1L)
            )));

            // when
            ParameterizedTypeReference<ApiResponse<OrderResponse.Orders>> responseType = new ParameterizedTypeReference<>() {};
            ResponseEntity<ApiResponse<OrderResponse.Orders>> response = restTemplate.exchange(
                    requestUrl, HttpMethod.GET, new HttpEntity<>(headers), responseType
            );

            // then
            Assertions.assertAll(
                    () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                    () -> assertThat(response.getBody().data().getOrders()).hasSize(1)
            );
        }

        @Test
        @DisplayName("주문 목록 조회 시, 유효하지 않은 userId일 경우, 401 Unauthorized 예외가 발생한다.")
        public void return_401Unauthorized_whenUserIdIsInvalid() throws Exception {
            // given
            String invalidUserId = "";
            String requestUrl = "/api/v1/orders";
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-USER-ID", invalidUserId);

            // when
            ParameterizedTypeReference<ApiResponse<OrderResponse.Orders>> responseType = new ParameterizedTypeReference<>() {};
            ResponseEntity<ApiResponse<OrderResponse.Orders>> response = restTemplate.exchange(
                    requestUrl, HttpMethod.GET, new HttpEntity<>(headers), responseType
            );

            // then
            Assertions.assertAll(
                    () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED)
            );
        }
    }

    @DisplayName("주문 상세 조회 E2E 테스트")
    @Nested
    public class GetOrderDetail {

        /**
         * - [O] 주문 상세 조회 시, 정상적으로 주문 상세 정보를 반환한다.
         * - [O] 유효하지 않은 userId일 경우, 401 Unauthorized 예외가 발생한다.
         */

        @Test
        @DisplayName("주문 상세 조회 시, 정상적으로 주문 상세 정보를 반환한다.")
        public void return_200OkAndOrderDetail_whenGetOrderDetail() throws Exception {
            // given
            String userId = "test";
            pointRepository.save(Point.create(userId, 1000L));
            Long productId = 1L;
            String requestUrl = "/api/v1/orders/1";  // Example orderId
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-USER-ID", userId);

            // 주문 생성
            orderFacade.order(OrderCriteria.Order.of(userId, List.of(
                    OrderCriteria.OrderProduct.of(productId, 1L)
            )));

            // when
            ParameterizedTypeReference<ApiResponse<OrderResponse.Order>> responseType = new ParameterizedTypeReference<>() {};
            ResponseEntity<ApiResponse<OrderResponse.Order>> response = restTemplate.exchange(
                    requestUrl, HttpMethod.GET, new HttpEntity<>(headers), responseType
            );

            // then
            Assertions.assertAll(
                    () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK),
                    () -> assertThat(response.getBody().data()).isNotNull()
            );
        }

        @Test
        @DisplayName("주문 상세 조회 시, 유효하지 않은 userId일 경우, 401 Unauthorized 예외가 발생한다.")
        public void return_401Unauthorized_whenUserIdIsInvalid() throws Exception {
            // given
            String invalidUserId = "";
            Long orderId = 1L;
            String requestUrl = "/api/v1/orders/" + orderId;
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-USER-ID", invalidUserId);

            // when
            ParameterizedTypeReference<ApiResponse<OrderResponse.Order>> responseType = new ParameterizedTypeReference<>() {};
            ResponseEntity<ApiResponse<OrderResponse.Order>> response = restTemplate.exchange(
                    requestUrl, HttpMethod.GET, new HttpEntity<>(headers), responseType
            );

            // then
            Assertions.assertAll(
                    () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED)
            );
        }
    }
}
