package com.loopers.application.order;

import com.loopers.domain.brand.Brand;
import com.loopers.domain.brand.BrandRepository;
import com.loopers.domain.order.OrderStatus;
import com.loopers.domain.point.Point;
import com.loopers.domain.point.PointRepository;
import com.loopers.domain.product.Product;
import com.loopers.domain.product.ProductRepository;
import com.loopers.domain.stock.Stock;
import com.loopers.domain.stock.StockRepository;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

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
            OrderCriteria.Order order = OrderCriteria.Order.of(userId, List.of());

            // when
            CoreException ex = assertThrows(CoreException.class, () -> {
                orderFacade.order(order);
            });

            // then
            assertThat(ex.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
            assertThat(ex.getMessage()).contains("주문할 상품이 없습니다.");
        }

        @Test
        @DisplayName("로그인되지 않은 사용자의 주문 시 UNAUTHORIZED 예외가 발생한다.")
        void throwUnauthorized_whenUserIdIsNull() {
            // given
            OrderCriteria.Order order = OrderCriteria.Order.of(null, List.of(
                    OrderCriteria.OrderProduct.of(1L, 1L)
            ));

            // when
            CoreException ex = assertThrows(CoreException.class, () -> {
                orderFacade.order(order);
            });

            // then
            assertThat(ex.getErrorType()).isEqualTo(ErrorType.UNAUTHORIZED);
            assertThat(ex.getMessage()).contains("로그인 후 이용 가능합니다.");
        }

        @Test
        @DisplayName("상품 ID 또는 수량이 null인 경우 BAD_REQUEST 예외가 발생한다.")
        void throwBadRequest_whenProductIdOrQuantityIsNull() {
            // given
            OrderCriteria.Order order = OrderCriteria.Order.of(userId, List.of(
                    OrderCriteria.OrderProduct.of(null, null)
            ));

            // when
            CoreException ex = assertThrows(CoreException.class, () -> {
                orderFacade.order(order);
            });

            // then
            assertThat(ex.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
            assertThat(ex.getMessage()).contains("상품 ID와 수량은 필수입니다.");
        }

        @Test
        @DisplayName("재고가 부족한 상품 주문 시 BAD_REQUEST 예외가 발생한다.")
        void throwBadRequest_whenStockIsNotEnough() {
            // given
            Brand brand = brandRepository.save(Brand.create("TestBrand", "desc"));
            Product product = productRepository.save(Product.create(brand.getId(), "TestProduct", 1000L));
            stockRepository.save(Stock.create(product.getId(), 1L)); // 재고 1개
            pointRepository.save(Point.create(userId, 5000L));

            OrderCriteria.Order order = OrderCriteria.Order.of(userId, List.of(
                    OrderCriteria.OrderProduct.of(product.getId(), 5L) // 주문 수량 > 재고
            ));

            // when
            CoreException ex = assertThrows(CoreException.class, () -> {
                orderFacade.order(order);
            });

            // then
            assertThat(ex.getErrorType()).isEqualTo(ErrorType.BAD_REQUEST);
            assertThat(ex.getMessage()).contains("재고가 부족합니다");
        }

        @Test
        @DisplayName("유효한 주문 정보로 주문을 생성하면 주문 상태가 COMPLETED로 설정된다.")
        void createOrder_shouldCompleteOrder_whenValidInput() {
            // given
            Brand brand = brandRepository.save(Brand.create("TestBrand", "desc"));
            Product product = productRepository.save(Product.create(brand.getId(), "TestProduct", 1000L));
            stockRepository.save(Stock.create(product.getId(), 10L));
            pointRepository.save(Point.create(userId, 5000L));

            OrderCriteria.Order order = OrderCriteria.Order.of(userId, List.of(
                    OrderCriteria.OrderProduct.of(product.getId(), 1L)
            ));

            // when
            orderFacade.order(order);

            // then
            OrderResult.Orders result
                    = orderFacade.getOrders(userId);
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
            Brand brand = brandRepository.save(Brand.create("Nike", "Shoes"));
            Product product = productRepository.save(Product.create(brand.getId(), "Air Max", 1200L));
            stockRepository.save(Stock.create(product.getId(), 5L));
            pointRepository.save(Point.create(userId, 5000L));

            OrderCriteria.Order order = OrderCriteria.Order.of(userId, List.of(
                    OrderCriteria.OrderProduct.of(product.getId(), 2L)
            ));
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
            Brand brand = brandRepository.save(Brand.create("Adidas", "Shoes"));
            Product product = productRepository.save(Product.create(brand.getId(), "Ultraboost", 1500L));
            stockRepository.save(Stock.create(product.getId(), 10L));
            pointRepository.save(Point.create(userId, 5000L));

            OrderCriteria.Order order = OrderCriteria.Order.of(userId, List.of(
                    OrderCriteria.OrderProduct.of(product.getId(), 1L)
            ));
            orderFacade.order(order);

            // when
            OrderResult.Orders orders = orderFacade.getOrders(userId);

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
}
