package com.loopers.domain.order;

import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class OrderServiceIntegrationTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @Nested
    @DisplayName("OrderService 통합 테스트")
    class Create {

        @Test
        @DisplayName("주문 생성시 주문 정보가 저장된다")
        void createOrder_savesOrderSuccessfully() {
            // given
            OrderCommand.Order command = OrderCommand.Order.of(
                    "user123",
                    OrderCommand.OrderProducts.of(List.of(
                            OrderCommand.OrderProduct.of(1L, 2L, 1000L),
                            OrderCommand.OrderProduct.of(2L, 1L, 1500L)
                    ))
            );

            // when
            OrderInfo.Order result = orderService.createOrder(command);

            // then
            assertThat(result.getUserId()).isEqualTo("user123");
            assertThat(result.getTotalPrice()).isEqualTo(3500L);
            assertThat(result.getOrderProducts().getOrderProducts()).hasSize(2);
            assertThat(result.getOrderStatus()).isEqualTo("PENDING");
        }

        @Test
        @DisplayName("주문 상태를 업데이트하면 변경된 상태로 반영된다")
        void updateOrderStatus_updatesSuccessfully() {
            // given
            Order order = orderRepository.save(Order.create("user1", OrderCommand.OrderProducts.of(
                    List.of(OrderCommand.OrderProduct.of(1L, 1L, 1000L))
            )));

            // when
            orderService.updateOrderStatus(OrderCommand.OrderStatus.of(order.getId(), OrderStatus.COMPLETE.getValue()));

            // then
            Order updated = orderRepository.findById(order.getId());
            assertThat(updated.getOrderStatus()).isEqualTo(OrderStatus.COMPLETE);
        }

        @Test
        @DisplayName("주문 상세 정보를 조회하면 전체 상품 목록과 함께 반환된다")
        void getOrderDetail_returnsOrderDetail() {
            // given
            String userId = "user";
            Order order = orderRepository.save(Order.create(userId, OrderCommand.OrderProducts.of(
                    List.of(OrderCommand.OrderProduct.of(1L, 2L, 1000L))
            )));

            // when
            OrderInfo.OrderDetail detail = orderService.getOrderDetail(OrderCommand.OrderDetail.of(userId, order.getId()));

            // then
            assertThat(detail.getOrderId()).isEqualTo(order.getId());
            assertThat(detail.getOrderProducts().getOrderProducts()).hasSize(1);
            assertThat(detail.getOrderStatus()).isEqualTo("PENDING");
        }

        @Test
        @DisplayName("특정 사용자의 주문 목록을 조회하면 전체 주문이 반환된다")
        void getOrders_returnsAllOrdersByUser() {
            // given
            orderRepository.save(Order.create("userX", OrderCommand.OrderProducts.of(
                    List.of(OrderCommand.OrderProduct.of(1L, 2L, 1000L))
            )));
            orderRepository.save(Order.create("userX", OrderCommand.OrderProducts.of(
                    List.of(OrderCommand.OrderProduct.of(2L, 1L, 1500L))
            )));

            // when
            OrderInfo.Orders orders = orderService.getOrders("userX");

            // then
            assertThat(orders.getOrders()).hasSize(2);
            assertThat(orders.getOrders().get(0).getUserId()).isEqualTo("userX");
        }
    }
}
