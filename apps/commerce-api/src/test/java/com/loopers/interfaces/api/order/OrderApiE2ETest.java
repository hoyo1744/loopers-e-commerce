package com.loopers.interfaces.api.order;

import com.loopers.application.order.OrderCriteria;
import com.loopers.application.order.OrderFacade;
import com.loopers.domain.brand.Brand;
import com.loopers.domain.brand.BrandRepository;
import com.loopers.domain.coupon.Coupon;
import com.loopers.domain.coupon.CouponRepository;
import com.loopers.domain.coupon.CouponStatus;
import com.loopers.domain.coupon.DiscountType;
import com.loopers.domain.point.Point;
import com.loopers.domain.point.PointRepository;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductRepository;
import com.loopers.domain.stock.Stock;
import com.loopers.domain.stock.StockRepository;
import com.loopers.domain.user.Gender;
import com.loopers.domain.user.User;
import com.loopers.domain.user.UserRepository;
import com.loopers.domain.user.UserService;
import com.loopers.domain.usercoupon.UserCoupon;
import com.loopers.domain.usercoupon.UserCouponRepository;
import com.loopers.domain.usercoupon.UserCouponStatus;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
    private CouponRepository couponRepository;

    @Autowired
    private UserCouponRepository userCouponRepository;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @Autowired
    private UserRepository userRepository;

    private final String userId = "hoyongeom";
    private final String password = "1q2w3e4r!@";
    private final String userName = "hoyong.eom";
    private final String email = "hoyong.eom@gmail.com";
    private final String phoneNumber = "010-1234-5678";
    private final String birthDate = "2025-04-20";


    @BeforeEach
    void setUp() {
        userRepository.save(User.create(userId, password, userName, email, phoneNumber, birthDate, Gender.MALE));
        Coupon coupon = couponRepository.save(
                Coupon.create("test", 100L, 10L, DiscountType.PERCENT, CouponStatus.ACTIVE, LocalDateTime.now().plusDays(10)
                )
        );
        UserCoupon userCoupon = userCouponRepository.save(UserCoupon.create(userId, coupon.getId()));

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
            Long productId = 1L;
            Long couponId = 1L;
            String requestUrl = "/api/v1/orders";
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-USER-ID", userId);

            pointRepository.save(Point.create(userId, 1000L));

            OrderRequest.Order orderRequest = new OrderRequest.Order();

            orderRequest.setOrderProducts(
                    List.of(new OrderRequest.OrderProduct(productId, 1L))
            );
            orderRequest.setCouponId(couponId);

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
                    () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND)
            );
        }

        @Test
        @DisplayName("존재하지 않는 쿠폰으로 주문 시도시, 404 Not found 예외가 발생하며 주문이 실패한다.")
        public void throw404NotFoundAndOrderFailed_whenCouponNotExist() throws Exception{
            // given
            Long productId = 1L;
            Long notExistCouponId = 999L;
            String requestUrl = "/api/v1/orders";
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-USER-ID", userId);

            pointRepository.save(Point.create(userId, 1000L));

            OrderRequest.Order orderRequest = new OrderRequest.Order();

            orderRequest.setOrderProducts(
                    List.of(new OrderRequest.OrderProduct(productId, 1L))
            );
            orderRequest.setCouponId(notExistCouponId);

            // when
            ParameterizedTypeReference<ApiResponse<Void>> responseType = new ParameterizedTypeReference<>() {};
            ResponseEntity<ApiResponse<Void>> response = restTemplate.exchange(
                    requestUrl, HttpMethod.POST, new HttpEntity<>(orderRequest, headers), responseType
            );

            // then
            Assertions.assertAll(
                    () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND)
            );
        }

        @Test
        @DisplayName("사용 불가능한 쿠폰으로 주문 시도시 409 Conflict 예외가 발생하며 주문은 실패한다.")
        public void throw409ConflictAndOrderFailed_whenCouponIsInactive() throws Exception{
            // given
            Long productId = 1L;
            String requestUrl = "/api/v1/orders";
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-USER-ID", userId);

            Coupon inactiveCoupon = couponRepository.save(
                    Coupon.create("test", 100L, 10L, DiscountType.PERCENT, CouponStatus.INACTIVE, LocalDateTime.now().plusDays(10)
                    )
            );
            UserCoupon save = userCouponRepository.save(UserCoupon.create(userId, inactiveCoupon.getId()));
            save.use();
            userCouponRepository.save(save);

            pointRepository.save(Point.create(userId, 1000L));

            OrderRequest.Order orderRequest = new OrderRequest.Order();

            orderRequest.setOrderProducts(
                    List.of(new OrderRequest.OrderProduct(productId, 1L))
            );
            orderRequest.setCouponId(inactiveCoupon.getId());

            // when
            ParameterizedTypeReference<ApiResponse<Void>> responseType = new ParameterizedTypeReference<>() {};
            ResponseEntity<ApiResponse<Void>> response = restTemplate.exchange(
                    requestUrl, HttpMethod.POST, new HttpEntity<>(orderRequest, headers), responseType
            );

            // then
            Assertions.assertAll(
                    () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT)
            );
        }

        @Test
        @DisplayName("재고가 존재하지않거나 부족한 경우 400 Bad request 예외가 발생하며 주문에 실패한다.")
        public void throw400BadRequestException_whenStockIsNotExistOrNotEnough() throws Exception{
            // given
            Long productId = 1L;
            Long couponId = 1L;
            Long illegalStockQuantity = 10000L;
            String requestUrl = "/api/v1/orders";
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-USER-ID", userId);

            pointRepository.save(Point.create(userId, 1000L));

            OrderRequest.Order orderRequest = new OrderRequest.Order();

            orderRequest.setOrderProducts(
                    List.of(new OrderRequest.OrderProduct(productId, illegalStockQuantity))
            );
            orderRequest.setCouponId(couponId);

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

        @Test
        @DisplayName("쿠폰 사용 오류 발생시, 주문 롤백처리되어야한다.")
        public void rollback_whenCouponUseFailed() throws Exception{
            // given
            Long productId = 1L;
            Long originalStockAmount = 100L;
            Long originPointAmount = 1000L;
            Long notExistCouponId = 999L;
            String requestUrl = "/api/v1/orders";
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-USER-ID", userId);

            pointRepository.save(Point.create(userId, 1000L));

            OrderRequest.Order orderRequest = new OrderRequest.Order();

            orderRequest.setOrderProducts(
                    List.of(new OrderRequest.OrderProduct(productId, 1L))
            );
            orderRequest.setCouponId(notExistCouponId);

            // when
            ParameterizedTypeReference<ApiResponse<Void>> responseType = new ParameterizedTypeReference<>() {};
            ResponseEntity<ApiResponse<Void>> response = restTemplate.exchange(
                    requestUrl, HttpMethod.POST, new HttpEntity<>(orderRequest, headers), responseType
            );

            Long rollbackPointAmount = pointRepository.findByUserId(userId).get().getAmount();
            Long rollbackStockAmount = stockRepository.findByProductId(productId).getQuantity();

            // then
            Assertions.assertAll(
                    () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND),
                    () -> assertThat(rollbackPointAmount).isEqualTo(originPointAmount),
                    () -> assertThat(rollbackStockAmount).isEqualTo(originalStockAmount)
            );
        }

        @Test
        @DisplayName("재고 처리 오류 발생시 주문이 롤백처리되어야한다.")
        public void rollback_whenStockUpdate() throws Exception{
            // given
            Long productId = 1L;
            Long originalStockAmount = 100L;
            Long originPointAmount = 1000L;
            Long couponId = 1L;
            Long userCouponId = 1L;
            Long illegalStockQuqntity = 9999999L;
            String requestUrl = "/api/v1/orders";
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-USER-ID", userId);

            pointRepository.save(Point.create(userId, 1000L));

            OrderRequest.Order orderRequest = new OrderRequest.Order();

            orderRequest.setOrderProducts(
                    List.of(new OrderRequest.OrderProduct(productId, illegalStockQuqntity))
            );
            orderRequest.setCouponId(couponId);

            // when
            ParameterizedTypeReference<ApiResponse<Void>> responseType = new ParameterizedTypeReference<>() {};
            ResponseEntity<ApiResponse<Void>> response = restTemplate.exchange(
                    requestUrl, HttpMethod.POST, new HttpEntity<>(orderRequest, headers), responseType
            );

            Long rollbackPointAmount = pointRepository.findByUserId(userId).get().getAmount();
            Long rollbackStockAmount = stockRepository.findByProductId(productId).getQuantity();
            UserCouponStatus userCouponStatus = userCouponRepository.findById(userCouponId).getUserCouponStatus();

            // then
            Assertions.assertAll(
                    () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST),
                    () -> assertThat(rollbackPointAmount).isEqualTo(originPointAmount),
                    () -> assertThat(rollbackStockAmount).isEqualTo(originalStockAmount),
                    () -> assertThat(userCouponStatus).isEqualTo(UserCouponStatus.NO_USED)
            );
        }

        @Test
        @DisplayName("포인트 사용 오류시, 주문이 롤백 처리되어야한다.")
        public void rollback_whenPointUpdate() throws Exception{
            // given
            Long productId = 1L;
            Long originalStockAmount = 100L;
            Long originPointAmount = 1L;
            Long couponId = 1L;
            Long userCouponId = 1L;
            String requestUrl = "/api/v1/orders";
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-USER-ID", userId);

            pointRepository.save(Point.create(userId, originPointAmount));

            OrderRequest.Order orderRequest = new OrderRequest.Order();

            orderRequest.setOrderProducts(
                    List.of(new OrderRequest.OrderProduct(productId, originalStockAmount))
            );
            orderRequest.setCouponId(couponId);

            // when
            ParameterizedTypeReference<ApiResponse<Void>> responseType = new ParameterizedTypeReference<>() {};
            ResponseEntity<ApiResponse<Void>> response = restTemplate.exchange(
                    requestUrl, HttpMethod.POST, new HttpEntity<>(orderRequest, headers), responseType
            );

            Long rollbackPointAmount = pointRepository.findByUserId(userId).get().getAmount();
            Long rollbackStockAmount = stockRepository.findByProductId(productId).getQuantity();
            UserCouponStatus userCouponStatus = userCouponRepository.findById(userCouponId).getUserCouponStatus();

            // then
            Assertions.assertAll(
                    () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT),
                    () -> assertThat(rollbackPointAmount).isEqualTo(originPointAmount),
                    () -> assertThat(rollbackStockAmount).isEqualTo(originalStockAmount),
                    () -> assertThat(userCouponStatus).isEqualTo(UserCouponStatus.NO_USED)
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
            Long coupondId = 1L;

            pointRepository.save(Point.create(userId, 1000L));
            String requestUrl = "/api/v1/orders";
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-USER-ID", userId);

            // 주문 생성
            Long productId = 1L;
            orderFacade.order(OrderCriteria.Order.of(userId, List.of(
                    OrderCriteria.OrderProduct.of(productId, 1L)
            ), coupondId));

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
            Long productId = 1L;
            String requestUrl = "/api/v1/orders/1";  // Example orderId

            Coupon coupon = couponRepository.save(
                    Coupon.create("test", 100L, 10L, DiscountType.PERCENT, CouponStatus.ACTIVE, LocalDateTime.now().plusDays(10)
                    )
            );

            pointRepository.save(Point.create(userId, 1000L));
            userCouponRepository.save(UserCoupon.create(userId, coupon.getId()));

            HttpHeaders headers = new HttpHeaders();
            headers.set("X-USER-ID", userId);

            // 주문 생성
            orderFacade.order(OrderCriteria.Order.of(userId, List.of(
                    OrderCriteria.OrderProduct.of(productId, 1L)
            ), coupon.getId()));

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
