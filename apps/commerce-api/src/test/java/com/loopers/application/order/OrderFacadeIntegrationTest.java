package com.loopers.application.order;

import com.loopers.domain.brand.Brand;
import com.loopers.domain.brand.BrandRepository;
import com.loopers.domain.coupon.Coupon;
import com.loopers.domain.coupon.CouponRepository;
import com.loopers.domain.coupon.CouponStatus;
import com.loopers.domain.coupon.DiscountType;
import com.loopers.domain.order.OrderStatus;
import com.loopers.domain.point.Point;
import com.loopers.domain.point.PointRepository;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductRepository;
import com.loopers.domain.stock.Stock;
import com.loopers.domain.stock.StockRepository;
import com.loopers.domain.user.Gender;
import com.loopers.domain.user.User;
import com.loopers.domain.user.UserRepository;
import com.loopers.domain.usercoupon.UserCoupon;
import com.loopers.domain.usercoupon.UserCouponRepository;
import com.loopers.domain.usercoupon.UserCouponStatus;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import com.loopers.utils.DatabaseCleanUp;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class OrderFacadeIntegrationTest {

    @Autowired
    private OrderFacade orderFacade;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private PointRepository pointRepository;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    private String userId = "user123";

    @Autowired
    private UserCouponRepository UserCouponRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserCouponRepository userCouponRepository;


    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }


    @DisplayName("OrderFacade 주문 통합 테스트")
    @Nested
    public class Order {
        @Test
        @DisplayName("null 주문 객체 전달 시 BAD_REQUEST 예외가 발생한다.")
        void throwBadRequest_whenOrderIsNull() {
            // when
            CoreException ex = assertThrows(CoreException.class, () -> {
                orderFacade.order(null);
            });

            // then
            assertThat(ex.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
            assertThat(ex.getMessage()).contains("주문할 상품이 없습니다.");
        }

        @Test
        @DisplayName("주문 상품 리스트가 비어있을 경우 BAD_REQUEST 예외가 발생한다.")
        void throwBadRequest_whenOrderProductListIsEmpty() {
            // given
            OrderCriteria.Order order = OrderCriteria.Order.of(userId, List.of(), 1L);

            // when
            CoreException ex = assertThrows(CoreException.class, () -> {
                orderFacade.order(order);
            });

            // then
            assertThat(ex.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
            assertThat(ex.getMessage()).contains("주문할 상품이 없습니다.");
        }

        @Test
        @DisplayName("로그인되지 않은 사용자의 주문 시 404 Not Found 예외가 발생한다.")
        void throwUnauthorized_whenUserIdIsNull() {
            // given
            OrderCriteria.Order order = OrderCriteria.Order.of(null, List.of(
                    OrderCriteria.OrderProduct.of(1L, 1L)
            ), 1L);

            // when
            CoreException ex = assertThrows(CoreException.class, () -> {
                orderFacade.order(order);
            });

            // then
            assertThat(ex.getMessage()).contains("사용자 ID가 입력되지 않았습니다.");
        }


        @Test
        @DisplayName("재고가 부족한 상품 주문 시 BAD_REQUEST 예외가 발생한다.")
        void throwBadRequest_whenStockIsNotEnough() {
            // given
            User user = userRepository.save(User.create(
                    "userId",
                    "1q2w3e4r!@",
                    "userName",
                    "email@loopers.com",
                    "010-1234-5678",
                    "1994-04-20",
                    Gender.MALE
            ));
            Coupon coupon = couponRepository.save(
                    Coupon.create("test", 100L, 10L, DiscountType.PERCENT, CouponStatus.ACTIVE, LocalDateTime.now().plusDays(10)
                    )
            );
            UserCoupon userCoupon = UserCouponRepository.save(UserCoupon.create(user.getId(), coupon.getId()));
            Brand brand = brandRepository.save(Brand.create("TestBrand", "desc"));
            Product product = productRepository.save(Product.create(brand.getId(), "TestProduct", 1000L));
            stockRepository.save(Stock.create(product.getId(), 1L));
            pointRepository.save(Point.create(user.getId(), 5000L));

            OrderCriteria.Order order = OrderCriteria.Order.of(user.getId(), List.of(
                    OrderCriteria.OrderProduct.of(product.getId(), 5L)
            ), coupon.getId());

            // when
            CoreException ex = assertThrows(CoreException.class, () -> {
                orderFacade.order(order);
            });

            // then
            assertThat(ex.getMessage()).contains("재고가 부족합니다");
        }

        @Test
        @DisplayName("유효한 주문 정보로 주문을 생성하면 주문 상태가 COMPLETED로 설정된다.")
        void createOrder_shouldCompleteOrder_whenValidInput() {
            // given
            User user = userRepository.save(User.create(
                    "userId",
                    "1q2w3e4r!@",
                    "userName",
                    "email@loopers.com",
                    "010-1234-5678",
                    "1994-04-20",
                    Gender.MALE
            ));
            Coupon coupon = couponRepository.save(
                    Coupon.create("test", 100L, 10L, DiscountType.PERCENT, CouponStatus.ACTIVE, LocalDateTime.now().plusDays(10)
                    )
            );
            UserCoupon userCoupon = UserCouponRepository.save(UserCoupon.create(user.getId(), coupon.getId()));
            Brand brand = brandRepository.save(Brand.create("TestBrand", "desc"));
            Product product = productRepository.save(Product.create(brand.getId(), "TestProduct", 1000L));
            stockRepository.save(Stock.create(product.getId(), 10L));
            pointRepository.save(Point.create(user.getId(), 5000L));

            OrderCriteria.Order order = OrderCriteria.Order.of(user.getId(), List.of(
                    OrderCriteria.OrderProduct.of(product.getId(), 1L)
            ), userCoupon.getCouponId());

            // when
            orderFacade.order(order);

            // then
            OrderResult.Orders result
                    = orderFacade.getOrders(user.getId());
            assertThat(result.getOrders()).hasSize(1);
            OrderResult.Order createdOrder = result.getOrders().get(0);
            assertThat(createdOrder.getOrderStatus()).isEqualTo(OrderStatus.COMPLETE.getValue());
            assertThat(createdOrder.getTotalPrice()).isEqualTo(1000L);
        }
    }


    @DisplayName("OrderFacade 주문 조회 통합 테스트")
    @Nested
    public class Get {
        @Test
        @DisplayName("주문 생성 후 주문 상세 조회시 전체 가격, 브랜드, 상품 이름, 상품 가격이 포함된다.")
        void getOrderDetail_shouldReturnCorrectOrderData() {
            // given
            String userId = "userId";
            String password = "1q2w3e4r!@";
            String userName = "userName";
            String email = "email@loopers.com";
            String phoneNumber = "010-1234-5678";
            String birthDate = "1994-04-20";
            userRepository.save(User.create(
                    userId,
                    password,
                    userName,
                    email,
                    phoneNumber,
                    birthDate,
                    Gender.MALE
            ));
            Brand brand = brandRepository.save(Brand.create("Nike", "Shoes"));
            Product product = productRepository.save(Product.create(brand.getId(), "Air Max", 1200L));
            stockRepository.save(Stock.create(product.getId(), 5L));
            pointRepository.save(Point.create(userId, 5000L));
            Coupon coupon = couponRepository.save(
                    Coupon.create("test", 100L, 10L, DiscountType.PERCENT, CouponStatus.ACTIVE, LocalDateTime.now().plusDays(10)
                    )
            );
            UserCouponRepository.save(UserCoupon.create(userId, coupon.getId()));


            OrderCriteria.Order order = OrderCriteria.Order.of(userId, List.of(
                    OrderCriteria.OrderProduct.of(product.getId(), 2L)
            ), coupon.getId());
            orderFacade.order(order);

            OrderResult.Orders orders = orderFacade.getOrders(userId);
            OrderResult.Order createdOrder = orders.getOrders().get(0);

            // when
            OrderResult.Order detail = orderFacade.getOrder(OrderCriteria.OrderDetail.of(userId, createdOrder.getOrderId()));

            // then
            assertThat(detail.getOrderStatus()).isEqualTo(OrderStatus.COMPLETE.getValue());
            assertThat(detail.getTotalPrice()).isEqualTo(2400L);
            assertThat(detail.getProducts().get(0).getPrice()).isEqualTo(1200L);
            assertThat(detail.getProducts().get(0).getBrand()).isEqualTo("Nike");
            assertThat(detail.getProducts().get(0).getName()).isEqualTo("Air Max");
        }

        @Test
        @DisplayName("유효한 사용자 ID로 주문 목록을 조회하면 주문 목록이 반환된다.")
        void shouldReturnOrderList_whenUserIdIsValid() {
            // given
            User user = userRepository.save(User.create(
                    "userId",
                    "1q2w3e4r!@",
                    "userName",
                    "email@loopers.com",
                    "010-1234-5678",
                    "1994-04-20",
                    Gender.MALE
            ));
            Coupon coupon = couponRepository.save(
                    Coupon.create("test", 100L, 10L, DiscountType.PERCENT, CouponStatus.ACTIVE, LocalDateTime.now().plusDays(10)
                    )
            );
            UserCoupon userCoupon = UserCouponRepository.save(UserCoupon.create(user.getId(), coupon.getId()));
            Brand brand = brandRepository.save(Brand.create("Adidas", "Shoes"));
            Product product = productRepository.save(Product.create(brand.getId(), "Ultraboost", 1500L));
            stockRepository.save(Stock.create(product.getId(), 10L));
            pointRepository.save(Point.create(user.getId(), 5000L));

            OrderCriteria.Order order = OrderCriteria.Order.of(user.getId(), List.of(
                    OrderCriteria.OrderProduct.of(product.getId(), 1L)
            ), userCoupon.getCouponId());
            orderFacade.order(order);

            // when
            OrderResult.Orders orders = orderFacade.getOrders(user.getId());

            // then
            assertThat(orders.getOrders()).hasSize(1);
            OrderResult.Order result = orders.getOrders().get(0);
            assertThat(result.getOrderStatus()).isEqualTo(OrderStatus.COMPLETE.getValue());
            assertThat(result.getTotalPrice()).isEqualTo(1500L);

            OrderResult.Product productResult = result.getProducts().get(0);
            assertThat(productResult.getName()).isEqualTo("Ultraboost");
            assertThat(productResult.getBrand()).isEqualTo("Adidas");
            assertThat(productResult.getPrice()).isEqualTo(1500L);
        }

        @Test
        @DisplayName("userId가 null인 경우 UNAUTHORIZED 예외가 발생한다.")
        void shouldThrowUnauthorized_whenUserIdIsNull() {
            // when
            CoreException ex = assertThrows(CoreException.class, () -> {
                orderFacade.getOrders(null);
            });

            // then
            assertThat(ex.getErrorType()).isEqualTo(ErrorType.UNAUTHORIZED);
            assertThat(ex.getMessage()).contains("로그인 후 이용 가능합니다.");
        }

        @Test
        @DisplayName("userId가 빈 문자열인 경우 UNAUTHORIZED 예외가 발생한다.")
        void shouldThrowUnauthorized_whenUserIdIsEmpty() {
            // when
            CoreException ex = assertThrows(CoreException.class, () -> {
                orderFacade.getOrders("");
            });

            // then
            assertThat(ex.getErrorType()).isEqualTo(ErrorType.UNAUTHORIZED);
            assertThat(ex.getMessage()).contains("로그인 후 이용 가능합니다.");
        }
    }


    @DisplayName("주문 동시성 테스트")
    @Nested
    public class Concurrent {
        
        @Test
        @DisplayName("동시에 주문해도 쿠폰은 1번만 사용된다.")
        public void coupon_used_only_once_under_concurrency() throws Exception{
            // given
            String userId = "hoyongeom";
            long price = 1_000L;
            long stockQty = 30L;
            long initialPoint = 100_000L;

            // 기초 데이터
            User user = userRepository.save(User.create(
                    userId,
                    "1q2w3e4r!@",
                    "userName",
                    "email@loopers.com",
                    "010-1234-5678",
                    "1994-04-20",
                    Gender.MALE
            ));
            Brand brand = brandRepository.save(Brand.create("나이키", "조던"));
            Product product = productRepository.save(Product.create(brand.getId(), "P", price));
            stockRepository.save(Stock.create(product.getId(), stockQty));
            pointRepository.save(Point.create(userId, initialPoint));
            Coupon coupon = couponRepository.save(Coupon.create("test", 100L, 10L, DiscountType.PERCENT, CouponStatus.ACTIVE, LocalDateTime.now().plusDays(10)));
            UserCoupon userCoupon = userCouponRepository.saveAndFlush(UserCoupon.create(userId, coupon.getId()));

            int threads = 20;

            // when
            List<CompletableFuture<Boolean>> results = runConcurrently(
                    threads,
                    () -> {
                        try {
                            orderFacade.order(orderOf(userId, product.getId(), 1L, userCoupon.getCouponId()));
                            return true;
                        } catch (CoreException e) {
                            return false;
                        }
                    },
                    12
            );

            long successCount = results.stream().map(CompletableFuture::join).filter(Boolean::booleanValue).count();

            // then
//            em.clear();
            UserCoupon uc = userCouponRepository.findByUserIdAndCouponId(userId, userCoupon.getCouponId())
                    .orElseThrow();
            boolean used = uc.getUserCouponStatus() == UserCouponStatus.USED;

            assertThat(used).isTrue();
            assertThat(successCount).isEqualTo(1);
        }

        @Test
        @DisplayName("동일 유저가 여러 상품을 포함한 주문을 동시에 수행해도 포인트는 정확히 차감된다.")
        void points_deduct_all_success_when_sameUser_manyOrders() throws Exception {
            // given
            String userId = "hoyong";
            int orders = 30;
            long pricePerItem = 2_000L;
            long quantityPerItem = 2L;
            int productCountPerOrder = 3;
            long initialPoint = (pricePerItem * quantityPerItem * productCountPerOrder) * orders + 10_000L;

            userRepository.save(User.create(
                    userId, "1q2w3e4r!@", "userName", "email@loopers.com",
                    "010-1234-5678", "1994-04-20", Gender.MALE
            ));

            pointRepository.save(Point.create(userId, initialPoint));
            Brand brand = brandRepository.save(Brand.create("나이키", "조던"));
            List<Product> products = IntStream.range(0, productCountPerOrder)
                    .mapToObj(i -> productRepository.save(Product.create(brand.getId(), "product-" + i, pricePerItem)))
                    .toList();

            products.forEach(p -> stockRepository.save(Stock.create(p.getId(), 100L)));


            CountDownLatch start = new CountDownLatch(1);

            // when
            List<CompletableFuture<Boolean>> results = IntStream.range(0, orders)
                    .mapToObj(i -> CompletableFuture.supplyAsync(() -> {
                        try {
                            start.await();
                            OrderCriteria.Order request = OrderCriteria.Order.of(
                                    userId,
                                    products.stream()
                                            .map(p -> OrderCriteria.OrderProduct.of(p.getId(), quantityPerItem))
                                            .toList()
                                    ,
                                    null
                            );
                            orderFacade.order(request);
                            return true;
                        } catch (CoreException e) {
                            return false;
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }))
                    .toList();

            // 모든 스레드 동시에 시작
            start.countDown();
            long success = results.stream().map(CompletableFuture::join).filter(Boolean::booleanValue).count();

            // then
            long expectedTotalPointDeducted = ((pricePerItem * quantityPerItem * productCountPerOrder)) * success;
            long remain = pointRepository.findByUserId(userId).orElseThrow().getAmount();
            assertThat(success).isEqualTo(orders);
            assertThat(remain).isEqualTo(initialPoint - expectedTotalPointDeducted);
        }

        @Test
        @DisplayName("동일 상품에 여러 주문이 동시에 수행되도 재고는 정상적으로 모두 차감된다.")
        void stock_deduct_all_success_when_sameProduct_manyOrders_with_ExecutorService() throws Exception {
            // given
            String userId = "hoyong";
            long price = 1_000L;
            int orders = 10;
            long quantityPerOrder = 1L;
            long initialStock = orders * quantityPerOrder;
            long initialPoint = price * orders * quantityPerOrder + 10_000L;

            // 사용자, 상품, 재고, 포인트 설정
            userRepository.save(User.create(
                    userId, "1q2w3e4r!@", "userName", "email@loopers.com",
                    "010-1234-5678", "1994-04-20", Gender.MALE
            ));
            Brand brand = brandRepository.save(Brand.create("나이키", "스포츠 브랜드"));
            Product product = productRepository.save(Product.create(brand.getId(), "Air Max", price));
            stockRepository.save(Stock.create(product.getId(), initialStock));
            pointRepository.save(Point.create(userId, initialPoint));

            ExecutorService executorService = Executors.newFixedThreadPool(16);
            CountDownLatch ready = new CountDownLatch(orders);
            CountDownLatch start = new CountDownLatch(1);
            CountDownLatch done = new CountDownLatch(orders);

            AtomicInteger successCount = new AtomicInteger(0);

            for (int i = 0; i < orders; i++) {
                executorService.submit(() -> {
                    try {
                        ready.countDown();
                        start.await();

                        OrderCriteria.Order request = OrderCriteria.Order.of(
                                userId,
                                List.of(OrderCriteria.OrderProduct.of(product.getId(), quantityPerOrder)),
                                null
                        );

                        orderFacade.order(request);
                        successCount.incrementAndGet();
                    } catch (Exception e) {
                        System.out.println("실패: " + e.getMessage());
                    } finally {
                        done.countDown();
                    }
                });
            }

            ready.await();
            start.countDown();
            done.await();
            executorService.shutdown();

            // then
            long remainingStock = stockRepository.findByProductId(product.getId()).getQuantity();
            long remainingPoint = pointRepository.findByUserId(userId).orElseThrow().getAmount();

            assertThat(successCount.get()).isEqualTo(orders);
            assertThat(remainingStock).isEqualTo(0L);
            assertThat(remainingPoint).isEqualTo(initialPoint - (orders * price));
        }


        private OrderCriteria.Order orderOf(String userId, Long productId, long qty, Long couponId) {
            return OrderCriteria.Order.of(
                    userId,
                    List.of(OrderCriteria.OrderProduct.of(productId, qty)),
                    couponId
            );
        }

        private <T> List<CompletableFuture<T>> runConcurrently(int threads, Callable<T> task, int poolSize) throws Exception {
            ExecutorService pool = Executors.newFixedThreadPool(poolSize);
            CountDownLatch start = new CountDownLatch(1);
            List<CompletableFuture<T>> futures = IntStream.range(0, threads)
                    .mapToObj(i -> CompletableFuture.supplyAsync(() -> {
                        try {
                            start.await();
                            return task.call();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            return null;
                        } catch (Exception e) {
                            throw new CompletionException(e);
                        }
                    }, pool)).toList();
            start.countDown();
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            pool.shutdownNow();
            return futures;
        }



    }
}
